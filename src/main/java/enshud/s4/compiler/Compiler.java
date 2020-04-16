package enshud.s4.compiler;

import enshud.casl.CaslSimulator;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Constructor;
import enshud.s3.checker.ValType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class Compiler {

	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
	public static void main(final String[] args) {
		// Compilerを実行してcasを生成する
		new Compiler().run("data/ts/normal15.ts", "tmp/out.cas");
		// 上記casを，CASLアセンブラ & COMETシミュレータで実行する
		CaslSimulator.run("tmp/out.cas", "tmp/out.ans");
	}

	/**
	 * TODO
	 * 
	 * 開発対象となるCompiler実行メソッド．
	 * 以下の仕様を満たすこと．
	 * 
	 * 仕様:
	 * 第一引数で指定されたtsファイルを読み込み，CASL IIプログラムにコンパイルする．
	 * コンパイル結果のCASL IIプログラムは第二引数で指定されたcasファイルに書き出すこと．
	 * 構文的もしくは意味的なエラーを発見した場合は標準エラーにエラーメッセージを出力すること．
	 * （エラーメッセージの内容はChecker.run()の出力に準じるものとする．）
	 * 入力ファイルが見つからない場合は標準エラーに"File not found"と出力して終了すること．
	 * 
	 * @param inputFileName 入力tsファイル名
	 * @param outputFileName 出力casファイル名
	 */
	public void run(final String inputFileName, final String outputFileName) {

		// TODO 
				String line;
				Checker checker=new Checker();
				checker.run(inputFileName);
				if(checker.success()){
					
					ConvertCASL2 convert=new ConvertCASL2();
					convert.run(checker.getRoot());

					try(FileWriter fw=new FileWriter(new File(outputFileName))){
						BufferedWriter bw=new BufferedWriter(fw);
						ArrayList<String> commands = convert.getCommand();
						LinkedHashMap<String,String> names = convert.getNames();
						
						for(String co : commands) {
							bw.write(co+"\n");	
						}
						for(Map.Entry<String, String> name:names.entrySet()) {
							bw.write(name.getKey()+"\tDC\t"+name.getValue()+"\n");
						}
						bw.write("LIBBUF\tDS\t256;\n");
						bw.write("\tEND\t;\n"); 
						File in=new File("data/cas/lib.cas");
						FileReader fr=new FileReader(in);
						BufferedReader br=new BufferedReader(fr);
						while((line=br.readLine())!=null){
							bw.write(line+"\n");			
						}
						
						br.close();
						fr.close();
						bw.close();
						fw.close();
					}catch(IOException e){
						System.out.print(e);
					}
				}
	}
	
}
