package br.edu.ulbra.election.election.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.edu.ulbra.election.election.output.v1.VoterOutput;

@Service
public class LoginClientService {
	private final LoginClientService.LoginClient loginClient;

	@Autowired
	public LoginClientService(LoginClientService.LoginClient loginClient) {
		this.loginClient = loginClient;
	}

	public VoterOutput checkToken(String token) {
		return this.loginClient.checkToken(token);
	}

	@FeignClient(value = "login-service", url = "${url.login-service}")
	private interface LoginClient {

		@GetMapping("/v1/check/{token}")
		VoterOutput checkToken(@PathVariable(name = "token") String token);
	}
}