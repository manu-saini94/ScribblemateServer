package com.scribblemate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.scribblemate.entities.Note;

import jakarta.transaction.Transactional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM note_images WHERE note_id = :noteId", nativeQuery = true)
	void deleteNoteImages(Long noteId);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM list_items WHERE common_note_id = :noteId", nativeQuery = true)
	void deleteListItems(Long noteId);

//	public List<Note> findAllByUser(User user);
//
//	@Query("select n from Note n where n.id=:x and n.user.id=:y")
//	public Note findByIdAndUser(@Param("x") int id, @Param("y") int userId);
//
//	@Query("select n from Note n where n.isTrashed=true and n.user.id=:y")
//	public List<Note> findAllByUserAndIsTrashed(@Param("y") int userId);
//
//	@Query(value = "DELETE FROM note WHERE id = :noteId AND user_id = :userId", nativeQuery = true)
//	public int deleteNoteByIdAndUserId(@Param("noteId") int id, @Param("userId") int userId);

}
