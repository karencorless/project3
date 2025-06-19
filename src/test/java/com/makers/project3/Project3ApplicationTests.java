package com.makers.project3;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = Project3Application.class)
@ActiveProfiles("test")
public class Project3ApplicationTests {

	@Test
	void contextLoads() {
	}

}
