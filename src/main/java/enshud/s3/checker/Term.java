package enshud.s3.checker;

public class Term extends Expression {
	private Expression left;
	private Expression right;

	public Term(Expression left, Expression right, Records record){
		super(record);
		this.left=left;
		this.right=right;
	}

	public Expression getLeft(){
		return left;
	}

	public Expression getRight(){
		return right;
	}
}
