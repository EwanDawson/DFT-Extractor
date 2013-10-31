package function.DTExtraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import function.util.ExcelUtil;
import function.util.FileUtil;
import function.util.SetUtil;

public class WikiHrefProcess {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Data_mining Data_structure Computer_network
		WikiHrefProcess whp = new WikiHrefProcess();
		String htmlPath="F:\\FacetedTaxonomy\\Data_structure\\html\\layer1-select";
		String txtPath="F:\\FacetedTaxonomy\\Data_structure\\process\\layer2.txt";
		String xlsPath="F:\\FacetedTaxonomy\\Data_structure\\process\\layer2.xls";
		String layer01Path="F:\\FacetedTaxonomy\\Data_structure\\process\\layer01.txt";
		File f=new File(htmlPath);
		File childs[]=f.listFiles();
		Vector<String> vTerm=new Vector<String>();
		Vector<String> vLayer01Term=SetUtil.readSetFromFile(layer01Path);
		for(int i=0;i<childs.length;i++){
			String filePath=childs[i].getAbsolutePath();
			vTerm.addAll(whp.getWikiTermFromFile(filePath));
		}
		vTerm=SetUtil.getSubSet(vTerm, vLayer01Term);
		vTerm=SetUtil.getNoRepeatVector(vTerm);
		SetUtil.writeSetToFile(vTerm, txtPath);
		ExcelUtil.writeSetToExcel(vTerm, xlsPath, 0, "term");
	}
	
	/**
	 * 从文件中获取首句术语
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
				if (ExtractorUtil.checkTerm(term))
					vTerm.add(html.substring(posA, posB));
				html = html.substring(posB, html.length());
			} else
				break;
		}
		return vTerm;
	}

	/**
	 * 从指定Web地址获取维基词条
	 * 
	 * @param url
	 * @return
	 */
	public Vector<String> getWikiTermFromWeb(String url) {
		String s = getPageFromWeb(url);
		try {
			s = new String(s.getBytes("gbk"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getWikiTermFromStr(s);
	}
	
	/**
	 * 从指定文件夹获取维基词条
	 * @param dir
	 * @return
	 */
	public Vector<String> getWikiTermFromDir(String dir){
		Vector<String> vTerm=new Vector<String>();
		File f=new File(dir);
		File childs[]=f.listFiles();
		for(int i=0;i<childs.length;i++){
			String filePath=childs[i].getAbsolutePath();
			vTerm.addAll(getWikiTermFromFile(filePath));
		}
		vTerm=SetUtil.getNoRepeatVector(vTerm);
		return vTerm;
	}

	/**
	 * 从指定文件获取维基词条
	 * 
	 * @param filePath
	 * @return
	 */
	public Vector<String> getWikiTermFromFile(String filePath) {
		String s = FileUtil.readFile(filePath);
		try {
			s = new String(s.getBytes("gbk"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Vector<String> v=getWikiTermFromStr(s);
		return v;
	}

	/**
	 * 获取指定字符串内的维基词条
	 * 
	 * @param filePath
	 * @return
	 */
	public Vector<String> getWikiTermFromStr(String s) {
		Vector<String> vRemoveTag = new Vector<String>();
		vRemoveTag.add("History");// 不包含如下部分的术语
		vRemoveTag.add("External links");
		vRemoveTag.add("Further reading");
		vRemoveTag.add("Software");
		vRemoveTag.add("Notes");// Euclidean_geometry出现过
		vRemoveTag.add("References");
		Vector<int[]> vInterval = getInterval(s, vRemoveTag);
		Vector<String> vTerm = new Vector<String>();
		String wikiTag = "a href=\"/wiki/";
		int posA = s.indexOf(wikiTag);
		int posB = 0;
		while (posA != -1) {
			posB = s.indexOf("\"", posA + wikiTag.length());
			String temp = s.substring(posA + wikiTag.length(), posB);
			if (temp.contains("#"))
				temp = temp.substring(0, temp.indexOf("#"));
			if (existInAmong(posA, vInterval) == false &&ExtractorUtil.checkTerm(temp))
				vTerm.add(temp);
			posA = s.indexOf(wikiTag, posB);
		}
		return vTerm;
	}

	/**
	 * 
	 * @param s
	 * @param vRemoveTag
	 * @return 获取网页字符串removeTag的区间位置，比如History
	 */
	private Vector<int[]> getInterval(String s, Vector<String> vRemoveTag) {
		Vector<int[]> v = new Vector<int[]>();
		for (String tag : vRemoveTag) {
			String t2Tag = tag + "</span></h2>";
			if (s.contains(t2Tag)) {
				int interval[] = new int[2];
				interval[0] = s.indexOf(t2Tag);
				int pos = s.indexOf("</h2>", interval[0] + t2Tag.length());
				if (pos != -1)
					interval[1] = pos;
				else
					interval[1] = s.length();
				v.add(interval);
			}
		}
		return v;
	}

	/**
	 * 
	 * @param pos
	 * @param vInterval
	 * @return 判断给定的pos是否在给定的区间向量里面
	 */
	private boolean existInAmong(int pos, Vector<int[]> vInterval) {
		for (int i = 0; i < vInterval.size(); i++) {
			int interval[] = vInterval.get(i);
			if (pos > interval[0] && pos < interval[1]) {
				return true;
			} else
				continue;
		}
		return false;
	}

	public String getPageFromWeb(String url) {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		InputStream is = null;
		try {
			URL my_url = new URL(url);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = dateFormat.format(new Date());
			System.out.println(url + "\t" + time);
			is = my_url.openStream();
			br = new BufferedReader(new InputStreamReader(is));
			String strTemp = "";
			while (null != (strTemp = br.readLine())) {
				sb.append(strTemp + "\r\n");
			}
			System.out.println("get page success from " + url);
			br.close();
		} catch (Exception ex) {
			System.err.println("page not found: " + url);
		}
		return sb.toString();
	}

}
