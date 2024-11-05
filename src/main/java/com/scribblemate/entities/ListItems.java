package com.scribblemate.entities;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "list_items")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ListItems extends CommonFields {

	@Column(length = Integer.MAX_VALUE)
	private String content;

	@ManyToOne
	private Note commonNote;

	@Column(name = "is_done")
	@JsonProperty(value = "is_done")
	private boolean isDone;

	@Column(nullable = false)
	private int orderIndex;

}
