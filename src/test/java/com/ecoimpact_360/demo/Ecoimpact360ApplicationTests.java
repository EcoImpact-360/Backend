package com.ecoimpact_360.demo;

import com.ecoimpact_360.backend.Ecoimpact360Application;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = Ecoimpact360Application.class)
@ActiveProfiles("test")
class Ecoimpact360ApplicationTests {

	@Test
	void contextLoads() {
	}

}
