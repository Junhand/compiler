package enshud.s4.compiler;

import enshud.s3.checker.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


public class ConvertCASL2 {
	
	private ArrayList<String> caslList=new ArrayList<>();
	private ArrayList<String> scope=new ArrayList<>();
	private LinkedHashMap<String,ValType> valList=new LinkedHashMap<String,ValType>();
	private LinkedHashMap<String,Integer> vLenList = new LinkedHashMap<String,Integer>();
	private LinkedHashMap<String,ValType> tmpvList=new LinkedHashMap<String,ValType>();
	private LinkedHashMap<String,Integer> tmpvLenList = new LinkedHashMap<String,Integer>();
	private LinkedHashMap<String,ValType> parList=new LinkedHashMap<String,ValType>();
	private LinkedHashMap<String,Integer> parLenList = new LinkedHashMap<String,Integer>();
	private LinkedHashMap<String,String> nameLists = new LinkedHashMap<String,String>();
	private int valLengs;
	private int trueNum =0;
	private int bothNum =0;
	private int endlpNum=0;
	private int loopNum =0;
	private int elseNum =0;
	private int endifNum=0;
	private int charNum =0;
		
	
	
	public void run(Program p){
		try{
			program(p);
		}catch(Exception e){
			System.out.print(e);
		}
	}
	
	public ValType nameType(String valName) {
		for(Map.Entry<String, ValType> entry : parList.entrySet()) {
            if(valName.equals(entry.getKey())) return entry.getValue();
        }
		for(Map.Entry<String, ValType> entry : tmpvList.entrySet()) {
            if(valName.equals(entry.getKey())) return entry.getValue();
        }
		for(Map.Entry<String, ValType> entry : valList.entrySet()) {
            if(valName.equals(entry.getKey())) return entry.getValue();
        }
		return null;
	}
	
	public int valLen(String vName) {
		for(Map.Entry<String, Integer> entry : parLenList.entrySet()) {
            if(vName.equals(entry.getKey())) return entry.getValue();
        }
		for(Map.Entry<String, Integer> entry : tmpvLenList.entrySet()) {
            if(vName.equals(entry.getKey())) return entry.getValue();
        }
		for(Map.Entry<String, Integer> entry : vLenList.entrySet()) {
            if(vName.equals(entry.getKey())) return entry.getValue();
        }
		for(String s:scope) {
			if(vName.equals(s)) return scope.indexOf(s);
		}
		return 0;
	}
	
	public void program(Program p) throws Exception{
		scope.add(p.getName());
		caslList.add("CASL\tSTART\tBEGIN");
		caslList.add("BEGIN\tLAD\tGR6,0");
		caslList.add("\tLAD\tGR7, LIBBUF");
		
		if(p.getBlock().getVariableDeclarations()!=null){
			for(VariableDeclaration v : p.getBlock().getVariableDeclarations()){
				variableDeclaration(v,valList,vLenList);
			}
		}
		
		CompoundStatement(p.getCompoundStatement());
		caslList.add("\tRET");
		block(p.getBlock());
		caslList.add("VAR\tDS\t"+String.valueOf(valLengs));
		
	}
	
	public void block(Blocks b) throws Exception{
		if(b.getSubProgramDeclarations()!=null){
			for(SubProgramDeclaration s : b.getSubProgramDeclarations()){
				int i = valLen(s.getName());
				caslList.add("PROC"+String.valueOf(i)+"\tNOP");
				subProgramDeclaration(s);
				caslList.add("\tRET");
				tmpvList.clear();
				tmpvLenList.clear();
				parList.clear();
				parLenList.clear();
			}
		}
	}
	
	public void variableDeclaration(VariableDeclaration v, LinkedHashMap<String,ValType> vTypeList,LinkedHashMap<String,Integer> vLengsList) throws Exception{
		for(String val:v.getNames()) {
			vTypeList.put(val, v.getType().getValType());
			vLengsList.put(val,valLengs);
			valLengs += v.getType().getLength();
			
		}
	}
	
	public void subProgramDeclaration(SubProgramDeclaration sub) throws Exception{
		if(sub.getParameters()!=null){
			ArrayList<FormalParameter> p=sub.getParameters();
			
			for(FormalParameter param : p){
				for(String name: param.getNames()) {
					parList.put(name,param.getValType());
					parLenList.put(name, valLengs);
					valLengs+=1;
				}
			}
		}
		
		if(sub.getVariableDeclaration()!=null){
			for(VariableDeclaration v : sub.getVariableDeclaration()){
				variableDeclaration(v,tmpvList,tmpvLenList);
			}
		}
		
		if(sub.getParameters()!=null){
			ArrayList<FormalParameter> p=sub.getParameters();
			Collections.reverse(p);
			for(FormalParameter param : p){
				ArrayList<String> paName = param.getNames();
				Collections.reverse(paName);
				for(String pp: paName) {
					int i = valLen(pp);
					caslList.add("\tLD\tGR1,GR8");
					caslList.add("\tADDA\tGR1,=1");
					caslList.add("\tLD\tGR2, 0, GR1");
					caslList.add("\tLD\tGR3,="+String.valueOf(i));
					caslList.add("\tST\tGR2,VAR,GR3");
					caslList.add("\tSUBA\tGR1,=1");
					caslList.add("\tLD\tGR1, 0, GR8");
					caslList.add("\tADDA\tGR8, =1");
					caslList.add("\tST\tGR1, 0, GR8");
				}
			}
		}else {
			caslList.add("\tLD\tGR1,GR8");
			caslList.add("\tADDA\tGR1,=0");
		}
		
		Statement comState = sub.getCompoundStatement();
		if(comState instanceof CompoundStatement) {
			CompoundStatement((CompoundStatement)comState);
		}
	}
		
	public void CompoundStatement(CompoundStatement com) throws Exception{
		if(com!=null) {
			ArrayList<Statement> statements = com.getStatements();
			
			for(Statement state:statements) {
				if(state instanceof AssignmentStatement) {
					AssignmentStatement assignState = (AssignmentStatement) state;
					assignmentStatement(assignState);
				}
				else if(state instanceof ProcedureCallStatement) {
					ProcedureCallStatement proState = (ProcedureCallStatement) state;
					procedureCallStatement(proState);
				}
				else if(state instanceof InputStatement) {
					InputStatement inState = (InputStatement) state;
					inputStatement(inState);
				}
				else if(state instanceof OutputStatement) {
					OutputStatement outState = (OutputStatement) state;
					outputStatement(outState);
				}
				else if(state instanceof CompoundStatement) {
					CompoundStatement comState = (CompoundStatement) state;
					CompoundStatement(comState);
				}
				else if(state instanceof IfThenStatement) {
					IfThenStatement ifThenState = (IfThenStatement) state;
					ifThenStatement(ifThenState);
				}
				else if(state instanceof IfThenElseStatement) {
					IfThenElseStatement ifThenElseState = (IfThenElseStatement) state;
					ifThenElseStatement(ifThenElseState);

				}
				else if(state instanceof WhileDoStatement) {
					WhileDoStatement whileState = (WhileDoStatement) state;
					whileDoStatement(whileState);
				}
			}
		}
		return;
	}
	
	public void ifThenElseStatement(IfThenElseStatement n) throws Exception{
		expression(n.getExpression());
		caslList.add("\tPOP\tGR1");
		caslList.add("\tCPA\tGR1, =#FFFF");
		caslList.add("\tJZE\tELSE"+String.valueOf(elseNum));
		int elNum=elseNum;
		elseNum+=1;
		
		Statement thenState = n.getThenStatement();
		if(thenState instanceof CompoundStatement) {
			CompoundStatement((CompoundStatement)thenState);
			caslList.add("\tJUMP\tENDIF"+String.valueOf(endifNum));
		}
		
		int enNum=endifNum;
		endifNum+=1;
		
		caslList.add("ELSE"+String.valueOf(elNum)+"\tNOP");
		Statement elseState = n.getElseStatement();
		if(elseState instanceof CompoundStatement) {
			CompoundStatement((CompoundStatement)elseState);
			caslList.add("ENDIF"+String.valueOf(enNum)+"\tNOP");
		}
		
	}
	
	public void ifThenStatement(IfThenStatement n) throws Exception{
		expression(n.getExpression());
		caslList.add("\tPOP\tGR1");
		caslList.add("\tCPA\tGR1, =#FFFF");
		caslList.add("\tJZE\tELSE"+String.valueOf(elseNum));
		int elNum=elseNum;
		elseNum+=1;
		Statement comState = n.getThenStatement();
		if(comState instanceof CompoundStatement) {
			CompoundStatement((CompoundStatement)comState);
			caslList.add("ELSE"+String.valueOf(elNum)+"\tNOP");
		}
	}
	
	
	public void whileDoStatement(WhileDoStatement n) throws Exception{
		caslList.add("LOOP"+String.valueOf(loopNum)+"\tNOP");
		int loNum=loopNum;
		loopNum+=1;
		
		expression(n.getCondition());
		caslList.add("\tPOP\tGR1");
		caslList.add("\tCPL\tGR1, =#FFFF");
		caslList.add("\tJZE\tENDLP"+String.valueOf(endlpNum));	
		int enNum=endlpNum;
		endlpNum+=1;
		
		Statement comState = n.getDoStatement();
		if(comState instanceof CompoundStatement) {
			CompoundStatement((CompoundStatement)comState);
			caslList.add("\tJUMP\tLOOP"+String.valueOf(loNum));
		}
		caslList.add("ENDLP"+String.valueOf(enNum)+"\tNOP");
	}
	
	public void assignmentStatement(AssignmentStatement assign) throws Exception{
		Expression assignExpress = assign.getExpression();
		expression(assignExpress);
		Variables assignV = assign.getVariable();
		variable(assignV);		
		caslList.add("\tPOP\tGR1");
		caslList.add("\tST\tGR1, VAR, GR2");
	}

	public void expression(Expression exp) throws Exception{
		Token t;
		if(exp.getRecord()!=null) {
			  t = exp.getRecord().getToken();
		}else {
			SimpleExpression sExp = (SimpleExpression)exp;
			simpleExpression(sExp);
			return;
		}
		
		if(t.isRelationalOperator()) {
			if(exp.getLeft()==null && exp.getRight()==null) {
				if(exp instanceof Factor) {
					Expression e =((Factor) exp).getExpression();
					expression(e);
				}
				return;
			}
			if(exp.getLeft() instanceof SimpleExpression) {
				simpleExpression((SimpleExpression)exp.getLeft());
			}else if(exp.getLeft() instanceof Term) {
				term((Term)exp.getLeft());
			}else if(exp.getLeft() instanceof Factor) {
				factor((Factor)exp.getLeft());
			}
			
		    if(exp.getRight() instanceof SimpleExpression) {
				simpleExpression((SimpleExpression)exp.getRight());
			}else if(exp.getRight() instanceof Term) {
				term((Term)exp.getRight());
			}else if(exp.getRight() instanceof Factor) {
				factor((Factor)exp.getRight());
			}
			
			String L1="TRUE"+String.valueOf(trueNum);
			String L2="BOTH"+String.valueOf(bothNum);
			trueNum+=1;
			bothNum+=1;
			
			switch(t){
				case SEQUAL:
					caslList.add("\tPOP\tGR2");
					caslList.add("\tPOP\tGR1");
					caslList.add("\tCPA\tGR1,GR2");
					caslList.add("\tJZE\t"+L1);
					caslList.add("\tLD\tGR1, =#FFFF");
					caslList.add("\tJUMP\t"+L2);
					caslList.add(L1+"\tLD\tGR1,=#0000");
					caslList.add(L2+"\tPUSH\t0,GR1");
					break;
				case SNOTEQUAL:
					caslList.add("\tPOP\tGR2");
					caslList.add("\tPOP\tGR1");
					caslList.add("\tCPA\tGR1,GR2");
					caslList.add("\tJNZ\t"+L1);
					caslList.add("\tLD\tGR1, =#FFFF");
					caslList.add("\tJUMP\t"+L2);
					caslList.add(L1+"\tLD\tGR1,=#0000");
					caslList.add(L2+"\tPUSH\t0,GR1");
					break;
				case SLESS:
					caslList.add("\tPOP\tGR2");
					caslList.add("\tPOP\tGR1");
					caslList.add("\tCPA\tGR1,GR2");
					caslList.add("\tJMI\t"+L1);
					caslList.add("\tLD\tGR1, =#FFFF");
					caslList.add("\tJUMP\t"+L2);
					caslList.add(L1+"\tLD\tGR1,=#0000");
					caslList.add(L2+"\tPUSH\t0,GR1");
					break;
				case SLESSEQUAL:
					caslList.add("\tPOP\tGR2");
					caslList.add("\tPOP\tGR1");
					caslList.add("\tCPA\tGR1,GR2");
					caslList.add("\tJPL\t"+L1);
					caslList.add("\tLD\tGR1, =#0000");
					caslList.add("\tJUMP\t"+L2);
					caslList.add(L1+"\tLD\tGR1,=#FFFF");
					caslList.add(L2+"\tPUSH\t0,GR1");
					break;
				case SGREAT:
					caslList.add("\tPOP\tGR2");
					caslList.add("\tPOP\tGR1");
					caslList.add("\tCPA\tGR1,GR2");
					caslList.add("\tJPL\t"+L1);
					caslList.add("\tLD\tGR1, =#FFFF");
					caslList.add("\tJUMP\t"+L2);
					caslList.add(L1+"\tLD\tGR1,=#0000");
					caslList.add(L2+"\tPUSH\t0,GR1");
					break;
				case SGREATEQUAL:
					caslList.add("\tPOP\tGR2");
					caslList.add("\tPOP\tGR1");
					caslList.add("\tCPA\tGR1,GR2");
					caslList.add("\tJMI\t"+L1);
					caslList.add("\tLD\tGR1, =#0000");
					caslList.add("\tJUMP\t"+L2);
					caslList.add(L1+"\tLD\tGR1,=#FFFF");
					caslList.add(L2+"\tPUSH\t0,GR1");
					break;
			default:
				break;
			}
		}else if(exp instanceof SimpleExpression) {
			simpleExpression((SimpleExpression)exp);
		}else if(exp instanceof Term) {
			term((Term)exp);
		}else if(exp instanceof Factor) {
			factor((Factor)exp);
		}
	}
	
	public void simpleExpression(SimpleExpression expression) throws Exception{
		Token token;
		if(expression.getRecord()!=null) {
			token = expression.getRecord().getToken();
		}else {
			token = expression.getLeft().getRecord().getToken();
		}

		if(token.isAdditiveOperator()) {	
			if(expression.getLeft() instanceof SimpleExpression) {			
				SimpleExpression left = (SimpleExpression)expression.getLeft();
				simpleExpression(left);
			}else if(expression.getLeft() instanceof Term) {			
				Term left = (Term)expression.getLeft();
				term(left);
			}else if(expression.getLeft() instanceof Factor){
				factor((Factor)expression.getLeft());
			}
		
			if(expression instanceof SimpleExpression) {
				SimpleExpression sExp = (SimpleExpression)expression;
				sign(sExp.getSign());
			}
		
			if(expression.getRight() instanceof SimpleExpression) {			
				SimpleExpression right = (SimpleExpression)expression.getRight();
				simpleExpression(right);
			}else if(expression.getRight() instanceof Term){
				Term right = (Term)expression.getRight();
				term(right);
				caslList.add("\tPOP\tGR2");
				caslList.add("\tPOP\tGR1");
				if(token ==Token.SPLUS) {
					caslList.add("\tADDA\tGR1, GR2");
				}else if(token == Token.SMINUS) {
					caslList.add("\tSUBA\tGR1, GR2");
				}else if(token ==Token.SOR) {
					caslList.add("\tOR\tGR1,GR2");
				}
				caslList.add("\tPUSH\t0, GR1");
			}else if(expression.getRight() instanceof Factor) {
				Factor right = (Factor)expression.getRight();
				factor(right);
				caslList.add("\tPOP\tGR2");
				caslList.add("\tPOP\tGR1");
				if(token ==Token.SPLUS) {
					caslList.add("\tADDA\tGR1, GR2");
				}else if(token == Token.SMINUS) {
					caslList.add("\tSUBA\tGR1, GR2");
				}else if(token ==Token.SOR) {
					caslList.add("\tOR\tGR1,GR2");
				}
				caslList.add("\tPUSH\t0, GR1");
			}
		}else {
			if(expression.getLeft() instanceof Factor) {
				Factor left = (Factor)expression.getLeft();
				factor(left);
			}
			sign(expression.getSign());
		}
	}
	
	public void sign(boolean s) throws Exception{
		if(s == false) {
			caslList.add("\tPOP\tGR2");
			caslList.add("\tLD\tGR1,=0");
			caslList.add("\tSUBA\tGR1,GR2");
			caslList.add("\tPUSH\t0,GR1");
		}
	}
	
	public void term(Term expression) throws Exception{
		Token token;
		if(expression.getRecord()!=null) {
			token = expression.getRecord().getToken();
		}else {
			token = expression.getLeft().getRecord().getToken();
		}
		
		if(token.isMultiplicativeOperator()){
			if(expression.getLeft() instanceof Term) {
				Term left = (Term)expression.getLeft();
				term(left);
			}else if(expression.getLeft() instanceof Factor) {
				Factor left = (Factor)expression.getLeft();
				factor(left);
			}
				
			if(expression.getRight() instanceof Term) {
				Term right = (Term)expression.getRight();
				term(right);
			}else if(expression.getRight() instanceof Factor){
				Factor right = (Factor)expression.getRight();
				factor(right);
				caslList.add("\tPOP\tGR2");
				caslList.add("\tPOP\tGR1");
				if(token == Token.SSTAR) {
					caslList.add("\tCALL\tMULT");
					caslList.add("\tPUSH\t0,GR2");
				}else if(token == Token.SDIVD ) {
					caslList.add("\tCALL\tDIV");
					caslList.add("\tPUSH\t0,GR2");
				}else if(token==Token.SMOD) {
					caslList.add("\tCALL\tDIV");
					caslList.add("\tPUSH\t0,GR1");
				}else if(token==Token.SAND) {
					caslList.add("\tAND\tGR1,GR2");
					caslList.add("\tPUSH\t0,GR1");
				}
			}
		}
	}
				
	
	public void factor(Factor f) throws Exception{
		if(f.getExpression()!=null) {
			Expression e = f.getExpression();
			expression(e);
		}else if(f.getValType() == ValType.tInteger) {//int
			caslList.add("\tPUSH\t"+f.getRecord().getName());
		}else if(f.getValType()==ValType.tBoolean) {
			if(f.getRecord().getToken()==Token.STRUE) {
				caslList.add("\tPUSH\t#0000");
			}else {
				caslList.add("\tPUSH\t#FFFF");
			}
		}else if(f.getValType()==ValType.tString){
			caslList.add("\tLAD\tGR2,CHAR"+String.valueOf(charNum));
			caslList.add("\tPUSH\t0,GR2");
			nameLists.put("CHAR"+String.valueOf(charNum),f.getRecord().getName());
			charNum+=1;
		}else if(f.getValType()==ValType.tChar) {
			caslList.add("\tLD\tGR1, ="+f.getRecord().getName());
			caslList.add("\tPUSH\t0,GR1");
		}else if(f.getNotFactor() !=null) {
			factor(f.getNotFactor());
			caslList.add("\tPOP\tGR1");
			caslList.add("\tXOR\tGR1, =#FFFF");
			caslList.add("\tPUSH\t0, GR1");
		}else if(f.getVariable()!=null) {
			variable(f.getVariable());
			caslList.add("\tLD\tGR1, VAR, GR2");
			caslList.add("\tPUSH\t0,GR1");
		}
	}
	
	public void variable(Variables v)  throws Exception{
		Expression subscript = v.getSubscript();
		if(subscript==null) {
			pureVariable(v);
		}else {
			subscriptedVariable(v);
		}
	}
	
	public void pureVariable(Variables pv) throws Exception{
		int i = valLen(pv.getRecord().getName());
		caslList.add("\tLD\tGR2\t,="+String.valueOf(i));
	}
	
	public void subscriptedVariable(Variables sv) throws Exception{
		Expression subscript = sv.getSubscript();
		expression(subscript);
		caslList.add("\tPOP\tGR2");
		int i = valLen(sv.getRecord().getName());
		caslList.add("\tADDA\tGR2, ="+String.valueOf(i-1));
		return;
	}
	
	public void procedureCallStatement(ProcedureCallStatement n) throws Exception{
		scope.add(n.getName());
		if(n.getExpressions() != null) {
			for(Expression e: n.getExpressions()) {
				expression(e);
			}
		}
		int i = valLen(n.getName());
		caslList.add("\tCALL\tPROC"+String.valueOf(i));
		return;
	}
	
	public void inputStatement(InputStatement in) throws Exception{
		if(in.getVariables()!=null){
			for(Variables v : in.getVariables()){
				variable(v);
				switch(v.getValType()){
					case tInteger:
						caslList.add("\tCALL\tRDINT");
						break;
					
					case tChar:
						caslList.add("\tCALL\tRDCH");
						break;
					
					case tString:
						caslList.add("\tCALL\tRDSTR");
						break;
						
					default:
						break;
				}
			}
			return;
		}
		caslList.add("\tCALL\tRDLN");
	}
	
	public void outputStatement(OutputStatement out) throws Exception{
		if(out.getExpressions()!=null){
			for(Expression e : out.getExpressions()){
				if(e.getValType()==ValType.tString) {
					String len = Integer.toString(e.getRecord().getName().length()-2);
					caslList.add("\tLD\tGR1,="+ len);
					caslList.add("\tPUSH\t0,GR1");
					expression(e);
					caslList.add("\tPOP\tGR2");
					caslList.add("\tPOP\tGR1");
					caslList.add("\tCALL\tWRTSTR");
					
				}else if(e.getValType()==ValType.tChar){	
					expression(e);
					caslList.add("\tPOP\tGR2");
					caslList.add("\tCALL\tWRTCH");
				}else if(e.getValType()==ValType.tInteger){
					expression(e);
					caslList.add("\tPOP\tGR2");
					caslList.add("\tCALL\tWRTINT");
				}else if(e.getValType() == null) {
					ValType type = nameType(e.getRecord().getName());
					if(type == ValType.tChar||type==ValType.tString) {
						expression(e);
						caslList.add("\tPOP\tGR2");
						caslList.add("\tCALL\tWRTCH");
					}else if(type==ValType.tInteger||type==ValType.tIntegerArray){
						expression(e);
						caslList.add("\tPOP\tGR2");
						caslList.add("\tCALL\tWRTINT");
					}
				}
			}	
			caslList.add("\tCALL\tWRTLN");
		}
	}
	public ArrayList<String> getCommand(){
		return caslList;
	}
	public LinkedHashMap<String,String> getNames(){
		return nameLists;
	}

}