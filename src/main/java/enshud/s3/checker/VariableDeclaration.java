package enshud.s3.checker;

import java.util.ArrayList;

public class VariableDeclaration extends GetToken {

	private ArrayList<String> names;
	private VariableType type;

	public VariableDeclaration(ArrayList<String> names, VariableType type, Records record){
		super(record);
		this.names=names;
		this.type=type;
	}

	public ArrayList<String> getNames(){
		return names;
	}

	public VariableType getType(){
		return type;
	}
}
