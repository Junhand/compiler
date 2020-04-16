package enshud.s3.checker;

import java.util.ArrayList;

public class OutputStatement extends Statement {
	private ArrayList<Expression> expressions;

	public OutputStatement(ArrayList<Expression> expressions, Records record){
		super(record);
		this.expressions=expressions;
	}

	public ArrayList<Expression> getExpressions(){
		return expressions;
	}
}
