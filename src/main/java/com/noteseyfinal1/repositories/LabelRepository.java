package com.noteseyfinal1.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.noteseyfinal1.entities.Label;
import com.noteseyfinal1.entities.User;

import jakarta.transaction.Transactional;

public interface LabelRepository extends JpaRepository<Label, Integer> {

	Label findByIdAndUser(Integer id, User user);

	List<Label> findAllByUser(User user);

	@Transactional
	@Modifying
	@Query(value = "DELETE from label WHERE id = :labelId and user_id = :userId", nativeQuery = true)
	int deleteByIdAndUser(@Param("labelId") int labelId, @Param("userId") int userId);

	@Transactional
	void deleteAllByUser(User user);

}
