package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.dto.*;
import com.artostapyshyn.studlabapi.entity.*;
import com.artostapyshyn.studlabapi.enums.Role;
import com.artostapyshyn.studlabapi.service.*;
import com.artostapyshyn.studlabapi.service.impl.UserDetailsServiceImpl;
import com.artostapyshyn.studlabapi.util.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.ERROR;
import static com.artostapyshyn.studlabapi.constant.ControllerConstants.MESSAGE;

@Log4j2
@Validated
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(maxAge = 3600, origins = "*")
@AllArgsConstructor
public class AuthController {

    private final StudentService studentService;

    private final VerificationCodeService verificationCodeService;

    private final EmailService emailService;

    private final UniversityService universityService;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsServiceImpl userDetailsService;

    private final JwtTokenUtil jwtTokenUtil;

    private final ModelMapper modelMapper;

    @Operation(summary = "Login to student system")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody LoginDto loginDto) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            Student foundStudent = studentService.findByEmail(loginDto.getEmail());
            if (foundStudent.getBlockedUntil() != null && foundStudent.getBlockedUntil().isAfter(LocalDateTime.now())) {
                return handleUnauthorized("User is blocked.");
            }

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        } catch (BadCredentialsException e) {
            log.warn(e.getMessage());
            return handleUnauthorized("Invalid Credentials");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getEmail());
        String token = jwtTokenUtil.generateToken(userDetails, loginDto.getId());
        log.info(token);

        responseMap.put(MESSAGE, "Logged In");
        responseMap.put("token", token);
        return ResponseEntity.ok(responseMap);
    }

    @PostMapping("/join")
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestBody VerificationDto verificationDto) {
        log.info("Attempting to verify email for: {}", verificationDto.getEmail());

        Map<String, Object> response = new HashMap<>();
        String email = verificationDto.getEmail();
        Student existingStudent = studentService.findByEmail(email);

        if (existingStudent != null && existingStudent.getFirstName() != null && existingStudent.getLastName() != null) {
            log.warn("Email {} is already registered", email);
            response.put(ERROR, "User already registered with this email");
            return ResponseEntity.badRequest().body(response);
        }

        Student student = new Student();
        if (existingStudent != null) {
            student = existingStudent;
        }

        if (isValidEmailDomain(email, student)) {
            student.setEnabled(false);
            student.setRole(Role.ROLE_STUDENT);
            studentService.save(student);

            VerificationCode existingCode = verificationCodeService.findByEmail(email);
            if (existingCode != null && existingCode.getExpirationDate().isAfter(LocalDateTime.now())) {
                return handleResendCodeError(response);
            }

            sendCode(email, response, false);
            log.info("Email verification initiated for: {}", email);
            return ResponseEntity.ok(response);
        }

        response.put(MESSAGE, "Invalid email");
        return ResponseEntity.badRequest().body(response);
    }

    @Operation(summary = "Resend verification code")
    @PostMapping("/resend-code")
    public ResponseEntity<Map<String, Object>> resendVerificationCode(@RequestBody ResendCodeDto resendCodeDto) {
        Map<String, Object> response = new HashMap<>();
        String email = resendCodeDto.getEmail();

        sendCode(email, response, false);
        response.put(MESSAGE, "Email sent successfully");

        log.info("Verification code sent to - " + email);
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

    @Operation(summary = "Verify student email")
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyCode(@RequestBody VerificationCode verificationCode) {
        return verify(verificationCode);
    }

    @Operation(summary = "Sign-up after verification")
    @PostMapping(value = "/sign-up")
    public ResponseEntity<Map<String, Object>> saveUser(@RequestBody SignUpDto signUpDto) {
        try {
            log.info("Sign-Up attempt for email: {}", signUpDto.getEmail());
            String email = signUpDto.getEmail();
            Student existingStudent = studentService.findByEmail(email);

            if (existingStudent == null) {
                return handleBadRequest("Invalid email address");
            }

            if (existingStudent.isEnabled()) {
                return registerStudent(signUpDto, existingStudent);
            } else {
                return handleBadRequest("Student not verified");
            }
        } catch (Exception e) {
            log.error("Error occurred while signing up for email: {}", signUpDto.getEmail(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(ERROR, "An unexpected error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Reset password with the new password")
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        Map<String, Object> response = new HashMap<>();
        String email = resetPasswordDto.getEmail();

        Student student = studentService.findByEmail(email);
        if (student == null) {
            response.put(ERROR, "User is not registered with this email");
            return ResponseEntity.badRequest().body(response);
        }

        studentService.updatePassword(student, resetPasswordDto.getPassword());

        response.put(MESSAGE, "Password has been successfully changed");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Forgot password")
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> resetUserPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        Map<String, Object> response = new HashMap<>();
        String email = forgotPasswordDto.getEmail();

        VerificationCode existingCode = verificationCodeService.findByEmail(email);
        if (existingCode != null && existingCode.getExpirationDate().isAfter(LocalDateTime.now())) {
            return handleResendCodeError(response);
        }

        sendCode(email, response, true);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Verify student email to reset password")
    @PostMapping("/reset-password/verify")
    public ResponseEntity<Map<String, Object>> verifyResetPasswordCode(@RequestBody VerificationCode verificationCode) {
        return verify(verificationCode);
    }

    @Operation(summary = "Logout from account")
    @GetMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> responseMap = new HashMap<>();

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.getValue();
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
        try {
            request.logout();
        } catch (ServletException e) {
            log.warn(e.getMessage());
            responseMap.put(MESSAGE, "Something went wrong while logging out");
            return ResponseEntity.internalServerError().body(responseMap);
        }

        SecurityContextHolder.clearContext();
        responseMap.put(MESSAGE, "Logged out successfully");

        return ResponseEntity.ok(responseMap);
    }

    private void sendCode(String email, Map<String, Object> response, boolean isResetPassword) {
        VerificationCode existingCode = verificationCodeService.findByEmail(email);
        if (existingCode != null) {
            LocalDateTime expirationDate = existingCode.getExpirationDate();
            LocalDateTime currentTime = LocalDateTime.now();
            if (expirationDate.isAfter(currentTime)) {
                handleResendCodeError(response);
            }
            verificationCodeService.delete(existingCode);
        }

        int verificationCode = verificationCodeService.generateCode(email).getCode();

        if (isResetPassword) {
            emailService.sendResetPasswordCode(email, verificationCode);
        } else {
            emailService.sendVerificationCode(email, verificationCode);
        }

        response.put(MESSAGE, "Email sent successfully");
        log.info("Verification code sent to - " + email);
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

    private ResponseEntity<Map<String, Object>> verify(VerificationCode code) {
        log.info("Verifying code for email: {}", code.getEmail());
        Map<String, Object> response = new HashMap<>();
        String email = code.getEmail();
        int verificationCode = code.getCode();
        Student student = studentService.findByEmail(email);

        if (student == null) {
            return handleBadRequest("Student with provided email does not exist");
        }

        Optional<VerificationCode> verificationCodeOptional = verificationCodeService.findByStudentId(student.getId());
        if (verificationCodeOptional.isEmpty() || verificationCodeOptional.get().getCode() != verificationCode) {
            return handleBadRequest("Invalid verification code");
        }

        LocalDateTime expirationTime = verificationCodeOptional.get().getExpirationDate();
        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isAfter(expirationTime)) {
            return handleBadRequest("Verification code has expired");
        }

        student.setEnabled(true);
        studentService.save(student);
        response.put(MESSAGE, "User successfully verified");
        log.info("User successfully verified with email: {}", code.getEmail());
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> registerStudent(SignUpDto signUpDto, Student existingStudent) {
        try {
            log.info("Registering student for email: {}", signUpDto.getEmail());
            Map<String, Object> responseMap = new HashMap<>();

            boolean isValid = checkSignUpDto(signUpDto);
            if (!isValid) {
                return handleBadRequest("Required fields are missing");
            }
            log.info("SignUpDto received: {}", signUpDto);
            studentService.signUpStudent(signUpDto, existingStudent);
            UserDetails userDetails = userDetailsService.loadUserByUsername(signUpDto.getEmail());
            String token = jwtTokenUtil.generateToken(userDetails, signUpDto.getId());
            log.info("Token received: {}", token);

            responseMap.put("email", signUpDto.getEmail());
            responseMap.put(MESSAGE, "Account created successfully");
            responseMap.put("token", token);
            log.info("Account registered with email: {}", signUpDto.getEmail());
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            log.error("Error occurred while registering student for email: {}", signUpDto.getEmail(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(ERROR, "An unexpected error occurred while registering");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    private ResponseEntity<Map<String, Object>> handleBadRequest(String message) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put(ERROR, message);
        return ResponseEntity.badRequest().body(responseMap);
    }

    private ResponseEntity<Map<String, Object>> handleResendCodeError(Map<String, Object> response) {
        response.put(ERROR, "Verification code has already been sent.");
        return ResponseEntity.badRequest().body(response);
    }

    private ResponseEntity<Map<String, Object>> handleUnauthorized(String message) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put(MESSAGE, message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
    }

    private boolean checkSignUpDto(SignUpDto signUpDto) {
        return signUpDto.getFirstName() != null &&
                signUpDto.getLastName() != null &&
                signUpDto.getPassword() != null &&
                signUpDto.getCity() != null &&
                signUpDto.getMajor() != null &&
                signUpDto.getCourse() != null;
    }
}
