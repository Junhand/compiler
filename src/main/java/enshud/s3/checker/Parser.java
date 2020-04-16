package enshud.s3.checker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import enshud.s1.lexer.Token;

public class Parser {

	/**
	 * サンプルmainメソッド． 単体テストの対象ではないので自由に改変しても良い．
	 * 
	 * @throws FileNotFoundException
	 */
	public static void main(final String[] args) throws FileNotFoundException {
		// normalの確認
		new Parser().run("data/ts/normal01.ts");
		new Parser().run("data/ts/normal02.ts");

		// synerrの確認
		new Parser().run("data/ts/synerr01.ts");
		new Parser().run("data/ts/synerr02.ts");
	}

	/**
	 * TODO
	 * 
	 * 開発対象となるParser実行メソッド． 以下の仕様を満たすこと．
	 * 
	 * 仕様: 第一引数で指定されたtsファイルを読み込み，構文解析を行う． 構文が正しい場合は標準出力に"OK"を，正しくない場合は"Syntax error:
	 * line"という文字列とともに， 最初のエラーを見つけた行の番号を標準エラーに出力すること （例: "Syntax error: line 1"）．
	 * 入力ファイル内に複数のエラーが含まれる場合は，最初に見つけたエラーのみを出力すること． 入力ファイルが見つからない場合は標準エラーに"File not
	 * found"と出力して終了すること．
	 * 
	 * @param inputFileName
	 *            入力tsファイル名
	 */
	ArrayList<Token> tokens = new ArrayList<Token>();
	ArrayList<String> strings = new ArrayList<String>();
	ArrayList<Integer> lineNumber = new ArrayList<Integer>();

	int rowNumber = 0;
	int rowStack = 0;
	boolean fail = false;

	private void syntaxErrorOutput() {
		if (!fail) {
			System.err.println("Syntax error: line " + lineNumber.get(rowNumber));
			fail = true;
		}
	}

	private boolean tokenSyntax(Token Token) {
		return tokens.get(rowNumber) == Token ? nextLine() : false;
	}

	private boolean nextLine() {
		if (rowNumber < tokens.size()) {
			rowNumber += 1;
			return true;
		}
		return false;
	}
	
	private void previousLine() {
		while(rowNumber > rowStack) rowNumber -= 1;
	}
	
	private void lineStack(int rowNumber) {
		rowStack = rowNumber;
	}

	public boolean run(final String inputFileName) {

		// TODO
		String[] list;
		String line;

		// Fileクラスに読み込むファイルを指定する
		try {
			File in = new File(inputFileName);
			// FileReaderクラスのオブジェクトを生成する
			FileReader fr = new FileReader(in);
			BufferedReader br = new BufferedReader(fr);

			while ((line = br.readLine()) != null) {
				list = line.split("\t");// タブで分割をする
				Token[] token = Token.values();

				for (int i = 0; i < token.length; i++) {
					if (token[i].toString().equals(list[1])) {// 1列目がトークンであれば
						tokens.add(token[i]);// そのトークンを加える
						break;
					}
				}

				strings.add(list[0]);// トークン名
				lineNumber.add(Integer.parseInt(list[3]));
			} // 行数

			program();
			
			br.close();
			fr.close();
			
			if(fail) return false;
			return true;
		} catch (FileNotFoundException e) {
			System.err.println("File not found");
			return false;
		} catch (IOException e) {
			System.err.println("File not found");
			return false;
		}
	}

	private boolean program() {// プログラム
		if (tokenSyntax(Token.SPROGRAM) && programName() && tokenSyntax(Token.SSEMICOLON) 
				&& block() && compoundStatement()
				&& tokenSyntax(Token.SDOT)) {
			return true;
		}
		syntaxErrorOutput();
		return false;
	}

	private boolean programName() {// プログラム名
		return tokenSyntax(Token.SIDENTIFIER);
	}

	private boolean block() {// ブロック
		if (variableDeclaration() && subProgramDeclarations()) {
			return true;
		}
		syntaxErrorOutput();
		return false;
	}

	private boolean variableDeclaration() {// 変数宣言
		if (tokenSyntax(Token.SVAR)) {
			if(variableDeclearationsLine()) return true;//1回
			syntaxErrorOutput();
			return false;
		}
		return true;//0回
	}

	private boolean variableDeclearationsLine() {//変数宣言の並び
		if(variableNameLine() && tokenSyntax(Token.SCOLON) && type() && tokenSyntax(Token.SSEMICOLON)) {
				while(variableNameLine()) {
					if(!tokenSyntax(Token.SCOLON) || !type() || !tokenSyntax(Token.SSEMICOLON))return false;//次の変数名の並びから確認
				}
				return true;
			}
			syntaxErrorOutput();
			return false;
	}
	
	private boolean variableNameLine() {// 変数名の並び
		if(variableName()) {
			while (tokenSyntax(Token.SCOMMA)) {//,があれば
				if(!variableName()) return false;//変数名も必要
			}
			return true;
		}
		return false;//1回目に変数名がない。,の後に変数名がない。
	}

	private boolean variableName() {// 変数名
		return tokenSyntax(Token.SIDENTIFIER);
	}
	
	private boolean type() {//型
		if(standardType() || arrayType()) return true;
		syntaxErrorOutput();
		return false;
	}
	
	private boolean standardType() {//標準型
		if(tokenSyntax(Token.SINTEGER) || tokenSyntax(Token.SCHAR) || tokenSyntax(Token.SBOOLEAN)) return true;
		return false;
	}
	
	private boolean arrayType() {//配列型
		if(tokenSyntax(Token.SARRAY) && tokenSyntax(Token.SLBRACKET) && minIndex()
			&& tokenSyntax(Token.SRANGE) && maxIndex() && tokenSyntax(Token.SRBRACKET) 
			&& tokenSyntax(Token.SOF) && standardType()) {
			return true;
		}
		syntaxErrorOutput();
		return false;
	}
	
	private boolean minIndex() {//添字の最小値
		return integer();
	}
	
	private boolean maxIndex() {//添字の最大値
		return integer();
	}
	
	private boolean integer() {//整数
		
		if(sign()) {
			if(naturalInteger()) return true;
			return false;
		}
		if(naturalInteger()) return true;
		return false;
	}
	
	private boolean sign() {//符号
		if(tokenSyntax(Token.SPLUS) || tokenSyntax(Token.SMINUS)) return true;
		return false;
	}
	
	private boolean subProgramDeclarations() {//副プログラム宣言群
		while(subProgramDeclaration()) {
			if(!tokenSyntax(Token.SSEMICOLON)) {
				syntaxErrorOutput();
				return false;
			}
		}
		return true;
	}
	
	private boolean subProgramDeclaration() {//副プログラム宣言
		if(subProgramHead() && variableDeclaration() && compoundStatement()) return true;
		return false;
	}
	
	private boolean subProgramHead() {//副プログラム頭部
		if(tokenSyntax(Token.SPROCEDURE) && procedureName() && formalParameter() && tokenSyntax(Token.SSEMICOLON)) return true;
		return false;
	}
	
	private boolean procedureName() {//手続き名
		return name();
	}
	private boolean formalParameter() {//仮パラメータ
		if(tokenSyntax(Token.SLPAREN)) {
			if(formalParameterLine() && tokenSyntax(Token.SRPAREN)) return true;//1回
			syntaxErrorOutput();
			return false;
		}
		return true;//0回
	}
	
	private boolean formalParameterLine() {//仮パラメータの並び
		if(formalParameterNameLine() && tokenSyntax(Token.SCOLON) && standardType()) {
			while(tokenSyntax(Token.SSEMICOLON)) {
				if(!formalParameterNameLine() || !tokenSyntax(Token.SCOLON) || !standardType()) {
					syntaxErrorOutput();
					return false;
				}
			}
			return true;
		}
		return false;//0回
		
	}
	
	private boolean formalParameterNameLine() {//仮パラメータ名の並び
		if(formalParameterName()) {
			while(tokenSyntax(Token.SCOMMA)) {
				if(!formalParameterName()) {
					syntaxErrorOutput();
					return false;
				}
			}
			return true;
		}
		return false;//0回
	}
	
	private boolean formalParameterName() {//仮パラメータ名
		return name();
	}
	
	private boolean compoundStatement() {//複合文
		if(tokenSyntax(Token.SBEGIN) && sentenceLine() && tokenSyntax(Token.SEND)) return true;
		return false;
	}
	
	private boolean sentenceLine() {//文の並び
		if(sentence() && tokenSyntax(Token.SSEMICOLON)) {
			while(sentence()) {
				if(!tokenSyntax(Token.SSEMICOLON)) {
					syntaxErrorOutput();
					return false;
				}
			}
			return true;
		}
		return false;//0回
	}
	
	private boolean sentence() {//文koko
		if(basicSentence() ||
		   (tokenSyntax(Token.SWHILE) && expression() && tokenSyntax(Token.SDO) && compoundStatement())
		   ) {
			return true;
		} else if(tokenSyntax(Token.SIF) && expression() && tokenSyntax(Token.STHEN) && compoundStatement()) {
			if (tokenSyntax(Token.SELSE)) {
				if(compoundStatement()) return true;
				syntaxErrorOutput();
				return false;
			}
			return true;
		}
		return false;
	}
	
	private boolean basicSentence() {//基本文koko
		if(InOutStatement() || compoundStatement()) {
			return true;
		}else {
			lineStack(rowNumber);
			if(assignmentStatement()) return true;
			previousLine();
			if(procedureCallstatement()) return true;
			return false;
		}
	}
	
	private boolean assignmentStatement() {//代入文
		if(leftSide() && tokenSyntax(Token.SASSIGN) && expression()) return true;
		return false;
	}
	
	private boolean leftSide() {//左辺
		return variable();
	}
	
	private boolean variable() {//変数
		if(pureVariable()) {
			if(subscriptedVariable()) return true;
			return false;
		}
		
		return false;
	}
	
	private boolean pureVariable() {//純変数
		return variableName();
	}
	
	private boolean subscriptedVariable() {//添字付き変数
		if(tokenSyntax(Token.SLBRACKET)) {
			if(subscript() && tokenSyntax(Token.SRBRACKET)) return true;
			return false;
		}
		return true;
	}
	
	private boolean subscript() {//添字
		return expression();
	}
	
	private boolean procedureCallstatement() {//手続き呼出し文
		if(procedureName()) {
			if(tokenSyntax(Token.SLPAREN)) {
				if(expressionLine() && tokenSyntax(Token.SRPAREN)) return true;
				return false;
			}
			return true;
		}
		return false;
	}
	
	private boolean expressionLine() {//式の並び
		if(expression()) {
			while(tokenSyntax(Token.SCOMMA)) {
				if(!expression()) return false;
			}
			return true;
		}
		syntaxErrorOutput();
		return false;
	}
	
	private boolean expression() {//式
		if(simpleExpression()) {
			if(relationalOperator()) {
				if(simpleExpression()) return true;
				return false;
			}
			return true;
		}
		return false;
	}
	
	private boolean simpleExpression() {//単純式
		sign();
		if(term()) {
			while(additiveOperator()) {
				if(!term()) return false;
			}
			return true;
		}
		return false;
	}
	
	private boolean term() {//項
		if(factor()) {
			while(multiplicativeOperator()) {
				if(!factor()) return false;
			}
			return true;
		}
		return false;
	}
	
	private boolean factor() {//因子
		if(variable() || constant() || 
		   (tokenSyntax(Token.SLPAREN) && expression() && tokenSyntax(Token.SRPAREN)) ||
		   (tokenSyntax(Token.SNOT) && factor())) return true;
		return false;
	}
	
	private boolean relationalOperator() {//関係演算子
		if(tokenSyntax(Token.SEQUAL) || tokenSyntax(Token.SNOTEQUAL) || tokenSyntax(Token.SLESS) ||
		   tokenSyntax(Token.SLESSEQUAL) || tokenSyntax(Token.SGREAT) || tokenSyntax(Token.SGREATEQUAL)) return true;
		return false;
	}
	
	private boolean additiveOperator() {//加法演算子
		if(tokenSyntax(Token.SPLUS) || tokenSyntax(Token.SMINUS) || tokenSyntax(Token.SOR)) return true;
		return false;
	}
	
	private boolean multiplicativeOperator() {//乗法演算子
		if(tokenSyntax(Token.SSTAR) || tokenSyntax(Token.SDIVD) || 
		   tokenSyntax(Token.SMOD) || tokenSyntax(Token.SAND)) return true;
		return false;
	}
	
	private boolean InOutStatement() {//入出力文
		if(tokenSyntax(Token.SREADLN)) {
			if(tokenSyntax(Token.SLPAREN)) {
				if(variableLine() && tokenSyntax(Token.SRPAREN)) return true;
				syntaxErrorOutput();
				return false;
			}
			return true;
		}
		
		if(tokenSyntax(Token.SWRITELN)) {
			if(tokenSyntax(Token.SLPAREN)) {
				if(expressionLine() && tokenSyntax(Token.SRPAREN)) return true;
				syntaxErrorOutput();
				return false;
			}
			return true;
		}
		return false;
	}
	
	private boolean variableLine() {//変数の並び
		if(variable()) {
			while(tokenSyntax(Token.SCOMMA)) {
				if(!variable()) return false;
			}
			return true;
		}
		return false;
	}
	
	private boolean constant() {//定数
		if(naturalInteger() || string() || tokenSyntax(Token.SFALSE) || tokenSyntax(Token.STRUE)) return true;
		return false;
	}
	
	private boolean naturalInteger() {//符号なし整数
		return tokenSyntax(Token.SCONSTANT);
	}
	
	private boolean string() {///文字列
		return tokenSyntax(Token.SSTRING);
	}
	
//	private boolean stringElement() {//文字列要素
//		
//	}

	private boolean name() {//名前
		return tokenSyntax(Token.SIDENTIFIER);
	}
	
//	private boolean englishCharacter() {//英字
//		return tokenSyntax(Token.SSTRING);
//	}
//
//	private boolean number() {//数字
//		return tokenSyntax(Token.SCONSTANT);
//	}
}
