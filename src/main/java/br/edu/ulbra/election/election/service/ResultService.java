package br.edu.ulbra.election.election.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ulbra.election.election.client.CandidateClientService;
import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.output.v1.CandidateOutput;
import br.edu.ulbra.election.election.output.v1.ElectionCandidateResultOutput;
import br.edu.ulbra.election.election.output.v1.ElectionOutput;
import br.edu.ulbra.election.election.output.v1.ResultOutput;
import br.edu.ulbra.election.election.repository.ElectionRepository;
import br.edu.ulbra.election.election.repository.VoteRepository;

@Service
public class ResultService {

	private final ModelMapper modelMapper;
	private final VoteRepository voteRepository;
	private final ElectionRepository electionRepository;
	private final CandidateClientService candidateClientService;

	private static final String MESSAGE_ELECTION_NOT_FOUND = "Election not found";
	private static final String MESSAGE_CANDIDAT_NOT_FOUND = "Candidate not found";

	@Autowired
	public ResultService(ModelMapper modelMapper, VoteRepository voteRepository, ElectionRepository electionRepository,
			CandidateClientService candidateClientService) {
		this.modelMapper = modelMapper;
		this.voteRepository = voteRepository;
		this.electionRepository = electionRepository;
		this.candidateClientService = candidateClientService;
	}

	public ResultOutput getResultByElection(Long electionId) {
		if (electionId == null) {
			throw new GenericOutputException(MESSAGE_ELECTION_NOT_FOUND);
		}

		Election election = electionRepository.findById(electionId).orElse(null);
		if (election == null) {
			throw new GenericOutputException("Invalid Election");
		}
		ResultOutput resultOutput = new ResultOutput();
		ElectionOutput electionOutput = modelMapper.map(election, ElectionOutput.class);
		resultOutput.setElection(electionOutput);
		candidateClientService.getByIdElection(electionId);

		List<ElectionCandidateResultOutput> candidates = new ArrayList<ElectionCandidateResultOutput>();

		List<CandidateOutput> candidateOutputs = candidateClientService.getAllByIdElection(electionId);
		for (CandidateOutput candidateOutput : candidateOutputs) {
			candidates.add(getResultByCandidate(candidateOutput.getId()));
		}
		resultOutput.setCandidates(candidates);
		Long blankVotes = voteRepository.countByElection_IdAndBlankVote(electionId, true);
		resultOutput.setBlankVotes(blankVotes);
		Long nullVotes = voteRepository.countByElection_IdAndNullVote(electionId, true);
		resultOutput.setNullVotes(nullVotes);
		Long totalVotes = voteRepository.countByElection_Id(electionId);
		resultOutput.setTotalVotes(totalVotes);

		return resultOutput;
	}

	public ElectionCandidateResultOutput getResultByCandidate(Long candidateId) {
		if (candidateId == null) {
			throw new GenericOutputException(MESSAGE_CANDIDAT_NOT_FOUND);
		}

		CandidateOutput candidateOutput = candidateClientService.getById(candidateId);
		if (candidateOutput == null) {
			throw new GenericOutputException(MESSAGE_CANDIDAT_NOT_FOUND);
		}
		ElectionCandidateResultOutput electionCandidateResultOutput = new ElectionCandidateResultOutput();
		electionCandidateResultOutput.setCandidate(candidateOutput);
		Long totalVotes = voteRepository.countByCandidateId(candidateId);
		electionCandidateResultOutput.setTotalVotes(totalVotes);
		return electionCandidateResultOutput;
	}
}
