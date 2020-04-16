package enshud.s3.checker;

import java.util.ArrayList;

public class InputStatement extends Statement {
	private ArrayList<Variables> variables;

	public InputStatement(ArrayList<Variables> variables, Records record){
		super(record);
		this.variables=variables;
	}

	public ArrayList<Variables> getVariables(){
		return variables;
	}
}
