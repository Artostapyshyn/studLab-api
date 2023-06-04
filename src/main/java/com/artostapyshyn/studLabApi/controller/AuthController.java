package com.artostapyshyn.studLabApi.controller;

import com.artostapyshyn.studLabApi.entity.Student;
import com.artostapyshyn.studLabApi.entity.University;
import com.artostapyshyn.studLabApi.entity.UserSession;
import com.artostapyshyn.studLabApi.entity.VerificationCode;
import com.artostapyshyn.studLabApi.enums.Role;
import com.artostapyshyn.studLabApi.service.*;
import com.artostapyshyn.studLabApi.service.impl.UserDetailsServiceImpl;
import com.artostapyshyn.studLabApi.util.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@Log4j2
public class AuthController {

    private final StudentService studentService;

    private final VerificationCodesService verificationCodesService;

    private final EmailService emailService;

    private final UniversityService universityService;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsServiceImpl userDetailsService;

    private final JwtTokenUtil jwtTokenUtil;

    private final UserSessionService userSessionService;

    @Operation(summary = "Login to student system")
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Student student, HttpServletResponse response) {
        Map<String, Object> responseMap;
        responseMap = new HashMap<>();
        try {
            Authentication auth;
            auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(student.getEmail(), student.getPassword()));
            if (auth.isAuthenticated()) {
                log.info("Logged In");
                UserDetails userDetails = userDetailsService.loadUserByUsername(student.getEmail());
                String token = jwtTokenUtil.generateToken(userDetails, student.getId());
                log.info(token);

                responseMap.put("message", "Logged In");
                responseMap.put("token", token);

                String sessionId = UUID.randomUUID().toString();
                Optional<UserSession> existingSession = userSessionService.findBySessionId(sessionId);
                existingSession.ifPresent(userSessionService::delete);
                UserSession userSession = new UserSession();
                userSession.setSessionId(sessionId);
                userSession.setUserEmail(userDetails.getUsername());
                userSessionService.save(userSession);

                Cookie cookie = new Cookie("JSESSIONID", sessionId);
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                cookie.setMaxAge(86400);
                response.addCookie(cookie);

                responseMap.put("sessionId", sessionId);
                return ResponseEntity.ok(responseMap);
            } else {
                responseMap.put("message", "Invalid Credentials");
                return ResponseEntity.status(401).body(responseMap);
            }
        } catch (DisabledException e) {
            e.printStackTrace();
            responseMap.put("message", "User is disabled");
            return ResponseEntity.internalServerError().body(responseMap);
        } catch (BadCredentialsException e) {
            responseMap.put("message", "Invalid Credentials");
            return ResponseEntity.status(401).body(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            responseMap.put("message", "Something went wrong");
            return ResponseEntity.internalServerError().body(responseMap);
        }
    }

    @Operation(summary = "Check session")
    @GetMapping("/checkSession")
    public ResponseEntity<?> checkSession(@RequestParam("sessionId") String sessionId) {
        Map<String, Object> responseMap = new HashMap<>();
        Optional<UserSession> userSessionOptional = userSessionService.findBySessionId(sessionId);

        if (userSessionOptional.isPresent()) {
            responseMap.put("message", "Session is valid");
            return ResponseEntity.ok(responseMap);
        } else {
            responseMap.put("message", "Session is not valid");
            return ResponseEntity.status(401).body(responseMap);
        }
    }

    @Operation(summary = "Join to the student service")
    @PostMapping("/join")
    public ResponseEntity<?> verifyEmail(@RequestBody Student student) {
        Map<String, Object> response = new HashMap<>();
        String email = student.getEmail();

        if (studentService.findByEmail(email) != null) {
            response.put("error", "User already registered with this email");
            return ResponseEntity.badRequest().body(response);
        }

        if (isValidEmailDomain(email, student)) {
            student.setEnabled(false);
            student.setRole(Role.ROLE_STUDENT);
            studentService.save(student);

            VerificationCode existingCode = verificationCodesService.findByEmail(email);
            if (existingCode != null && existingCode.getExpirationDate().isAfter(LocalDateTime.now())) {
                return handleResendCodeError(response, "Verification code has already been sent.");
            }

            int verificationCode = verificationCodesService.generateCode(email).getCode();
            emailService.sendVerificationCode(email, verificationCode);

            VerificationCode verification = new VerificationCode();
            verification.setCode(verificationCode);
            verification.setExpirationDate(LocalDateTime.now().plusMinutes(5));
            verification.setEmail(email);

            response.put("message", "Email sent successfully");
            log.info("Verification code sent to - " + email);
            return ResponseEntity.ok(response);
        }

        response.put("message", "Invalid email");
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/resend-code")
    public ResponseEntity<?> resendVerificationCode(@RequestBody Student student) {
        Map<String, Object> response = new HashMap<>();
        String email = student.getEmail();
        VerificationCode existingCode = verificationCodesService.findByEmail(email);

        if (existingCode != null) {
            LocalDateTime expirationDate = existingCode.getExpirationDate();
            LocalDateTime currentTime = LocalDateTime.now();

            if (expirationDate.isBefore(currentTime)) {
                verificationCodesService.deleteExpiredTokens();
            } else {
                return handleResendCodeError(response, "Verification code has already been sent. Please wait before requesting another code.");
            }
        }

        int verificationCode = verificationCodesService.generateCode(email).getCode();
        emailService.sendVerificationCode(email, verificationCode);

        VerificationCode verification = new VerificationCode();
        verification.setCode(verificationCode);
        verification.setExpirationDate(LocalDateTime.now().plusMinutes(5));
        verification.setEmail(email);

        response.put("message", "Verification code sent successfully");
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<?> handleResendCodeError(Map<String, Object> response, String errorMessage) {
        response.put("error", errorMessage);
        return ResponseEntity.badRequest().body(response);
    }

    public boolean isValidEmailDomain(String email, Student student) {
        String domain = Arrays.stream(email.split("@"))
                .skip(1)
                .findFirst()
                .orElse("");

        University university = universityService.findByDomain(domain);
        if (university != null) {
            student.setUniversity(university);
            return true;
        }
        return false;
    }

    @Operation(summary = "Verify student email")
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyCode(@RequestBody VerificationCode verificationCode) {
        Map<String, Object> response = new HashMap<>();
        String email = verificationCode.getEmail();
        int code = verificationCode.getCode();
        Student student = studentService.findByEmail(email);

        if (student == null) {
            response.put("message", "Student with provided email does not exist");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<VerificationCode> verifCode = verificationCodesService.findByStudentId(student.getId());
        if (verifCode.isEmpty() || verifCode.get().getCode() != code) {
            response.put("message", "Invalid verification code");
            return ResponseEntity.badRequest().body(response);
        }

        LocalDateTime expirationTime = verifCode.get().getExpirationDate();
        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isAfter(expirationTime)) {
            response.put("message", "Verification code has expired");
            return ResponseEntity.badRequest().body(response);
        }

        student.setEnabled(true);
        studentService.save(student);
        response.put("message", "User successfully verified");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Sign-up after verification")
    @PostMapping(value = "/sign-up")
    public ResponseEntity<?> saveUser(@RequestBody Student student) {
        Map<String, Object> responseMap = new HashMap<>();

        String email = student.getEmail();
        Student existingStudent = studentService.findByEmail(email);
        if (existingStudent == null) {
            responseMap.put("message", "Invalid email address");
            return ResponseEntity.badRequest().body(responseMap);
        }
        if (existingStudent.isEnabled()) {
            existingStudent.setFirstName(student.getFirstName());
            existingStudent.setLastName(student.getLastName());

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(student.getPassword());
            existingStudent.setPassword(encodedPassword);

            if (existingStudent.getPassword() == null) {
                responseMap.put("error", "Password cannot be null");
                return ResponseEntity.badRequest().body(responseMap);
            }

            existingStudent.setMajor(student.getMajor());
            existingStudent.setCourse(student.getCourse());
            existingStudent.setBirthDate(student.getBirthDate());

            byte[] imageBytes = student.getPhotoBytes();
            existingStudent.setPhotoFilename(student.getPhotoFilename());
            existingStudent.setPhotoBytes(imageBytes);

            studentService.save(existingStudent);
            UserDetails userDetails = userDetailsService.loadUserByUsername(student.getEmail());
            String token = jwtTokenUtil.generateToken(userDetails, student.getId());

            responseMap.put("email", student.getEmail());
            responseMap.put("message", "Account created successfully");
            log.info("Account registered with email - " + student.getEmail());
            responseMap.put("token", token);
        } else {
            responseMap.put("message", "Student not verified");
            return ResponseEntity.badRequest().body(responseMap);
        }
        return ResponseEntity.ok(responseMap);

    }

    @Operation(summary = "Logout from account")
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String sessionId = getSessionId(request);
        Optional<UserSession> userSession = userSessionService.findBySessionId(sessionId);
        Map<String, Object> responseMap = new HashMap<>();

        if (userSession.isPresent()) {
            userSessionService.delete(userSession.get());

            responseMap.put("message", "Logged out successfully");
            responseMap.put("userSession", null);

            return ResponseEntity.ok(responseMap);
        } else {
            responseMap.put("message", "User session not found");
            return ResponseEntity.badRequest().body(responseMap);
        }
    }

    private String getSessionId(HttpServletRequest request) {
        String sessionId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JSESSIONID")) {
                    sessionId = cookie.getValue();
                    break;
                }
            }
        }
        return sessionId;
    }

}
