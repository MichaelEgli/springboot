package ch.eglim.springboot.exercise.persistence;

import ch.eglim.springboot.exercise.persistence.dto.DepartmentSalaryStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepo extends JpaRepository<Department, Integer> {

    List<DepartmentSalaryStatistics> avgSalary();
}
