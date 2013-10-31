package function.jsoup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Html2Txt {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Html2Txt ht = new Html2Txt();
		// parseHtmlString测试
		String html = "<html><head><title>First parse</title></head>"
				+ "<body><p>Parsed HTML into a doc.</p></body></html>";
		String text = ht.parseHtmlString(html);
		System.out.println(text);
		// parseBodyString测试
		String bodyStr = "<div><p>Lorem ipsum.</p>";
		String bodyText = ht.parseBodyString(bodyStr);
		System.out.println(bodyText);
		// parseURLText测试
		String url = "http://en.wikipedia.org/wiki/Data_mining";
		String urlText = ht.parseURLText(url);
		System.out.println(urlText);
		// parseURLHtml测试
		String urlHtml = ht.parseURLHtml(url);
		System.out.println(urlHtml);
		//parsePathText测试
		String path="f:/AVL_tree.html";
		String pathText= ht.parsePathText(path);
		System.out.println(pathText);
		//parseDirText测试
		String dir="f:/DM_html";
		ht.parseDirText(dir);

	}

	/**
	 * 
	 * @param htmlStr
	 * @return 给定的htmlStr解析出的文本
	 */
	public String parseHtmlString(String htmlStr) {
		Document doc = Jsoup.parse(htmlStr);
		String text = doc.body().text();
		return text;
	}

	/**
	 * 
	 * @param bodyStr
	 * @return 从给定的bodyStr解析出的文本
	 */
	public String parseBodyString(String bodyStr) {
		Document doc = Jsoup.parseBodyFragment(bodyStr);
		String text = doc.body().text();
		return text;
	}

	/**
	 * 
	 * @param url
	 * @return 从给定的url解析出的文本
	 */
	public String parseURLText(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).data("query", "Java").userAgent("Mozilla")
					.cookie("auth", "token").timeout(20000).post();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String text = doc.body().text();
		return text;
	}
	
	/**
	 * 
	 * @param url
	 * @return 从给定的url获得html内容
	 */
	public String parseURLHtml(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).data("query", "Java").userAgent("Mozilla")
					.cookie("auth", "token").timeout(20000).post();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String html = doc.body().html();
		return html;
	}
	
	/**
	 * 解析指定路径的html文件
	 * @param path
	 */
	public String parsePathText(String path) {
			Document doc = null;
			try {
				File input = new File(path);
				doc = Jsoup.parse(input, "UTF-8", "");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String text = doc.body().text();
			return text;
	}
	
	/**
	 * 解析指定目录的html文件，解析结果存放到dir-txt目录下面
	 * @param dir
	 */
	public void parseDirText(String dir) {
		String desDir="";
		if(dir.endsWith("/"))
			desDir=dir.substring(0, dir.length()-1)+"_txt/";
		else
			desDir=dir+"_txt/";
		File desFile=new File(desDir);
		desFile.mkdirs();
		File f=new File(dir);
		File childs[]=f.listFiles();
		for(int i=0;i<childs.length;i++){
			String fileName=childs[i].getName().replace(".html", ".txt");
			Document doc = null;
			try {
				doc = Jsoup.parse(childs[i], "UTF-8", "");
				String text = doc.body().text();
				FileWriter fw=new FileWriter(desDir+fileName);
				BufferedWriter bw=new BufferedWriter(fw);
				bw.write(text);
				bw.flush();
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
