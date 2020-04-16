package enshud.s3.checker;

public class VariableType extends GetToken {
	private ValType valType;
	private int length;
	private int offset=0;

	VariableType(Records record){
		super(record);
		valType=record.getToken()==Token.SINTEGER ? ValType.tInteger : record.getToken()==Token.SBOOLEAN ? ValType.tBoolean : ValType.tChar;
		length=1;
	}

	VariableType(int minOfIndex, int maxOfIndex, ValType type, Records record){
		super(record);
		this.length=maxOfIndex-minOfIndex+1;
		this.offset=minOfIndex;
		valType=type;
	}

	public ValType getValType(){
		return valType;
	}

	public int getLength(){
		return length;
	}

	public int getOffset(){
		return offset;
	}
}
