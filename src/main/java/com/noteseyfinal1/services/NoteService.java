package com.noteseyfinal1.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noteseyfinal1.dto.CollaboratorDto;
import com.noteseyfinal1.dto.LabelDto;
import com.noteseyfinal1.dto.NoteDto;
import com.noteseyfinal1.entities.Label;
import com.noteseyfinal1.entities.Note;
import com.noteseyfinal1.entities.SpecificNote;
import com.noteseyfinal1.entities.User;
import com.noteseyfinal1.exceptions.NoteNotDeletedException;
import com.noteseyfinal1.exceptions.NoteNotFoundException;
import com.noteseyfinal1.exceptions.NoteNotPersistedException;
import com.noteseyfinal1.exceptions.NoteNotUpdatedException;
import com.noteseyfinal1.exceptions.NotesNotFoundException;
import com.noteseyfinal1.repositories.LabelRepository;
import com.noteseyfinal1.repositories.NoteRepository;
import com.noteseyfinal1.repositories.SpecificNoteRepository;
import com.noteseyfinal1.repositories.UserRepository;
import com.noteseyfinal1.utility.NoteUtils;
import com.noteseyfinal1.utility.Utils;
import com.noteseyfinal1.utility.Utils.Role;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NoteService {

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LabelRepository labelRepository;

	@Autowired
	private SpecificNoteRepository specificNoteRepository;

	@Transactional
	public NoteDto createNewNote(NoteDto noteDto, User user) {
		try {
			SpecificNote specificNote = new SpecificNote();
			Note savedNote = setSpecificNoteFromNoteDto(noteDto, specificNote, user);
			log.info(NoteUtils.NOTE_PERSIST_SUCCESS, savedNote);
			return setNoteDtoFromNote(savedNote, user);
		} catch (Exception ex) {
			log.error(NoteUtils.NOTE_PERSIST_ERROR, new NoteNotPersistedException(ex.getMessage()));
			throw new NoteNotPersistedException(ex.getMessage());
		}
	}

	public NoteDto updateExistingNote(NoteDto noteDto, User user) {
		SpecificNote note = specificNoteRepository.findByIdAndUser(noteDto.getId(), user);
		if (note != null) {
			try {
				Note savedNote = setSpecificNoteFromNoteDto(noteDto, note, user);
				log.info(NoteUtils.NOTE_UPDATE_SUCCESS, savedNote);
				return setNoteDtoFromNote(savedNote, user);
			} catch (Exception ex) {
				log.error(NoteUtils.NOTE_UPDATE_ERROR, new NoteNotUpdatedException(ex.getMessage()));
				throw new NoteNotUpdatedException(ex.getMessage());
			}
		} else {
			log.error(NoteUtils.NOTE_NOT_FOUND, noteDto.getId());
			throw new NoteNotFoundException();
		}
	}

	public List<NoteDto> getAllNotesByUser(User user) {
		try {
			List<SpecificNote> noteList = specificNoteRepository.findAllByUserAndIsTrashedFalseAndIsArchivedFalse(user);
			List<NoteDto> noteDtoList = getNoteDtoFromNoteList(noteList, user);
			return noteDtoList;
		} catch (Exception exp) {
			log.error(NoteUtils.ERROR_FETCHING_NOTES_FOR_USER, user, new NotesNotFoundException(exp.getMessage()));
			throw new NotesNotFoundException(exp.getMessage());

		}
	}

	public List<NoteDto> getAllNotesByUserAndLabelId(User user, int labelId) {
		try {
			Label label = labelRepository.findById(labelId).get();
			List<SpecificNote> noteList = specificNoteRepository.findByUserAndLabel(user, label);
			List<NoteDto> noteDtoList = getNoteDtoFromNoteList(noteList, user);
			return noteDtoList;
		} catch (Exception exp) {
			log.error(NoteUtils.ERROR_FETCHING_NOTES_FOR_USER, user, new NotesNotFoundException(exp.getMessage()));
			throw new NotesNotFoundException(exp.getMessage());
		}
	}

	public List<NoteDto> getAllNotesByIsTrashed(User user) {
		try {
			List<SpecificNote> noteList = specificNoteRepository.findAllByUserAndIsTrashedTrue(user);
			List<NoteDto> noteDtoList = getNoteDtoFromNoteList(noteList, user);
			return noteDtoList;
		} catch (Exception exp) {
			log.error(NoteUtils.ERROR_FETCHING_NOTES_FOR_USER, user);
			throw new NoteNotFoundException();
		}
	}

	public List<NoteDto> getAllNotesByIsArchived(User user) {
		try {
			List<SpecificNote> noteList = specificNoteRepository.findAllByUserAndIsArchivedTrue(user);
			List<NoteDto> noteDtoList = getNoteDtoFromNoteList(noteList, user);
			return noteDtoList;
		} catch (Exception exp) {
			log.error(NoteUtils.ERROR_FETCHING_NOTES_FOR_USER, user);
			throw new NoteNotFoundException();
		}
	}

	public List<NoteDto> getAllNotesByReminder(User user) {
		try {
			List<SpecificNote> noteList = specificNoteRepository.findAllByUserAndReminderNotNull(user);
			List<NoteDto> noteDtoList = getNoteDtoFromNoteList(noteList, user);
			return noteDtoList;
		} catch (Exception exp) {
			log.error(NoteUtils.ERROR_FETCHING_NOTES_FOR_USER, user);
			throw new NoteNotFoundException();
		}
	}

	public NoteDto pinNote(User user, int noteId) {
		SpecificNote note = specificNoteRepository.findByIdAndUser(noteId, user);
		if (note != null) {
			try {
				note.setArchived(false);
				note.setTrashed(false);
				note.setPinned(true);
				Note updatedNote = note.getCommonNote();
				Note savedNote = noteRepository.save(updatedNote);
				log.info(NoteUtils.NOTE_UPDATE_SUCCESS, savedNote);
				return setNoteDtoFromNote(savedNote, user);
			} catch (Exception ex) {
				log.error(NoteUtils.NOTE_UPDATE_ERROR, new NoteNotUpdatedException(ex.getMessage()));
				throw new NoteNotUpdatedException(ex.getMessage());
			}
		} else {
			log.error(NoteUtils.NOTE_NOT_FOUND, noteId);
			throw new NoteNotFoundException();
		}
	}

	public NoteDto unpinNote(User user, int noteId) {
		SpecificNote note = specificNoteRepository.findByIdAndUser(noteId, user);
		if (note != null) {
			try {
				note.setArchived(false);
				note.setTrashed(false);
				note.setPinned(false);
				Note updatedNote = note.getCommonNote();
				Note savedNote = noteRepository.save(updatedNote);
				log.info(NoteUtils.NOTE_UPDATE_SUCCESS, savedNote);
				return setNoteDtoFromNote(savedNote, user);
			} catch (Exception ex) {
				log.error(NoteUtils.NOTE_UPDATE_ERROR, new NoteNotUpdatedException(ex.getMessage()));
				throw new NoteNotUpdatedException(ex.getMessage());
			}
		} else {
			log.error(NoteUtils.NOTE_NOT_FOUND, noteId);
			throw new NoteNotFoundException();
		}
	}

	public NoteDto archiveNote(User user, int noteId) {
		SpecificNote note = specificNoteRepository.findByIdAndUser(noteId, user);
		if (note != null) {
			try {
				note.setArchived(true);
				note.setTrashed(false);
				note.setPinned(false);
				Note updatedNote = note.getCommonNote();
				Note savedNote = noteRepository.save(updatedNote);
				log.info(NoteUtils.NOTE_UPDATE_SUCCESS, savedNote);
				return setNoteDtoFromNote(savedNote, user);
			} catch (Exception ex) {
				log.error(NoteUtils.NOTE_UPDATE_ERROR, new NoteNotUpdatedException(ex.getMessage()));
				throw new NoteNotUpdatedException(ex.getMessage());
			}
		} else {
			log.error(NoteUtils.NOTE_NOT_FOUND, noteId);
			throw new NoteNotFoundException();
		}
	}

	public NoteDto trashNote(User user, int noteId) {
		SpecificNote note = specificNoteRepository.findByIdAndUser(noteId, user);
		if (note != null) {
			try {
				note.setArchived(false);
				note.setTrashed(true);
				note.setPinned(false);
				Note updatedNote = note.getCommonNote();
				Note savedNote = noteRepository.save(updatedNote);
				log.info(NoteUtils.NOTE_UPDATE_SUCCESS, savedNote);
				return setNoteDtoFromNote(savedNote, user);
			} catch (Exception ex) {
				log.error(NoteUtils.NOTE_UPDATE_ERROR, new NoteNotUpdatedException(ex.getMessage()));
				throw new NoteNotUpdatedException(ex.getMessage());
			}
		} else {
			log.error(NoteUtils.NOTE_NOT_FOUND, noteId);
			throw new NoteNotFoundException();
		}
	}

	public boolean deleteNoteByUserAndId(User user, int noteId) {
		SpecificNote note = specificNoteRepository.findByIdAndUser(noteId, user);
		if (note != null) {
			try {
				Note commonNote = note.getCommonNote();
				List<SpecificNote> noteList = specificNoteRepository.findAllByCommonNote(commonNote);
				if (noteList.size() > 1) {
					log.info(NoteUtils.NOTE_DELETE_SUCCESS);
					specificNoteRepository.delete(note);
				} else {
					log.info(NoteUtils.NOTE_PERMANENT_DELETE_SUCCESS);
					noteRepository.deleteById(commonNote.getId());
				}
				return true;
			} catch (Exception ex) {
				log.error(NoteUtils.ERROR_DELETING_NOTE_FOR_USER, new NoteNotDeletedException(ex.getMessage()));
				throw new NoteNotDeletedException(ex.getMessage());
			}
		} else {
			log.error(NoteUtils.NOTE_NOT_FOUND, noteId, new NoteNotFoundException());
			throw new NoteNotFoundException();
		}
	}

	private List<NoteDto> getNoteDtoFromNoteList(List<SpecificNote> noteList, User user) {
		List<NoteDto> noteDtoList = noteList.stream().map(specificNote -> {
			Note note = specificNote.getCommonNote();
			return setNoteDtoFromNote(note, user);
		}).collect(Collectors.toList());
		log.info(NoteUtils.NOTE_FETCH_SUCCESS, user);
		return noteDtoList;
	}

	private NoteDto setNoteDtoFromNote(Note note, User user) {
		NoteDto noteDto = new NoteDto();
		noteDto.setTitle(note.getTitle());
		noteDto.setContent(note.getContent());
		noteDto.setImages(note.getImages());
		if (note.getCollaboratorList() != null) {
			List<User> collaboratorList = note.getCollaboratorList();
			List<CollaboratorDto> collaboratorDtoList = collaboratorList.stream().map(collaboratorItem -> {
				CollaboratorDto collaboratorDto = new CollaboratorDto();
				collaboratorDto.setId(collaboratorItem.getId());
				collaboratorDto.setEmail(collaboratorItem.getEmail());
				return collaboratorDto;
			}).collect(Collectors.toList());
			noteDto.setCollaboratorList(collaboratorDtoList);
		}
		List<SpecificNote> specificNoteList = note.getSpecificNoteList();
		SpecificNote specificNote = specificNoteList.stream().filter(noteItem -> user.equals(noteItem.getUser()))
				.findFirst().get();
		noteDto.setId(specificNote.getId());
		noteDto.setColor(specificNote.getColor());
		noteDto.setArchived(specificNote.isArchived());
		noteDto.setUpdatedAt(specificNote.getUpdatedAt());
		noteDto.setCreatedAt(specificNote.getCreatedAt());
		if (specificNote.getLabelSet() != null) {
			Set<Label> labelSet = specificNote.getLabelSet();
			Set<LabelDto> labelDtoSet = labelSet.stream().map(labelItem -> {
				LabelDto labelDto = new LabelDto();
				labelDto.setId(labelItem.getId());
				labelDto.setLabelName(labelItem.getLabelName());
				return labelDto;
			}).collect(Collectors.toSet());
			noteDto.setLabelSet(labelDtoSet);
		}
		noteDto.setPinned(specificNote.isPinned());
		noteDto.setReminder(specificNote.getReminder());
		noteDto.setTrashed(specificNote.isTrashed());
		return noteDto;
	}

	@Transactional
	private Note setNoteFromNoteDto(NoteDto noteDto, Note note, User user) {
		if (note.getId() != null) {
			note.setUpdatedBy(user);
		} else {
			note.setUpdatedBy(user);
			note.setCreatedBy(user);
		}
		note.setTitle(noteDto.getTitle());
		note.setContent(noteDto.getContent());
		note.setImages(noteDto.getImages());
//		note.setUpdatedAt(noteDto.getUpdatedAt());
//		note.setCreatedAt(noteDto.getCreatedAt());
		Note mappedNote = noteRepository.save(note);
		if (noteDto.getCollaboratorList() != null) {

			List<String> collaboratorEmails = noteDto.getCollaboratorList().stream()
					.map(collaborator -> collaborator.getEmail()).collect(Collectors.toList());
			Iterable<String> iterableEmails = collaboratorEmails;
			List<User> collaboratorList = userRepository.findAllByEmailIn(iterableEmails);
			List<User> nonExistingCollaboratorList = collaboratorList.stream().filter(
					collaborator -> specificNoteRepository.findByCommonNoteAndUser(mappedNote, collaborator) == null)
					.collect(Collectors.toList());
			nonExistingCollaboratorList.stream().forEach(collaborator -> {
				if (collaborator.getNoteList() != null) {
					collaborator.getNoteList().add(mappedNote);
				} else {
					List<Note> noteList = new ArrayList<Note>();
					noteList.add(mappedNote);
					collaborator.setNoteList(noteList);
				}
				SpecificNote specificNote = new SpecificNote();
				specificNote.setCommonNote(mappedNote);
				specificNote.setUser(collaborator);
				specificNote.setRole(Utils.Role.COLLABORATOR);
				specificNoteRepository.save(specificNote);
			});
			if (mappedNote.getCollaboratorList() == null) {
				List<User> userList = new ArrayList<User>();
				userList.addAll(nonExistingCollaboratorList);
				mappedNote.setCollaboratorList(userList);
			} else {
				mappedNote.getCollaboratorList().addAll(nonExistingCollaboratorList);
			}
		}
		Note savedNote = noteRepository.save(mappedNote);
		return savedNote;
	}

	@Transactional
	private Note setSpecificNoteFromNoteDto(NoteDto noteDto, SpecificNote specificNote, User user) {
		Note note = specificNote.getCommonNote();
		if (note == null) {
			note = new Note();
		}
		Note updatedNote = setNoteFromNoteDto(noteDto, note, user);
		specificNote.setColor(noteDto.getColor());
		specificNote.setArchived(noteDto.isArchived());
		specificNote.setUpdatedAt(noteDto.getUpdatedAt());
		specificNote.setCreatedAt(noteDto.getCreatedAt());
		if (noteDto.getLabelSet() != null) {
			List<Integer> labelIds = noteDto.getLabelSet().stream().map(label -> label.getId())
					.collect(Collectors.toList());
			Iterable<Integer> iterableIds = labelIds;
			List<Label> labelList = labelRepository.findAllById(iterableIds);
			labelList.stream().forEach(label -> {
				if (label.getNoteList() != null) {
					label.getNoteList().add(specificNote);
				} else {
					List<SpecificNote> noteList = new ArrayList<SpecificNote>();
					noteList.add(specificNote);
					label.setNoteList(noteList);
				}
			});
			if (specificNote.getLabelSet() == null) {
				Set<Label> labelSet = new HashSet<Label>();
				labelSet.addAll(labelList);
				specificNote.setLabelSet(labelSet);
			} else {
				specificNote.getLabelSet().addAll(labelList);
			}
		}
		if (specificNote.getRole() == null) {
			specificNote.setRole(Utils.Role.OWNER);
		}
		specificNote.setPinned(noteDto.isPinned());
		specificNote.setReminder(noteDto.getReminder());
		specificNote.setTrashed(noteDto.isTrashed());
		specificNote.setUser(user);
		specificNote.setCommonNote(updatedNote);
		// Save specificNote to ensure it gets an ID
		SpecificNote savedSpecificNote = specificNoteRepository.save(specificNote);

		// Associate the saved specificNote with the Note
		List<SpecificNote> specificNoteList = updatedNote.getSpecificNoteList();
		if (specificNoteList == null) {
			specificNoteList = new ArrayList<>();
			updatedNote.setSpecificNoteList(specificNoteList);
		}
		specificNoteList.add(savedSpecificNote);

		// Save updatedNote if needed (if there are changes to be persisted)
		noteRepository.save(updatedNote);
		return updatedNote;
	}

}
