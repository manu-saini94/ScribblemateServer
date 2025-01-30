package com.scribblemate.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scribblemate.entities.Label;
import com.scribblemate.entities.Note;
import com.scribblemate.entities.SpecificNote;
import com.scribblemate.entities.User;

import jakarta.transaction.Transactional;

@Repository
public interface SpecificNoteRepository extends JpaRepository<SpecificNote, Long> {

	Optional<SpecificNote> findById(Long id);

	List<SpecificNote> findAllByUserAndIsTrashedFalseAndIsArchivedFalseOrderByCommonNoteCreatedAtDesc(User user);

	List<SpecificNote> findAllByUserOrderByCommonNoteCreatedAtDesc(User user);

	SpecificNote findByCommonNoteAndUser(Note note, User collaborator);

	@Query("SELECT sn FROM SpecificNote sn JOIN sn.labelSet ls WHERE sn.user = :user AND ls = :label")
	List<SpecificNote> findByUserAndLabelOrderByCommonNoteCreatedAtDesc(@Param("user") User user,
			@Param("label") Label label);

	List<SpecificNote> findAllByUserAndIsTrashedTrueOrderByUpdatedAtDesc(User user);

	List<SpecificNote> findAllByUserAndIsArchivedTrueOrderByCommonNoteCreatedAtDesc(User user);

	List<SpecificNote> findAllByUserAndReminderNotNullOrderByCommonNoteCreatedAtDesc(User user);

	List<SpecificNote> findAllByCommonNote(Note commonNote);

	@Transactional
	@Modifying
	@Query(value = "DELETE from specific_note WHERE common_note_id = :commonNoteId and user_id = :userId", nativeQuery = true)
	void deleteByCommonNoteIdAndUserId(@Param("commonNoteId") Long commonNoteId, @Param("userId") Long userId);

	@Transactional
	@Modifying
	@Query(value = "DELETE from note_label WHERE label_id = :labelId", nativeQuery = true)
	int deleteLabelsFromLabelNote(@Param("labelId") Long labelId);

	@Transactional
	@Modifying
	@Query(value = "DELETE from note_label WHERE note_id = :noteId", nativeQuery = true)
	void deleteAllByNoteId(@Param("noteId") Long noteId);

	@Transactional
	@Modifying
	@Query(value = "DELETE from note_collaborator WHERE user_id = :userId and note_id = :noteId ", nativeQuery = true)
	void deleteCollaboratorByUserIdAndCommonNoteId(@Param("userId") Long userId, @Param("noteId") Long noteId);

	@Transactional
	void deleteByCommonNoteAndUser(Note commonNote, User user);

	@Transactional
	void deleteAllByUser(User user);

}
