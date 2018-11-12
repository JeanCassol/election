package br.edu.ulbra.election.election.repository;

import org.springframework.data.repository.CrudRepository;

import br.edu.ulbra.election.election.model.Vote;

public interface VoteRepository extends CrudRepository<Vote, Long> {

}
