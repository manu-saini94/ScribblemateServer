package com.noteseyfinal1.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.noteseyfinal1.dto.CollaboratorDto;
import com.noteseyfinal1.dto.LabelDto;
import com.noteseyfinal1.dto.NoteDto;
import com.noteseyfinal1.entities.User;
import com.noteseyfinal1.responses.SuccessResponse;
import com.noteseyfinal1.services.NoteService;
import com.noteseyfinal1.services.UserService;
import com.noteseyfinal1.utility.ResponseSuccessUtils;
import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("/note")
@RestController
public class NoteController {

	@Autowired
	private NoteService noteService;

	@Autowired
	private UserService userService;

	@PostMapping("/create")
	public ResponseEntity<SuccessResponse> createNote(@RequestBody NoteDto notedto, HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		NoteDto note = noteService.createNewNote(notedto, user);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_PERSIST_SUCCESS, note));
	}

	@PostMapping("/add/collaborator")
	public ResponseEntity<SuccessResponse> addCollaboratorToNote(@RequestBody CollaboratorDto collaboratorDto,
			@RequestParam("id") int noteId, HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		NoteDto note = noteService.addCollaboratorToNote(user, noteId, collaboratorDto);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_UPDATE_SUCCESS, note));
	}

	@PostMapping("/add/label")
	public ResponseEntity<SuccessResponse> addLabelToNote(@RequestBody LabelDto labelDto,
			@RequestParam("id") int noteId, HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		NoteDto note = noteService.addLabelToNote(user, noteId, labelDto);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_UPDATE_SUCCESS, note));
	}
	
	@PostMapping("/add/new/label")
	public ResponseEntity<SuccessResponse> addNewLabelAndAddToNote(@RequestBody LabelDto labelDto,
			@RequestParam("id") int noteId, HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		NoteDto note = noteService.addNewLabelToNote(user, noteId, labelDto);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_UPDATE_SUCCESS, note));
	}
	
	

	@GetMapping("/get")
	public ResponseEntity<SuccessResponse> getAllEssentialNotes(HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		List<NoteDto> notesList = noteService.getAllNotesByUser(user);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_FETCHING_SUCCESS, notesList));
	}

	@GetMapping("/get/label")
	public ResponseEntity<SuccessResponse> getAllNotesByLabel(@RequestParam("labelId") int labelId,
			HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		List<NoteDto> notesList = noteService.getAllNotesByUserAndLabelId(user, labelId);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_FETCHING_SUCCESS, notesList));
	}

	@GetMapping("/get/trash")
	public ResponseEntity<SuccessResponse> getAllTrashedNotes(HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		List<NoteDto> notesList = noteService.getAllNotesByIsTrashed(user);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_FETCHING_SUCCESS, notesList));
	}

	@GetMapping("/get/archive")
	public ResponseEntity<SuccessResponse> getAllArchivedNotes(HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		List<NoteDto> notesList = noteService.getAllNotesByIsArchived(user);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_FETCHING_SUCCESS, notesList));
	}

	@GetMapping("/get/reminder")
	public ResponseEntity<SuccessResponse> getAllReminderNotes(HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		List<NoteDto> notesList = noteService.getAllNotesByReminder(user);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_FETCHING_SUCCESS, notesList));
	}

	@PutMapping("/update")
	public ResponseEntity<SuccessResponse> updateNote(@RequestBody NoteDto notedto, HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		NoteDto note = noteService.updateExistingNote(notedto, user);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_UPDATE_SUCCESS, note));
	}

	@PutMapping("/update/pin")
	public ResponseEntity<SuccessResponse> pinNote(@RequestParam("noteId") int noteId, HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		NoteDto note = noteService.pinNote(user, noteId);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_UPDATE_SUCCESS, note));
	}

	@PutMapping("/update/archive")
	public ResponseEntity<SuccessResponse> archiveNote(@RequestParam("noteId") int noteId,
			HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		NoteDto note = noteService.archiveNote(user, noteId);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_UPDATE_SUCCESS, note));
	}

	@PutMapping("/update/trash")
	public ResponseEntity<SuccessResponse> trashNote(@RequestParam("noteId") int noteId,
			HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		NoteDto note = noteService.trashNote(user, noteId);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_UPDATE_SUCCESS, note));
	}

	@DeleteMapping("/delete/collaborator")
	public ResponseEntity<SuccessResponse> removeCollaboratorFromNote(@RequestParam("noteId") int noteId,
			@RequestParam("collaboratorId") int collaboratorId, HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		NoteDto note = noteService.deleteCollaboratorFromNote(user, noteId, collaboratorId);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.COLLABORATOR_DELETE_SUCCESS, note));
	}

	@DeleteMapping("/delete")
	public ResponseEntity<SuccessResponse> deleteNoteByUser(@RequestParam("noteId") int noteId,
			HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		boolean isDeleted = noteService.deleteNoteByUserAndId(user, noteId);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_DELETE_SUCCESS, isDeleted));
	}

	@DeleteMapping("/delete/label")
	public ResponseEntity<SuccessResponse> removeLabelFromNote(@RequestParam("noteId") int noteId,
			@RequestParam("labelId") int labelId, HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		NoteDto note = noteService.deleteLabelFromNote(user, noteId, labelId);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.COLLABORATOR_DELETE_SUCCESS, note));
	}

}
