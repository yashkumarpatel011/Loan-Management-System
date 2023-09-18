package com.aspire.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aspire.model.UserData;

import java.util.List;

@Repository
public interface UserRepository  extends JpaRepository<UserData, Integer>{

	UserData findByEmail(String username);

	List<UserData> findByRole(String role);
}
