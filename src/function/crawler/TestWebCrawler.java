package function.crawler;

import java.util.Vector;

import function.util.FileUtil;
import function.util.SetUtil;



public class TestWebCrawler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Data_mining Data_structure Computer_network
		/*String listFile="F:\\extractTest\\Data_structure\\process\\layer3CatCrawl.txt";
		String savePath="F:\\compute\\DS\\html";
		Vector<String> vCrawl=FileUtil.getDirFileSet(savePath);
		SetUtil.writeSetToFile(vCrawl, listFile);
		WebCrawler wc=new WebCrawler();
		wc.removeProxy();
		wc.checkDirCompleteness(savePath, 2);*/
		String url = "http://www.youtube.com/";
		String filename = "youtube.html";
		String filepath = "f:\\try\\";
		
		WebCrawler wc = new WebCrawler();
		wc.setFilePath(filepath);
		wc.crawlPageByUrl(url, filename);
	}
}
