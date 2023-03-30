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

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

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

        int verificationCode = verificationCodeService.generateCode(email).getCode();
        emailService.sendVerificationCode(email, verificationCode);
        Student student = new Student();
        student.setEmail(email);
        student.setEnabled(false);
        studentService.save(student);

        VerificationCode verification = new VerificationCode();
        verification.setEmail(email);
        verification.setCode(verificationCode);
        verification.setExpirationDate(verificationCodeService.generateCode(email).getExpirationDate());
        verificationCodeService.save(verification);
        return ResponseEntity.ok().body("Verification code has been sent to your email successfully");
    }

    @PostMapping("/verification")
    public ResponseEntity<String> verifyCode(@RequestBody VerificationCode verificationCode) {
        String email = verificationCode.getEmail();
        int code = verificationCode.getCode();
        Student student = studentService.findByEmail(email);

        if (student == null) {
            return new ResponseEntity<>("Student with provided email does not exist", HttpStatus.BAD_REQUEST);
        }

        VerificationCode savedVerificationCode = verificationCodeService.findByEmail(email);
        if (savedVerificationCode == null) {
            return new ResponseEntity<>("Invalid verification code", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime expirationTime = savedVerificationCode.getExpirationDate();
        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isAfter(expirationTime)) {
            return new ResponseEntity<>("Verification code has expired", HttpStatus.BAD_REQUEST);
        }

        int savedCode = savedVerificationCode.getCode();
        if (savedCode != code) {
            return new ResponseEntity<>("Invalid verification code", HttpStatus.BAD_REQUEST);
        }

        student.setEnabled(true);
        studentService.save(student);
        return new ResponseEntity<>("User successfully verified", HttpStatus.OK);
    }

    public boolean isValidCode(String email, int code) {
        VerificationCode verificationCode = verificationCodeService.findByEmail(email);
        if (verificationCode != null) {
            int savedCode = verificationCode.getCode();
            LocalDateTime expirationTime = verificationCode.getExpirationDate();
            LocalDateTime currentTime = LocalDateTime.now();
            return (savedCode == code) && currentTime.isBefore(expirationTime);
        }
        return false;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> saveUser(@RequestParam("first_name") String firstName, @RequestParam("last_name") String lastName,
                                      @RequestParam("email") String email, @RequestParam("password") String password) {
        Map<String, Object> responseMap = new HashMap<>();

        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setPassword(new BCryptPasswordEncoder().encode(password));
        student.setRole(Role.ROLE_STUDENT);
        studentService.save(student);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String token = jwtTokenUtil.generateToken(userDetails);

        responseMap.put("email", email);
        responseMap.put("message", "Account created successfully");
        responseMap.put("token", token);
        return ResponseEntity.ok(responseMap);
    }
}