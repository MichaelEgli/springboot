package ch.eglim.springboot.exercise.persistence;

import ch.eglim.springboot.exercise.persistence.dto.DepartmentSalaryStatistics;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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
        var cq = cb.createQuery(DepartmentSalaryStatistics.class);
        var employee = cq.from(Employee.class);
        var department = employee.join(Employee_.department);
        cq.multiselect(department.get(Department_.name), cb.avg(employee.get(Employee_.salary)))
                .groupBy(department.get(Department_.name));
        TypedQuery<DepartmentSalaryStatistics> criteriaResult = em.createQuery(cq);

        // Mit JPQL
        TypedQuery<DepartmentSalaryStatistics> jpqlResult = em.createQuery(
                "SELECT d.name, AVG(e.salary) FROM Employee e JOIN e.department d GROUP BY d.name", DepartmentSalaryStatistics.class
        );

        // Hilfsmethode zum Vergleichen der Ergebnisse, unabhängig von der Reihenfolge
        assertDepartmentAverages(criteriaResult.getResultList());
        assertDepartmentAverages(jpqlResult.getResultList());
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

    /**
     * Ex3: Find the employee with the lowest salary
     */
    @Test
    @Sql("/db/migration/afterMigrate.sql")
    void findEmployeeWithLowestSalary() {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Employee.class);
        var employee = cq.from(Employee.class);
        cq.select(employee)
                .orderBy(cb.asc(employee.get(Employee_.salary)));
        TypedQuery<Employee> query = em.createQuery(cq);
        query.setMaxResults(1);
        Employee result = query.getSingleResult();

        assertNotNull(result);
        // Optional: Überprüfe, ob das Gehalt dem erwarteten Minimalwert entspricht
        assertEquals(72000.0, result.getSalary());
    }

    /**
     * Ex4: Create a query that returns the employee name and the complete address, ordered by the employee’s name
     */
    @Test
    @Sql("/db/migration/afterMigrate.sql")
    void findEmployeeAndAddressOrderedByName() {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Employee.class);
        var employee = cq.from(Employee.class);
        cq.select(employee)
                .orderBy(cb.asc(employee.get(Employee_.address)));
        TypedQuery<Employee> query = em.createQuery(cq);
        List<Employee> result = query.getResultList();

        // Überprüfe, ob Du Einträge findest (6 Mitarbeiter in der Testdatenbank)
        assertEquals(6, result.size());
        assertEquals("Ursula Friedman", result.get(0).getName());
        assertEquals("Plattenstrasse 26", result.get(0).getAddress().getStreet());
        assertEquals("Felix Beyer", result.get(5).getName());
        assertEquals("Geiss", result.get(5).getAddress().getCity());

    }

    /**
     * Ex5: Find employees who are not assigned to a project
     */
    @Test
    @Sql("/db/migration/afterMigrate.sql")
    void findEmployeesNotAssignedToProject() {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Employee.class);
        var employee = cq.from(Employee.class);

        cq.select(employee).where(cb.isEmpty(employee.get(Employee_.projects)));

        TypedQuery<Employee> query = em.createQuery(cq);
        List<Employee> result = query.getResultList();

        assertEquals(3, result.size(), "Es sollten genau 3 Employees ohne Project gefunden werden");
    }

    /**
     * Ex6: Find all business phone numbers ordered by number
     */
    @Test
    @Sql("/db/migration/afterMigrate.sql")
    void findAllWorkPhonesOrderedByNumber() {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Employee.class);
        var employee = cq.from(Employee.class);
        Join<Employee, Phone> phone = employee.join(Employee_.phones);

        cq.select(employee).where(cb.equal(phone.get(Phone_.type), "WORK")).orderBy(cb.asc(phone.get(Phone_.phone_number)));
        TypedQuery<Employee> query = em.createQuery(cq);
        List<Employee> result = query.getResultList();
        assertEquals(5, result.size(), "Es sollen nur business phones ordered by number gefunden werden");

    }

        /**
     * Ex7: Find employees who do not have a business phone number yet
     */
    @Test
    @Sql("/db/migration/afterMigrate.sql")
    void findAllEmployeesNotHavingBusinessPhone() {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Employee.class);
        var employee = cq.from(Employee.class);

        // Subquery: Existiert für diesen Employee ein Phone vom Typ "WORK"?
        var subquery = cq.subquery(Phone.class);
        var subEmployee = subquery.correlate(employee);
        Join<Employee, Phone> phone = subEmployee.join(Employee_.phones);
        subquery.select(phone)
                .where(cb.equal(phone.get(Phone_.type), "WORK"));

        // Nur Employees ohne ein einziges WORK-Phone
        cq.select(employee).where(cb.not(cb.exists(subquery)));
        TypedQuery<Employee> query = em.createQuery(cq);
        List<Employee> result = query.getResultList();
        assertEquals(1, result.size(), "Es soll nur 1 Employee ohne business phone gefunden werden");
    }

    private void assertDepartmentAverages(List<DepartmentSalaryStatistics> result) {
        assertEquals(2, result.size());
        boolean itFound = false, hrFound = false;
        for (DepartmentSalaryStatistics row : result) {
            if ("IT".equals(row.departmentName())) {
                assertEquals(97200.0, (Double) row.averageSalary());
                itFound = true;
            } else if ("HR".equals(row.departmentName())) {
                assertEquals(95000.0, (Double) row.averageSalary());
                hrFound = true;
            }
        }
        assertTrue(itFound && hrFound);
    }
}