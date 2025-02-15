package com.scribblemate.entities;

import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scribblemate.utility.Utils.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "specific_note", indexes = { @Index(name = "index_user", columnList = "userId"),
		@Index(name = "index_common_note", columnList = "commonNoteId") })
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SpecificNote extends CommonFields {

	@Column(name = "color")
	private String color;

	@Column(name = "is_pinned")
	@JsonProperty(value = "isPinned")
	private boolean isPinned;

	@Column(name = "is_archived")
	@JsonProperty(value = "isArchived")
	private boolean isArchived;

	@Column(name = "is_trashed")
	@JsonProperty(value = "isTrashed")
	private boolean isTrashed;

	@Column(name = "reminder")
	private LocalDateTime reminder;

	@Enumerated(EnumType.STRING)
	@Column(name = "role")
	private Role role;

	@ManyToOne
	private User user;

	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@JoinTable(name = "note_label", joinColumns = { @JoinColumn(name = "note_id") }, inverseJoinColumns = {
			@JoinColumn(name = "label_id") })
	private Set<Label> labelSet;

	@ManyToOne
	private Note commonNote;

}
