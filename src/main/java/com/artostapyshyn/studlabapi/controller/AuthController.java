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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.MESSAGE;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(maxAge = 3600, origins = "*")
@AllArgsConstructor
@Log4j2
public class AuthController {

    private final StudentService studentService;

    private final VerificationCodeService verificationCodeService;

    private final EmailService emailService;

    private final UniversityService universityService;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsServiceImpl userDetailsService;

    private final JwtTokenUtil jwtTokenUtil;

    @Operation(summary = "Login to student system")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Student student) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            Student foundStudent = studentService.findByEmail(student.getEmail());

            if (foundStudent.getBlockedUntil() != null && foundStudent.getBlockedUntil().isAfter(LocalDateTime.now())) {
                responseMap.put(MESSAGE, "User is blocked.");
                return ResponseEntity.status(401).body(responseMap);
            }

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(student.getEmail(), student.getPassword()));
        } catch (BadCredentialsException e) {
            responseMap.put(MESSAGE, "Invalid Credentials");
            return ResponseEntity.status(401).body(responseMap);
        }
        String token = generateToken(student);
        log.info(token);

        responseMap.put(MESSAGE, "Logged In");
        responseMap.put("token", token);
        return ResponseEntity.ok(responseMap);
    }

    private String generateToken(Student student) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(student.getEmail());
        return jwtTokenUtil.generateToken(userDetails, student.getId());
    }

    @Operation(summary = "Check user login status")
    @GetMapping("/login-status")
    public ResponseEntity<Map<String, Object>> checkLoginStatus(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Map<String, Object> responseMap = new HashMap<>();

        if (token == null) {
            responseMap.put(MESSAGE, "User is not logged in");
            return ResponseEntity.ok(responseMap);
        }

        try {
            String email = jwtTokenUtil.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtTokenUtil.validateToken(token, userDetails)) {
                responseMap.put(MESSAGE, "User is logged in");
            } else {
                responseMap.put(MESSAGE, "User is not logged in");
            }
        } catch (ExpiredJwtException | UsernameNotFoundException e) {
            responseMap.put(MESSAGE, "User is not logged in");
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
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestBody Student student) {
        Map<String, Object> response = new HashMap<>();
        String email = student.getEmail();

        if (studentService.findByEmail(email) != null) {
            response.put(MESSAGE, "User already registered with this email");
            return ResponseEntity.badRequest().body(response);
        }

        if (isValidEmailDomain(email, student)) {
            student.setEnabled(false);
            student.setRole(Role.ROLE_STUDENT);
            studentService.save(student);

            VerificationCode existingCode = verificationCodeService.findByEmail(email);
            if (existingCode != null && existingCode.getExpirationDate().isAfter(LocalDateTime.now())) {
                return handleResendCodeError(response, "Verification code has already been sent.");
            }

            submitVerificationCode(email);

            response.put(MESSAGE, "Email sent successfully");
            log.info("Verification code sent to - " + email);
            return ResponseEntity.ok(response);
        }

        response.put(MESSAGE, "Invalid email");
        return ResponseEntity.badRequest().body(response);
    }

    private void submitVerificationCode(String email) {
        int verificationCode = verificationCodeService.generateCode(email).getCode();
        emailService.sendVerificationCode(email, verificationCode);

        VerificationCode verification = new VerificationCode();
        verification.setCode(verificationCode);
        verification.setExpirationDate(LocalDateTime.now().plusMinutes(1));
        verification.setEmail(email);
    }

    @PostMapping("/resend-code")
    public ResponseEntity<Map<String, Object>> resendVerificationCode(@RequestBody Student student) {
        Map<String, Object> response = new HashMap<>();
        String email = student.getEmail();
        VerificationCode existingCode = verificationCodeService.findByEmail(email);

        if (existingCode != null) {
            LocalDateTime expirationDate = existingCode.getExpirationDate();
            LocalDateTime currentTime = LocalDateTime.now();

            if (expirationDate.isBefore(currentTime)) {
                verificationCodeService.deleteExpiredTokens();
            } else {
                return handleResendCodeError(response, "Verification code has already been sent. Please wait before requesting another code.");
            }
        }

        submitVerificationCode(email);

        response.put(MESSAGE, "Verification code sent successfully");
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> handleResendCodeError(Map<String, Object> response, String errorMessage) {
        response.put("error", errorMessage);
        return ResponseEntity.badRequest().body(response);
    }

    public boolean isValidEmailDomain(String email, Student student) {
        String domain;
        domain = Arrays.stream(new String[]{email.split("@")[1]})
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
            response.put(MESSAGE, "Student with provided email does not exist");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<VerificationCode> verifCode = verificationCodeService.findByStudentId(student.getId());
        if (verifCode.isEmpty() || verifCode.get().getCode() != code) {
            response.put(MESSAGE, "Invalid verification code");
            return ResponseEntity.badRequest().body(response);
        }

        LocalDateTime expirationTime = verifCode.get().getExpirationDate();
        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isAfter(expirationTime)) {
            response.put(MESSAGE, "Verification code has expired");
            return ResponseEntity.badRequest().body(response);
        }

        student.setEnabled(true);
        studentService.save(student);
        response.put(MESSAGE, "User successfully verified");
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
            response.put(MESSAGE, "Student with provided email does not exist");
            return ResponseEntity.badRequest().body(response);
        }

        if (checkedStudent.isEnabled() && student.getPassword() != null) {
            response.put(MESSAGE, "Student is verified and signed-in");
            log.info("Checking verification status for student - " + email);
        } else {
            response.put(MESSAGE, "Student is not verified");
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
            responseMap.put(MESSAGE, "Invalid email address");
            return ResponseEntity.badRequest().body(responseMap);
        }
        if (existingStudent.isEnabled()) {

            boolean isValid = checkStudent(student);
            if (!isValid) {
                responseMap.put(MESSAGE, "Required fields are missing");
                return ResponseEntity.badRequest().body(responseMap);
            }

            signUpStudent(student, existingStudent);
            String token = generateToken(student);

            responseMap.put("email", student.getEmail());
            responseMap.put(MESSAGE, "Account created successfully");
            log.info("Account registered with email - " + student.getEmail());
            responseMap.put("token", token);
        } else {
            responseMap.put(MESSAGE, "Student not verified");
            return ResponseEntity.badRequest().body(responseMap);
        }
        return ResponseEntity.ok(responseMap);
    }

    private void signUpStudent(Student student, Student existingStudent) {
        existingStudent.setFirstName(student.getFirstName());
        existingStudent.setLastName(student.getLastName());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(student.getPassword());
        existingStudent.setPassword(encodedPassword);
        existingStudent.setHasNewMessages(false);
        existingStudent.setCity(student.getCity());
        existingStudent.setMajor(student.getMajor());
        existingStudent.setCourse(student.getCourse());
        byte[] imageBytes = student.getPhotoBytes();
        existingStudent.setPhotoBytes(imageBytes);
        existingStudent.setRegistrationDate(LocalDateTime.now());

        studentService.save(existingStudent);
    }

    private boolean checkStudent(Student student) {
        return student.getFirstName() != null &&
                student.getLastName() != null &&
                student.getPassword() != null &&
                student.getCity() != null &&
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
            responseMap.put(MESSAGE, "Something went wrong while logging out");
            return ResponseEntity.internalServerError().body(responseMap);
        }

        SecurityContextHolder.clearContext();
        responseMap.put(MESSAGE, "Logged out successfully");

        return ResponseEntity.ok(responseMap);
    }
}
