package enshud.s3.checker;

public class AssignmentStatement extends Statement {
	private Variables variable;
	private Expression expression;

	public AssignmentStatement(Variables variable, Expression expression,Records record) {
		super(record);
		this.variable = variable;
		this.expression = expression;
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public Variables getVariable() {
		return variable;
	}

	public Expression getExpression() {
		return expression;
	}

}
