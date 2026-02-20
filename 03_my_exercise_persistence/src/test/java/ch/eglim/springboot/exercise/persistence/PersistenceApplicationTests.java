package ch.eglim.springboot.exercise.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class PersistenceApplicationTests {

    @Autowired
    private EmployeeRepo employeeRepo;

	@Test
	void addEmployee() {

        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("John Doe");
        employee.setSalary(50000);

        employeeRepo.save(employee);

        Employee savedEmployee = employeeRepo.findById(1).orElseThrow();
        System.out.println("Saved Employee: " + savedEmployee.getName() + ", Salary: " + savedEmployee.getSalary());

        System.out.println("Breakpoint here to access manually to http://localhost:8080/h2-console (User sa, pw <leer>");
	}

}
