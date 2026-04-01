package ch.eglim.springboot.exercise.persistence;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(TestcontainersConfiguration.class)
@DataJpaTest()
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CriteriaTest {

    @Autowired
    private EmployeeRepo employeeRepository;
    @Autowired
    private EntityManager em;

    /**
     * Ex1	Find all employees who live in the canton of Zurich
     */


    /** because we have @DataJpaTest which by default rolls back transactions after each test method, so the data inserted in one test method is not visible in another test method. To fix this, we can use @Sql annotation to run a SQL script that inserts the necessary data before the test method runs. This way, we can ensure that the employee table is populated with the required data for our test. */
    @Test
    @Sql("/db/migration/afterMigrate.sql")
    void findAllZuercher() {
        var criteriaBuilder = em.getCriteriaBuilder();
        var criteriaQuery = criteriaBuilder.createQuery(Employee.class);
        var root = criteriaQuery.from(Employee.class);
        criteriaQuery.select(root).where(criteriaBuilder.like(root.get("address").get("state"), "ZH"));
        var result = em.createQuery(criteriaQuery).getResultList();
        assertEquals(3, result.size());

    }
}
