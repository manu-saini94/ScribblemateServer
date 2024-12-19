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

import com.scribblemate.dto.LabelDto;
import com.scribblemate.entities.User;
import com.scribblemate.responses.SuccessResponse;
import com.scribblemate.services.LabelService;
import com.scribblemate.services.UserService;
import com.scribblemate.utility.ResponseSuccessUtils;

import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("/api/v1/label")
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
public class LabelController {

	@Autowired
	private LabelService labelService;

	@Autowired
	private UserService userService;

	@PostMapping("/create")
	public ResponseEntity<SuccessResponse> createLabel(@RequestBody LabelDto labelDto, HttpServletRequest httpRequest) {
		User user = userService.getUserFromHttpRequest(httpRequest);
		LabelDto newLabelDto = labelService.createNewLabel(labelDto, user);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.LABEL_PERSIST_SUCCESS, newLabelDto));
	}

	@PutMapping("/update")
	public ResponseEntity<SuccessResponse> updateLabel(@RequestBody LabelDto labelDto, HttpServletRequest httpRequest) {
		User user = userService.getUserFromHttpRequest(httpRequest);
		LabelDto updatedLabelDto = labelService.editLabel(labelDto, user);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.LABEL_UPDATE_SUCCESS, updatedLabelDto));
	}

	@GetMapping("/get")
	public ResponseEntity<SuccessResponse> getAllLabelsByUser(HttpServletRequest httpRequest) {
		User user = userService.getUserFromHttpRequest(httpRequest);
		List<LabelDto> labelList = labelService.getLabelsByUser(user);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.LABEL_FETCHING_SUCCESS, labelList));
	}

	@DeleteMapping("/delete")
	public ResponseEntity<SuccessResponse> deleteLabelByUser(@RequestParam("id") Long labelId,
			HttpServletRequest httpRequest) {
		User user = userService.getUserFromHttpRequest(httpRequest);
		boolean isDeleted = labelService.deleteLabel(labelId, user);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.LABEL_DELETE_SUCCESS, isDeleted));
	}

}
