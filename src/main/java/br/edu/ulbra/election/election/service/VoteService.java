package br.edu.ulbra.election.election.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ulbra.election.election.client.CandidateClientService;
import br.edu.ulbra.election.election.client.VoterClientService;
import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.model.Vote;
import br.edu.ulbra.election.election.output.v1.CandidateOutput;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.output.v1.VoteOutput;
import br.edu.ulbra.election.election.output.v1.VoterOutput;
import br.edu.ulbra.election.election.repository.ElectionRepository;
import br.edu.ulbra.election.election.repository.VoteRepository;
import feign.FeignException;

@Service
public class VoteService {

	private final ModelMapper modelMapper;
	private final VoteRepository voteRepository;
	private final ElectionRepository electionRepository;
	private final CandidateClientService candidateClientService;
	private final VoterClientService voterClientService;

	@Autowired
	public VoteService(ModelMapper modelMapper, VoteRepository voteRepository, ElectionRepository electionRepository,
			CandidateClientService candidateClientService, VoterClientService voterClientService) {
		this.modelMapper = modelMapper;
		this.voteRepository = voteRepository;
		this.electionRepository = electionRepository;
		this.candidateClientService = candidateClientService;
		this.voterClientService = voterClientService;
	}

	private static final String MESSAGE_INVALID_ID = "Invalid id";
	// private static final String MESSAGE_ELECTION_NOT_FOUND = "Election not
	// found";
	// private static final String MESSAGE_VOTER_NOT_FOUND = "Voter not found";

	public VoteOutput getByElectionId(Long electionId) {
		if (electionId == null) {
			throw new GenericOutputException(MESSAGE_INVALID_ID);
		}

		Vote vote = voteRepository.findFirstByElection_Id(electionId).orElse(null);
		if (vote == null) {
			vote = new Vote();
			vote.setVoterId(0L);
		}

		return modelMapper.map(vote, VoteOutput.class);
	}

	public VoteOutput getByVoterId(Long voterId) {
		if (voterId == null) {
			throw new GenericOutputException(MESSAGE_INVALID_ID);
		}

		Vote vote = voteRepository.findFirstByVoterId(voterId);
		if (vote == null) {
			vote = new Vote();
			vote.setVoterId(0L);
		}

		VoteOutput ret = modelMapper.map(vote, VoteOutput.class);
		return ret;
	}

	public GenericOutput electionVote(VoteInput voteInput) {

		Election election = validateInput(voteInput.getElectionId(), voteInput);
		Vote vote = new Vote();
		vote.setElection(election);
		vote.setVoterId(voteInput.getVoterId());

		if (voteInput.getCandidateNumber() == null) {
			vote.setBlankVote(true);
		} else {
			vote.setBlankVote(false);
			vote.setCandidateId(voteInput.getCandidateId());
		}

		// TODO: Validate null candidate
		if (!vote.getBlankVote()) {
			try {

				CandidateOutput candidateOutput = candidateClientService.getById(voteInput.getCandidateId());

				if (candidateOutput == null) {
					vote.setNullVote(true);
					vote.setCandidateId(null);
				} else
					vote.setNullVote(false);

			} catch (FeignException e) {
				if (e.status() == 500) {
					throw new GenericOutputException("Invalid Candidate");
				}
			}
		} else
			vote.setNullVote(false);

		voteRepository.save(vote);

		return new GenericOutput("OK");
	}

	public GenericOutput multiple(List<VoteInput> voteInputList) {
		for (VoteInput voteInput : voteInputList) {
			this.electionVote(voteInput);
		}
		return new GenericOutput("OK");
	}

	public Election validateInput(Long electionId, VoteInput voteInput) {
		Election election = electionRepository.findById(electionId).orElse(null);
		if (election == null) {
			throw new GenericOutputException("Invalid Election");
		}
		if (voteInput.getVoterId() == null) {
			throw new GenericOutputException("Invalid Voter");
		}
		// TODO: Validate voter
		Vote vote = voteRepository.findFirstByVoterId(voteInput.getVoterId());
		if (vote != null) {
			throw new GenericOutputException("Elector already voted");
		}

		VoterOutput voterOutput = voterClientService.getById(voteInput.getVoterId());
		if (voterOutput == null) {
			throw new GenericOutputException("Invalid Voter");
		}
		return election;
	}
}
