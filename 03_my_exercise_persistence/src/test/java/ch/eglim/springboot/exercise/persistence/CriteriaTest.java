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

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@DataJpaTest()
/** because we have @DataJpaTest which by default rolls back transactions after each test method, so the data inserted in one test method is not visible in another test method. To fix this, we can use @Sql annotation to run a SQL script that inserts the necessary data before the test method runs. This way, we can ensure that the employee table is populated with the required data for our test. */
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CriteriaTest {

    @Autowired
    private EmployeeRepo employeeRepository;
    @Autowired
    private EntityManager em;

    /**
     * Ex1: Find all employees who live in the canton of Zurich
     */
    @Test
    @Sql("/db/migration/afterMigrate.sql")
    void findAllZuercher() {
        List<Employee> zuercher = employeeRepository.findAll((employee, cb) -> {
            Join<Employee, Address> address = employee.join(Employee_.address);
            return cb.equal(address.get(Address_.state), "ZH");
        });

        assertEquals(3, zuercher.size());

    }

    /**
     * Ex2: Calculate the average salary of employees per department
     */
    @Test
    @Sql("/db/migration/afterMigrate.sql")
    void calculateAverageSalaryPerDepartment() {
        // Mit Criteria API
        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Object[].class);
        var employee = cq.from(Employee.class);
        var department = employee.join(Employee_.department);
        cq.multiselect(department.get(Department_.name), cb.avg(employee.get(Employee_.salary)))
                .groupBy(department.get(Department_.name));
        List<Object[]> result = em.createQuery(cq).getResultList();

        assertArrayEquals(new Object[]{"IT", 97200.0}, result.get(0));
        assertArrayEquals(new Object[]{"HR", 95000.0}, result.get(1));

        // Mit JPQL
        List<Object[]> jpqlResult = em.createQuery("SELECT d.name, AVG(e.salary) FROM Employee e JOIN e.department d GROUP BY d.name", Object[].class).getResultList();
        assertArrayEquals(new Object[]{"IT", 97200.0}, jpqlResult.get(0));
        assertArrayEquals(new Object[]{"HR", 95000.0}, jpqlResult.get(1));
        }

    /*
    Criteria API
    ============
    Vorteile:
            - Typsicherheit zur Compile-Zeit (z.B. durch Metamodellklassen wie Employee_)
            - Gut für dynamisch zusammengesetzte Abfragen
            - Refactoring-sicher, da keine String-Feldnamen
    Nachteile:
            - Weniger lesbar, mehr Boilerplate-Code
            - Komplexer bei einfachen Abfragen

    JPQL
    ====
    Vorteile:
            - Kürzer und besser lesbar
            - Ähnelt SQL, daher für viele Entwickler intuitiver
            - Gut für einfache, statische Abfragen
    Nachteile:
            - Weniger typsicher (Fehler oft erst zur Laufzeit)
            - Refactoring-anfällig, da Feldnamen als Strings verwendet werden
            - Für dynamische Abfragen ungeeignet
 */

}
