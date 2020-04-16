package enshud.s3.checker;

public class Records {
	private Token Token;
	private String name;
	private int lineNumber;

	public Records(Token Token, String name, int lineNumber){
		this.Token=Token;
		this.name=name;
		this.lineNumber=lineNumber;
	}

	public boolean tokenCheck(Token Token){
		return this.Token==Token;
	}

	public Token getToken(){
		return Token;
	}

	public String getName(){
		return name;
	}

	public int getLineNumber(){
		return lineNumber;
	}

}
