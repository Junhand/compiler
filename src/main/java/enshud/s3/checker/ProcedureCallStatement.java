package enshud.s3.checker;

import java.util.ArrayList;

public class ProcedureCallStatement extends Statement {

	private String name;
	private ArrayList<Expression> expressions;

	public ProcedureCallStatement(String name, ArrayList<Expression> expressions, Records record){
		super(record);
		this.name=name;
		this.expressions=expressions;
	}

	public String getName(){
		return name;
	}

	public ArrayList<Expression> getExpressions(){
		return expressions;
	}
}
