package com.noteseyfinal1.entities;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "note")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Note extends CommonFields {

	@Column(name = "title")
	private String title;

	@Column(name = "content", length = 10000)
	private String content;

	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> images;

	@ManyToMany(mappedBy = "noteList", fetch = FetchType.EAGER)
	private List<User> collaboratorList;

	@OneToMany(mappedBy = "commonNote", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<SpecificNote> specificNoteList;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@OneToOne
	private User updatedBy;

	@OneToOne
	private User createdBy;

}
