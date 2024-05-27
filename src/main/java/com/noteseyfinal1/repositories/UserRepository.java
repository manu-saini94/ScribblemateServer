package com.noteseyfinal1.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.noteseyfinal1.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

	Optional<User> findByEmail(String email);

//	List<User> findAllByEmail(Iterable<String> emails);

	List<User> findAllByEmailIn(Iterable<String> emails);
}
