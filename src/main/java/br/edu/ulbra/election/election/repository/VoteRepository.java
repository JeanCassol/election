package br.edu.ulbra.election.election.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import br.edu.ulbra.election.election.model.Vote;

public interface VoteRepository extends CrudRepository<Vote, Long> {
	
	Optional<Vote> findFirstByElection_Id(Long electionId);
	Vote findFirstByVoterId(Long voterId);
	Long countByCandidateId(Long candidateId);
	Long countByElection_IdAndBlankVote(Long electionId, Boolean blankVote);
	Long countByElection_IdAndNullVote(Long electionId, Boolean nullVote);
	Long countByElection_Id(Long electionId);
}
