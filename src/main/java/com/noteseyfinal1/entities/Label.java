package com.noteseyfinal1.entities;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "label")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Label extends CommonFields {

	@Column(name = "label_name")
	private String labelName;

	@ManyToMany(mappedBy = "labelSet", fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REMOVE })
	private List<SpecificNote> noteList;

	@ManyToOne(fetch = FetchType.EAGER)
	private User user;

	@Override
	public int hashCode() {
		return Objects.hash(labelName, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Label other = (Label) obj;
		return Objects.equals(labelName, other.labelName) && Objects.equals(user, other.user);
	}

}
