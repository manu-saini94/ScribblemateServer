package com.scribblemate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scribblemate.entities.ListItems;

@Repository
public interface ListItemsRepository extends JpaRepository<ListItems, Integer> {

}
