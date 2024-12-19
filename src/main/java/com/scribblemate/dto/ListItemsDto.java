package com.scribblemate.dto;

import java.time.LocalDateTime;

public class ListItemsDto {

	private Long id;

	private String content;

	private boolean isDone;

	private Integer orderIndex;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isDone() {
		return isDone;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
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

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	public ListItemsDto() {
		super();
	}

	public ListItemsDto(Long id, String content, boolean isDone, Integer orderIndex, LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.content = content;
		this.isDone = isDone;
		this.orderIndex = orderIndex;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "ListItemsDto [id=" + id + ", content=" + content + ", isDone=" + isDone + ", orderIndex=" + orderIndex
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}

}
