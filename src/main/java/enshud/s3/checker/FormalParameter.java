package enshud.s3.checker;

import java.util.ArrayList;

public class FormalParameter extends GetToken {
	private ArrayList<String> names;
	private VariableType standardType;

	public FormalParameter(ArrayList<String> names, VariableType standardType, Records record){
		super(record);
		this.names=names;
		this.standardType=standardType;
	}

	public ArrayList<String> getNames(){
		return names;
	}

	public ValType getValType(){
		return standardType.getValType();
	}

	public VariableType getStandardType(){
		return standardType;
	}
}
