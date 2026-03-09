package ch.eglim.springboot.exercise.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepo extends JpaRepository<Project,Integer> {
}