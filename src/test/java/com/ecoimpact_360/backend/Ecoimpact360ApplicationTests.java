package com.ecoimpact_360.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.ecoimpact_360.backend.Ecoimpact360Application;

@SpringBootTest(classes = Ecoimpact360Application.class)
@ActiveProfiles("test")
class Ecoimpact360ApplicationTests {

	@Test
	void contextLoads() {
	}

}
