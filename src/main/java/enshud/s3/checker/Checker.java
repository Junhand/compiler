package enshud.s3.checker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;



public class Checker {

	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
	public static void main(final String[] args) {
		// normalの確認
		new Checker().run("data/ts/normal02.ts");
		//new Checker().run("data/ts/normal20.ts");

		// synerrの確認
		//new Checker().run("data/ts/synerr07.ts");
		//new Checker().run("data/ts/synerr02.ts");

		// semerrの確認
		//new Checker().run("data/ts/semerr01.ts");
		//new Checker().run("data/ts/semerr08.ts");
	}

	/**
	 * TODO
	 *
	 * 開発対象となるChecker実行メソッド．
	 * 以下の仕様を満たすこと．
	 *
	 * 仕様:
	 * 第一引数で指定されたtsファイルを読み込み，意味解析を行う．
	 * 意味的に正しい場合は標準出力に"OK"を，正しくない場合は"Semantic error: line"という文字列とともに，
	 * 最初のエラーを見つけた行の番号を標準エラーに出力すること （例: "Semantic error: line 6"）．
	 * また，構文的なエラーが含まれる場合もエラーメッセージを表示すること（例： "Syntaxerror: line 1"）．
	 * 入力ファイル内に複数のエラーが含まれる場合は，最初に見つけたエラーのみを出力すること．
	 * 入力ファイルが見つからない場合は標準エラーに"File not found"と出力して終了すること．
	 *
	 * @param inputFileName 入力tsファイル名
	 */

	private boolean fail = false;
	Program root;
	

	
	public void run(final String inputFileName){
		String line;
		String [] list;
		ArrayList<Records> records = new ArrayList<Records>();
		
		try{
			File in=new File(inputFileName);
			FileReader fr=new FileReader(in);
			BufferedReader br=new BufferedReader(fr);
			while((line=br.readLine())!=null){

				list=line.split("\t");
				Token[] t=Token.values();
				int i;
				for(i=0; i<t.length; ++i){

					if(t[i].toString().equals(list[1])){
						records.add(new Records(t[i], list[0], Integer.parseInt(list[3])));
						break;
					}
				}
			}
			br.close();
			fr.close();
			
		}catch(FileNotFoundException e){
			System.err.println("File not found");
			return;
			
		}catch(IOException e){
			System.err.println(e);
			return;
		}
		
		Constructor c= new Constructor(inputFileName, records);
		if(c.success()){
			root = c.getRoot();
						
			System.out.println("OK");
		}else {
			fail = true;
		}
	}
	
	public Program getRoot(){
		return root;
	}

	public boolean success(){
		return null!=root && !fail;
	}
}
