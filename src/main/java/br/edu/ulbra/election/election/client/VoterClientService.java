package br.edu.ulbra.election.election.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.edu.ulbra.election.election.output.v1.VoterOutput;

@Service
public class VoterClientService {

	private final VoterClientService.VoterClient voterClient;

	@Autowired
	public VoterClientService(VoterClientService.VoterClient voterClient) {
		this.voterClient = voterClient;
	}

	public VoterOutput getById(Long id) {
		return this.voterClient.getById(id);
	}

	@FeignClient(value = "voter-service", url = "${url.voter-service}")
	private interface VoterClient {

		@GetMapping("/v1/voter/{voterId}")
		VoterOutput getById(@PathVariable(name = "voterId") Long candidateId);
	}
}
