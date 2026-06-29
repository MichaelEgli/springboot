package ch.eglim.springboot.exercise.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(TestcontainersConfiguration.class)
@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

public class QueryTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private EmployeeRepo employeeRepository;
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

        TypedQuery<DepartmentSalaryStatistics> query = em.createQuery(
                "select new ch.eglim.springboot.exercise.persistence.dto.DepartmentSalaryStatistics(d.name, avg(e.salary)) " +
                        "from Employee e join e.department d " +
                        "group by d.name", DepartmentSalaryStatistics.class);
        System.out.println("hello: " + query.getResultList());
    }
}
