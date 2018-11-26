package br.edu.ulbra.election.election.service;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ulbra.election.election.client.CandidateClientService;
import br.edu.ulbra.election.election.enums.StateCodes;
import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.input.v1.ElectionInput;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.model.Vote;
import br.edu.ulbra.election.election.output.v1.CandidateOutput;
import br.edu.ulbra.election.election.output.v1.ElectionOutput;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.repository.ElectionRepository;
import br.edu.ulbra.election.election.repository.VoteRepository;

@Service
public class ElectionService {

	private final ElectionRepository electionRepository;
	private final VoteRepository voteRepository;
	private final ModelMapper modelMapper;
	private final CandidateClientService candidateClientService;

	@Autowired
	public ElectionService(ElectionRepository electionRepository, ModelMapper modelMapper,
			VoteRepository voteRepository, CandidateClientService candidateClientService) {
		this.electionRepository = electionRepository;
		this.modelMapper = modelMapper;
		this.voteRepository = voteRepository;
		this.candidateClientService = candidateClientService;
	}

	private static final String MESSAGE_INVALID_ID = "Invalid id";
	private static final String MESSAGE_ELECTION_NOT_FOUND = "Election not found";

	public List<ElectionOutput> getAll() {
		Type electionOutputListType = new TypeToken<List<ElectionOutput>>() {
		}.getType();
		return modelMapper.map(electionRepository.findAll(), electionOutputListType);
	}

	public ElectionOutput create(ElectionInput electionInput) {
		validateInput(electionInput);
		validateDuplicate(electionInput, null);
		Election election = modelMapper.map(electionInput, Election.class);
		election = electionRepository.save(election);
		return modelMapper.map(election, ElectionOutput.class);
	}

	public ElectionOutput getById(Long electionId) {
		if (electionId == null) {
			throw new GenericOutputException(MESSAGE_INVALID_ID);
		}

		Election election = electionRepository.findById(electionId).orElse(null);
		if (election == null) {
			throw new GenericOutputException(MESSAGE_ELECTION_NOT_FOUND);
		}

		return modelMapper.map(election, ElectionOutput.class);
	}

	public ElectionOutput update(Long electionId, ElectionInput electionInput) {
		if (electionId == null) {
			throw new GenericOutputException(MESSAGE_INVALID_ID);
		}
		validateInput(electionInput);
		validateDuplicate(electionInput, electionId);
		validateReference(electionId);

		Election election = electionRepository.findById(electionId).orElse(null);
		if (election == null) {
			throw new GenericOutputException(MESSAGE_ELECTION_NOT_FOUND);
		}

		Vote vote = voteRepository.findFirstByElection_Id(electionId).orElse(null);
		if (vote != null) {
			throw new GenericOutputException("Election with votes");
		}

		election.setStateCode(electionInput.getStateCode());
		election.setDescription(electionInput.getDescription());
		election.setYear(electionInput.getYear());
		election = electionRepository.save(election);
		return modelMapper.map(election, ElectionOutput.class);
	}

	public GenericOutput delete(Long electionId) {
		if (electionId == null) {
			throw new GenericOutputException(MESSAGE_INVALID_ID);
		}
		validateReference(electionId);
		
		Election election = electionRepository.findById(electionId).orElse(null);
		if (election == null) {
			throw new GenericOutputException(MESSAGE_ELECTION_NOT_FOUND);
		}
		
		
		electionRepository.delete(election);

		return new GenericOutput("Election deleted");
	}

	private void validateReference(Long electionId) {
		Vote vote = voteRepository.findFirstByElection_Id(electionId).orElse(null);
		if (vote != null) {
			throw new GenericOutputException("Election with votes");
		}

		CandidateOutput candidateOutput = candidateClientService.getByIdElection(electionId);
		if (candidateOutput != null) {
			throw new GenericOutputException("Election with candidates");
		}
	}
	
	private void validateDuplicate(ElectionInput electionInput, Long id) {
		Election election = electionRepository.findFirstByYearAndStateCodeAndDescription(electionInput.getYear(),
				electionInput.getStateCode(), electionInput.getDescription());
		if (election != null && !election.getId().equals(id)) {
			throw new GenericOutputException("Duplicate Code");
		}
	}

	private void validateInput(ElectionInput electionInput) {
		if (StringUtils.isBlank(electionInput.getDescription()) || electionInput.getDescription().length() < 5) {
			throw new GenericOutputException("Invalid Description");
		}
		if (StringUtils.isBlank(electionInput.getStateCode())) {
			throw new GenericOutputException("Invalid State Code");
		}
		try {
			StateCodes.valueOf(electionInput.getStateCode());
		} catch (IllegalArgumentException e) {
			throw new GenericOutputException("Invalid State Code");
		}
		if (electionInput.getYear() == null || electionInput.getYear() < 2000 || electionInput.getYear() > 2200) {
			throw new GenericOutputException("Invalid Year");
		}
	}

}
