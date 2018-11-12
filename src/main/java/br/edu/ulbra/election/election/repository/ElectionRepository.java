package br.edu.ulbra.election.election.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.output.v1.ElectionOutput;

public interface ElectionRepository extends CrudRepository<Election, Long> {

	List<Election> findByYear(Integer year);
}
