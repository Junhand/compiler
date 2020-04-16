package enshud.s3.checker;

public class GetToken {

	protected Records record;
	public GetToken(Records record){
		this.record=record;
	}

	public Records getRecord(){
		return record;
	}

	public int getLineNumber(){
		return record.getLineNumber();
	}
}
