package enshud.s3.checker;

public class Variables extends GetToken {
	protected ValType valType;
	protected String name;
	protected int length;
	private Expression subscript;

	public Variables(Records record){
		super(record);
	}

	public Variables(String name, Records record){
		super(record);
		this.name = name;
	}

	public Variables(String name, Expression subscript, Records record){
		super(record);
		this.name=name;
		this.subscript=subscript;
		this.length=1;
	}

	public String getName(){
		return name;
	}

	public int getLength(){
		return length;
	}

	public ValType getValType(){
		return valType;
	}

	public Expression getSubscript(){
		return subscript;
	}
}