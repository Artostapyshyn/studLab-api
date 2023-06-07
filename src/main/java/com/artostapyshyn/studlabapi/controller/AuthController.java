package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.*;
import com.artostapyshyn.studlabapi.enums.Role;
import com.artostapyshyn.studlabapi.service.*;
import com.artostapyshyn.studlabapi.service.impl.UserDetailsServiceImpl;
import com.artostapyshyn.studlabapi.util.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.ServletException;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

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

    @Operation(summary = "Login to student system")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Student student, HttpServletResponse response) {
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

    @Operation(summary = "Check user login status")
    @GetMapping("/login-status")
    public ResponseEntity<Map<String, Object>> checkLoginStatus(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Map<String, Object> responseMap = new HashMap<>();

        if (token == null) {
            responseMap.put("message", "User is not logged in");
            return ResponseEntity.ok(responseMap);
        }

        try {
            String email = jwtTokenUtil.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtTokenUtil.validateToken(token, userDetails)) {
                responseMap.put("message", "User is logged in");
            } else {
                responseMap.put("message", "User is not logged in");
            }
        } catch (ExpiredJwtException | UsernameNotFoundException e) {
            responseMap.put("message", "User is not logged in");
        }

        return ResponseEntity.ok(responseMap);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Operation(summary = "Join to the student service")
    @PostMapping("/join")
    public ResponseEntity<?> verifyEmail(@RequestBody Student student) {
        Map<String, Object> response = new HashMap<>();
        String email = student.getEmail();

        if (studentService.findByEmail(email) != null) {
            response.put("message", "User already registered with this email");
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
            verification.setExpirationDate(LocalDateTime.now().plusMinutes(1));
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
        verification.setExpirationDate(LocalDateTime.now().plusMinutes(1));
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
        log.info("User successfully verified with email - " + email);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Check student status")
    @PostMapping("/check-status")
    public ResponseEntity<Map<String, Object>> checkStatus(@RequestBody Student student) {
        Map<String, Object> response = new HashMap<>();
        String email = student.getEmail();
        Student checkedStudent = studentService.findByEmail(email);

        if (checkedStudent == null) {
            response.put("message", "Student with provided email does not exist");
            return ResponseEntity.badRequest().body(response);
        }

        if (checkedStudent.isEnabled() && student.getPassword() != null) {
            response.put("message", "Student is verified and signed-in");
            log.info("Checking verification status for student - " + email);
        } else {
            response.put("message", "Student is not verified");
        }
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Sign-up after verification")
    @PostMapping(value = "/sign-up")
    public ResponseEntity<Map<String, Object>> saveUser(@RequestBody Student student) {
        Map<String, Object> responseMap = new HashMap<>();

        String email = student.getEmail();
        Student existingStudent = studentService.findByEmail(email);
        if (existingStudent == null) {
            responseMap.put("message", "Invalid email address");
            return ResponseEntity.badRequest().body(responseMap);
        }
        if (existingStudent.isEnabled()) {

            boolean isValid = checkStudent(student);
            if (!isValid) {
                responseMap.put("message", "Required fields are missing");
                return ResponseEntity.badRequest().body(responseMap);
            }

            existingStudent.setFirstName(student.getFirstName());
            existingStudent.setLastName(student.getLastName());

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(student.getPassword());
            existingStudent.setPassword(encodedPassword);
            existingStudent.setHasNewMessages(false);
            existingStudent.setMajor(student.getMajor());

            byte[] imageBytes = student.getPhotoBytes();
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

    private boolean checkStudent(Student student) {
        return student.getFirstName() != null &&
                student.getLastName() != null &&
                student.getPassword() != null &&
                student.getMajor() != null;
    }

    @Operation(summary = "Logout from account")
    @GetMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> responseMap = new HashMap<>();

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String cookieValue = cookie.getValue();
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
                break;
            }
        }
        try {
            request.logout();
        } catch (ServletException e) {
            responseMap.put("message", "Something went wrong while logging out");
            return ResponseEntity.internalServerError().body(responseMap);
        }

        SecurityContextHolder.clearContext();
        responseMap.put("message", "Logged out successfully");

        return ResponseEntity.ok(responseMap);
    }
}
