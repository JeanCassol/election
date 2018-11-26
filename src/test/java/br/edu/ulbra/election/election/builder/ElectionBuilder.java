package br.edu.ulbra.election.election.builder;

import java.util.Collections;
import java.util.List;

import br.edu.ulbra.election.election.input.v1.ElectionInput;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.output.v1.ElectionOutput;

public class ElectionBuilder {
	public static ElectionOutput getElectionOutput(){
		ElectionOutput electionOutput = new ElectionOutput();
		electionOutput.setId(1L);
		electionOutput.setDescription("Election description");
		electionOutput.setStateCode("RS");
		electionOutput.setYear(2018);
        return electionOutput;
    }

    public static List<ElectionOutput> getElectionOutputList(){
        return Collections.singletonList(getElectionOutput());
    }

    public static ElectionInput getElectionInput() {
    	ElectionOutput electionOutput = getElectionOutput();
    	ElectionInput electionInput = new ElectionInput();
    	electionInput.setDescription(electionOutput.getDescription());
    	electionInput.setStateCode(electionOutput.getStateCode());
    	electionInput.setYear(electionOutput.getYear());
        return electionInput;
    }

    public static Election getElection(){
    	Election election = new Election();
    	election.setId(1L);
    	election.setDescription("Election description");
    	election.setStateCode("RS");
    	election.setYear(2018);
        return election;
    }

    public static List<Election> getElectionList() {
        return Collections.singletonList(getElection());
    }
}
