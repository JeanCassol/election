package br.edu.ulbra.election.election.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import br.edu.ulbra.election.election.model.Vote;

public interface VoteRepository extends CrudRepository<Vote, Long> {
	
	Optional<Vote> findByElection_Id(Long Id);
}
