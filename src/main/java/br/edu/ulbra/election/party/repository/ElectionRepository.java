package br.edu.ulbra.election.party.repository;

import org.springframework.data.repository.CrudRepository;
import br.edu.ulbra.election.party.model.Election;

public interface ElectionRepository extends CrudRepository<Election, Long> {
}
