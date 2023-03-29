package com.artostapyshyn.studLabApi.service;

import com.artostapyshyn.studLabApi.entity.Student;
import com.artostapyshyn.studLabApi.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Student student = studentRepository.findByEmail(email);
        if (student != null) {
            List<GrantedAuthority> authorityList = new ArrayList<>();
            authorityList.add(new SimpleGrantedAuthority("ROLE_STUDENT"));
            return new org.springframework.security.core.userdetails.User(
                    student.getEmail(), student.getPassword(), authorityList);
        } else {
            throw new UsernameNotFoundException("User doesn't exists");
        }
    }
}