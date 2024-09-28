package com.scribblemate.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.scribblemate.dto.UserResponseDto;
import com.scribblemate.entities.SpecificNote;
import com.scribblemate.entities.User;
import com.scribblemate.exceptions.users.UserNotDeletedException;
import com.scribblemate.exceptions.users.UserNotFoundException;
import com.scribblemate.repositories.LabelRepository;
import com.scribblemate.repositories.NoteRepository;
import com.scribblemate.repositories.SpecificNoteRepository;
import com.scribblemate.repositories.UserRepository;
import com.scribblemate.utility.NoteUtils;
import com.scribblemate.utility.Utils.Status;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SpecificNoteRepository specificNoteRepository;

	@Autowired
	private LabelRepository labelRepository;

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private JwtAuthenticationService jwtService;

	public List<User> allUsers() {
		List<User> users = new ArrayList<>();
		userRepository.findAll().forEach(users::add);
		return users;
	}

	public User getEmailFromJwt(HttpServletRequest httpRequest) {
		final String authHeader = httpRequest.getHeader("Authorization");
		final String jwt = authHeader.substring(7);
		final String userEmail = jwtService.extractUsername(jwt);
		User user = userRepository.findByEmail(userEmail).get();
		return user;
	}

	@Transactional
	public boolean deleteUser(User currentUser) {
		try {
			User user = userRepository.findByEmail(currentUser.getEmail())
					.orElseThrow(() -> new UserNotFoundException());

			if (!user.getLabelSet().isEmpty()) {
				user.getLabelSet().forEach(label -> specificNoteRepository.deleteLabelsFromLabelNote(label.getId()));
			}
			labelRepository.deleteAllByUser(user);

			user.getNoteList().forEach(note -> {
				if (!note.getCollaboratorList().isEmpty()) {
					List<User> userList = note.getCollaboratorList().stream().filter(item -> !item.equals(user))
							.toList();
					note.setCollaboratorList(userList);
				}
				if (!note.getSpecificNoteList().isEmpty()) {
					List<SpecificNote> noteList = note.getSpecificNoteList().stream()
							.filter(item -> !item.getUser().equals(user)).toList();
					note.setSpecificNoteList(noteList);
				}
				noteRepository.save(note);
			});

			user.getLabelSet().clear();
			user.setStatus(Status.INACTIVE);
			userRepository.save(user);
			return true;
		} catch (Exception exp) {
			// TODO Auto-generated catch block
			log.error(NoteUtils.ERROR_DELETING_USER, new UserNotDeletedException(exp.getMessage()));
			throw new UserNotDeletedException(exp.getMessage());
		}
	}

	public UserResponseDto getUserDtoFromUser(User user) {
		UserResponseDto userDto = new UserResponseDto();
		userDto.setEmail(user.getEmail());
		userDto.setFullName(user.getFullName());
		userDto.setProfilePicture(user.getProfilePicture());
		userDto.setStatus(user.getStatus());
		userDto.setCreatedAt(user.getCreatedAt());
		userDto.setUpdatedAt(user.getUpdatedAt());
		return userDto;
	}

}
