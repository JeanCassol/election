package br.edu.ulbra.election.election.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.edu.ulbra.election.election.TestConfig;
import br.edu.ulbra.election.election.builder.ElectionBuilder;
import br.edu.ulbra.election.election.input.v1.ElectionInput;
import br.edu.ulbra.election.election.output.v1.ElectionOutput;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.repository.ElectionRepository;

@RunWith(SpringRunner.class)
@WebMvcTest(ElectionService.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
public class ElectionServiceTest {
	@MockBean
	private ElectionRepository electionRepository;

	@Autowired
	private ElectionService electionService;

	@Test
	public void shouldReturnEmptyList() {
		given(electionRepository.findAll()).willReturn(new ArrayList<>());
		List<ElectionOutput> electionOutputList = electionService.getAll();
		Assert.assertEquals(0, electionOutputList.size());
	}

	@Test
	public void shouldFindAllElements() {
		given(electionRepository.findAll()).willReturn(ElectionBuilder.getElectionList());
		List<ElectionOutput> electionOutputList = electionService.getAll();
		Assert.assertEquals(1, electionOutputList.size());
	}

	@Test
	public void shouldGetById() {
		given(electionRepository.findById(anyLong())).willReturn(Optional.of(ElectionBuilder.getElection()));
		ElectionOutput candidateOutput = electionService.getById(1L);
		Assert.assertEquals((Long) 1L, candidateOutput.getId());
	}

	@Test
	public void shouldCreate() {
		ElectionInput electionInput = ElectionBuilder.getElectionInput();
		given(electionRepository.save(any())).willReturn(ElectionBuilder.getElection());
		ElectionOutput electionOutput = electionService.create(electionInput);
		Assert.assertEquals(electionInput.getDescription(), electionOutput.getDescription());
		Assert.assertEquals(electionInput.getStateCode(), electionOutput.getStateCode());
		Assert.assertEquals(electionInput.getYear(), electionOutput.getYear());
	}

	@Test
	public void shouldUpdate() {
		ElectionInput electionInput = ElectionBuilder.getElectionInput();
		given(electionRepository.findById(anyLong())).willReturn(Optional.of(ElectionBuilder.getElection()));
		given(electionRepository.save(any())).willReturn(ElectionBuilder.getElection());
		ElectionOutput electionOutput = electionService.update(1L, electionInput);
		Assert.assertEquals(electionInput.getDescription(), electionOutput.getDescription());
		Assert.assertEquals(electionInput.getStateCode(), electionOutput.getStateCode());
		Assert.assertEquals(electionInput.getYear(), electionOutput.getYear());
	}

	@Test
	public void shouldDelete() {
		given(electionRepository.findById(anyLong())).willReturn(Optional.of(ElectionBuilder.getElection()));
		doNothing().when(electionRepository).delete(any());
		GenericOutput genericOutput = electionService.delete(1L);
		Assert.assertEquals("Candidate deleted", genericOutput.getMessage());
	}
}