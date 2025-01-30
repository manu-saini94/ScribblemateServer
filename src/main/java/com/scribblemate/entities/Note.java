package com.scribblemate.entities;

import java.util.List;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "note", indexes = { @Index(name = "index_note_created_updated", columnList = "createdAt, updatedAt") })
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Note extends CommonFields {

	@Column(name = "title", length = 1000)
	private String title;

	@Column(name = "content")
	@Lob
	private String content;

	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> images;

	@ManyToMany(mappedBy = "noteList", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<User> collaboratorList;

	@OneToMany(mappedBy = "commonNote", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private List<SpecificNote> specificNoteList;

	@OneToMany(mappedBy = "commonNote", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private List<ListItems> listItems;

	@ManyToOne(fetch = FetchType.LAZY)
	private User updatedBy;

	@ManyToOne(fetch = FetchType.LAZY)
	private User createdBy;

}
