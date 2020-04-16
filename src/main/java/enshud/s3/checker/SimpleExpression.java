package enshud.s3.checker;

public class SimpleExpression extends Expression {

	//public static final boolean POSITIVE=true;
	//public static final boolean NEGATIVE=false;

	protected boolean sign;
	private Expression left;
	private Expression right;

	public SimpleExpression(boolean sign, Expression left, Expression right, Records record){
		super(record);
		this.sign=sign;
		this.left=left;
		this.right=right;
	}

	public Expression getLeft(){
		return left;
	}

	public Expression getRight(){
		return right;
	}

	public boolean getSign(){
		return sign;
	}
}
