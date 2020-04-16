package enshud.s3.checker;

public enum ValType {
	tInteger,
	tBoolean,
	tChar,
	tIntegerArray,
	tBooleanArray,
	tString;

	public boolean isStandardType(){
		return this==tInteger || this==tBoolean || this==tChar;
	}

	public boolean isArrayType(){
		return !isStandardType();
	}

	public ValType toStandardType(){
		return this==tIntegerArray ? tInteger : this==tBooleanArray ? tBoolean : tChar;
	}

	public ValType toArrayType(){
		return this==tInteger ? tIntegerArray : this==tBoolean ? tBooleanArray : tString;
	}
}
