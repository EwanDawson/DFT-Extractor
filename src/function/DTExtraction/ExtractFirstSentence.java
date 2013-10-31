package function.DTExtraction;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import function.base.ExcelBase;

/**
 * 
 * @author MJ
 * @description 抽取统计术语的category标签
 */
public class ExtractFirstSentence extends ExcelBase {

	String xlsFileName = "";
	String htmlPath = "";// 网页文件夹

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Data_mining Data_structure Computer_network
		String htmlPath = "F:\\FacetedTaxonomy\\Data_structure\\html\\layer2-select";
		String xlsFileName = "F:\\FacetedTaxonomy\\Data_structure\\process\\layer2-select-fs.xls";
		ExtractFirstSentence ec = new ExtractFirstSentence(htmlPath,
				xlsFileName);
		ec.run(ec);
	}

	// 构造函数
	public ExtractFirstSentence(String htmlPath, String xlsFileName) {
		this.xlsFileName = xlsFileName;
		this.htmlPath = htmlPath;
	}

	public void extractFS() {
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		File f = new File(htmlPath);
		File childs[] = f.listFiles();
		int recordID = 0;
		int stringRecordID = 1;
		for (int i = 0; i < childs.length; i++) {// 遍历每个文件
			System.out.println(i);
			String fileName = childs[i].getName();
			String filePath = childs[i].getAbsolutePath();
			Vector<String> fsV = getFirstSentenceWikiTerm(filePath);// 获取每个文件的supterm
			String superTermString = "";
			String term = fileName.substring(0, fileName.length() - 5);
			for (int j = 0; j < fsV.size(); j++) {
				String superTerm = fsV.get(j);
				superTermString += superTerm + ",";
				if (hm.containsKey(superTerm)) {
					int n = hm.get(superTerm);
					n++;
					hm.put(superTerm, n);
				} else
					hm.put(superTerm, 1);
				setStringValue(0, "term", ++recordID, term);
				setStringValue(0, "FSWikiTerm", recordID, superTerm);
			}
			if (superTermString.length() >= 1)
				superTermString = superTermString.substring(0,
						superTermString.length() - 1);
			setStringValue(1, "term", stringRecordID, term);
			setStringValue(1, "FSWikiTerm", stringRecordID++, superTermString);
		}// end for
		Iterator<String> it = hm.keySet().iterator();
		int number = 0;
		while (it.hasNext()) {
			String superTerm = it.next();
			int n = hm.get(superTerm);
			setStringValue(2, "FSWikiTerm", ++number, superTerm);
			setIntegerValue(2, "frequency", number, n);
		}
	}

	/**
	 * 获取文件的first Sentence
	 * 
	 * @param filePath
	 * @return
	 */
	public Vector<String> getFirstSentenceWikiTerm(String filePath) {
		Vector<String> vTerm = new Vector<String>();
		Document doc = null;
		try {
			File input = new File(filePath);
			doc = Jsoup.parse(input, "UTF-8", "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Elements es = doc.body().getAllElements();
		String html = "";
		for (int i = 0; i < es.size(); i++) {
			Element e = es.get(i);
			if (e.tagName().equals("p")) {
				html = e.html();
				if (html.contains("<b>"))
					break;
			}
		}
		int posTemp = html.indexOf(".");
		while (posTemp != -1) {
			int posA = html.indexOf("<", posTemp);
			int posB = html.indexOf(">", posTemp);
			if (posA <= posB)
				break;
			posTemp = html.indexOf(".", posTemp + 1);
		}
		if (posTemp != -1)
			html = html.substring(0, posTemp);
		System.out.println("sententce:" + html);
		String wikiTag = "<a href=\"/wiki/";
		while (html.contains(wikiTag)) {
			int posA = html.indexOf(wikiTag) + wikiTag.length();
			int posB = html.indexOf("\"", posA);
			String term = "";
			if (posB > posA) {
				term = html.substring(posA, posB);
				if (!term.contains(":"))
					vTerm.add(html.substring(posA, posB));
				html = html.substring(posB, html.length());
			} else
				break;
		}
		return vTerm;
	}

	@Override
	public void process() throws Exception {
		// TODO Auto-generated method stub
		extractFS();
	}

	@Override
	public void run(ExcelBase eb) {
		// TODO Auto-generated method stub
		eb.go(xlsFileName);
	}
}
