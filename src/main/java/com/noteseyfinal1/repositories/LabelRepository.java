package com.noteseyfinal1.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noteseyfinal1.entities.Label;
import com.noteseyfinal1.entities.User;

public interface LabelRepository extends JpaRepository<Label, Integer> {

	void deleteByIdAndUser(int labelId, User user);

	Label findByIdAndUser(Integer id, User user);

	List<Label> findAllByUser(User user);

}
