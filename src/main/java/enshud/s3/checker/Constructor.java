package enshud.s3.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Constructor {

	private Records lookahead;
	private boolean fail=false;
	private boolean semafail=false;

	private ArrayList<Records> records;
	private int rowNumber;

	private Program root;
	HashMap<String,ValType> nameTypes = new HashMap<String,ValType>();
	private ArrayList<String> procedureNames = new ArrayList<String>();
	

	public Constructor(String inputFileName,ArrayList<Records> records){
		this.records = records;
		rowNumber=0;
		lookahead=records.get(0);
		run();	
	}

	public void run(){
		root=program();

		if(null==root){
			syntaxError();
		}
	}

		private boolean match(final Token Token){
			if(lookahead.tokenCheck(Token)){
				return read();
			}

			throw new Error("expecting "+Token.toString()+"; "+lookahead.getToken().toString()+" found.");
		}

		private boolean read(){
			if(rowNumber<records.size()-1){
				rowNumber += 1;
				lookahead=records.get(rowNumber);
				return true;
			}
			return false;
		}

		private int store(){
			return rowNumber;
		}

		private void back(int previous){
			rowNumber=previous;
			lookahead=records.get(rowNumber);
		}

		private void syntaxError(){
			if(!fail){
				System.err.println("Syntax error: line "+records.get(rowNumber).getLineNumber());
				fail=true;
			}
		}
		
		private void semanticErrorOutput(int rowNumber) {
			if (!semafail) {
				System.err.println("Semantic error: line " + Integer.toString(rowNumber));
				semafail = true;
			}
		}
		
		private boolean nameCheck(String name,HashMap<String,ValType> nameTypes) {
			for(String s:nameTypes.keySet()) {
				if(name.equals(s)) return false;
			}
			return true;
		}
		
		private ValType getType(String name,HashMap<String,ValType> nameTypes) {
			for(Map.Entry<String, ValType> nT:nameTypes.entrySet()) {
				if(name.equals(nT.getKey())) return nT.getValue(); 
			}
			return null;
		}
		
		private boolean checkFactorType(Expression expression, ValType checkType, HashMap<String,ValType> tempNameTypes) {
			Token token; 
			if(expression.getRecord() != null) {
				token = expression.getRecord().getToken();
			}else {
				token = expression.getLeft().getRecord().getToken();
			}
			if(checkType == ValType.tIntegerArray) checkType = ValType.tInteger;
			if(checkType == ValType.tBooleanArray) checkType = ValType.tBoolean;
			if(token.isAdditiveOperator() || token.isMultiplicativeOperator()) {
				if(expression instanceof Factor){
					if(((Factor) expression).getExpression()!=null) {
						checkFactor(((Factor) expression).getExpression());
					}
				}else {
					if(expression.getLeft() instanceof Factor) {
						Factor left = (Factor)expression.getLeft();
					
						if(left != null) {
							int leftRow = left.getLineNumber();
							if(left.getExpression() != null) checkFactor(left.getExpression());
							else {
								ValType leftValType = left.getValType();
								if(leftValType == null) {
									leftValType = getType(left.getVariable().getName(),tempNameTypes);
									if(leftValType == null) leftValType = getType(left.getVariable().getName(),nameTypes);
								}
								if(leftValType != checkType) {
									semanticErrorOutput(leftRow);
									return false;
								}
							}
						}
					}else {
						checkFactor(expression.getLeft());
					}
				
					if(expression.getRight() instanceof Factor){
						Factor right = (Factor)expression.getRight();
					
						if(right != null) {
							int rightRow = right.getLineNumber();
							if(right.getExpression() != null) checkFactor(right.getExpression());
							else {
								ValType rightValType = right.getValType();
								if(rightValType == null) {
									rightValType = getType(right.getVariable().getName(),tempNameTypes);
									if(rightValType == null) rightValType = getType(right.getVariable().getName(),nameTypes);
								}
								if(rightValType != checkType) {
									semanticErrorOutput(rightRow);
									return false;
								}
							}
						}
					}else {
						checkFactor(expression.getRight()); 
					}
				}
			}
			return true;
		}
		
		private boolean checkFactor(Expression expression) {
			Token token;
			if(expression.getRecord()!=null) {
				token = expression.getRecord().getToken();
			}else {
				token = expression.getLeft().getRecord().getToken();
			}
			if(token.isAdditiveOperator() || token.isMultiplicativeOperator()) {
				if(expression instanceof Factor){
					if(((Factor) expression).getExpression()!=null) {
						checkFactor(((Factor) expression).getExpression());
					}
				}else {
					
				
					if(expression.getLeft() instanceof Factor) {
						Factor left = (Factor)expression.getLeft();
					
						if(left != null) {
							int leftRow = left.getLineNumber();
							if(left.getValType() == ValType.tBoolean || left.getValType() == ValType.tChar) {
								semanticErrorOutput(leftRow);
								return false;
							}
							if(left.getExpression() != null) checkFactor(left.getExpression());
						}
					}else {
						checkFactor(expression.getLeft());
					}
				
					if(expression.getRight() instanceof Factor){
						Factor right = (Factor)expression.getRight();
					
						if(right != null) {
							int rightRow = right.getLineNumber();
			
							if(right.getValType() == ValType.tBoolean || right.getValType() == ValType.tChar) {
								semanticErrorOutput(rightRow);
								return false;
							}
							if(right.getExpression() != null) checkFactor(right.getExpression());
						}
					}else {
						checkFactor(expression.getRight()); 
					}
				}
			}
			return true;
		}
		
		private void checkCompoundStatement(CompoundStatement compoundStatement,HashMap<String,ValType> tempNameTypes) {
			if(compoundStatement!=null) {
				ArrayList<Statement> statements = compoundStatement.getStatements();
				
				for(Statement state:statements) {
					if(state instanceof AssignmentStatement) {
						AssignmentStatement assignState = (AssignmentStatement) state;
						Expression assignExpress = assignState.getExpression();
						checkFactor(assignExpress);
						Variables assignV = assignState.getVariable();
						String assignVname = assignV.getName();
						int assignVrow = assignV.getLineNumber();
						if(nameCheck(assignVname,nameTypes) && nameCheck(assignVname,tempNameTypes)) {
							semanticErrorOutput(assignVrow);
						}
						ValType assignType = getType(assignVname,tempNameTypes);
						if(assignType == null) assignType = getType(assignVname,nameTypes);
						if(assignType == ValType.tBooleanArray || assignType == ValType.tIntegerArray) {
							if(assignV.getSubscript() == null) semanticErrorOutput(assignVrow);
						}
						
						checkFactorType(assignExpress, assignType,tempNameTypes);
						
						
						Expression subscript = assignV.getSubscript();
						if(subscript!=null) {
							 
							int subRow = subscript.getLineNumber();
							if(subscript instanceof Factor) {
								Factor subFactor = (Factor)subscript;
								Variables subV = subFactor.getVariable();
								if(subV!=null) {String subVName = subV.getName();
									ValType subVType = getType(subVName,tempNameTypes);
									if(subVType==null) subVType = getType(subVName,nameTypes);
									if(subVType != ValType.tInteger) {
										semanticErrorOutput(subRow);
									}
								}
							}
						}
						
						
					}
					else if(state instanceof ProcedureCallStatement) {
						ProcedureCallStatement proState = (ProcedureCallStatement) state;
						int proRow = proState.getLineNumber();
						boolean flag = false;
						for(String s: procedureNames) {
							if(s.equals( proState.getName())) flag = true;
						}
						if(!flag) semanticErrorOutput(proRow);
					}
					else if(state instanceof InputStatement) {
						InputStatement inState = (InputStatement) state;
					}
					else if(state instanceof OutputStatement) {
						OutputStatement outState = (OutputStatement) state;
					}
					else if(state instanceof CompoundStatement) {
						CompoundStatement comState = (CompoundStatement) state;
						checkCompoundStatement(comState,tempNameTypes);
					}
					else if(state instanceof IfThenStatement) {
						IfThenStatement ifThenState = (IfThenStatement) state;
						Expression expression = ifThenState.getExpression();
						Expression left = expression.getLeft();
						Expression right = expression.getRight();
						
						if(expression instanceof Factor && left==null && right==null) {
							Factor exFactor = (Factor)expression;
							int exRow = exFactor.getLineNumber();
							Variables exV = exFactor.getVariable();
							if(exV!=null) { 
								ValType exVType = getType(exV.getName(),tempNameTypes);
								if(exVType==null) exVType = getType(exV.getName(),nameTypes);
								if(exVType != ValType.tBoolean) {
									semanticErrorOutput(exRow);
								}
							}
						}
						Statement comThenState = ifThenState.getThenStatement();
						if(comThenState instanceof CompoundStatement) {
							checkCompoundStatement((CompoundStatement)comThenState,tempNameTypes);
						}
					}
					else if(state instanceof IfThenElseStatement) {
						IfThenElseStatement ifThenElseState = (IfThenElseStatement) state;
						Statement comThenState = ifThenElseState.getThenStatement();
						if(comThenState instanceof CompoundStatement) {
							checkCompoundStatement((CompoundStatement)comThenState,tempNameTypes);
						}
						Statement comElseState = ifThenElseState.getElseStatement();
						if(comElseState instanceof CompoundStatement) {
							checkCompoundStatement((CompoundStatement)comElseState,tempNameTypes);
						}

					}
					else if(state instanceof WhileDoStatement) {
						WhileDoStatement whileState = (WhileDoStatement) state;
						Statement comState = whileState.getDoStatement();
						if(comState instanceof CompoundStatement) {
							checkCompoundStatement((CompoundStatement)comState,tempNameTypes);
						}
					}
				}
			}
			return;
		}

		private Program program(){
			if(lookahead.tokenCheck(Token.SPROGRAM)){
				Records r=records.get(rowNumber);
				match(Token.SPROGRAM);
				String name=programName();
				nameTypes.put(name, null);

				if(null!=name && lookahead.tokenCheck(Token.SSEMICOLON)){
					match(Token.SSEMICOLON);
					Blocks block=block();
					CompoundStatement compoundStatement=compoundStatement();
					
					
					if(null!=block && null!=compoundStatement && lookahead.tokenCheck(Token.SDOT)){
						match(Token.SDOT);
						checkCompoundStatement(compoundStatement,nameTypes);//semErrorCheck
						return new Program(name, block, compoundStatement, r);
					}
				}
			}
			syntaxError();
			return null;
		}

		private String programName(){
			return name();
		}

		private Blocks block(){
			Records r = records.get(rowNumber);
			
			ArrayList<VariableDeclaration> variableDeclaration = variableDeclaration();
			//name exist? else put name and type
			if(variableDeclaration!=null) {
				for(VariableDeclaration v:variableDeclaration) {
					ArrayList<String> vNames = v.getNames();
					ValType vType = v.getType().getValType();
					int vRow = v.getLineNumber();
					for(String vName:vNames) {
						if(!nameCheck(vName,nameTypes)) {
							semanticErrorOutput(vRow);
						}
						nameTypes.put(vName, vType);
					}
				}
			}//
			ArrayList<SubProgramDeclaration> subProgramDeclarations = subProgramDeclarations();
			
			return new Blocks(variableDeclaration, subProgramDeclarations, r);
		}

		private ArrayList<VariableDeclaration> variableDeclaration(){
			ArrayList<VariableDeclaration> variableDeclarations=null;

			if(lookahead.tokenCheck(Token.SVAR)){
				match(Token.SVAR);
				variableDeclarations=variableDeclarationList();

				if(null==variableDeclarations){
					syntaxError();
					return null;
				}
			}

			return variableDeclarations;
		}

		private ArrayList<VariableDeclaration> variableDeclarationList(){
			Records r=records.get(rowNumber);
			ArrayList<String> names=variableNameList();
			ArrayList<VariableDeclaration> variableDeclarations=new ArrayList<>();
			VariableType variableType;
			if(null!=names){
				do{
					if(!lookahead.tokenCheck(Token.SCOLON)){
						syntaxError();
						return null;
					}
					match(Token.SCOLON);
					variableType=type();

					if(null==variableType || !lookahead.tokenCheck(Token.SSEMICOLON)){
						syntaxError();
						return null;
					}
					match(Token.SSEMICOLON);

					variableDeclarations.add(new VariableDeclaration(names, variableType, r));
					r=records.get(rowNumber);
				}while(lookahead.tokenCheck(Token.SIDENTIFIER) && null!=(names=variableNameList()));

				return variableDeclarations;
			}
			syntaxError();
			return null;
		}

		private ArrayList<String> variableNameList(){
			ArrayList<String> names=new ArrayList<>();
			int previous=store();
			String name=variableName();

			if(null!=name){
				names.add(name);
				while(lookahead.tokenCheck(Token.SCOMMA)){
					match(Token.SCOMMA);
					name=variableName();
					if(null==name){
						syntaxError();
						return null;
					}
					names.add(name);
				}

				return names;
			}
			back(previous);
			return null;
		}

		private String variableName(){
			return name();
		}

		private VariableType type(){
			int previous=store();
			VariableType type=standardType();
			if(null==type){
				back(previous);
				type=arrayType();
				if(null==type){
					syntaxError();
					return null;
				}
			}

			return type;
		}

		private VariableType standardType(){

			for(Token t:new Token[]{Token.SINTEGER, Token.SCHAR, Token.SBOOLEAN}){

				if(lookahead.tokenCheck(t)){
					Records r=records.get(rowNumber);
					match(t);
					return new VariableType(r);
				}
			}

			return null;
		}

		private VariableType arrayType(){

			if(lookahead.tokenCheck(Token.SARRAY)){
				Records r=records.get(rowNumber);
				match(Token.SARRAY);

				if(lookahead.tokenCheck(Token.SLBRACKET)){
					match(Token.SLBRACKET);
					Integer minOfIndex=minOfIndex();

					if(null!=minOfIndex && lookahead.tokenCheck(Token.SRANGE)){
						match(Token.SRANGE);
						Integer maxOfIndex=maxOfIndex();

						if(null!=maxOfIndex && lookahead.tokenCheck(Token.SRBRACKET)){
							match(Token.SRBRACKET);

							if(lookahead.tokenCheck(Token.SOF)){
								match(Token.SOF);
								VariableType type=standardType();

								if(null!=type){
									return new VariableType(minOfIndex, maxOfIndex, type.getValType().toArrayType(), r);
								}

							}
						}
					}
				}
			}
			syntaxError();
			return null;
		}

		private Integer minOfIndex(){
			return integer();
		}

		private Integer maxOfIndex(){
			return integer();
		}

		private Integer integer(){
			boolean sign=sign();
			Integer n=natural();
			if(n!=null) {

				if(sign) {
					return n;
				}else {
					return -n;
				}

			}

			return null;
		}

		private boolean sign(){
			if(lookahead.tokenCheck(Token.SPLUS)){
				match(Token.SPLUS);
				return true;

			}else if(lookahead.tokenCheck(Token.SMINUS)){
				match(Token.SMINUS);
				return false;
			}

			return true;
		}

		private ArrayList<SubProgramDeclaration> subProgramDeclarations(){
			SubProgramDeclaration s;
			ArrayList<SubProgramDeclaration> subProgramDeclarations=new ArrayList<>();

			while(null!=(s=subProgramDeclaration())){
				subProgramDeclarations.add(s);

				if(!lookahead.tokenCheck(Token.SSEMICOLON)){
					syntaxError();
					return null;
				}
				match(Token.SSEMICOLON);
			}
			return subProgramDeclarations;
		}

		private SubProgramDeclaration subProgramDeclaration(){
			SubProgramDeclaration subProgramDeclaration=subProgramHead();

			if(null!=subProgramDeclaration){
				ArrayList<VariableDeclaration> variableDeclaration=variableDeclaration();
				HashMap<String,ValType> tempNameTypes = new HashMap<String,ValType>();
				
				
				//name exist? else put name and type
				if(subProgramDeclaration.getParameters()!=null) {	
					for(FormalParameter subPar:subProgramDeclaration.getParameters()) {
						ArrayList<String> vNames = subPar.getNames();
						ValType vType = subPar.getValType();
						int vRow = subPar.getLineNumber();
						for(String vName:vNames) {
							if(!nameCheck(vName,tempNameTypes)) {
								semanticErrorOutput(vRow);
							}
							tempNameTypes.put(vName, vType);
						}
					}
				}
				
				if(variableDeclaration!=null) {
					
					for(VariableDeclaration v:variableDeclaration) {
						ArrayList<String> vNames = v.getNames();
						ValType vType = v.getType().getValType();
						int vRow = v.getLineNumber();
						for(String vName:vNames) {
							if(!nameCheck(vName,tempNameTypes)) {
								semanticErrorOutput(vRow);
							}
							tempNameTypes.put(vName, vType);
						}
					}
				}//
				
				
				
				CompoundStatement compoundStatement=compoundStatement();
			
				if(null!=compoundStatement){
					subProgramDeclaration.set(variableDeclaration, compoundStatement);
					checkCompoundStatement(compoundStatement,tempNameTypes);//semErrorCheck
					return subProgramDeclaration;
				}
				syntaxError();
				
			}
			return null;
		}

		private SubProgramDeclaration subProgramHead(){
			if(lookahead.tokenCheck(Token.SPROCEDURE)){
				Records r=records.get(rowNumber);
				match(Token.SPROCEDURE);
				String name=procedureName();
				procedureNames.add(name);

				if(null!=name){
					ArrayList<FormalParameter> parameters=formalParameter();

					if(lookahead.tokenCheck(Token.SSEMICOLON)){
						match(Token.SSEMICOLON);
						return new SubProgramDeclaration(name, parameters, null, null, r);
					}
				}
				syntaxError();
			}
			return null;
		}

		private String procedureName(){
			return name();
		}

		private ArrayList<FormalParameter> formalParameter(){
			if(lookahead.tokenCheck(Token.SLPAREN)){
				match(Token.SLPAREN);
				ArrayList<FormalParameter> parameters=formalParameterList();

				if(null!=parameters && lookahead.tokenCheck(Token.SRPAREN)){
					match(Token.SRPAREN);
					return parameters;
				}
				syntaxError();
			}
			return null;
		}

		private ArrayList<FormalParameter> formalParameterList(){
			ArrayList<String> names;
			VariableType type;
			ArrayList<FormalParameter> parameters=new ArrayList<>();
			Records r;
			do{
				r=records.get(rowNumber);
				names=formalParameterNameList();

				if(null!=names && lookahead.tokenCheck(Token.SCOLON)){
					match(Token.SCOLON);
					type=standardType();

					if(null!=type){
						parameters.add(new FormalParameter(names, type, r));
						continue;
					}
				}
				syntaxError();
				return null;
			}while(lookahead.tokenCheck(Token.SSEMICOLON) && match(Token.SSEMICOLON));

			return parameters;
		}

		private ArrayList<String> formalParameterNameList(){
			String name;
			ArrayList<String> names=new ArrayList<>();

			do{
				name=formalParameterName();
				if(null!=name){
					names.add(name);
					continue;
				}
				syntaxError();
				return null;
			}while(lookahead.tokenCheck(Token.SCOMMA) && match(Token.SCOMMA));

			return names;
		}

		private String formalParameterName(){
			return name();
		}

		private CompoundStatement compoundStatement(){
			if(lookahead.tokenCheck(Token.SBEGIN)){
				Records r=records.get(rowNumber);
				match(Token.SBEGIN);
				ArrayList<Statement> statements=statementList();
				if(null!=statements && lookahead.tokenCheck(Token.SEND)){
					match(Token.SEND);
					return new CompoundStatement(statements, r);
				}
				syntaxError();
			}
			return null;
		}

		private ArrayList<Statement> statementList(){
			ArrayList<Statement> statements=new ArrayList<>();
			Statement statement=statement();
			if(null!=statement){
				do{
					statements.add(statement);
					if(!lookahead.tokenCheck(Token.SSEMICOLON)){
						return statements;
					}
					match(Token.SSEMICOLON);
				}while(null!=(statement=statement()));
				return statements;
			}
			syntaxError();
			return null;
		}

		private Statement statement(){
			Records r=records.get(rowNumber);
			Statement statement=basicStatement();
			if(null!=statement){
				return statement;
			}

			if(lookahead.tokenCheck(Token.SIF)){
				match(Token.SIF);
				Expression expression=expression();
				if(null!=expression && lookahead.tokenCheck(Token.STHEN)){
					match(Token.STHEN);
					CompoundStatement thenStatement=compoundStatement();

					if(null!=thenStatement){

						if(lookahead.tokenCheck(Token.SELSE)){
							match(Token.SELSE);
							CompoundStatement elseStatement=compoundStatement();

							if(null!=elseStatement){
								return new IfThenElseStatement(expression, thenStatement, elseStatement, r);
							}
							syntaxError();
							return null;
						}
						return new IfThenStatement(expression, thenStatement, r);
					}
				}
				syntaxError();
				return null;
			}

			if(lookahead.tokenCheck(Token.SWHILE)){
				match(Token.SWHILE);
				Expression expression=expression();

				if(null!=expression && lookahead.tokenCheck(Token.SDO)){
					match(Token.SDO);
					Statement doStatement=statement();

					if(null!=doStatement){
						return new WhileDoStatement(expression, doStatement, r);
					}
				}
				syntaxError();
			}
			return null;
		}

		private Statement basicStatement(){
			Statement statement=assignmentStatement();
			if(null!=statement){
				return statement;
			}

			statement=procedureCallStatement();
			if(null!=statement){
				return statement;
			}

			statement=InOutStatement();
			if(null!=statement){
				return statement;
			}

			statement=compoundStatement();
			if(null!=statement){
				return statement;
			}

			return null;
		}

		private AssignmentStatement assignmentStatement(){
			int previous=store();
			Records r=records.get(rowNumber);
			Variables variable=leftSide();

			if(null!=variable && lookahead.tokenCheck(Token.SASSIGN)){
				match(Token.SASSIGN);
				Expression expression=expression();

				if(null!=expression){
					return new AssignmentStatement(variable, expression, r);
				}
				syntaxError();
				return null;
			}
			back(previous);
			return null;
		}

		private Variables leftSide(){
			return variable();
		}

		private Variables variable(){
			Variables subscriptedVariable=subscriptedVariable();
			if(null!=subscriptedVariable){
				return subscriptedVariable;
			}
			
			Variables pureVariable=pureVariable();
			if(null!=pureVariable){
				return pureVariable;
			}
			return null;
		}

		private Variables pureVariable(){
			int previous=store();
			Records r=records.get(rowNumber);
			String name=variableName();
			if(null!=name){
				return new Variables(name,r);
			}
			back(previous);
			return null;
		}

		private Variables subscriptedVariable(){
			int previous=store();
			Records r=records.get(rowNumber);
			String name=variableName();
			if(null!=name && lookahead.tokenCheck(Token.SLBRACKET)){
				match(Token.SLBRACKET);
				Expression subscript=subscript();

				if(null!=subscript && lookahead.tokenCheck(Token.SRBRACKET)){
					match(Token.SRBRACKET);
					return new Variables(name,subscript,r);
				}
				syntaxError();
			}
			back(previous);
			return null;
		}

		private Expression subscript(){
			return expression();
		}

		private ProcedureCallStatement procedureCallStatement(){
			Records r=records.get(rowNumber);
			String name=procedureName();
			if(null!=name){

				if(lookahead.tokenCheck(Token.SLPAREN)){
					match(Token.SLPAREN);
					ArrayList<Expression> expressions=expressionList();

					if(lookahead.tokenCheck(Token.SRPAREN)){
						match(Token.SRPAREN);
						return new ProcedureCallStatement(name, expressions, r);
					}
					syntaxError();
					return null;
				}
				return new ProcedureCallStatement(name, null, r);
			}
			return null;
		}

		private ArrayList<Expression> expressionList(){
			Expression expression;
			ArrayList<Expression> expressions=new ArrayList<>();

			do{
				expression=expression();
				if(null!=expression){
					expressions.add(expression);
					continue;
				}
				syntaxError();
				return null;
			}while(lookahead.tokenCheck(Token.SCOMMA) && match(Token.SCOMMA));

			return expressions;
		}

		private Expression expression(){
			int previous=store();
			Expression left=simpleExpression();
			if(null!=left){
				Records r=relationalOperator();
				if(null!=r){
					Expression right=simpleExpression();
					if(null!=right){
						return new Expression(left, right, r);
					}
					syntaxError();
					return null;
				}
				return left;
			}
			back(previous);
			return null;
		}

		private Expression simpleExpression(){
			boolean sign=sign();
			Expression left=term();

			if(left==null){
				syntaxError();
				return null;
			}
			Records r=additiveOperator();

			if(r==null){
				if(sign) {
					return left;
				}else if(left.getRecord().getToken().isAdditiveOperator()||left.getRecord().getToken().isMultiplicativeOperator()) {
					if(left.getLeft()==null && left.getRight()==null) {
						return  new SimpleExpression(sign, left, null, null);
					}else {
						return left;
					}
				}else{
					return  new SimpleExpression(sign, left, null, null);
				}
			}

			Expression right=term();
			if(right==null){
				syntaxError();
				return null;
			}

			left=new SimpleExpression(sign, left, right, r);

			if((r=additiveOperator())==null){
				return left;
			}

			do{
				right=term();
				if(right==null){
					syntaxError();
					return null;
				}
				left=new SimpleExpression(true, left, right, r);
				
			}while((r=additiveOperator())!=null);

			return left;
		}

		private Expression term(){
			Expression left=factor();
			if(left==null){
				syntaxError();
				return null;
			}

			Records r=multiplicativeOperator();
			if(r!=null){
				Expression right;
				do{
					right=factor();
					if(right==null){
						syntaxError();
						return null;
					}
					left=new Term(left, right, r);
				}while((r=multiplicativeOperator())!=null);
			}
			return left;
		}

		private Factor factor(){
			Records r=records.get(rowNumber);
			Variables variable=variable();
			if(variable!=null){
				return new Factor(variable, r);
			}

			Factor constant=constant();
			if(constant!=null){
				return constant;
			}

			if(lookahead.tokenCheck(Token.SLPAREN)){
				match(Token.SLPAREN);
				r=records.get(rowNumber);
				Expression expression=expression();
				if(expression!=null && lookahead.tokenCheck(Token.SRPAREN)){
					match(Token.SRPAREN);
					return new Factor(expression, expression.getRecord());
				}
				syntaxError();
				return null;
			}

			if(lookahead.tokenCheck(Token.SNOT)){
				match(Token.SNOT);
				Factor factor=factor();
				if(factor!=null){
					return new Factor(factor, r);
				}
			}
			syntaxError();
			return null;
		}

		private Records relationalOperator(){
			for(Token t:new Token[]{Token.SEQUAL, Token.SNOTEQUAL, Token.SLESS, Token.SLESSEQUAL, Token.SGREAT, Token.SGREATEQUAL}){
				if(lookahead.tokenCheck(t)){
					Records r=records.get(rowNumber);
					match(t);
					return r;
				}
			}
			return null;
		}

		private Records additiveOperator(){
			for(Token t:new Token[]{Token.SPLUS, Token.SMINUS, Token.SOR}){
				if(lookahead.tokenCheck(t)){
					Records r=records.get(rowNumber);
					match(t);
					return r;
				}
			}
			return null;
		}

		private Records multiplicativeOperator(){
			for(Token t:new Token[]{Token.SSTAR, Token.SDIVD, Token.SMOD, Token.SAND}){
				if(lookahead.tokenCheck(t)){
					Records r=records.get(rowNumber);
					match(t);
					return r;
				}
			}
			return null;
		}

		private Statement InOutStatement(){
			Records r=records.get(rowNumber);

			if(lookahead.tokenCheck(Token.SREADLN)){
				match(Token.SREADLN);

				if(lookahead.tokenCheck(Token.SLPAREN)){
					match(Token.SLPAREN);
					ArrayList<Variables> variables=variableList();

					if(lookahead.tokenCheck(Token.SRPAREN)){
						match(Token.SRPAREN);
						return new InputStatement(variables, r);
					}
					syntaxError();
					return null;
				}
				return new InputStatement(null, r);

			}else if(lookahead.tokenCheck(Token.SWRITELN)){
				match(Token.SWRITELN);
				if(lookahead.tokenCheck(Token.SLPAREN)){
					match(Token.SLPAREN);
					ArrayList<Expression> expressions=expressionList();
					if(lookahead.tokenCheck(Token.SRPAREN)){
						match(Token.SRPAREN);
						return new OutputStatement(expressions, r);
					}
					syntaxError();
					return null;
				}
				return new OutputStatement(null, r);
			}
			return null;
		}

		private ArrayList<Variables> variableList(){
			ArrayList<Variables> variables=new ArrayList<>();
			do{
				Variables variable=variable();
				if(null==variable){
					syntaxError();
					return null;
				}
				variables.add(variable);
			}
			while(lookahead.tokenCheck(Token.SCOMMA) && match(Token.SCOMMA));

			return variables;
		}

		private Factor constant(){
			Records r=records.get(rowNumber);
			Integer n=natural();

			if(null!=n){
				return new Factor(r);

			}else if(lookahead.tokenCheck(Token.SSTRING)){
				match(Token.SSTRING);
				return new Factor(r);

			}else if(lookahead.tokenCheck(Token.SFALSE)){
				match(Token.SFALSE);
				return new Factor(r);

			}else if(lookahead.tokenCheck(Token.STRUE)){
				match(Token.STRUE);
				return new Factor(r);
			}
			return null;
		}

		private Integer natural(){
			if(lookahead.tokenCheck(Token.SCONSTANT)){
				Integer s=Integer.parseInt(lookahead.getName());
				match(Token.SCONSTANT);
				return s;
			}
			return null;
		}

		private String name(){
			if(lookahead.tokenCheck(Token.SIDENTIFIER)){
				String s=lookahead.getName();
				match(Token.SIDENTIFIER);
				return s;
			}
			return null;
		}
		public Program getRoot(){
			return root;
		}

		public boolean success(){
			return null!=root && !fail;
		}
}
