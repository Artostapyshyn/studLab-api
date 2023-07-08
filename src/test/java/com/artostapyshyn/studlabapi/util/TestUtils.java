package com.artostapyshyn.studlabapi.util;

import com.artostapyshyn.studlabapi.entity.*;
import com.artostapyshyn.studlabapi.enums.EventType;
import com.artostapyshyn.studlabapi.enums.Role;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;

@AllArgsConstructor
public class TestUtils {

    public static Student createRandomStudent() {
        Student student = new Student();
        student.setLastName(RandomStringUtils.randomAlphabetic(10));
        student.setFirstName(RandomStringUtils.randomAlphabetic(10));
        student.setBirthDate("1990-01-01");

        student.setMajor(RandomStringUtils.randomAlphabetic(10));
        student.setCourse(RandomStringUtils.randomAlphabetic(10));
        student.setPhotoBytes(generateRandomBytes());

        student.setEmail(RandomStringUtils.randomAlphabetic(10) + "@gmail.com");

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String password = bCryptPasswordEncoder.encode(RandomStringUtils.randomAlphabetic(10));
        student.setPassword(password);

        student.setEnabled(true);
        student.setHasNewMessages(false);
        student.setRegistrationDate(LocalDateTime.now());
        student.setRole(Role.ROLE_STUDENT);

        return student;
    }

    private static byte[] generateRandomBytes() {
        return RandomUtils.nextBytes(10);
    }

    public static Event createRandomEvent() {
        Event event = new Event();
        event.setNameOfEvent(RandomStringUtils.randomAlphabetic(10));
        event.setEventPhoto(generateRandomBytes());

        event.setCreationDate(LocalDateTime.now());
        event.setDate(LocalDateTime.now());

        event.setFavoriteCount(0);
        event.setDescription(RandomStringUtils.randomAlphabetic(10));
        event.setEventType(EventType.GENERAL_EVENT);
        event.setVenue(RandomStringUtils.randomAlphabetic(10));

        return event;
    }

    public static Message createRandomMessages() {
        Message message = new Message();
        message.setSentTime(LocalDateTime.now());
        message.setContent(RandomStringUtils.randomAlphabetic(10));
        message.setStudent(createRandomStudent());
        return message;
    }

    public static Course createRandomCourse() {
        Course course = new Course();
        course.setCourseName(RandomStringUtils.randomAlphabetic(10));
        course.setCreationDate(LocalDateTime.now());
        course.setCoursePhoto(generateRandomBytes());
        course.setCourseLink(RandomStringUtils.randomAlphabetic(10));
        course.setCourseDescription(RandomStringUtils.randomAlphabetic(10));
        return course;
    }

    public static FavouriteEvent createRandomFavouriteEvent(Event event) {
        FavouriteEvent favouriteEvent = new FavouriteEvent();

        favouriteEvent.setEvent(event);
        favouriteEvent.setStudentId(createRandomStudent().getId());

        return favouriteEvent;
    }

    public static FavouriteEvent createRandomFavouriteEvent() {
        FavouriteEvent favouriteEvent = new FavouriteEvent();
        favouriteEvent.setEvent(createRandomEvent());
        favouriteEvent.setStudentId(createRandomStudent().getId());

        return favouriteEvent;
    }

    public static University createRandomUniversity() {
        University university = new University();
        university.setDomain("gmail.com");
        university.setName(RandomStringUtils.randomAlphabetic(10));

        return university;
    }

    public static VerificationCode createRandomVerificationCode() {
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(RandomUtils.nextInt());
        verificationCode.setExpirationDate(LocalDateTime.now().minusMinutes(30));
        verificationCode.setStudentId(createRandomStudent().getId());
        verificationCode.setEmail(RandomStringUtils.randomAlphabetic(10) + "@gmail.com");
        return verificationCode;
    }

    public static Complaint createRandomComplaint(){
        Complaint complaint = new Complaint();
        complaint.setStudentId(createRandomStudent().getId());
        complaint.setCloseComplaint(true);
        complaint.setStatus("Відкрито");
        complaint.setComplaintReason(RandomStringUtils.randomAlphabetic(10));
        complaint.setCommentId(1L);
        complaint.setBlockDuration(null);
        complaint.setBlockUser(false);
        complaint.setDeleteComment(true);
        return complaint;
    }

    public static Comment createRandomComment() {
        Student student = createRandomStudent();
        Comment comment = new Comment();
        Event event = createRandomEvent();

        comment.setCommentText(RandomStringUtils.randomAlphabetic(10));
        comment.setStudent(student);
        comment.setLikes(0);
        comment.setEventId(event.getId());
        comment.setReplies(new ArrayList<>());

        return comment;
    }

    public static Reply createRandomReply() {
        Student student = createRandomStudent();
        Comment comment = createRandomComment();
        Reply reply = new Reply();
        reply.setReplyText(RandomStringUtils.randomAlphabetic(10));
        reply.setStudent(student);
        reply.setComment(comment);

        return reply;
    }
}