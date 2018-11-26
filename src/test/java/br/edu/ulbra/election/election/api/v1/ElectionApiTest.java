package br.edu.ulbra.election.election.api.v1;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;

import br.edu.ulbra.election.election.TestConfig;
import br.edu.ulbra.election.election.builder.ElectionBuilder;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.service.ElectionService;

@RunWith(SpringRunner.class)
@WebMvcTest(ElectionApi.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
public class ElectionApiTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ElectionService electionService;

	private final Gson gson = new Gson();
	private String URL_BASE = "/v1/candidate/";

	@Test
	public void getAll() throws Exception {
		given(electionService.getAll()).willReturn(ElectionBuilder.getElectionOutputList());

		mockMvc.perform(get(URL_BASE)).andExpect(status().isOk()).andExpect(jsonPath("$[0].id", equalTo(1)))
				.andExpect(jsonPath("$[0].name", equalTo("Candidate Name")))
				.andExpect(jsonPath("$[0].numberelection", equalTo(99L)));
	}

	@Test
	public void getOne() throws Exception {
		given(electionService.getById(anyLong())).willReturn(ElectionBuilder.getElectionOutput());
		mockMvc.perform(get(URL_BASE + "1")).andExpect(status().isOk()).andExpect(jsonPath("$.id", equalTo(1)))
				.andExpect(jsonPath("$.name", equalTo("Candidate Name")))
				.andExpect(jsonPath("$.numberelection", equalTo(99L)));
	}

	@Test
	public void create() throws Exception {
		given(electionService.create(any())).willReturn(ElectionBuilder.getElectionOutput());

		mockMvc.perform(post(URL_BASE).contentType(MediaType.APPLICATION_JSON)
				.content(gson.toJson(ElectionBuilder.getElectionInput()))).andExpect(status().isOk())
				.andExpect(jsonPath("$.id", equalTo(1))).andExpect(jsonPath("$.name", equalTo("Candidate Name")))
				.andExpect(jsonPath("$.numberelection", equalTo(99L)));
	}

	@Test
	public void update() throws Exception {
		given(electionService.update(anyLong(), any())).willReturn(ElectionBuilder.getElectionOutput());

		mockMvc.perform(put(URL_BASE + "1").contentType(MediaType.APPLICATION_JSON)
				.content(gson.toJson(ElectionBuilder.getElectionInput()))).andExpect(status().isOk())
				.andExpect(jsonPath("$.id", equalTo(1))).andExpect(jsonPath("$.name", equalTo("Candidate Name")))
				.andExpect(jsonPath("$.numberelection", equalTo(99L)));
	}

	@Test
	public void deleteVoter() throws Exception {
		given(electionService.delete(anyLong())).willReturn(new GenericOutput("OK"));

		mockMvc.perform(delete(URL_BASE + "1").contentType(MediaType.APPLICATION_JSON)
				.content(gson.toJson(ElectionBuilder.getElectionInput()))).andExpect(status().isOk())
				.andExpect(jsonPath("$.message", equalTo("OK")));
	}

}
