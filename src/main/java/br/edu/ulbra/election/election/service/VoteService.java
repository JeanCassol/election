package br.edu.ulbra.election.election.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.model.Vote;
import br.edu.ulbra.election.election.output.v1.ElectionOutput;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.repository.VoteRepository;

@Service
public class VoteService {
	private final VoteRepository voteRepository;

	private final ModelMapper modelMapper;

	@Autowired
	public VoteService(VoteRepository voteRepository, ModelMapper modelMapper) {
		this.voteRepository = voteRepository;
		this.modelMapper = modelMapper;
	}

	public List<GenericOutput> createMultiple(List<VoteInput> voteInput) {
		List<GenericOutput> ElectionOutputList = new ArrayList<GenericOutput>();
		for (VoteInput vote : voteInput) {
			ElectionOutputList.add(create(vote));
		}

		return ElectionOutputList;
	}

	public GenericOutput create(VoteInput voteInput) {
		validateInput(voteInput, false);
		Vote vote = modelMapper.map(voteInput, Vote.class);
		vote = voteRepository.save(vote);
		return new GenericOutput("voted");
	}

	private void validateInput(VoteInput voteInput, boolean isUpdate) {
		//TODO - Não permitir mais de uma vez o voto
	}

}
