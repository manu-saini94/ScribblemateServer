package com.noteseyfinal1.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.noteseyfinal1.entities.Label;
import com.noteseyfinal1.entities.Note;
import com.noteseyfinal1.entities.SpecificNote;
import com.noteseyfinal1.entities.User;

import jakarta.transaction.Transactional;

public interface SpecificNoteRepository extends JpaRepository<SpecificNote, Integer> {

	SpecificNote findByIdAndUser(Integer id, User user);

	List<SpecificNote> findAllByUserAndIsTrashedFalseAndIsArchivedFalse(User user);

	SpecificNote findByCommonNoteAndUser(Note note, User collaborator);

	@Query("SELECT sn FROM SpecificNote sn JOIN sn.labelSet ls WHERE sn.user = :user AND ls = :label")
	List<SpecificNote> findByUserAndLabel(@Param("user") User user, @Param("label") Label label);

	@Transactional
	@Modifying
	@Query("UPDATE SpecificNote sn SET sn.isPinned = :isPinned WHERE sn.commonNote.id = :noteId AND sn.user = :user")
	int updatePinStatus(@Param("isPinned") boolean isPinned, @Param("noteId") Integer noteId, @Param("user") User user);

	List<SpecificNote> findAllByUserAndIsTrashedTrue(User user);

	List<SpecificNote> findAllByUserAndIsArchivedTrue(User user);

	List<SpecificNote> findAllByUserAndReminderNotNull(User user);

	List<SpecificNote> findAllByCommonNote(Note commonNote);

	void deleteByCommonNoteAndUser(Note commonNote, User collaborator);

}
