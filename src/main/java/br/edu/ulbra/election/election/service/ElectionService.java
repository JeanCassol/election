package br.edu.ulbra.election.election.service;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.input.v1.ElectionInput;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.model.Estados;
import br.edu.ulbra.election.election.output.v1.ElectionOutput;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.repository.ElectionRepository;

@Service
public class ElectionService {
	private final ElectionRepository electionRepository;

	private final ModelMapper modelMapper;

	private static final String MESSAGE_INVALID_ID = "Invalid id";
	private static final String MESSAGE_ELECTION_NOT_FOUND = "Election not found";
	private static final String MESSAGE_ELECTION_YEAR ="Year of election must be greater than or equal to 2000 and less than 2200";
	private static final String MESSAGE_ELECTION_STATECODE = "The state code must correspond to a valid state code in Brazil (or BR for national election)";
	@Autowired
	public ElectionService(ElectionRepository electionRepository, ModelMapper modelMapper) {
		this.electionRepository = electionRepository;
		this.modelMapper = modelMapper;
	}

	public List<ElectionOutput> getAll() {
		Type electionOutputListType = new TypeToken<List<ElectionOutput>>() {
		}.getType();
		return modelMapper.map(electionRepository.findAll(), electionOutputListType);
	}

	public ElectionOutput create(ElectionInput electionInput) {
		validateInput(electionInput, false);
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
		validateInput(electionInput, true);

		Election election = electionRepository.findById(electionId).orElse(null);
		if (election == null) {
			throw new GenericOutputException(MESSAGE_ELECTION_NOT_FOUND);
		}

		election.setDescription(electionInput.getDescription());
		election.setStateCode(electionInput.getStateCode());
		election.setYear(electionInput.getYear());

		election = electionRepository.save(election);
		return modelMapper.map(election, ElectionOutput.class);
	}

	public GenericOutput delete(Long electionId) {
		if (electionId == null) {
			throw new GenericOutputException(MESSAGE_INVALID_ID);
		}

		Election election = electionRepository.findById(electionId).orElse(null);
		if (election == null) {
			throw new GenericOutputException(MESSAGE_ELECTION_NOT_FOUND);
		}

		electionRepository.delete(election);

		return new GenericOutput("Election deleted");
	}

	public List<ElectionOutput> findByYear(Integer year) {
		Type electionOutputListType = new TypeToken<List<ElectionOutput>>() {
		}.getType();
		List<Election> election = electionRepository.findByYear(year);
		if (election == null) {
			throw new GenericOutputException(MESSAGE_ELECTION_NOT_FOUND);
		}

		return modelMapper.map(election, electionOutputListType);
	}

	private void validateInput(ElectionInput electionInput, boolean isUpdate) {
		if (StringUtils.isBlank(electionInput.getDescription())) {
			throw new GenericOutputException("Invalid description");
		}

		if (electionInput.getYear() == 0) {
			throw new GenericOutputException("Invalid Year");
		}

		if (StringUtils.isBlank(electionInput.getStateCode())) {
			throw new GenericOutputException("Invalid State Code");
		}
		
		if (electionInput.getYear() < 2000 || electionInput.getYear() > 2200){
            throw new GenericOutputException(MESSAGE_ELECTION_YEAR);
        }
		
		if (asEstados(electionInput.getStateCode()) == null){
            throw new GenericOutputException(MESSAGE_ELECTION_STATECODE);
        }

	}
	
	public Estados asEstados(String str) {
	    for (Estados me : Estados.values()) {
	        if (me.getCodigo().equalsIgnoreCase(str))
	            return me;
	    }
	    return null;
	}
}
