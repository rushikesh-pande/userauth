package com.userauth.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.userauth.user.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
