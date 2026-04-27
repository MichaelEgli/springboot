package ch.eglim.springboot.exercise.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Join;
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
        List<Employee> zuercher = employeeRepository.findAll((employee, cq, cb) -> {
            Join<Employee, Address> address = employee.join(Employee_.address);
            return cb.equal(address.get(Address_.state), "ZH");
        });

        assertEquals(3, zuercher.size());
    }
}