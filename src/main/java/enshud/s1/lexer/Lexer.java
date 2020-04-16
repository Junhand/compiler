package enshud.s1.lexer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
	public static void main(final String[] args) {
		// normalの確認
		new Lexer().run("data/pas/normal01.pas", "tmp/out1.ts");
		//new Lexer().run("data/pas/normal02.pas", "tmp/out2.ts");
		//new Lexer().run("data/pas/normal03.pas", "tmp/out3.ts");
	}

	/**
	 * TODO
	 * 
	 * 開発対象となるLexer実行メソッド．
	 * 以下の仕様を満たすこと．
	 * 
	 * 仕様:
	 * 第一引数で指定されたpasファイルを読み込み，トークン列に分割する．
	 * トークン列は第二引数で指定されたtsファイルに書き出すこと．
	 * 正常に処理が終了した場合は標準出力に"OK"を，
	 * 入力ファイルが見つからない場合は標準エラーに"File not found"と出力して終了すること．
	 * 
	 * @param inputFileName 入力pasファイル名
	 * @param outputFileName 出力tsファイル名
	 */
	public static String getLineOutput(String string, Token token, int lineNumber)
	{
		String s=string+"\t"+token.toString()+"\t"+token.ordinal()+"\t"+lineNumber+"\n";
		//System.out.print(s);
		String output = s;
		return output;
	}
	
	public void run(final String inputFileName, final String outputFileName) {

		// TODO
		 try {
			 	//Fileクラスに読み込むファイルを指定する
				File in=new File(inputFileName);
				File out=new File(outputFileName);
				
				//FileReaderクラスのオブジェクトを生成する
				FileReader fr=new FileReader(in);
				BufferedReader br=new BufferedReader(fr);
				
				//FileWriterクラスのオブジェクトを生成する
				FileWriter fw=new FileWriter(out);
				BufferedWriter bw=new BufferedWriter(fw);
        		
            	int lineNumber = 0;
	            String line;
	            String output = "";
	            String writeLine = "";
	            int charNumber = 0;
	            boolean found;
	    
	              //  String spell = "program|var|array|of|procedure|begin|end|if|then|else|while|do|not|or|div|mod|and|char|integer|boolean|readln|writeln|true|false";
	               // String special = "+ | - | * | / | = | <> | < | <= | > | >= | ( | ) | [ | ] | := | . | , | .. | : | ; |"+ spell;
	               // String alphabet = "[a-zA-Z]";
	               // String number = "[0-9]";
	                //String name = alphabet +"("+ alphabet +"|"+ number +")";
	               // String unsignedInt = number +"("+ number +")";
	               // String word = "[^\n']+";
	               // String words = "'"+ word + "("+ word + ")" +"'";
	                
	               // String splitWord = "[^}]+|\n";
	                //String splitWords = "{|" + splitWord + "|}";
	                //String splitSymbol = "\t|" +splitWords +"|\n";
	                //String[] split = target.split("((?<=:)|(?=:))");
	                
	                while((line=br.readLine())!=null){
	                	
	    				lineNumber += 1;
	    				//System.out.println(lineNumber+": |"+line) ;
	    				
	    				for(charNumber=0; charNumber<line.length();){
	    					
	    					output="";
	    					writeLine="";
	    					if(line.charAt(charNumber)=='{'){//{}かどうか判別
	    					
	    						charNumber += 1;
	    						while(charNumber<line.length() && line.charAt(charNumber)!='}')
	    						{
	    							charNumber += 1;
	    						}
	    						charNumber += 1;
	    						
	    					}else if(line.charAt(charNumber)=='\''){//文字かどうか判別
	    					
	    						output+="\'";
	    						charNumber += 1;
	    						
	    						while(charNumber<line.length() && line.charAt(charNumber)!='\''){
	    						
	    							output+=line.charAt(charNumber);
	    							charNumber += 1;
	    						}
	    						
	    						if(charNumber<line.length() && line.charAt(charNumber)=='\''){
	    						
	    							output+="\'";
	    						}
	    						
	    						writeLine += getLineOutput(output, Token.SSTRING, lineNumber);
	    						bw.write(writeLine);
	    						charNumber += 1;
	    						
	    					}else if(Character.isDigit(line.charAt(charNumber))){//数字かどうか判別
	    					
	    						while(charNumber<line.length() && Character.isDigit(line.charAt(charNumber))) {
	    						
	    							output+=line.charAt(charNumber);
	    							charNumber += 1;
	    						}
	    						writeLine += getLineOutput(output, Token.SCONSTANT, lineNumber);
	    						bw.write(writeLine);
	    						
	    					}else if(!Character.isWhitespace(line.charAt(charNumber))){//スペースがない場合
	    						
	    						found=false;
	    						for(Token Token : Arrays.copyOfRange(Token.values(), 0, Token.SDOT.ordinal()+1)){//Token全部見る
	    						
	    							if(line.indexOf(Token.getToken(), charNumber)==charNumber){//トークンの比較
	    								
	    								if(!((Token==Token.SLESS || Token==Token.SGREAT || Token==Token.SCOLON) && line.charAt(charNumber+1)=='=') &&
	    										(charNumber+Token.getToken().length()>=line.length() ||Token.ordinal()>Token.SWRITELN.ordinal() ||!Character.isLetterOrDigit(line.charAt(charNumber+Token.getToken().length()))
	    								)){
	    									writeLine += getLineOutput(Token.getToken(), Token, lineNumber);
	    									found=true;
	    									charNumber = charNumber + Token.getToken().length();
	    									bw.write(writeLine);
	    									break;
	    								}
	    								
	    							}else if(line.indexOf("/", charNumber)==charNumber){
	    								writeLine += getLineOutput("/", Token.SDIVD, lineNumber);
	    								found=true;
	    								charNumber += 1;
	    								bw.write(writeLine);
	    								break;
	    							}
	    						}
	    						
	    						if(!found){
	    							
	    							while(charNumber<line.length() && Character.isLetterOrDigit(line.charAt(charNumber))){
	    								output+=line.charAt(charNumber);
	    								charNumber += 1;
	    							}
	    							writeLine += getLineOutput(output, Token.SIDENTIFIER, lineNumber);
	    							bw.write(writeLine);
	    						}
	    						
	    					}else{//スペースを飛ばす
	    						
	    						charNumber += 1;
	    					}
	    				}
	    			}

	    		System.out.println("OK");
	    		br.close();
	    		fr.close();
	    		bw.close();
	    		fw.close();
	                
	        }catch(FileNotFoundException e){
	        	  System.err.println("File not found");
	        } catch (IOException e) {
	            System.err.println("File not found");
	        }
	}
}
