package com.scribblemate.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scribblemate.dto.CollaboratorDto;
import com.scribblemate.dto.ColorUpdateDto;
import com.scribblemate.dto.LabelDto;
import com.scribblemate.dto.ListItemsDto;
import com.scribblemate.dto.NoteDto;
import com.scribblemate.entities.Label;
import com.scribblemate.entities.ListItems;
import com.scribblemate.entities.Note;
import com.scribblemate.entities.SpecificNote;
import com.scribblemate.entities.User;
import com.scribblemate.exceptions.labels.LabelNotDeletedException;
import com.scribblemate.exceptions.labels.LabelNotFoundException;
import com.scribblemate.exceptions.notes.CollaboratorAlreadyExistException;
import com.scribblemate.exceptions.notes.CollaboratorDoesNotExistException;
import com.scribblemate.exceptions.notes.CollaboratorNotDeletedException;
import com.scribblemate.exceptions.notes.NoteNotDeletedException;
import com.scribblemate.exceptions.notes.NoteNotFoundException;
import com.scribblemate.exceptions.notes.NoteNotPersistedException;
import com.scribblemate.exceptions.notes.NoteNotUpdatedException;
import com.scribblemate.exceptions.notes.NotesNotFoundException;
import com.scribblemate.exceptions.users.UserNotFoundException;
import com.scribblemate.repositories.LabelRepository;
import com.scribblemate.repositories.ListItemsRepository;
import com.scribblemate.repositories.NoteRepository;
import com.scribblemate.repositories.SpecificNoteRepository;
import com.scribblemate.repositories.UserRepository;
import com.scribblemate.utility.NoteUtils;
import com.scribblemate.utility.ResponseErrorUtils;
import com.scribblemate.utility.Utils;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NoteService {

	@Autowired
	private LabelService labelService;

	private ListItemsRepository listItemsRepository;

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
		SpecificNote note = specificNoteRepository.findById(noteDto.getId()).get();
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
	public NoteDto addCollaboratorToNote(User user, Long noteId, String collaboratorEmail) {
		SpecificNote note = specificNoteRepository.findById(noteId).get();
		if (note != null) {
			User collaborator = userRepository.findByEmail(collaboratorEmail)
					.orElseThrow(() -> new CollaboratorDoesNotExistException(
							ResponseErrorUtils.COLLABORATOR_DOES_NOT_EXIST_ERROR.getMessage()));
			Note commonNote = note.getCommonNote();
			List<Note> noteList = collaborator.getNoteList();
			if (noteList != null) {
				if (noteList.contains(commonNote)) {
					log.error(NoteUtils.COLLABORATOR_ALREADY_EXIST_ERROR);
					throw new CollaboratorAlreadyExistException(
							ResponseErrorUtils.COLLABORATOR_ALREADY_EXIST_ERROR.getMessage());
				}
				noteList.add(commonNote);
			} else {
				List<Note> newNoteList = new ArrayList<Note>();
				newNoteList.add(commonNote);
				collaborator.setNoteList(newNoteList);
			}
			if (commonNote.getCollaboratorList() == null) {
				List<User> userList = new ArrayList<User>();
				userList.add(collaborator);
				commonNote.setCollaboratorList(userList);
			} else {
				commonNote.getCollaboratorList().add(collaborator);
			}
			try {
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
	public NoteDto deleteCollaboratorFromNote(User user, Long noteId, String collaboratorEmail) {
		SpecificNote note = specificNoteRepository.findById(noteId).get();
		if (note != null) {
			try {
				User collaborator = userRepository.findByEmail(collaboratorEmail)
						.orElseThrow(() -> new UserNotFoundException());
				Note commonNote = note.getCommonNote();
				SpecificNote collabNote = specificNoteRepository.findByCommonNoteAndUser(commonNote, collaborator);
				specificNoteRepository.deleteAllByNoteId(collabNote.getId());
				specificNoteRepository.deleteCollaboratorByUserIdAndCommonNoteId(collaborator.getId(),
						commonNote.getId());
				specificNoteRepository.deleteByCommonNoteIdAndUserId(commonNote.getId(), collaborator.getId());

				// Flush the session to ensure changes are persisted
				entityManager.flush();
				// Clear the persistence context to detach all entities
				entityManager.clear();

				log.info(NoteUtils.COLLABORATOR_DELETE_SUCCESS, collaborator.getId());
				SpecificNote updatedNote = specificNoteRepository.findById(noteId).get();
				Note updatedCommonNote = updatedNote.getCommonNote();
				return setNoteDtoFromNote(updatedCommonNote, user);
			} catch (Exception ex) {
				log.error(NoteUtils.COLLABORATOR_DELETE_ERROR, collaboratorEmail,
						new CollaboratorNotDeletedException(ex.getMessage()));
				throw new CollaboratorNotDeletedException(ex.getMessage());
			}
		} else {
			log.error(NoteUtils.NOTE_NOT_FOUND, noteId, new NoteNotFoundException());
			throw new NoteNotFoundException();
		}
	}

	public NoteDto addLabelToNote(User user, Long noteId, Long labelId) {
		SpecificNote note = specificNoteRepository.findById(noteId).get();
		if (note != null) {
			try {
				Label label = labelRepository.findById(labelId).orElseThrow(() -> new LabelNotFoundException());
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
				NoteDto noteDto = saveNoteAndReturnDto(user, note);
				log.info(NoteUtils.NOTE_UPDATE_SUCCESS);
				return noteDto;
			} catch (Exception ex) {
				log.error(NoteUtils.NOTE_UPDATE_ERROR, new NoteNotUpdatedException(ex.getMessage()));
				throw new NoteNotUpdatedException(ex.getMessage());
			}
		} else {
			log.error(NoteUtils.NOTE_NOT_FOUND, noteId, new NoteNotFoundException());
			throw new NoteNotFoundException();
		}
	}

	public NoteDto addNewLabelToNote(User user, Long noteId, LabelDto labelDto) {
		LabelDto newlabelDto = labelService.createNewLabel(labelDto, user);
		return addLabelToNote(user, noteId, newlabelDto.getId());
	}

	public NoteDto deleteLabelFromNote(User user, Long noteId, Long labelId) {
		SpecificNote note = specificNoteRepository.findById(noteId).get();
		if (note != null) {
			try {
				Set<Label> labelSet = note.getLabelSet();
				Set<Label> filteredLabelSet = labelSet.stream().filter(label -> {
					return !label.getId().equals(labelId);
				}).collect(Collectors.toSet());
				note.setLabelSet(filteredLabelSet);
				NoteDto noteDto = saveNoteAndReturnDto(user, note);
				log.info(NoteUtils.LABEL_DELETE_SUCCESS, labelId);
				return noteDto;
			} catch (Exception ex) {
				log.error(NoteUtils.LABEL_DELETE_ERROR, labelId, new LabelNotDeletedException(ex.getMessage()));
				throw new LabelNotDeletedException(ex.getMessage());
			}
		} else {
			log.error(NoteUtils.NOTE_NOT_FOUND, noteId, new NoteNotFoundException());
			throw new NoteNotFoundException();
		}
	}

	public List<NoteDto> getAllNotesForUser(User user) {
		try {
			List<SpecificNote> noteList = specificNoteRepository.findAllByUserOrderByCommonNoteCreatedAtDesc(user);
			List<NoteDto> noteDtoList = getNoteDtoFromNoteList(noteList, user);
			return noteDtoList;
		} catch (Exception exp) {
			log.error(NoteUtils.ERROR_FETCHING_NOTES_FOR_USER, user, new NotesNotFoundException(exp.getMessage()));
			throw new NotesNotFoundException(exp.getMessage());

		}
	}

	public List<NoteDto> getAllNotesNonTrashedNonArchivedByUser(User user) {
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
		List<NoteDto> noteDtoList = getAllNotesForUser(user);
		List<NoteDto> notesWithLabels = noteDtoList.stream().filter(noteDto -> {
			return !noteDto.getLabelSet().isEmpty();
		}).collect(Collectors.toList());
		return notesWithLabels;
	}

	public Map<Long, List<Long>> getAllNotesByUserAndLabelIds(User user) {
		List<NoteDto> notesWithLabels = getAllNotesWithLabelsByUser(user);
		List<Long> notesWithLabelsIds = notesWithLabels.stream().map(note -> note.getId()).collect(Collectors.toList());
		List<Long> labelIds = labelRepository.getLabelIdsByUser(user.getId());
		Map<Long, List<Long>> notesMap = new HashMap<>();
		labelIds.forEach(id -> {
			notesMap.put(id, notesWithLabels.stream().filter(note -> {
				Set<LabelDto> labelSet = note.getLabelSet();
				return labelSet.stream().anyMatch(label -> label.getId() == id);
			}).map(note -> note.getId()).collect(Collectors.toList()));
		});
		notesMap.put((long) 0, notesWithLabelsIds);
		return notesMap;
	}

	public List<NoteDto> getNotesByUserAndLabelId(User user, Long labelId) {
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

	public NoteDto saveNoteAndReturnDto(User user, SpecificNote note) {
		try {
			SpecificNote specificNote = specificNoteRepository.save(note);
			Note commonNote = specificNote.getCommonNote();
			log.info(NoteUtils.NOTE_UPDATE_SUCCESS);
			return setNoteDtoFromNote(commonNote, user);
		} catch (Exception ex) {
			log.error(NoteUtils.NOTE_UPDATE_ERROR, new NoteNotUpdatedException(ex.getMessage()));
			throw new NoteNotUpdatedException(ex.getMessage());
		}
	}

	public NoteDto updateColorOfNote(User user, ColorUpdateDto colorDto) {
		SpecificNote note = specificNoteRepository.findById(colorDto.getNoteId()).get();
		if (note != null) {
			note.setColor(colorDto.getColor());
			return saveNoteAndReturnDto(user, note);
		} else {
			log.error(NoteUtils.NOTE_NOT_FOUND, colorDto.getNoteId(), new NoteNotFoundException());
			throw new NoteNotFoundException();
		}
	}

	public NoteDto pinNote(User user, Long noteId) {
		SpecificNote note = specificNoteRepository.findById(noteId).get();
		if (note != null) {
			note.setArchived(false);
			note.setTrashed(false);
			note.setPinned(!note.isPinned());
			return saveNoteAndReturnDto(user, note);
		} else {
			log.error(NoteUtils.NOTE_NOT_FOUND, noteId, new NoteNotFoundException());
			throw new NoteNotFoundException();
		}
	}

	public NoteDto archiveNote(User user, Long noteId) {
		SpecificNote note = specificNoteRepository.findById(noteId).get();
		if (note != null) {
			note.setArchived(!note.isArchived());
			note.setTrashed(false);
			note.setPinned(false);
			return saveNoteAndReturnDto(user, note);
		} else {
			log.error(NoteUtils.NOTE_NOT_FOUND, noteId, new NoteNotFoundException());
			throw new NoteNotFoundException();
		}
	}

	public NoteDto trashNote(User user, Long noteId) {
		SpecificNote note = specificNoteRepository.findById(noteId).get();
		if (note != null) {
			note.setArchived(false);
			note.setTrashed(!note.isTrashed());
			note.setPinned(false);
			return saveNoteAndReturnDto(user, note);
		} else {
			log.error(NoteUtils.NOTE_NOT_FOUND, noteId, new NoteNotFoundException());
			throw new NoteNotFoundException();
		}
	}

	@Transactional
	public boolean deleteNoteByUserAndId(User currentUser, Long noteId) {
		User user = userRepository.findByEmail(currentUser.getEmail()).orElseThrow(() -> new UserNotFoundException());
		SpecificNote note = specificNoteRepository.findById(noteId).get();
		if (note != null) {
			try {
				Note commonNote = note.getCommonNote();
				List<SpecificNote> noteList = specificNoteRepository.findAllByCommonNote(commonNote);
				specificNoteRepository.deleteAllByNoteId(note.getId());
				log.info(NoteUtils.NOTE_DELETE_SUCCESS);
				if (noteList.size() == 1) {
					log.info(NoteUtils.NOTE_PERMANENT_DELETE_SUCCESS);
					specificNoteRepository.deleteCollaboratorByUserIdAndCommonNoteId(user.getId(), commonNote.getId());
					noteRepository.deleteNoteImages(commonNote.getId());
					noteRepository.deleteListItems(commonNote.getId());
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

	@Transactional
	public void insertListItemAtPosition(Note note, ListItems newItem, int position) {
		List<ListItems> listItems = note.getListItems();
		for (int i = position; i < listItems.size(); i++) {
			ListItems item = listItems.get(i);
			item.setOrderIndex(item.getOrderIndex() + 1);
		}
		newItem.setOrderIndex(position);
		listItems.add(position, newItem);
		listItemsRepository.saveAll(listItems);
		listItemsRepository.save(newItem);
	}

	@Transactional
	public void removeListItem(Note note, ListItems itemToRemove) {
		List<ListItems> listItems = note.getListItems();
		int removedIndex = itemToRemove.getOrderIndex();
		listItems.remove(itemToRemove);
		for (ListItems item : listItems) {
			if (item.getOrderIndex() > removedIndex) {
				item.setOrderIndex(item.getOrderIndex() - 1);
			}
		}
		listItemsRepository.saveAll(listItems);
	}

	private List<NoteDto> getNoteDtoFromNoteList(List<SpecificNote> noteList, User user) {
		List<NoteDto> noteDtoList = noteList.stream().map(specificNote -> {
			Note note = specificNote.getCommonNote();
			return setNoteDtoFromNote(note, user);
		}).collect(Collectors.toList());
		log.info(NoteUtils.NOTE_FETCH_SUCCESS, user);
		return noteDtoList;
	}

	private CollaboratorDto getCollaboratorDto(User user) {
		CollaboratorDto collaboratorDto = new CollaboratorDto();
		collaboratorDto.setId(user.getId());
		collaboratorDto.setName(user.getFullName());
		collaboratorDto.setEmail(user.getEmail());
		return collaboratorDto;
	}

	private LabelDto getLabelDto(Label label) {
		LabelDto labelDto = new LabelDto();
		labelDto.setId(label.getId());
		labelDto.setLabelName(label.getLabelName());
		labelDto.setImportant(label.isImportant());
		labelDto.setCreatedAt(label.getCreatedAt());
		labelDto.setUpdatedAt(label.getUpdatedAt());
		return labelDto;
	}

	private NoteDto setNoteDtoFromNote(Note note, User user) {
		NoteDto noteDto = new NoteDto();
		noteDto.setTitle(note.getTitle());
		noteDto.setContent(note.getContent());
		setNoteDtoListItemsFromNoteListItems(noteDto, note.getListItems());
		noteDto.setImages(note.getImages());
		noteDto.setCreatedBy(getCollaboratorDto(note.getCreatedBy()));

		if (note.getCollaboratorList() != null) {
			List<User> collaboratorList = note.getCollaboratorList();
			List<CollaboratorDto> collaboratorDtoList = collaboratorList.stream().map(collaboratorItem -> {
				CollaboratorDto collaboratorDto = getCollaboratorDto(collaboratorItem);
				return collaboratorDto;
			}).collect(Collectors.toList());
			noteDto.setCollaboratorList(collaboratorDtoList);
		}
		List<SpecificNote> specificNoteList = note.getSpecificNoteList();
		SpecificNote specificNote = specificNoteList.stream().filter(noteItem -> user.equals(noteItem.getUser()))
				.findFirst().get();
		noteDto.setId(specificNote.getId());
		noteDto.setColor(specificNote.getColor());
		noteDto.setCreatedAt(specificNote.getCreatedAt());
		if (note.getUpdatedAt() != null && note.getCreatedAt().compareTo(note.getUpdatedAt()) != 0) {
			noteDto.setUpdatedAt(note.getUpdatedAt());
			noteDto.setUpdatedBy(getCollaboratorDto(note.getUpdatedBy()));
		}
		if (specificNote.getLabelSet() != null) {
			Set<Label> labelSet = specificNote.getLabelSet();
			Set<LabelDto> labelDtoSet = labelSet.stream().map(labelItem -> {
				LabelDto labelDto = getLabelDto(labelItem);
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

	private void setNoteDtoListItemsFromNoteListItems(NoteDto noteDto, List<ListItems> listItems) {
		List<ListItemsDto> dtoListItems = noteDto.getListItems();
		if (listItems != null) {
			listItems.forEach(item -> {
				ListItemsDto itemDto = new ListItemsDto();
				itemDto.setContent(item.getContent());
				itemDto.setDone(item.isDone());
				itemDto.setId(item.getId());
				itemDto.setOrderIndex(item.getOrderIndex());
				itemDto.setCreatedAt(item.getCreatedAt());
				itemDto.setUpdatedAt(item.getUpdatedAt());
				dtoListItems.add(itemDto);
			});
		}
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
		setNoteListItemsFromNoteDtoListItems(noteDto.getListItems(), note);
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

			}
		}
		Note savedNote = noteRepository.save(mappedNote);
		return savedNote;
	}

	private void setNoteListItemsFromNoteDtoListItems(List<ListItemsDto> noteDtoListItems, Note note) {
		List<ListItems> listItems = note.getListItems();
		if (noteDtoListItems != null) {
			noteDtoListItems.forEach(itemDto -> {
				ListItems item = new ListItems();
				item.setContent(itemDto.getContent());
				item.setDone(itemDto.isDone());
				item.setOrderIndex(itemDto.getOrderIndex());
				item.setId(itemDto.getId());
				item.setCreatedAt(itemDto.getCreatedAt());
				item.setUpdatedAt(itemDto.getUpdatedAt());
				listItems.add(item);
			});
		}

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
			List<Long> labelIds = noteDto.getLabelSet().stream().map(label -> label.getId())
					.collect(Collectors.toList());
			Iterable<Long> iterableIds = labelIds;
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
		SpecificNote savedSpecificNote = specificNoteRepository.save(specificNote);
		List<SpecificNote> specificNoteList = updatedNote.getSpecificNoteList();
		if (specificNoteList == null) {
			specificNoteList = new ArrayList<>();
			updatedNote.setSpecificNoteList(specificNoteList);
		}
		specificNoteList.add(savedSpecificNote);
		return updatedNote;
	}

	public NoteDto getNoteById(User user, Long id) {
		SpecificNote specificNote = specificNoteRepository.findById(id).get();
		Note note = specificNote.getCommonNote();
		NoteDto noteDto = setNoteDtoFromNote(note, user);
		return noteDto;
	}

}
