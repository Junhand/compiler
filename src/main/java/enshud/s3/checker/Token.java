package enshud.s3.checker;

public enum Token {
	SAND("and"),
	SARRAY("array"),
	SBEGIN("begin"),
	SBOOLEAN("boolean"),
	SCHAR("char"),
	SDIVD("div"),
	SDO("do"),
	SELSE("else"),
	SEND("end"),
	SFALSE("false"),
	SIF("if"),
	SINTEGER("integer"),
	SMOD("mod"),
	SNOT("not"),
	SOF("of"),
	SOR("or"),
	SPROCEDURE("procedure"),
	SPROGRAM("program"),
	SREADLN("readln"),
	STHEN("then"),
	STRUE("true"),
	SVAR("var"),
	SWHILE("while"),
	SWRITELN("writeln"),
	SEQUAL("="),
	SNOTEQUAL("<>"),
	SLESS("<"),
	SLESSEQUAL("<="),
	SGREATEQUAL(">="),
	SGREAT(">"),
	SPLUS("+"),
	SMINUS("-"),
	SSTAR("*"),
	SLPAREN("("),
	SRPAREN(")"),
	SLBRACKET("["),
	SRBRACKET("]"),
	SSEMICOLON(";"),
	SCOLON(":"),
	SRANGE(".."),
	SASSIGN(":="),
	SCOMMA(","),
	SDOT("."),
	SIDENTIFIER(""),
	SCONSTANT(""),
	SSTRING("");

	private String token;

	Token(String token){
		this.token=token;
	}

	public String getToken(){
		return this.token;
	}

	public boolean isRelationalOperator(){
		return this==SEQUAL || this==SNOTEQUAL || this==SLESS || this==SLESSEQUAL || this==SGREAT || this==SGREATEQUAL;
		// "= | <> | < | <= | > | >="
	}

	public boolean isAdditiveOperator(){
		return this==SPLUS || this==SMINUS || this==SOR;
		// "+ | - | or"
	}

	public boolean isMultiplicativeOperator(){
		return this==SSTAR || this==SDIVD || this==SMOD || this==SAND;
		// "* | / | mod | and"
	}

	public boolean isLogicalOperator(){
		return this==SOR || this==SAND;
		// "or | and"
	}

	public boolean isArithmeticOperator(){
		return this==SPLUS || this==SMINUS || this==SSTAR || this==SDIVD || this==SMOD;
		// "+ | - | * | / | mod "
	}

}


