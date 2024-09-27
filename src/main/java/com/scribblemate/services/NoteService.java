package com.scribblemate.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scribblemate.dto.CollaboratorDto;
import com.scribblemate.dto.LabelDto;
import com.scribblemate.dto.NoteDto;
import com.scribblemate.entities.Label;
import com.scribblemate.entities.Note;
import com.scribblemate.entities.SpecificNote;
import com.scribblemate.entities.User;
import com.scribblemate.exceptions.labels.LabelNotDeletedException;
import com.scribblemate.exceptions.labels.LabelNotFoundException;
import com.scribblemate.exceptions.notes.CollaboratorNotDeletedException;
import com.scribblemate.exceptions.notes.NoteNotDeletedException;
import com.scribblemate.exceptions.notes.NoteNotFoundException;
import com.scribblemate.exceptions.notes.NoteNotPersistedException;
import com.scribblemate.exceptions.notes.NoteNotUpdatedException;
import com.scribblemate.exceptions.notes.NotesNotFoundException;
import com.scribblemate.exceptions.users.UserNotFoundException;
import com.scribblemate.repositories.LabelRepository;
import com.scribblemate.repositories.NoteRepository;
import com.scribblemate.repositories.SpecificNoteRepository;
import com.scribblemate.repositories.UserRepository;
import com.scribblemate.utility.NoteUtils;
import com.scribblemate.utility.Utils;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NoteService {

	@Autowired
	private LabelService labelService;

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LabelRepository labelRepository;

	@Autowired
	private SpecificNoteRepository specificNoteRepository;

	@Autowired
	private EntityManager entityManager;

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

	@Transactional
	public NoteDto addCollaboratorToNote(User user, int noteId, CollaboratorDto collaboratorDto) {
		SpecificNote note = specificNoteRepository.findByIdAndUser(noteId, user);
		if (note != null) {
			try {
				User collaborator = userRepository.findByEmail(collaboratorDto.getEmail())
						.orElseThrow(() -> new UserNotFoundException());
				Note commonNote = note.getCommonNote();
				if (collaborator.getNoteList() != null) {
					collaborator.getNoteList().add(commonNote);
				} else {
					List<Note> noteList = new ArrayList<Note>();
					noteList.add(commonNote);
					collaborator.setNoteList(noteList);
				}
				if (commonNote.getCollaboratorList() == null) {
					List<User> userList = new ArrayList<User>();
					userList.add(collaborator);
					commonNote.setCollaboratorList(userList);
				} else {
					commonNote.getCollaboratorList().add(collaborator);
				}
				SpecificNote specificNote = new SpecificNote();
				specificNote.setCommonNote(commonNote);
				specificNote.setUser(collaborator);
				specificNote.setRole(Utils.Role.COLLABORATOR);
				List<SpecificNote> specificNoteList = commonNote.getSpecificNoteList();
				if (specificNoteList == null) {
					specificNoteList = new ArrayList<>();
				}
				specificNoteList.add(specificNote);
				commonNote.setSpecificNoteList(specificNoteList);
				Note savedNote = noteRepository.save(commonNote);
				log.info(NoteUtils.NOTE_UPDATE_SUCCESS, savedNote);
				return setNoteDtoFromNote(savedNote, user);
			} catch (Exception ex) {
				log.error(NoteUtils.NOTE_UPDATE_ERROR, new NoteNotUpdatedException(ex.getMessage()));
				throw new NoteNotUpdatedException(ex.getMessage());
			}

		} else {
			log.error(NoteUtils.NOTE_NOT_FOUND, noteId, new NoteNotFoundException());
			throw new NoteNotFoundException();
		}
	}

	@Transactional
	public NoteDto deleteCollaboratorFromNote(User user, int noteId, int collaboratorId) {
		SpecificNote note = specificNoteRepository.findByIdAndUser(noteId, user);
		if (note != null) {
			try {
				User collaborator = userRepository.findById(collaboratorId)
						.orElseThrow(() -> new UserNotFoundException());
				Note commonNote = note.getCommonNote();
				SpecificNote collabNote = specificNoteRepository.findByCommonNoteAndUser(commonNote, collaborator);
				specificNoteRepository.deleteAllByNoteId(collabNote.getId());
				specificNoteRepository.deleteCollaboratorByUserIdAndCommonNoteId(collaboratorId, commonNote.getId());
				specificNoteRepository.deleteByCommonNoteIdAndUserId(commonNote.getId(), collaborator.getId());

				// Flush the session to ensure changes are persisted
				entityManager.flush();
				// Clear the persistence context to detach all entities
				entityManager.clear();

				log.info(NoteUtils.COLLABORATOR_DELETE_SUCCESS, collaboratorId);
				SpecificNote updatedNote = specificNoteRepository.findByIdAndUser(noteId, user);
				Note updatedCommonNote = updatedNote.getCommonNote();
				return setNoteDtoFromNote(updatedCommonNote, user);
			} catch (Exception ex) {
				log.error(NoteUtils.COLLABORATOR_DELETE_ERROR, collaboratorId,
						new CollaboratorNotDeletedException(ex.getMessage()));
				throw new CollaboratorNotDeletedException(ex.getMessage());
			}
		} else {
			log.error(NoteUtils.NOTE_NOT_FOUND, noteId, new NoteNotFoundException());
			throw new NoteNotFoundException();
		}
	}

	public NoteDto addLabelToNote(User user, int noteId, LabelDto labelDto) {
		SpecificNote note = specificNoteRepository.findByIdAndUser(noteId, user);
		if (note != null) {
			try {
				Label label = labelRepository.findById(labelDto.getId())
						.orElseThrow(() -> new LabelNotFoundException());
				if (note.getLabelSet() == null) {
					Set<Label> labelSet = new HashSet<Label>();
					labelSet.add(label);
					note.setLabelSet(labelSet);
				} else {
					note.getLabelSet().add(label);
				}

				if (label.getNoteList() != null) {
					label.getNoteList().add(note);
				} else {
					List<SpecificNote> noteList = new ArrayList<SpecificNote>();
					noteList.add(note);
					label.setNoteList(noteList);
				}
				SpecificNote savedNote = specificNoteRepository.save(note);
				log.info(NoteUtils.NOTE_UPDATE_SUCCESS, savedNote);
				Note commonNote = savedNote.getCommonNote();
				return setNoteDtoFromNote(commonNote, user);
			} catch (Exception ex) {
				log.error(NoteUtils.NOTE_UPDATE_ERROR, new NoteNotUpdatedException(ex.getMessage()));
				throw new NoteNotUpdatedException(ex.getMessage());
			}
		} else {
			log.error(NoteUtils.NOTE_NOT_FOUND, noteId, new NoteNotFoundException());
			throw new NoteNotFoundException();
		}
	}

	public NoteDto addNewLabelToNote(User user, int noteId, LabelDto labelDto) {
		LabelDto newlabelDto = labelService.createNewLabel(labelDto, user);
		return addLabelToNote(user, noteId, newlabelDto);
	}

	public NoteDto deleteLabelFromNote(User user, int noteId, int labelId) {
		SpecificNote note = specificNoteRepository.findByIdAndUser(noteId, user);
		if (note != null) {
			try {
				Set<Label> labelSet = note.getLabelSet();
				Set<Label> filteredLabelSet = labelSet.stream().filter(label -> {
					return !label.getId().equals(labelId);
				}).collect(Collectors.toSet());
				note.setLabelSet(filteredLabelSet);
				SpecificNote savedNote = specificNoteRepository.save(note);
				Note commonNote = savedNote.getCommonNote();
				log.info(NoteUtils.LABEL_DELETE_SUCCESS, labelId);
				return setNoteDtoFromNote(commonNote, user);
			} catch (Exception ex) {
				log.error(NoteUtils.LABEL_DELETE_ERROR, labelId, new LabelNotDeletedException(ex.getMessage()));
				throw new LabelNotDeletedException(ex.getMessage());
			}
		} else {
			log.error(NoteUtils.NOTE_NOT_FOUND, noteId, new NoteNotFoundException());
			throw new NoteNotFoundException();
		}
	}

	public List<NoteDto> getAllNotesByUser(User user) {
		try {
			List<SpecificNote> noteList = specificNoteRepository
					.findAllByUserAndIsTrashedFalseAndIsArchivedFalseOrderByCommonNoteCreatedAtDesc(user);
			List<NoteDto> noteDtoList = getNoteDtoFromNoteList(noteList, user);
			return noteDtoList;
		} catch (Exception exp) {
			log.error(NoteUtils.ERROR_FETCHING_NOTES_FOR_USER, user, new NotesNotFoundException(exp.getMessage()));
			throw new NotesNotFoundException(exp.getMessage());

		}
	}

	public List<NoteDto> getAllNotesWithLabelsByUser(User user) {
		// TODO Auto-generated method stub
		List<NoteDto> noteDtoList = getAllNotesByUser(user);
		List<NoteDto> notesWithLabels = noteDtoList.stream().filter(noteDto -> {
			return !noteDto.getLabelSet().isEmpty();
		}).collect(Collectors.toList());
		return notesWithLabels;
	}

	public List<NoteDto> getAllNotesByUserAndLabelId(User user, int labelId) {
		try {
			Label label = labelRepository.findById(labelId).get();
			List<SpecificNote> noteList = specificNoteRepository.findByUserAndLabelOrderByCommonNoteCreatedAtDesc(user,
					label);
			List<NoteDto> noteDtoList = getNoteDtoFromNoteList(noteList, user);
			return noteDtoList;
		} catch (Exception exp) {
			log.error(NoteUtils.ERROR_FETCHING_NOTES_FOR_USER, user, new NotesNotFoundException(exp.getMessage()));
			throw new NotesNotFoundException(exp.getMessage());
		}
	}

	public List<NoteDto> getAllNotesByIsTrashed(User user) {
		try {
			List<SpecificNote> noteList = specificNoteRepository
					.findAllByUserAndIsTrashedTrueOrderByUpdatedAtDesc(user);
			List<NoteDto> noteDtoList = getNoteDtoFromNoteList(noteList, user);
			return noteDtoList;
		} catch (Exception exp) {
			log.error(NoteUtils.ERROR_FETCHING_NOTES_FOR_USER, user, new NoteNotFoundException(exp.getMessage()));
			throw new NoteNotFoundException(exp.getMessage());
		}
	}

	public List<NoteDto> getAllNotesByIsArchived(User user) {
		try {
			List<SpecificNote> noteList = specificNoteRepository
					.findAllByUserAndIsArchivedTrueOrderByCommonNoteCreatedAtDesc(user);
			List<NoteDto> noteDtoList = getNoteDtoFromNoteList(noteList, user);
			return noteDtoList;
		} catch (Exception exp) {
			log.error(NoteUtils.ERROR_FETCHING_NOTES_FOR_USER, user, new NoteNotFoundException(exp.getMessage()));
			throw new NoteNotFoundException(exp.getMessage());
		}
	}

	public List<NoteDto> getAllNotesByReminder(User user) {
		try {
			List<SpecificNote> noteList = specificNoteRepository
					.findAllByUserAndReminderNotNullOrderByCommonNoteCreatedAtDesc(user);
			List<NoteDto> noteDtoList = getNoteDtoFromNoteList(noteList, user);
			return noteDtoList;
		} catch (Exception exp) {
			log.error(NoteUtils.ERROR_FETCHING_NOTES_FOR_USER, user, new NoteNotFoundException(exp.getMessage()));
			throw new NoteNotFoundException(exp.getMessage());
		}
	}

	public NoteDto pinNote(User user, int noteId) {
		SpecificNote note = specificNoteRepository.findByIdAndUser(noteId, user);
		if (note != null) {
			try {
				note.setArchived(false);
				note.setTrashed(false);
				note.setPinned(!note.isPinned());
				Note updatedNote = note.getCommonNote();
				Note savedNote = noteRepository.save(updatedNote);
				log.info(NoteUtils.NOTE_UPDATE_SUCCESS, savedNote);
				return setNoteDtoFromNote(savedNote, user);
			} catch (Exception ex) {
				log.error(NoteUtils.NOTE_UPDATE_ERROR, new NoteNotUpdatedException(ex.getMessage()));
				throw new NoteNotUpdatedException(ex.getMessage());
			}
		} else {
			log.error(NoteUtils.NOTE_NOT_FOUND, noteId, new NoteNotFoundException());
			throw new NoteNotFoundException();
		}
	}

	public NoteDto archiveNote(User user, int noteId) {
		SpecificNote note = specificNoteRepository.findByIdAndUser(noteId, user);
		if (note != null) {
			try {
				note.setArchived(!note.isArchived());
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
			log.error(NoteUtils.NOTE_NOT_FOUND, noteId, new NoteNotFoundException());
			throw new NoteNotFoundException();
		}
	}

	public NoteDto trashNote(User user, int noteId) {
		SpecificNote note = specificNoteRepository.findByIdAndUser(noteId, user);
		if (note != null) {
			try {
				note.setArchived(false);
				note.setTrashed(!note.isTrashed());
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
			log.error(NoteUtils.NOTE_NOT_FOUND, noteId, new NoteNotFoundException());
			throw new NoteNotFoundException();
		}
	}

	@Transactional
	public boolean deleteNoteByUserAndId(User currentUser, int noteId) {
		User user = userRepository.findByEmail(currentUser.getEmail()).orElseThrow(() -> new UserNotFoundException());
		SpecificNote note = specificNoteRepository.findByIdAndUser(noteId, user);
		if (note != null) {
			try {
				Note commonNote = note.getCommonNote();
				List<SpecificNote> noteList = specificNoteRepository.findAllByCommonNote(commonNote);
				specificNoteRepository.deleteAllByNoteId(note.getId());
				specificNoteRepository.deleteByCommonNoteIdAndUserId(commonNote.getId(), user.getId());
				log.info(NoteUtils.NOTE_DELETE_SUCCESS);
				if (noteList.size() == 1) {
					log.info(NoteUtils.NOTE_PERMANENT_DELETE_SUCCESS);
					specificNoteRepository.deleteCollaboratorByUserIdAndCommonNoteId(user.getId(), commonNote.getId());
					noteRepository.deleteNoteImages(commonNote.getId());
					noteRepository.deleteById(commonNote.getId());
				}
				return true;
			} catch (Exception ex) {
				log.error(NoteUtils.ERROR_DELETING_NOTE_FOR_USER, new NoteNotDeletedException(ex.getMessage()));
				throw new NoteNotDeletedException(ex.getMessage());
			}
		} else

		{
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
		noteDto.setUpdatedAt(specificNote.getUpdatedAt());
		noteDto.setCreatedAt(specificNote.getCreatedAt());
		if (specificNote.getLabelSet() != null) {
			Set<Label> labelSet = specificNote.getLabelSet();
			Set<LabelDto> labelDtoSet = labelSet.stream().map(labelItem -> {
				LabelDto labelDto = new LabelDto();
				labelDto.setId(labelItem.getId());
				labelDto.setLabelName(labelItem.getLabelName());
				labelDto.setImportant(labelItem.isImportant());
				return labelDto;
			}).collect(Collectors.toSet());
			noteDto.setLabelSet(labelDtoSet);
		}
		noteDto.setArchived(specificNote.isArchived());
		noteDto.setPinned(specificNote.isPinned());
		noteDto.setReminder(specificNote.getReminder());
		noteDto.setTrashed(specificNote.isTrashed());
		log.info(NoteUtils.NOTE_CREATED_AND_RETURN, noteDto);
		return noteDto;
	}

	@Transactional
	private Note setNoteFromNoteDto(NoteDto noteDto, Note note, User currentUser) {
		User user = userRepository.findByEmail(currentUser.getEmail()).orElseThrow(() -> new UserNotFoundException());
		if (note.getId() != null) {
			note.setUpdatedBy(user);
		} else {
			note.setUpdatedBy(user);
			note.setCreatedBy(user);
		}
		note.setTitle(noteDto.getTitle());
		note.setContent(noteDto.getContent());
		note.setImages(noteDto.getImages());
		Note mappedNote = noteRepository.save(note);
		if (noteDto.getCollaboratorList() != null) {
			List<CollaboratorDto> collabDtoList = noteDto.getCollaboratorList().stream()
					.filter(collaborator -> !collaborator.getEmail().equals(user.getEmail())).toList();
			List<String> collaboratorEmails = collabDtoList.stream().map(collaborator -> collaborator.getEmail())
					.collect(Collectors.toList());
			Iterable<String> iterableEmails = collaboratorEmails;
			List<User> collaboratorList = userRepository.findAllByEmailIn(iterableEmails);
			List<User> nonExistingCollaboratorList = collaboratorList.stream().filter(
					collaborator -> specificNoteRepository.findByCommonNoteAndUser(mappedNote, collaborator) == null)
					.collect(Collectors.toList());

			// Adding current user also as a collaborator
			if (user.getNoteList() != null) {
				user.getNoteList().add(mappedNote);
			} else {
				List<Note> noteList = new ArrayList<Note>();
				noteList.add(mappedNote);
				user.setNoteList(noteList);
			}

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
				userList.add(user);
				mappedNote.setCollaboratorList(userList);
			} else {
				List<User> collabList = mappedNote.getCollaboratorList();
				collabList.addAll(nonExistingCollaboratorList);
				collabList.add(user);
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
			List<Label> labelList = labelRepository.findAllById(iterableIds).stream()
					.filter(label -> label.getUser().getEmail().equals(user.getEmail())).toList();
			if (!labelList.isEmpty()) {
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
//		noteRepository.save(updatedNote);
		return updatedNote;
	}

}
