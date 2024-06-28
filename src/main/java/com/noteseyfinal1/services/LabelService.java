package com.noteseyfinal1.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.noteseyfinal1.dto.LabelDto;
import com.noteseyfinal1.entities.Label;
import com.noteseyfinal1.entities.User;
import com.noteseyfinal1.exceptions.labels.LabelNotDeletedException;
import com.noteseyfinal1.exceptions.labels.LabelNotPersistedException;
import com.noteseyfinal1.exceptions.labels.LabelNotUpdatedException;
import com.noteseyfinal1.exceptions.labels.LabelsNotFoundException;
import com.noteseyfinal1.exceptions.users.UserNotFoundException;
import com.noteseyfinal1.repositories.LabelRepository;
import com.noteseyfinal1.repositories.SpecificNoteRepository;
import com.noteseyfinal1.repositories.UserRepository;
import com.noteseyfinal1.utility.LabelUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LabelService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LabelRepository labelRepository;

	@Autowired
	private SpecificNoteRepository specificNoteRepository;

	@Transactional
	public LabelDto createNewLabel(LabelDto labelDto, User currentUser) {
		User user = userRepository.findByEmail(currentUser.getEmail()).orElseThrow(() -> new UserNotFoundException());
		try {
			Label label = new Label();
			label.setLabelName(labelDto.getLabelName());
			label.setUser(user);
			Set<Label> labelSet = user.getLabelSet();
			if (labelSet == null) {
				labelSet = new HashSet<Label>();
				labelSet.add(label);
				user.setLabelSet(labelSet);
			} else {
				labelSet.add(label);
			}
			Label savedLabel = labelRepository.save(label);
			log.info(LabelUtils.LABEL_PERSIST_SUCCESS, savedLabel);
			return getLabelDtoFromLabel(savedLabel);
		} catch (Exception ex) {
			log.error(LabelUtils.LABEL_PERSIST_ERROR, new LabelNotPersistedException(ex.getMessage()));
			throw new LabelNotPersistedException(ex.getMessage());
		}
	}

	@Transactional
	public boolean deleteLabel(int labelId, User currentUser) {
		User user = userRepository.findByEmail(currentUser.getEmail()).orElseThrow(() -> new UserNotFoundException());
		try {
			specificNoteRepository.deleteLabelsFromLabelNote(labelId);
			labelRepository.deleteByIdAndUser(labelId, user.getId());
			log.info(LabelUtils.LABEL_DELETE_SUCCESS, labelId);
			return true;
		} catch (Exception ex) {
			log.error(LabelUtils.LABEL_DELETE_ERROR, labelId, new LabelNotDeletedException(ex.getMessage()));
			throw new LabelNotDeletedException(ex.getMessage());
		}
	}

	@Transactional
	public LabelDto editLabel(LabelDto labelDto, User currentUser) {
		User user = userRepository.findByEmail(currentUser.getEmail()).orElseThrow(() -> new UserNotFoundException());
		try {
			Label label = labelRepository.findByIdAndUser(labelDto.getId(), user);
			label.setLabelName(labelDto.getLabelName());
			Label savedLabel = labelRepository.save(label);
			log.info(LabelUtils.LABEL_UPDATE_SUCCESS, savedLabel.getId());
			return getLabelDtoFromLabel(savedLabel);
		} catch (Exception ex) {
			log.error(LabelUtils.LABEL_UPDATE_ERROR, labelDto.getId(), new LabelNotUpdatedException(ex.getMessage()));
			throw new LabelNotUpdatedException(ex.getMessage());
		}
	}

	@Transactional
	public List<LabelDto> getLabelsByUser(User currentUser) {
		User user = userRepository.findByEmail(currentUser.getEmail()).orElseThrow(() -> new UserNotFoundException());
		try {
			List<Label> labelList = labelRepository.findAllByUser(user);
			List<LabelDto> labelDtoList = labelList.stream().map(label -> {
				return getLabelDtoFromLabel(label);
			}).toList();
			log.info(LabelUtils.LABEL_FETCH_SUCCESS, user.getId());
			return labelDtoList;
		} catch (Exception ex) {
			log.error(LabelUtils.LABEL_FETCH_ERROR, user.getId(), new LabelsNotFoundException(ex.getMessage()));
			throw new LabelsNotFoundException(ex.getMessage());
		}
	}

	private LabelDto getLabelDtoFromLabel(Label savedLabel) {
		LabelDto labelDto = new LabelDto();
		labelDto.setId(savedLabel.getId());
		labelDto.setLabelName(savedLabel.getLabelName());
		return labelDto;
	}

}
