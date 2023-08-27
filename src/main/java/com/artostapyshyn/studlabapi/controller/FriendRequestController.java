package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.FriendRequest;
import com.artostapyshyn.studlabapi.service.FriendRequestService;
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
@RequestMapping("/api/v1/friend-request")
@CrossOrigin(maxAge = 3600, origins = "*")
@AllArgsConstructor
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> sendFriendRequest(@RequestBody FriendRequest request, Authentication authentication) {
        Long studentId = studentService.getAuthStudentId(authentication);
        Long receiverId = request.getReceiver().getId();

        FriendRequest savedRequest = friendRequestService.sendFriendRequest(studentId, receiverId);

        Map<String, Object> response = new HashMap<>();
        if (savedRequest != null && savedRequest.getId() != null) {
            response.put(MESSAGE, "Friend request sent successfully.");
            response.put("requestId", savedRequest.getId());
            return ResponseEntity.ok(response);
        } else {
            response.put(ERROR, "Error while sending friend request.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/received")
    public ResponseEntity<?> getAllFriendRequests(Authentication authentication) {
        Long studentId = studentService.getAuthStudentId(authentication);

        Map<String, Object> response = new HashMap<>();
        if (studentId != null && authentication != null) {
            List<FriendRequest> requests = friendRequestService.getReceivedFriendRequests(studentId);
            return ResponseEntity.ok(requests);
        } else {
            response.put(ERROR, "Error while sending friend request.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/accept")
    public ResponseEntity<Map<String, Object>> acceptFriendRequest(@RequestParam("requestId") Long requestId) {
        Map<String, Object> response = new HashMap<>();
        if (requestId != null) {
            try {
                friendRequestService.acceptFriendRequest(requestId);
                response.put(MESSAGE, "Friend request accepted successfully");
                return ResponseEntity.ok(response);
            } catch (Exception ex) {
                log.error("Error while declining the friend request with id: " + requestId, ex);
                response.put(ERROR, "Error while declining friend request.");
                return ResponseEntity.internalServerError().body(response);
            }
        } else {
            response.put(ERROR, "Error while sending friend request.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/decline")
    public ResponseEntity<Map<String, Object>> declineFriendRequest(@RequestParam("requestId") Long requestId) {
        Map<String, Object> response = new HashMap<>();
        if (requestId != null) {
            try {
                friendRequestService.declineFriendRequest(requestId);
                response.put(MESSAGE, "Friend request declined successfully");
                return ResponseEntity.ok(response);
            } catch (Exception ex) {
                log.error("Error while declining the friend request with id: " + requestId, ex);
                response.put(ERROR, "Error while declining friend request.");
                return ResponseEntity.internalServerError().body(response);
            }
        } else {
            response.put(ERROR, "RequestId is null.");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
