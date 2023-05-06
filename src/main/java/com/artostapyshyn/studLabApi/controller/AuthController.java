package com.artostapyshyn.studLabApi.controller;

import com.artostapyshyn.studLabApi.entity.Student;
import com.artostapyshyn.studLabApi.entity.University;
import com.artostapyshyn.studLabApi.entity.VerificationCode;
import com.artostapyshyn.studLabApi.enums.Role;
import com.artostapyshyn.studLabApi.service.EmailService;
import com.artostapyshyn.studLabApi.service.StudentService;
import com.artostapyshyn.studLabApi.service.UniversityService;
import com.artostapyshyn.studLabApi.service.VerificationCodesService;
import com.artostapyshyn.studLabApi.service.impl.UserDetailsServiceImpl;
import com.artostapyshyn.studLabApi.util.JwtTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.Optional;

@CrossOrigin(origins = "https://stud-lab-api.onrender.com", maxAge = 3600)
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
    public ResponseEntity<?> loginUser(@RequestParam("email") String email, @RequestParam("password") String password) {
        Map<String, Object> responseMap;
        responseMap = new HashMap<>();
        try {
            Authentication auth;
            auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            if (auth.isAuthenticated()) {
                log.info("Logged In");
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                String token = jwtTokenUtil.generateToken(userDetails);
                log.info(token);
                responseMap.put("message", "Logged In");
                responseMap.put("token", "Bearer " + token);
                return ResponseEntity.ok(responseMap);
            } else {
                responseMap.put("message", "Invalid Credentials");
                return ResponseEntity.status(401).body(responseMap);
            }
        } catch (DisabledException e) {
            e.printStackTrace();
            responseMap.put("message", "User is disabled");
            return ResponseEntity.status(500).body(responseMap);
        } catch (BadCredentialsException e) {
            responseMap.put("message", "Invalid Credentials");
            return ResponseEntity.status(401).body(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            responseMap.put("message", "Something went wrong");
            return ResponseEntity.status(500).body(responseMap);
        }
    }

    @Operation(summary = "Join to the student service")
    @PostMapping("/join")
    public ResponseEntity<?> verifyEmail(@RequestParam("email") String email) {

        if (studentService.findByEmail(email) != null) {
            return ResponseEntity.badRequest().body("User already registered with this email");
        }

        Student student = new Student();
        student.setEmail(email);
        student.setEnabled(false);
        studentService.save(student);

        int verificationCode = verificationCodesService.generateCode(email).getCode();
        emailService.sendVerificationCode(email, verificationCode);
        VerificationCode verification = new VerificationCode();
        verification.setCode(verificationCode);
        verification.setExpirationDate(LocalDateTime.now().plusMinutes(15));
        if(isValidEmailDomain(email, student)) {
            verification.setEmail(student.getEmail());
            verificationCodesService.save(verification);
            return ResponseEntity.ok().body("Verification code has been sent to your email successfully");
        }

        return ResponseEntity.ok().body("Your email is not valid");
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
            return new ResponseEntity<>("Invalid verification code", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime expirationTime = verifCode.get().getExpirationDate();
        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isAfter(expirationTime)) {
            return new ResponseEntity<>("Verification code has expired", HttpStatus.BAD_REQUEST);
        }

        int savedCode = verifCode.get().getCode();
        if (savedCode != code) {
            return new ResponseEntity<>("Invalid verification code", HttpStatus.BAD_REQUEST);
        }

        student.setEnabled(true);
        studentService.save(student);
        return new ResponseEntity<>("User successfully verified", HttpStatus.OK);
    }

    @Operation(summary = "Sign-up after verification")
    @PostMapping("/sign-up")
    public ResponseEntity<?> saveUser(@RequestBody Student student, @RequestParam("image") MultipartFile image) {
        Map<String, Object> responseMap = new HashMap<>();

        studentService.findByEmail(student.getEmail());
        if (student != null) {
            if (student.isEnabled()) {
                student.setRole(Role.ROLE_STUDENT);
                studentService.save(student);
                UserDetails userDetails = userDetailsService.loadUserByUsername(student.getEmail());
                String token = jwtTokenUtil.generateToken(userDetails);

                responseMap.put("email", student.getEmail());
                responseMap.put("message", "Account created successfully");
                responseMap.put("token", token);
            } else {
                responseMap.put("message", "Student not verified");
                return ResponseEntity.badRequest().body(responseMap);
            }
            return ResponseEntity.ok(responseMap);
        } else {
            responseMap.put("message", "Error occurred while registering student");
            return ResponseEntity.badRequest().body(responseMap);
        }

    }

    @Operation(summary = "Logout from account")
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        deleteCookie(request, response, "access_token");
        return "You have been successfully logged out!";
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }
}
