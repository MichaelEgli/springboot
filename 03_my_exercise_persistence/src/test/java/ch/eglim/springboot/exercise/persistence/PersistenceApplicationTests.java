package ch.eglim.springboot.exercise.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class PersistenceApplicationTests {

	@Test
	void contextLoads() {
        System.out.println("Breakpoint here to access manually to http://localhost:8080/h2-console (User sa, pw <leer>");
	}

}
