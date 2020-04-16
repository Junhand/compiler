package enshud.s3.checker;

public class IfThenStatement extends Statement  {
	private Expression expression;
	private CompoundStatement thenStatement;

	public IfThenStatement(Expression expression, CompoundStatement thenStatement, Records record){
		super(record);
		this.expression=expression;
		this.thenStatement=thenStatement;
	}

	public Expression getExpression(){
		return expression;
	}

	public CompoundStatement getThenStatement(){
		return thenStatement;
	}
}
