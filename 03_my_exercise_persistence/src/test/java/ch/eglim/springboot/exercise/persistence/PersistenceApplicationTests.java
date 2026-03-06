package ch.eglim.springboot.exercise.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class PersistenceApplicationTests {

    @Autowired
    private EmployeeRepo employeeRepo;

	@Test
	void addAndRemoveEmployee() {

        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("John Doe");
        employee.setSalary(50000);

        employeeRepo.save(employee);

        Employee savedEmployee = employeeRepo.findById(1).orElseThrow();
        System.out.println("Saved Employee: " + savedEmployee.getName() + ", Salary: " + savedEmployee.getSalary());

        System.out.println("Breakpoint here to access manually to http://localhost:8080/h2-console (User sa, pw <leer>");

        Employee removedEmployee = employeeRepo.findById(1).orElseThrow();
        employeeRepo.delete(removedEmployee);

        // Prüfen, dass keine Einträge mehr vorhanden sind
        assertTrue(employeeRepo.findAll().isEmpty(), "EmployeeRepo sollte leer sein");
	}

}
