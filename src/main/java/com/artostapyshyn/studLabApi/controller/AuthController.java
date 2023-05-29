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
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> loginUser(@RequestBody Student student) {
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
                responseMap.put("token", "Bearer " + token);

                String sessionId = UUID.randomUUID().toString();
                Optional<UserSession> existingSession = userSessionService.findBySessionId(sessionId);
                if (existingSession.isPresent()) {
                    userSessionService.delete(existingSession.get());
                }
                UserSession userSession = new UserSession();
                userSession.setSessionId(sessionId);
                userSession.setUserEmail(userDetails.getUsername());
                userSessionService.save(userSession);
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
        if (studentService.findByEmail(student.getEmail()) != null) {
            return ResponseEntity.badRequest().body("User already registered with this email");
        }

        student.setEmail(student.getEmail());
        student.setEnabled(false);
        student.setRole(Role.ROLE_STUDENT);
        studentService.save(student);

        int verificationCode = verificationCodesService.generateCode(student.getEmail()).getCode();
        emailService.sendVerificationCode(student.getEmail(), verificationCode);
        VerificationCode verification = new VerificationCode();
        verification.setCode(verificationCode);
        verification.setExpirationDate(LocalDateTime.now().plusMinutes(15));
        if (isValidEmailDomain(student.getEmail(), student)) {
            verification.setEmail(student.getEmail());
            verificationCodesService.save(verification);
            response.put("sent", true);

            log.info("Verification code sent to - " + student.getEmail());
            return ResponseEntity.ok(response);
        }
        response.put("valid", false);
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
    public ResponseEntity<String> verifyCode(@RequestBody VerificationCode verificationCode) {
        String email = verificationCode.getEmail();
        int code = verificationCode.getCode();
        Student student = studentService.findByEmail(email);

        if (student == null) {
            return new ResponseEntity<>("Student with provided email does not exist", HttpStatus.BAD_REQUEST);
        }

        Optional<VerificationCode> verifCode = verificationCodesService.findByStudentId(student.getId());
        if (verifCode.isEmpty() || verifCode.get().getCode() != code) {
            return ResponseEntity.badRequest().body("Invalid verification code");
        }

        LocalDateTime expirationTime = verifCode.get().getExpirationDate();
        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isAfter(expirationTime)) {
            return ResponseEntity.badRequest().body("Verification code has expired");
        }

        student.setEnabled(true);
        studentService.save(student);
        return ResponseEntity.ok().body("User successfully verified");
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
    public String logout(HttpServletRequest request) {
        getSessionId(request);
        Optional<UserSession> userSession = userSessionService.findBySessionId(getSessionId(request));
        if (userSession.isPresent()) {
            userSessionService.delete(userSession.get());
        }
        return "You have been successfully logged out!";
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
