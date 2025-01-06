package com.scribblemate.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.scribblemate.entities.RefreshToken;
import com.scribblemate.entities.User;

import jakarta.transaction.Transactional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByToken(String token);

	@Transactional
	@Modifying
	@Query("DELETE FROM RefreshToken r WHERE r.user = :user")
	int deleteByUser(@Param("user") User user);

}
