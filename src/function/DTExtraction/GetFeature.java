package function.DTExtraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import function.util.FileUtil;
import function.util.SetUtil;
import function.util.StringUtil;
import function.crawler.WikiCategoryCrawler;

public class GetFeature {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String categoryDir = "F:\\compute\\CM\\html\\";
		File f=new File(categoryDir);
		File childs[]=f.listFiles();
		Vector<String> lines = new Vector<String>();
		Vector<String> terms = SetUtil.readSetFromFile("F:\\compute\\CM\\LdaNames_select.csv");
		for(int i=0;i<childs.length;i++){
			String path = childs[i].getAbsolutePath();
			System.out.println(path);
			String name = childs[i].getName();
			System.out.println(name);
			String term = name.substring(0, name.lastIndexOf("."));
			System.out.println(term);
			if(terms.contains(term.toLowerCase())) {
				Vector<String> cat = extractCategoryTerm(path);
				String line = term;
				for(int j = 0;j < cat.size();j++) {
					line = line + "," + cat.get(j);
				}
				lines.add(line);
			}
		}
		SetUtil.writeSetToFile(lines, "F:\\compute\\CM\\category.csv");
	}
	
	/**
	 * 获取文件的first Sentence
	 * 
	 * @param filePath
	 * @return
	 */
	public static Vector<String> extractFSWikiTerm(String filePath) {
		Vector<String> vTerm = new Vector<String>();
		String fileStr = FileUtil.readFile(filePath);
		int posStart = fileStr.indexOf("<p>") + 3;
		int posEnd = fileStr.indexOf(".", posStart);
		while (fileStr.substring(posEnd, posEnd + 3).equals(".S."))
			// 特殊处理U.S.
			posEnd = fileStr.indexOf(".", posEnd + 3);
		while(!commaOutBrackets(fileStr,posEnd))
			posEnd=fileStr.indexOf(".", posEnd + 1);
		while (fileStr.substring(posEnd, posEnd + 2).equals(".:"))
			// 特殊处理pron.:
			posEnd = fileStr.indexOf(".", posEnd + 2);
		while (fileStr.charAt(fileStr.indexOf("</",posEnd)+2)=='b')
			// 特殊处理C4.5_algorithm
			posEnd = fileStr.indexOf(".", posEnd + 1);
		String html = fileStr.substring(posStart, posEnd);
		System.out.println("sententce:" + html);
		String wikiTag = "<a href=\"/wiki/";
		while (html.contains(wikiTag)) {
			int posA = html.indexOf(wikiTag) + wikiTag.length();
			int posB = html.indexOf("\"", posA);
			String term = "";
			if (posB > posA) {
				term = html.substring(posA, posB);
				if (!term.contains(":") && !term.equals("Computer_science"))
					vTerm.add(html.substring(posA, posB));
				html = html.substring(posB, html.length());
			} else
				break;
		}
		return vTerm;
	}
	
	/**
	 * 判断pos的位置在尖括号里面还是外面，里面返回false，外面返回true
	 * 
	 * @param str
	 * @param pos
	 * @return
	 */
	public static boolean commaOutBrackets(String str, int pos) {
		int lastLeft = StringUtil.upIndexOf(str, "<", pos);
		int lastRight = StringUtil.upIndexOf(str, ">", pos);
		if (lastLeft > lastRight)
			return false;
		else
			return true;
	}
	
	/**
	 * 目录
	 * @param filePath
	 * @return
	 */
	public static Vector<String> extractCategoryTerm(String filePath){
		WikiCategoryCrawler wcs = new WikiCategoryCrawler();
		FileReader fr;
		Vector<String> superV=new Vector<String>();
		try {
			fr = new FileReader(filePath);
			BufferedReader br = new BufferedReader(fr);
			StringBuffer sb = new StringBuffer();
			String s = br.readLine();
			while (s != null) {
				sb.append(s);
				s = br.readLine();
			}
			superV = wcs.extractSupCategory(sb);// 获取每个文件的supterm
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return superV;
	}
	
	/**
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 编辑者信息
	 */
	public static Vector<String> extractHistoryEditor(String filePath) {
		String s=FileUtil.readFile(filePath);
		Vector<String> vHistory = new Vector<String>();
		String editorTag = "<span class='history-user'>";
		int pos = s.indexOf(editorTag);
		while (pos != -1) {
			int posL1 = s.indexOf("<", pos + 1);// 第一个左尖括号
			int posR1 = s.indexOf(">", posL1);// 第一个右尖括号
			int posL2 = s.indexOf("<", posR1);// 第二个左尖括号
			String editor = s.substring(posR1 + 1, posL2);// 作者
			String editInfo = editor;
			vHistory.add(editInfo);
			pos = s.indexOf(editorTag, posL2);
		}
		vHistory=SetUtil.getNoRepeatVector(vHistory);
		return vHistory;
	}

}
