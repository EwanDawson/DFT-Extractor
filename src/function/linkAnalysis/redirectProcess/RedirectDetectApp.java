package function.linkAnalysis.redirectProcess;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RedirectDetectApp {

	public RedirectDetectApp() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		dsTermsLemma();

	}
	

	
	
	public static void dsTermsLemma(){
		String termFile = "F:/Wiki分类体系/DS/DS_terms.csv";
		String outfile = "F:/Wiki分类体系/DS/DS_redirect_auto.csv";
		testFromTerms(termFile,outfile);
		System.out.println("w2");
	}
	
	public static void testFile() {

		RedirectDetect rd = new RedirectDetect();
		
		FileReader fr = null;
		BufferedReader br = null;

		FileWriter fw = null;
		String file="F:/Wiki目录/redirect.csv";
		String outfile="F:/Wiki目录/result.csv";
		
		try {
			fw = new FileWriter(outfile);
			
			System.out.println("processing:"+file);
			fr = new FileReader(file);
			br = new BufferedReader(fr); // 可以按行读取
			String str = null;
			while ((str = br.readLine()) != null) {
				
				String[] terms=str.split(",");
				String term = terms[0];
				String[] newTerm = new String[1];
				boolean flag = rd.DetectFromWeb(term, newTerm);
				if (flag) {
					System.out.println("redirect: " + term + "->" + newTerm[0]);
				} else {
					System.out.println("nodirect: " + term + "=" + newTerm[0]);
				}
				
				String result=terms[0];
				for(int i=1;i<terms.length;i++){
					result=result+","+terms[i];
				}
				newTerm[0]=newTerm[0].replaceAll(" ", "_");
				result=result+","+newTerm[0]+","+flag;
				
				fw.write(result+"\n");
			}
			
			br.close();
			fr.close();
			fw.close();
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*输入：term list文件
	*输出：csv文件；4列
	*  1: 原始term
	 * 2: 重定向的term
	 * 3: 标志 true 表示进行重定向了
	 * 4: 与3相同，用于手工修改。 
	*/
	public static void testFromTerms(String termFile, String outfile) {
		RedirectDetect rd = new RedirectDetect();
		FileReader fr = null;
		BufferedReader br = null;
		FileWriter fw = null;
		try {
			fw = new FileWriter(outfile);

			System.out.println("processing:" + termFile);
			fr = new FileReader(termFile);
			br = new BufferedReader(fr); // 可以按行读取
			String str = null;
			while ((str = br.readLine()) != null) {

				String term = str;
				String[] newTerm = new String[1];
				boolean flag = rd.DetectFromWeb(term, newTerm);
				if (flag) {
					System.out.println("redirect: " + term + "->" + newTerm[0]);
				} else {
					System.out.println("nodirect: " + term + "=" + newTerm[0]);
				}
				newTerm[0] = newTerm[0].replaceAll(" ", "_");
				String result = str + "," + newTerm[0] + "," + flag+ "," + flag;
				fw.write(result + "\n");
			}
			br.close();
			fr.close();
			fw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
