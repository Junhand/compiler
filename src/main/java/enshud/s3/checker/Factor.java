package enshud.s3.checker;

public class Factor extends Expression {
	private Variables variable=null;
	private Expression expression=null;
	private Factor notFactor=null;
	private Integer cinteger=null;
	private Boolean cboolean=null;
	private String cstring=null;

	public Factor(Variables variable, Records record){
		super(record);
		this.variable=variable;
		
	}

	public Factor(Expression expression, Records record){
		super(record);
		this.expression=expression;
		
	}

	public Factor(Factor notFactor, Records record){
		super(record);
		this.notFactor=notFactor;
	}

	public Factor(Records record){
		super(record);
		switch(record.getToken()){
			case SCONSTANT:
			{
				cinteger=Integer.parseInt(record.getName());
				valType=ValType.tInteger;
				break;
			}

			case STRUE:
			{
				cboolean=true;
				valType=ValType.tBoolean;
				break;
			}

			case SFALSE:
			{
				cboolean=false;
				valType=ValType.tBoolean;
				break;
			}

			default:
			{
				cstring=record.getName().substring(1, record.getName().length()-1);
				valType=cstring.length()>1 ? ValType.tString : ValType.tChar;
				break;
			}
		}
	}

	public Variables getVariable(){
		return variable;
	}

	public Expression getExpression(){
		return expression;
	}

	public Factor getNotFactor(){
		return notFactor;
	}
}
