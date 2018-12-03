package br.edu.ulbra.election.election.api.v1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.output.v1.VoteOutput;
import br.edu.ulbra.election.election.service.VoteService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1/vote")
public class VoteApi {

	private final VoteService voteService;

	@Autowired
	public VoteApi(VoteService voteService) {
		this.voteService = voteService;
	}

	@GetMapping("/election/{electionId}")
	@ApiOperation(value = "Get election by Id")
	public VoteOutput getByElectionId(@PathVariable Long electionId) {
		return voteService.getByElectionId(electionId);
	}

	@GetMapping("/voter/{voterId}")
	@ApiOperation(value = "Get voter by Id")
	public VoteOutput getByVoterId(@PathVariable Long voterId) {
		VoteOutput output = voteService.getByVoterId(voterId);
		return output;
	}

	@PutMapping("/")
	public GenericOutput electionVote(@RequestHeader(value = "x-token") String token,
			@RequestBody VoteInput voteInput) {
		return voteService.electionVote(token, voteInput);
	}

	@PutMapping("/multiple")
	public GenericOutput multipleElectionVote(@RequestHeader(value = "x-token") String token,
			@RequestBody List<VoteInput> voteInputList) {
		return voteService.multiple(token, voteInputList);
	}
}
