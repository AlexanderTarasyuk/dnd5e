package com.dnd5e.wiki.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dnd5e.wiki.model.creature.Creature;

@Repository
public abstract interface CreatureRepository extends JpaRepository<Creature, Integer>, JpaSpecificationExecutor<Creature> {

	  @Query("SELECT c FROM Creature c WHERE c.name LIKE %:searchTerm% OR c.englishName LIKE %:searchTerm%")
	List<Creature> findByNameAndEnglishNameContaining(@Param("searchTerm") String search);

	List<Creature> findAllByRaceIdOrderByExpAsc(Integer id);

	Creature findByName(String name);
}