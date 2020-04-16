package enshud.s3.checker;

import java.util.ArrayList;

public class Blocks extends GetToken {
	private ArrayList<VariableDeclaration> variableDeclarations;
	private ArrayList<SubProgramDeclaration> subProgramDeclarations;

	public Blocks(ArrayList<VariableDeclaration> variableDeclarations, ArrayList<SubProgramDeclaration> subProgramDeclarations, Records record){
		super(record);
		this.variableDeclarations=variableDeclarations;
		this.subProgramDeclarations=subProgramDeclarations;
	}

	public ArrayList<VariableDeclaration> getVariableDeclarations(){
		return variableDeclarations;
	}

	public ArrayList<SubProgramDeclaration> getSubProgramDeclarations(){
		return subProgramDeclarations;
	}
}
