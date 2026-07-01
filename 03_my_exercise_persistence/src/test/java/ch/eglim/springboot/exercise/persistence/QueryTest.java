package ch.eglim.springboot.exercise.persistence;

import ch.eglim.springboot.exercise.persistence.dto.DepartmentSalaryStatistics;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(TestcontainersConfiguration.class)
@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

class QueryTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private DepartmentRepo departmentRepository;

    /**
     * Ex1	Find all employees who live in the canton of Zurich
     */
    @Test
    @Sql("/db/migration/afterMigrate.sql")
    void findAllZuercher() {
        TypedQuery<Employee> query = em.createQuery(
                "select e from Employee e where e.address.state = 'ZH'", Employee.class);
        List<Employee> zuercher = query.getResultList();

        assertEquals(3, zuercher.size());
    }
    /**
     * Ex2: Calculate the average salary of employees per department
     */
    @Test
    @Sql("/db/migration/afterMigrate.sql")
    void calculateAverageSalaryPerDepartment() {

        TypedQuery<DepartmentSalaryStatistics> query = em.createNamedQuery(Department.AVG_SALARY, DepartmentSalaryStatistics.class);
        List<DepartmentSalaryStatistics> result = query.getResultList();

        assertEquals(2, result.size());
        for (DepartmentSalaryStatistics stat : result) {
            switch (stat.departmentName()) {
                case "IT" -> assertEquals(97200, stat.averageSalary(), 0.001);
                case "HR" -> assertEquals(95000, stat.averageSalary(), 0.001);
                default -> fail("Department and/or average salary not expected: "
                        + stat.departmentName() + " - " + stat.averageSalary());
            }
        }
    }
    
    /**
     * Ex3: Find the employee with the lowest salary
     */
    @Test
    @Sql("/db/migration/afterMigrate.sql")
    void findEmployeeWithLowestSalary() {
        System.out.println("hello");
    }

}
