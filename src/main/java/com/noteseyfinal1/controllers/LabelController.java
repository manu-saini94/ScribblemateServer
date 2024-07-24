package com.noteseyfinal1.controllers;

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
import com.noteseyfinal1.dto.LabelDto;
import com.noteseyfinal1.entities.User;
import com.noteseyfinal1.responses.SuccessResponse;
import com.noteseyfinal1.services.LabelService;
import com.noteseyfinal1.services.UserService;
import com.noteseyfinal1.utility.ResponseSuccessUtils;
import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("/label")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LabelController {

	@Autowired
	private LabelService labelService;

	@Autowired
	private UserService userService;

	@PostMapping("/create")
	public ResponseEntity<SuccessResponse> createLabel(@RequestBody LabelDto labelDto, HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		LabelDto newLabelDto = labelService.createNewLabel(labelDto, user);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.LABEL_PERSIST_SUCCESS, newLabelDto));
	}

	@PutMapping("/update")
	public ResponseEntity<SuccessResponse> updateLabel(@RequestBody LabelDto labelDto, HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		LabelDto updatedLabelDto = labelService.editLabel(labelDto, user);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.LABEL_UPDATE_SUCCESS, updatedLabelDto));
	}

	@GetMapping("/get")
	public ResponseEntity<SuccessResponse> getAllLabelsByUser(HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		List<LabelDto> labelList = labelService.getLabelsByUser(user);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.LABEL_FETCHING_SUCCESS, labelList));
	}

	@DeleteMapping("/delete")
	public ResponseEntity<SuccessResponse> deleteLabelByUser(@RequestParam("id") int labelId,
			HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		boolean isDeleted = labelService.deleteLabel(labelId, user);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.LABEL_DELETE_SUCCESS, isDeleted));
	}

}
