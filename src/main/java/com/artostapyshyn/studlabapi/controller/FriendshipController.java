package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Friendship;
import com.artostapyshyn.studlabapi.service.FriendshipService;
import com.artostapyshyn.studlabapi.service.StudentService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.ERROR;
import static com.artostapyshyn.studlabapi.constant.ControllerConstants.MESSAGE;

@Log4j2
@Validated
@RestController
@RequestMapping("/api/v1/friends")
@CrossOrigin(maxAge = 3600, origins = "*")
@AllArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    private final StudentService studentService;

    @GetMapping("/all")
    public ResponseEntity<List<Friendship>> getAllFriendsByStudentId(@RequestParam("studentId") Long studentId) {
       List<Friendship> friends = friendshipService.findAllByStudentId(studentId);
       return ResponseEntity.ok(friends);
    }

    @DeleteMapping("/delete-friend")
    public ResponseEntity<Map<String, Object>> deleteFriendById(@RequestParam("friendId") Long friendId, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long studentId = studentService.getAuthStudentId(authentication);
            if (friendId != null && studentId != null) {
                friendshipService.deleteByFriendId(friendId);
                response.put(MESSAGE, "Friend deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put(ERROR, "Invalid ID provided.");
                return ResponseEntity.badRequest().body(response);
            }
        } catch(Exception e) {
            response.put(ERROR, "Error while deleting friend: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
