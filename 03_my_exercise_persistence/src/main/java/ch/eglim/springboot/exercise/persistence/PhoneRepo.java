package ch.eglim.springboot.exercise.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneRepo extends JpaRepository<Phone,Integer> {
}
