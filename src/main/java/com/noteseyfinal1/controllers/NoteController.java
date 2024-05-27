package com.noteseyfinal1.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

	@PutMapping("/update")
	public ResponseEntity<SuccessResponse> updateNote(@RequestBody NoteDto notedto, HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		NoteDto note = noteService.updateExistingNote(notedto, user);
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
//
//	@GetMapping("/get/trash")
//	public ResponseEntity<SuccessResponse> getAllTrashedSpecificToNotes(HttpServletRequest httpRequest) {
//		User user = userService.getEmailFromJwt(httpRequest);
//		List<NoteDto> notesList = noteService.getAllNotesByIsTrashed(user);
//		return ResponseEntity.ok().body(
//				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_FETCHING_SUCCESS, notesList));
//	}
//
//	@PutMapping("/trash")
//	public ResponseEntity<SuccessResponse> trashSpecificToNote(@RequestParam("id") int id, HttpServletRequest httpRequest) {
//		User user = userService.getEmailFromJwt(httpRequest);
//		boolean isTrashed = noteService.trashNoteByUserAndId(user, id);
//		return ResponseEntity.ok()
//				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_TRASH_SUCCESS, isTrashed));
//	}
//	@DeleteMapping("/delete")
//	public ResponseEntity<SuccessResponse> deleteSpecificToNote(@RequestParam("id") int id, HttpServletRequest httpRequest) {
//		User user = userService.getEmailFromJwt(httpRequest);
//		boolean isDeleted = noteService.deleteNoteByUserAndId(user, id);
//		return ResponseEntity.ok()
//				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.NOTE_DELETE_SUCCESS, isDeleted));
//	}

}
