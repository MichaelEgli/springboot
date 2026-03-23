package ch.eglim.springboot.exercise.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestcontainersConfiguration.class)
@DataJpaTest(properties = {"spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=create"})
public class EmployeeRepoJpaTests {

    @Autowired
    private EmployeeRepo employeeRepo;

    @Test
    void addAndRemoveEmployee() {

        // add Employee
        Employee employee = new Employee();
        employee.setName("John Doe");
        employee.setSalary(50000);

        Employee saved = employeeRepo.save(employee);
        int savedId = saved.getId();

        Employee savedEmployee = employeeRepo.findById(savedId).orElseThrow();
        System.out.println("Saved Employee: " + savedEmployee.getName() + ", Salary: " + savedEmployee.getSalary());

        // remove Employee
        Employee removedEmployee = employeeRepo.findById(savedId).orElseThrow();
        employeeRepo.delete(removedEmployee);
        employeeRepo.flush();

        // Prüfen, dass der gelöschte Employee nicht mehr existiert
        assertTrue(employeeRepo.findById(savedId).isEmpty(), "Employee sollte nicht mehr existieren");
    }
}
