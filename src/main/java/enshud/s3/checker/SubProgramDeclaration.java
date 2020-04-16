package enshud.s3.checker;

import java.util.ArrayList;

public class SubProgramDeclaration extends GetToken {
	private String name;
	private ArrayList<FormalParameter> parameters;
	private ArrayList<VariableDeclaration> variableDeclaration;
	private CompoundStatement compoundStatement;


	public SubProgramDeclaration(String name, ArrayList<FormalParameter> parameters, ArrayList<VariableDeclaration> variableDeclaration, CompoundStatement compoundStatement, Records record){
		super(record);
		this.name=name;
		this.parameters=parameters;
		this.variableDeclaration=variableDeclaration;
		this.compoundStatement=compoundStatement;
	}

	public void set(ArrayList<VariableDeclaration> variableDeclaration, CompoundStatement compoundStatement){
		this.variableDeclaration=variableDeclaration;
		this.compoundStatement=compoundStatement;
	}

	public String getName(){
		return name;
	}

	public ArrayList<FormalParameter> getParameters(){
		return parameters;
	}

	public ArrayList<VariableDeclaration> getVariableDeclaration(){
		return variableDeclaration;
	}

	public CompoundStatement getCompoundStatement(){
		return compoundStatement;
	}
}
