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

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
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
                responseMap.put(CODE, "401");
                responseMap.put(STATUS, "Unauthorized");
                responseMap.put(MESSAGE, "User is blocked.");
                return ResponseEntity.status(401).body(responseMap);
            }

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(student.getEmail(), student.getPassword()));
        } catch (BadCredentialsException e) {
            responseMap.put(CODE, "401");
            responseMap.put(STATUS, "Unauthorized");
            responseMap.put(MESSAGE, "Invalid Credentials");
            return ResponseEntity.status(401).body(responseMap);
        }
        String token = generateToken(student);

        responseMap.put(CODE, "200");
        responseMap.put(STATUS, "success");
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
        try {
            String email = jwtTokenUtil.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtTokenUtil.validateToken(token, userDetails)) {
                responseMap.put(CODE, "200");
                responseMap.put(STATUS, "success");
                responseMap.put(MESSAGE, "User is logged in");
            } else {
                responseMap.put(CODE, "400");
                responseMap.put(STATUS, "error");
                responseMap.put(MESSAGE, "User is not logged in");
            }
        } catch (ExpiredJwtException | UsernameNotFoundException e) {
            responseMap.put(CODE, "400");
            responseMap.put(STATUS, "error");
            responseMap.put(MESSAGE, "User is not logged in");
        }

        return ResponseEntity.ok(responseMap);
    }

    @Operation(summary = "Join to the student service")
    @PostMapping("/join")
    public ResponseEntity<Map<String, Object>> joinToService(@RequestBody Student student) {
        String email = student.getEmail();
        Map<String, Object> response = new HashMap<>();

        if (studentService.findByEmail(email) != null) {
            response.put(CODE, "400");
            response.put(STATUS, "error");
            response.put(MESSAGE, "User already registered with this email");
            return ResponseEntity.badRequest().body(response);
        }

        if (isValidEmailDomain(email, student)) {
            saveStudent(student);

            VerificationCode existingCode = verificationCodeService.findByEmail(email);
            if (existingCode != null && existingCode.getExpirationDate().isAfter(LocalDateTime.now())) {
                response.put(CODE, "400");
                response.put(STATUS, "error");
                response.put(MESSAGE, "Verification code has already been sent. Please wait before requesting another code.");
            }

            sendCodeAndSetExpiration(email);
            response.put(CODE, "200");
            response.put(STATUS, "success");
            response.put(MESSAGE, "Verification code sent successfully");
            return ResponseEntity.ok(response);
        }

        response.put(CODE, "400");
        response.put(STATUS, "error");
        response.put(MESSAGE, "Invalid email");
        return ResponseEntity.badRequest().body(response);
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

    @Operation(summary = "Resend verification code to student email")
    @PostMapping("/resend-code")
    public ResponseEntity<Map<String, Object>> resendVerificationCode(@RequestBody Student student) {
        String email = student.getEmail();
        Map<String, Object> response = new HashMap<>();

        VerificationCode existingCode = verificationCodeService.findByEmail(email);
        if (existingCode != null) {
            LocalDateTime expirationDate = existingCode.getExpirationDate();
            LocalDateTime currentTime = LocalDateTime.now();

            if (expirationDate.isBefore(currentTime)) {
                verificationCodeService.deleteExpiredTokens();
            } else {
                response.put(CODE, "400");
                response.put(STATUS, "error");
                response.put(MESSAGE, "Verification code has already been sent. Please wait before requesting another code.");
            }
        }

        sendCodeAndSetExpiration(email);
        response.put(CODE, "200");
        response.put(STATUS, "success");
        response.put(MESSAGE, "Verification code sent successfully");
        return ResponseEntity.ok(response);
    }

    private void sendCodeAndSetExpiration(String email) {
        int verificationCode = verificationCodeService.generateCode(email).getCode();
        emailService.sendVerificationCode(email, verificationCode);

        VerificationCode verification = new VerificationCode();
        verification.setCode(verificationCode);
        verification.setExpirationDate(LocalDateTime.now().plusMinutes(1));
        verification.setEmail(email);
    }

    private void saveStudent(Student student) {
        student.setEnabled(false);
        student.setRole(Role.ROLE_STUDENT);
        studentService.save(student);
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
        Student student = studentService.findByEmail(verificationCode.getEmail());

        if (student == null) {
            Map<String, Object> response = new HashMap<>();
            response.put(CODE, "400");
            response.put(STATUS, "error");
            response.put(MESSAGE, "Student with provided email does not exist");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<VerificationCode> verifyCode = verificationCodeService.findByStudentId(student.getId());
        if (verifyCode.isEmpty() || verifyCode.get().getCode() != verificationCode.getCode()) {
            Map<String, Object> response = new HashMap<>();
            response.put(CODE, "400");
            response.put(STATUS, "error");
            response.put(MESSAGE, "Invalid verification code");
            return ResponseEntity.badRequest().body(response);
        }

        LocalDateTime expirationTime = verifyCode.get().getExpirationDate();
        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isAfter(expirationTime)) {
            Map<String, Object> response = new HashMap<>();
            response.put(CODE, "400");
            response.put(STATUS, "error");
            response.put(MESSAGE, "Verification code has expired");
            return ResponseEntity.badRequest().body(response);
        }

        student.setEnabled(true);
        studentService.save(student);

        Map<String, Object> response = new HashMap<>();
        response.put(CODE, "200");
        response.put(STATUS, "success");
        response.put(MESSAGE, "User successfully verified");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Check student status")
    @PostMapping("/check-status")
    public ResponseEntity<Map<String, Object>> checkStatus(@RequestBody Student student) {
        Map<String, Object> response = new HashMap<>();
        String email = student.getEmail();
        Student checkedStudent = studentService.findByEmail(email);

        if (checkedStudent == null) {
            response.put(CODE, "400");
            response.put(STATUS, "error");
            response.put(MESSAGE, "Student with provided email does not exist");
            return ResponseEntity.badRequest().body(response);
        }

        if (checkedStudent.isEnabled() && student.getPassword() != null) {
            response.put(MESSAGE, "Student is verified and signed-in");
        } else {
            response.put(MESSAGE, "Student is not verified");
        }
        response.put(CODE, "200");
        response.put(STATUS, "success");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Sign-up after verification")
    @PostMapping(value = "/sign-up")
    public ResponseEntity<Map<String, Object>> saveUser(@RequestBody Student student) {
        String email = student.getEmail();
        Student existingStudent = studentService.findByEmail(email);
        if (existingStudent == null) {
            throw new IllegalArgumentException("Invalid email address");
        }
        if (!existingStudent.isEnabled()) {
            throw new IllegalArgumentException("Student not verified");
        }

        validateRequiredFields(student);

        updateStudentDetails(existingStudent, student);

        String token = generateToken(existingStudent);

        log.info("Account registered with email - " + email);
        Map<String, Object> response = new HashMap<>();
        response.put(CODE, "200");
        response.put(STATUS, "success");
        response.put(MESSAGE, "Account created successfully");
        response.put("token", token);

        return ResponseEntity.ok().body(response);
    }

    private void validateRequiredFields(Student student) {
        if (student.getFirstName() == null || student.getLastName() == null ||
                student.getPassword() == null || student.getMajor() == null) {
            throw new IllegalArgumentException("Required fields are missing");
        }
    }

    private void updateStudentDetails(Student existingStudent, Student newStudent) {
        existingStudent.setFirstName(newStudent.getFirstName());
        existingStudent.setLastName(newStudent.getLastName());
        existingStudent.setHasNewMessages(false);
        existingStudent.setMajor(newStudent.getMajor());
        existingStudent.setCourse(newStudent.getCourse());
        byte[] imageBytes = newStudent.getPhotoBytes();
        if (imageBytes != null) {
            existingStudent.setPhotoBytes(imageBytes);
        }
        existingStudent.setRegistrationDate(LocalDateTime.now());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newStudent.getPassword());
        existingStudent.setPassword(encodedPassword);

        studentService.save(existingStudent);
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
            responseMap.put(CODE, "500");
            responseMap.put(STATUS, "error");
            responseMap.put(MESSAGE, "Something went wrong while logging out");
            return ResponseEntity.internalServerError().body(responseMap);
        }

        SecurityContextHolder.clearContext();
        responseMap.put(CODE, "200");
        responseMap.put(STATUS, "success");
        responseMap.put(MESSAGE, "Logged out successfully");

        return ResponseEntity.ok(responseMap);
    }
}
