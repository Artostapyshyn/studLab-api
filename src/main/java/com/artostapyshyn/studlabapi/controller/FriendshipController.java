package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.dto.FriendshipDTO;
import com.artostapyshyn.studlabapi.service.FriendRequestService;
import com.artostapyshyn.studlabapi.service.FriendshipService;
import com.artostapyshyn.studlabapi.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

@Log4j2
@Validated
@RestController
@RequestMapping("/api/v1/friends")
@CrossOrigin(maxAge = 3600, origins = "*")
@AllArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    private final StudentService studentService;

    private final FriendRequestService friendRequestService;

    @Operation(summary = "Get all friends by student id",
            security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/all")
    public ResponseEntity<List<FriendshipDTO>> getAllFriendsByStudentId(@RequestParam("studentId") Long studentId) {
        List<FriendshipDTO> friendDTOs = friendshipService.findAllByStudentId(studentId);
        return ResponseEntity.ok(friendDTOs);
    }

    @Operation(summary = "Get friendship status",
            security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getFriendShipStatus(Authentication authentication, @RequestParam("studentId") Long studentId) {
        Map<String, Object> response = new HashMap<>();
        Long authStudentId = studentService.getAuthStudentId(authentication);

        List<FriendshipDTO> friendships = friendshipService.findAllByStudentId(authStudentId);

        boolean isFriend = friendships.stream().anyMatch(friendship -> friendship.getFriendId().equals(studentId));
        boolean hasSentFriendRequest = friendRequestService.isSentRequest(authStudentId, studentId);

        if(isFriend) {
            response.put("status", "You are friends");
        } else if(hasSentFriendRequest) {
            response.put(STATUS, "Friend request sent");
        } else {
            response.put(STATUS, "You are not friends");
        }
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete friend by id",
            security = @SecurityRequirement(name = "basicAuth"))
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
