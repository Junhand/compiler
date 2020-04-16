package enshud.s3.checker;

public class WhileDoStatement extends Statement {
	private Expression expression;
	private Statement doStatement;

	public WhileDoStatement(Expression expression, Statement doStatement, Records record){
		super(record);
		this.expression=expression;
		this.doStatement=doStatement;
	}

	public Expression getCondition(){
		return expression;
	}

	public Statement getDoStatement(){
		return doStatement;
	}
}
