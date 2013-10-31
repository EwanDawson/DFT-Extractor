package function.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Vector;

import function.util.SetUtil;

/*****爬虫程序****/
/******功能及用法******/
/**
 * @author MJ
 * @Description 1).先设置下面的路径filePath,作为保存网页的根目录;
 *              2).crawlPageByUrl()是根据url爬取，如crawlPageByUrl("www.baidu.com",
 *              "f:/baidu.html");//注意，后面参数需要设定保存路径，而WebCrawler的不需要
 *              3).crawlPageByList(termSetPath)从指定的termSetPath读取术语集合，爬取历史页面，结果
 *               保存到同路径下的termSetPath(去除后缀)_history里面
 */

public class WikiHistoryCrawler {
	public String crawlPageName="";//保存正在爬取的頁面
	/**
	 * 构造函数，主要用来生成目录
	 */
	public WikiHistoryCrawler() {
	}// WikiHistoryCrawler()

	/**
	 * 按照参数指定的url爬取，第二个参数是存放的文件名
	 * 
	 * @param url
	 * @param fileName
	 */
	@SuppressWarnings("finally")
	public boolean crawlPageByUrl(String url, String fileName) {
		boolean resultTag = false;
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		InputStream is = null;
		try {
			URL my_url = new URL(url);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = dateFormat.format(new Date());
			System.out.println(url + time);
			is = my_url.openStream();
			br = new BufferedReader(new InputStreamReader(is));
			String strTemp = "";
			while (null != (strTemp = br.readLine())) {
				sb.append(strTemp + "\r\n");
			}
			System.out.println("get page success from " + url);
			FileWriter fw = new FileWriter(fileName);
			fw.write(sb.toString());
			fw.flush();
			fw.close();
			br.close();
			resultTag = true;
			crawlPageName=fileName;
		} catch (Exception ex) {
			System.err.println("page not found: " + url);
		} finally {
			return resultTag;
		}
	}// crawlPageByUrl()

	/**
	 * 
	 * @param termSetPath 指定的爬取集合文件路径
	 * 
	 */
	public void crawlPageByList(String termSetPath) {
		String filePath="";
		if(termSetPath.contains("\\"))
			filePath=termSetPath.substring(0, termSetPath.lastIndexOf("."))+"_history";
		else
			filePath=termSetPath.substring(0, termSetPath.lastIndexOf("."))+"_history";
		File f=new File(filePath);
		f.mkdirs();
		Vector<String> v=SetUtil.readSetFromFile(termSetPath);
		v.retainAll(getHavenotCrawlUrlSet(v,filePath));//获取没有爬取的集合
		int sum = v.size();
		int n = 1;
		while (!v.isEmpty()) {
			HashSet<String> hsCrawled = new HashSet<String>();//保存已经爬取过的页面集合
			String crawlUrl = getUrlByTerm(v.get(0));// 爬取地址
			String term0 = v.get(0);// termName
			String fileName=term0+"_historyE.html";
			if (!hsCrawled.contains(crawlUrl)) {
				String pathFileName = filePath + "/" + fileName;
				if (crawlPageByUrl(crawlUrl, pathFileName)) {
					hsCrawled.add(fileName);// 如果爬取成功，則添加到已爬取列表里
					v.remove(0);
					System.out.println(pathFileName + "------(" + (n++) + "/"
							+ sum + ")");
				} else {// 爬取失败
					v.remove(0);
					v.add(term0);
					System.out.println("wait……");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				System.out.println(fileName + "------(" + (n++) + "/" + sum
						+ ")--------(Crawled)");
			}
		}// end while
		System.out.println("Crawled success!");
	}

	/**
	 * 通过术语名构造url地址
	 * 
	 * @param termName
	 * @return
	 */
	public String getUrlByTerm(String termName) {
		//String fileNameSuffix = ".html";
		String prefix = "http://en.wikipedia.org/w/index.php?title=";
		String suffix="&dir=prev&action=history";
		//String prefix = "http://en.wikipedia.org/wiki/";
		//String suffix="";
		/*String pureName = fileName.substring(0,
				fileName.length() - fileNameSuffix.length());*/
		return prefix + termName+suffix;
	}

	/**
	 * 
	 * @param v 总的集合
	 * @param savePath 已爬取集合的保存路径
	 * @return 未爬取的集合
	 */
	public Vector<String> getHavenotCrawlUrlSet(Vector<String> v,String savePath) {
		File f = new File(savePath);
		File childs[] = f.listFiles();
		Vector<String> haveCrawlSet = new Vector<String>();
		Vector<String> havenotCrawlSet = new Vector<String>();
		for (int j = 0; j < childs.length; j++) {
			haveCrawlSet.add(childs[j].getName());
		}
		for (String s : v) {
			String temp=s+"_historyE.html";//临时添加的规则
			if (!haveCrawlSet.contains(temp) && !havenotCrawlSet.contains(temp)) {
				havenotCrawlSet.add(s);
			}
		}
		return havenotCrawlSet;
	}
}
