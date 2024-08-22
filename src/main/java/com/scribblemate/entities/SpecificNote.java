package com.scribblemate.entities;

import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.UpdateTimestamp;

import com.scribblemate.utility.Utils.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
@Table(name = "specific_note")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SpecificNote extends CommonFields {

	@Column(name = "color")
	private String color;

	@Column(name = "is_pinned")
	private boolean isPinned;

	@Column(name = "is_archived")
	private boolean isArchived;

	@Column(name = "is_trashed")
	private boolean isTrashed;

	@Column(name = "reminder")
	private LocalDateTime reminder;

	@Enumerated(EnumType.STRING)
	@Column(name = "role")
	private Role role;

	@ManyToOne(fetch = FetchType.EAGER)
	private User user;

	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@JoinTable(name = "note_label", joinColumns = { @JoinColumn(name = "note_id") }, inverseJoinColumns = {
			@JoinColumn(name = "label_id") })
	private Set<Label> labelSet;

	@ManyToOne
	private Note commonNote;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

}
