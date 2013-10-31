package function.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import function.DTExtraction.ExtractorUtil;
import function.util.FileUtil;
import function.util.SetUtil;

/*****爬虫程序****/
/******功能及用法******/
/**
 * @author MJ
 * @Description 1).先设置下面的路径filePath,作为保存网页的根目录;
 *              2).crawlPageByUrl()是根据url爬取，如crawlPageByUrl("www.baidu.com",
 *              "f:/baidu.html");//注意，后面参数需要设定保存路径，而WebCrawler的不需要
 *              3).crawlPageByList(termSetPath)从指定的termSetPath读取术语集合，爬取历史页面，结果
 *              保存到同路径下的termSetPath(去除后缀)_history里面
 */

public class WikiHistoryCrawler_new {
	public String filePath = "";

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
		File f = new File(filePath);
		f.mkdirs();
	}

	public String crawlPageName = "";// 保存正在爬取的頁面

	/**
	 * 构造函数，主要用来生成目录
	 */
	public WikiHistoryCrawler_new() {
		Proxy proxy = new Proxy();
		proxy.setLocalRandomHttpProxy();// 设置代理
	}// WikiHistoryCrawler()
	
	/**
	 * 去除代理
	 */
	public void removeProxy(){
		Proxy proxy=new Proxy();
		proxy.removeLocalHttpProxy();
		System.out.println("去除代理！");
	}
	
	/**
	 * 打开代理
	 */
	public void openProxy(){
		Proxy proxy=new Proxy();
		proxy.setLocalRandomHttpProxy();//设置代理
		System.out.println("打开代理！");
	}

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
			System.out.println(url + "\t" + time);
			is = my_url.openStream();
			br = new BufferedReader(new InputStreamReader(is));
			String strTemp = "";
			while (null != (strTemp = br.readLine())) {
				sb.append(strTemp + "\r\n");
			}
			System.out.println("get page success from " + url);
			FileWriter fw = new FileWriter(filePath + "\\" + fileName);
			String s = sb.toString();
			fw.write(s);
			fw.flush();
			fw.close();
			br.close();
			resultTag = true;
		} catch (Exception ex) {
			System.err.println("page not found: " + url);
		} finally {
			return resultTag;
		}
	}// crawlPageByUrl()

	/**
	 * 按照指定列表路径爬取页面
	 * 
	 * @param listFilePath
	 */
	public void crawlPageByList(String listFilePath, String savePath,
			int ThreadNumber) {
		int aliveThread = ThreadNumber;
		Vector<Thread> v = new Vector<Thread>();
		ThreadCrawlByList tcl = new ThreadCrawlByList(listFilePath, savePath);
		for (int i = 0; i < ThreadNumber; i++) {
			Thread t = new Thread(tcl);
			v.add(t);
			t.start();
		}
		while (true) {
			aliveThread = 0;
			for (int i = 0; i < ThreadNumber; i++) {
				if (v.get(i).isAlive())
					aliveThread++;
			}
			if (aliveThread == 0)
				break;
		}
		System.out.println("全部爬取完毕！");
	}

	/**
	 * 按照指定列表路径爬取页面
	 * 
	 * @param listFilePath
	 * @param haveCrawlDir
	 *            已经爬取的页面文件夹
	 */
	public void crawlPageByList(String listFilePath, String haveCrawlDir,
			String savePath, int ThreadNumber) {
		ThreadCrawlByList tcl = new ThreadCrawlByList(listFilePath,
				haveCrawlDir, savePath);
		for (int i = 0; i < ThreadNumber; i++) {
			Thread t = new Thread(tcl);
			t.start();
		}
		System.out.println("全部爬取完毕！");

	}
	

	/**
	 * 
	 * @author MJ
	 * @description 按列表爬取的线程类
	 */
	public class ThreadCrawlByList implements Runnable {

		private Vector<String> vHavenotCrawl = new Vector<String>();
		int size = 0;
		int curId = 1;
		int failedNumber = 0;// 爬取失败的数量
		Proxy proxy = new Proxy();

		public ThreadCrawlByList(String listFilePath, String savePath) {
			Vector<String> vTerm = SetUtil.readSetFromFile(listFilePath);
			Vector<String> vHaveCrawl = new Vector<String>();
			setFilePath(savePath);// 设置保存路径
			File f = new File(savePath);
			File childs[] = f.listFiles();
			for (int i = 0; i < childs.length; i++) {
				String pageName = childs[i].getName();
				String term = pageName.substring(0, pageName.lastIndexOf("."));
				vHaveCrawl.add(term);
			}
			vHavenotCrawl.addAll(SetUtil.getSubSet(vTerm, vHaveCrawl));
			size = vHavenotCrawl.size();
		}

		public ThreadCrawlByList(String listFilePath, String haveCrawlDir,
				String savePath) {
			Vector<String> vTerm = SetUtil.readSetFromFile(listFilePath);
			Vector<String> vHaveCrawl = new Vector<String>();
			Vector<String> vExistPage = new Vector<String>();
			setFilePath(savePath);// 设置保存路径
			File f = new File(savePath);
			File childs[] = f.listFiles();
			for (int i = 0; i < childs.length; i++) {
				String pageName = childs[i].getName();
				String term = pageName.substring(0, pageName.lastIndexOf("."));
				vExistPage.add(term);
			}// 保存路径现有的
			File fHave = new File(haveCrawlDir);
			File childsHave[] = fHave.listFiles();
			for (int i = 0; i < childsHave.length; i++) {
				String pageName = childsHave[i].getName();
				String term = pageName.substring(0, pageName.lastIndexOf("."));
				vHaveCrawl.add(term);
				if (vTerm.contains(term)) {
					try {
						FileUtil.copyFile(new File(haveCrawlDir + "/"
								+ pageName),
								new File(savePath + "/" + pageName));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}// 别的文件夹中已爬取的
			vHavenotCrawl.addAll(SetUtil.getSubSet(
					SetUtil.getSubSet(vTerm, vHaveCrawl), vExistPage));
			size = vTerm.size();
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (vHavenotCrawl.size() != 0) {
				String term = "";
				synchronized ("") {
					if (vHavenotCrawl.size() != 0) {
						term = vHavenotCrawl.get(0);
						vHavenotCrawl.removeElementAt(0);
						curId = size - vHavenotCrawl.size();
					}
					if (failedNumber >= 10) {
						failedNumber = 0;
						proxy.setLocalRandomHttpProxy();// 如果次数超过10，重新设置代理
					}
				}
				if (term.length() != 0) {
					String url = "http://en.wikipedia.org/w/index.php?title="
							+ term + "&dir=prev&action=history";
					String fileName = term + ".html";
					boolean b = crawlPageByUrl(url, fileName);
					if (b == false) {
						try {
							failedNumber++;
							System.out.println(fileName + "------"
									+ Thread.currentThread().getName()
									+ "------(" + "爬取失败，重新添加……" + ")");
							Thread.currentThread();
							System.out.println(Thread.currentThread().getName()
									+ "进入休眠……");
							vHavenotCrawl.add(term);
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						System.out.println(fileName + "------"
								+ Thread.currentThread().getName() + "------("
								+ ExtractorUtil.scanDirPage(getFilePath()).size() + "/" + size + ")");
						failedNumber = 0;// 不累加，只计算连续失败次数
					}
				}
			}
		}

	}
}
