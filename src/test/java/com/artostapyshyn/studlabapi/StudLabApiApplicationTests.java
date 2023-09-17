package com.artostapyshyn.studlabapi;

import com.artostapyshyn.studlabapi.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class StudLabApiApplicationTests {

	@Autowired
	private StudentService studentService;

	@Test
	void contextLoads() {
		assertNotNull(studentService, "Primary service should not be null");
	}
}

