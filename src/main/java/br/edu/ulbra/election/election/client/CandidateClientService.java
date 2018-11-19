package br.edu.ulbra.election.election.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.edu.ulbra.election.election.output.v1.CandidateOutput;

@Service
public class CandidateClientService {
	private final CandidateClientService.CandidateClient candidateClient;

	@Autowired
	public CandidateClientService(CandidateClientService.CandidateClient candidateClient) {
		this.candidateClient = candidateClient;
	}

	public CandidateOutput getById(Long id) {
		return this.candidateClient.getById(id);
	}

	@FeignClient(value = "candidate-service", url = "${url.candidate-service}")
	private interface CandidateClient {

		@GetMapping("/v1/candidate/{candidateId}")
		CandidateOutput getById(@PathVariable(name = "candidateId") Long candidateId);
		
		@GetMapping("/v1/candidate/{candidateId}")
		CandidateOutput getByNumber(@PathVariable(name = "numberElection") String numberElection);
	}
}
