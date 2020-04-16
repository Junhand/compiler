package enshud.s3.checker;

import java.util.ArrayList;

public class CompoundStatement extends Statement{
	private ArrayList<Statement> statements;

	public CompoundStatement(ArrayList<Statement> statements, Records record){
		super(record);
		this.statements=statements;
	}

	//public void add(Statement statement){
	//	statements.add(statement);
	//}

	public ArrayList<Statement> getStatements(){
		return statements;
	}
}
