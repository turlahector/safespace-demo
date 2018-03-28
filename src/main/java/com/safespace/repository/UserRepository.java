package com.safespace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.safespace.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	@Query("SELECT c FROM User c WHERE c.userName = :userName ")
	User findByUserName(@Param("userName") String userName);

}
