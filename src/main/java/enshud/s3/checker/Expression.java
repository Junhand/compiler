package enshud.s3.checker;

public class Expression extends GetToken {

	protected ValType valType;
	private Expression left;
	private Expression right;
	
	public Expression(Records record){
		super(record);
	}

	public Expression(Expression left, Expression right, Records record){
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

	public ValType getValType(){
		return valType;
	}
}
