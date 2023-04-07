package com.artostapyshyn.studLabApi.controller;

import com.artostapyshyn.studLabApi.entity.Student;
import com.artostapyshyn.studLabApi.entity.VerificationCode;
import com.artostapyshyn.studLabApi.enums.Role;
import com.artostapyshyn.studLabApi.service.EmailService;
import com.artostapyshyn.studLabApi.service.StudentService;
import com.artostapyshyn.studLabApi.service.VerificationCodeService;
import com.artostapyshyn.studLabApi.service.impl.UserDetailsServiceImpl;
import com.artostapyshyn.studLabApi.util.JwtTokenUtil;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
@Log4j2
public class AuthController {

    private final StudentService studentService;
    private final VerificationCodeService verificationCodeService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

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

    @PostMapping("/join")
    public ResponseEntity<?> verifyEmail(@RequestParam("email") String email) {

        if (studentService.findByEmail(email) != null) {
            return ResponseEntity.badRequest().body("User already registered with this email");
        }

        Student student = new Student();
        student.setEmail(email);
        student.setEnabled(false);
        studentService.save(student);

        int verificationCode = verificationCodeService.generateCode(email).getCode();
        emailService.sendVerificationCode(email, verificationCode);
        VerificationCode verification = new VerificationCode();
        verification.setCode(verificationCode);
        verification.setExpirationDate(LocalDateTime.now().plusMinutes(15));

        verification.setStudentId(student.getId());
        verificationCodeService.save(verification);

        return ResponseEntity.ok().body("Verification code has been sent to your email successfully");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyCode(@RequestBody VerificationCode verificationCode) {
        Long id = verificationCode.getStudentId();
        int code = verificationCode.getCode();
        Optional<Student> student = studentService.findById(id);

        if (student.isEmpty()) {
            return new ResponseEntity<>("Student with provided email does not exist", HttpStatus.BAD_REQUEST);
        }

        Optional<VerificationCode> verifCode = verificationCodeService.findByStudentId(student.get().getId());
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

        student.get().setEnabled(true);
        studentService.save(student.get());
        return new ResponseEntity<>("User successfully verified", HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> saveUser(@RequestParam("first_name") String firstName, @RequestParam("last_name") String lastName,
                                      @RequestParam("email") String email, @RequestParam("password") String password,
                                      @RequestParam("photo") MultipartFile photo) {
        Map<String, Object> responseMap = new HashMap<>();

        Student student = studentService.findByEmail(email);
        if (student == null) {
            responseMap.put("message", "Invalid email address");
            return ResponseEntity.badRequest().body(responseMap);
        }

        if (student.isEnabled()) {
            try {
                student.setFirstName(firstName);
                student.setLastName(lastName);
                student.setPassword(new BCryptPasswordEncoder().encode(password));
                student.setPhoto(photo.getBytes());
                student.setRole(Role.ROLE_STUDENT);
                studentService.save(student);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                String token = jwtTokenUtil.generateToken(userDetails);

                responseMap.put("email", email);
                responseMap.put("message", "Account created successfully");
                responseMap.put("token", token);
            } catch (IOException e) {
                return ResponseEntity.badRequest().body("Failed to register student: " + e.getMessage());
            }
        } else {
            responseMap.put("message", "Student not verified");
            return ResponseEntity.badRequest().body(responseMap);
        }
        return ResponseEntity.ok(responseMap);
    }
}