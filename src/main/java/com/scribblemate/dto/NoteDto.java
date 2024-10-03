package com.scribblemate.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scribblemate.entities.User;

public class NoteDto {

	private Integer id;

	private String title;

	private String content;

	private List<String> images;

	private boolean isTrashed;

	private boolean isArchived;

	private boolean isPinned;

	private String color;

	private LocalDateTime reminder;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private Set<LabelDto> labelSet;

	private List<CollaboratorDto> collaboratorList;

	@JsonIgnore
	private User user;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public boolean isTrashed() {
		return isTrashed;
	}

	public void setTrashed(boolean isTrashed) {
		this.isTrashed = isTrashed;
	}

	public boolean isArchived() {
		return isArchived;
	}

	public void setArchived(boolean isArchived) {
		this.isArchived = isArchived;
	}

	public boolean isPinned() {
		return isPinned;
	}

	public void setPinned(boolean isPinned) {
		this.isPinned = isPinned;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public LocalDateTime getReminder() {
		return reminder;
	}

	public void setReminder(LocalDateTime reminder) {
		this.reminder = reminder;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<CollaboratorDto> getCollaboratorList() {
		return collaboratorList;
	}

	public void setCollaboratorList(List<CollaboratorDto> collaboratorList) {
		this.collaboratorList = collaboratorList;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<LabelDto> getLabelSet() {
		return labelSet;
	}

	public void setLabelSet(Set<LabelDto> labelSet) {
		this.labelSet = labelSet;
	}

	public NoteDto() {
		super();
	}

	public NoteDto(Integer id, String title, String content, List<String> images, boolean isTrashed, boolean isArchived,
			boolean isPinned, String color, LocalDateTime reminder, LocalDateTime createdAt, LocalDateTime updatedAt,
			Set<LabelDto> labelSet, List<CollaboratorDto> collaboratorList, User user) {
		super();
		this.id = id;
		this.title = title;
		this.content = content;
		this.images = images;
		this.isTrashed = isTrashed;
		this.isArchived = isArchived;
		this.isPinned = isPinned;
		this.color = color;
		this.reminder = reminder;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.labelSet = labelSet;
		this.collaboratorList = collaboratorList;
		this.user = user;
	}

}
