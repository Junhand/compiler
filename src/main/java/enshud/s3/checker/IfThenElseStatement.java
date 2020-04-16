package enshud.s3.checker;

public class IfThenElseStatement extends Statement {
	private Expression expression;
	private CompoundStatement thenStatement;
	private CompoundStatement elseStatement;

	public IfThenElseStatement(Expression expression, CompoundStatement thenStatement, CompoundStatement elseStatement, Records record){
		super(record);
		this.expression=expression;
		this.thenStatement=thenStatement;
		this.elseStatement=elseStatement;
	}

	public Expression getExpression(){
		return expression;
	}

	public CompoundStatement getThenStatement(){
		return thenStatement;
	}

	public CompoundStatement getElseStatement(){
		return elseStatement;
	}
}
