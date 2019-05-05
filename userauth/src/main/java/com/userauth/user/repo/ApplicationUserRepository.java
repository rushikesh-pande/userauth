package com.userauth.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.userauth.user.ApplicationUser;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long>{
	 ApplicationUser findByUsername(String username);
}
