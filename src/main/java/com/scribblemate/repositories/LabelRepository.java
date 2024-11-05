package com.scribblemate.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scribblemate.entities.Label;
import com.scribblemate.entities.User;

import jakarta.transaction.Transactional;

@Repository
public interface LabelRepository extends JpaRepository<Label, Integer> {

	Label findByIdAndUser(Integer id, User user);

	List<Label> findAllByUserOrderByLabelName(User user);

	@Transactional
	@Modifying
	@Query(value = "DELETE from label WHERE id = :labelId and user_id = :userId", nativeQuery = true)
	int deleteByIdAndUser(@Param("labelId") int labelId, @Param("userId") int userId);

	@Transactional
	void deleteAllByUser(User user);

}
