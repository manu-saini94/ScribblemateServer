package com.scribblemate.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scribblemate.dto.CollaboratorDto;
import com.scribblemate.dto.ColorUpdateDto;
import com.scribblemate.dto.LabelDto;
import com.scribblemate.dto.NoteDto;
import com.scribblemate.entities.User;
import com.scribblemate.responses.SuccessResponse;
import com.scribblemate.services.AuthenticationService;
import com.scribblemate.services.NoteService;
import com.scribblemate.services.UserService;
import com.scribblemate.utility.ResponseSuccessUtils;

import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("/api/v1/note")
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
public class NoteController {

	@Autowired
	private NoteService noteService;

	@Autowired
	private UserService userService;

	@PostMapping("/create")
	public ResponseEntity<SuccessResponse> createNote(@RequestBody NoteDto notedto, HttpServletRequest httpRequest) {
		User user = userService.getUserFromHttpRequest(httpRequest);
		NoteDto note = noteService.createNewNote(notedto, user);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_PERSIST_SUCCESS, note));
	}

	@PostMapping("/add/label")
	public ResponseEntity<SuccessResponse> addLabelToNote(@RequestParam("labelId") Long labelId,
			@RequestParam("noteId") Long noteId, HttpServletRequest httpRequest) {
		User user = userService.getUserFromHttpRequest(httpRequest);
		NoteDto note = noteService.addLabelToNote(user, noteId, labelId);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_UPDATE_SUCCESS, note));
	}

	@DeleteMapping("/delete/label")
	public ResponseEntity<SuccessResponse> removeLabelFromNote(@RequestParam("noteId") Long noteId,
			@RequestParam("labelId") Long labelId, HttpServletRequest httpRequest) {
		User user = userService.getUserFromHttpRequest(httpRequest);
		NoteDto note = noteService.deleteLabelFromNote(user, noteId, labelId);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.COLLABORATOR_DELETE_SUCCESS, note));
	}

	@PostMapping("/add/new/label")
	public ResponseEntity<SuccessResponse> addNewLabelAndAddToNote(@RequestBody LabelDto labelDto,
			@RequestParam("id") Long noteId, HttpServletRequest httpRequest) {
		User user = userService.getUserFromHttpRequest(httpRequest);
		NoteDto note = noteService.addNewLabelToNote(user, noteId, labelDto);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_UPDATE_SUCCESS, note));
	}

	@GetMapping("/all")
	public ResponseEntity<SuccessResponse> getAllNotes(HttpServletRequest httpRequest) {
		User user = userService.getUserFromHttpRequest(httpRequest);
		List<NoteDto> notesList = noteService.getAllNotesForUser(user);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_FETCHING_SUCCESS, notesList));
	}

	@GetMapping("/get/label")
	public ResponseEntity<SuccessResponse> getAllNotesByLabel(@RequestParam("labelId") Long labelId,
			HttpServletRequest httpRequest) {
		User user = userService.getUserFromHttpRequest(httpRequest);
		List<NoteDto> notesList = noteService.getAllNotesByUserAndLabelId(user, labelId);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_FETCHING_SUCCESS, notesList));
	}

	@GetMapping("/get/label/all")
	public ResponseEntity<SuccessResponse> getAllNotesWithLabels(HttpServletRequest httpRequest) {
		User user = userService.getUserFromHttpRequest(httpRequest);
		List<NoteDto> notesList = noteService.getAllNotesWithLabelsByUser(user);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_FETCHING_SUCCESS, notesList));
	}

	@GetMapping("/get/trash")
	public ResponseEntity<SuccessResponse> getAllTrashedNotes(HttpServletRequest httpRequest) {
		User user = userService.getUserFromHttpRequest(httpRequest);
		List<NoteDto> notesList = noteService.getAllNotesByIsTrashed(user);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_FETCHING_SUCCESS, notesList));
	}

	@GetMapping("/get/archive")
	public ResponseEntity<SuccessResponse> getAllArchivedNotes(HttpServletRequest httpRequest) {
		User user = userService.getUserFromHttpRequest(httpRequest);
		List<NoteDto> notesList = noteService.getAllNotesByIsArchived(user);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_FETCHING_SUCCESS, notesList));
	}

	@GetMapping("/get/reminder")
	public ResponseEntity<SuccessResponse> getAllReminderNotes(HttpServletRequest httpRequest) {
		User user = userService.getUserFromHttpRequest(httpRequest);
		List<NoteDto> notesList = noteService.getAllNotesByReminder(user);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_FETCHING_SUCCESS, notesList));
	}

	@PutMapping("/update")
	public ResponseEntity<SuccessResponse> updateNote(@RequestBody NoteDto notedto, HttpServletRequest httpRequest) {
		User user = userService.getUserFromHttpRequest(httpRequest);
		NoteDto note = noteService.updateExistingNote(notedto, user);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_UPDATE_SUCCESS, note));
	}

	@PutMapping("/update/pin")
	public ResponseEntity<SuccessResponse> pinNote(@RequestParam("noteId") Long noteId,
			HttpServletRequest httpRequest) {
		User user = userService.getUserFromHttpRequest(httpRequest);
		NoteDto note = noteService.pinNote(user, noteId);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_UPDATE_SUCCESS, note));
	}

	@PutMapping("/update/color")
	public ResponseEntity<SuccessResponse> updateColor(@RequestBody ColorUpdateDto colorDto,
			HttpServletRequest httpRequest) {
		User user = userService.getUserFromHttpRequest(httpRequest);
		NoteDto note = noteService.updateColorOfNote(user, colorDto);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_UPDATE_SUCCESS, note));
	}

	@PutMapping("/update/archive")
	public ResponseEntity<SuccessResponse> archiveNote(@RequestParam("noteId") Long noteId,
			HttpServletRequest httpRequest) {
		User user = userService.getUserFromHttpRequest(httpRequest);
		NoteDto note = noteService.archiveNote(user, noteId);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_UPDATE_SUCCESS, note));
	}

	@PutMapping("/update/trash")
	public ResponseEntity<SuccessResponse> trashNote(@RequestParam("noteId") Long noteId,
			HttpServletRequest httpRequest) {
		User user = userService.getUserFromHttpRequest(httpRequest);
		NoteDto note = noteService.trashNote(user, noteId);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_UPDATE_SUCCESS, note));
	}

	@PostMapping("/add/collaborator")
	public ResponseEntity<SuccessResponse> addCollaboratorToNote(
			@RequestParam("collaboratorId") String collaboratorEmail, @RequestParam("noteId") Long noteId,
			HttpServletRequest httpRequest) {
		User user = userService.getUserFromHttpRequest(httpRequest);
		NoteDto note = noteService.addCollaboratorToNote(user, noteId, collaboratorEmail);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_UPDATE_SUCCESS, note));
	}

	@DeleteMapping("/delete/collaborator")
	public ResponseEntity<SuccessResponse> removeCollaboratorFromNote(@RequestParam("noteId") Long noteId,
			@RequestParam("collaboratorId") Long collaboratorId, HttpServletRequest httpRequest) {
		User user = userService.getUserFromHttpRequest(httpRequest);
		NoteDto note = noteService.deleteCollaboratorFromNote(user, noteId, collaboratorId);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.COLLABORATOR_DELETE_SUCCESS, note));
	}

	@DeleteMapping("/delete")
	public ResponseEntity<SuccessResponse> deleteNoteByUser(@RequestParam("noteId") Long noteId,
			HttpServletRequest httpRequest) {

		User user = userService.getUserFromHttpRequest(httpRequest);
		boolean isDeleted = noteService.deleteNoteByUserAndId(user, noteId);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_DELETE_SUCCESS, isDeleted));
	}

}
