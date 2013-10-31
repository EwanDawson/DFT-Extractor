package function.DTExtraction;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import function.crawler.WebCrawler;
import function.crawler.WikiCategoryCrawler;
import function.linkAnalysis.NavboxExtractor;
import function.txtAnalysis.TFIDF;
import function.txtAnalysis.Vectorization;
import function.util.FileUtil;
import function.util.SetUtil;

public class Layer1Extractor_old {

	private String DTPath = "";// 领域术语存放路径
	private String DomainTerm = "";// 领域名称
	private String layer0Path = "";// 领域术语初始存放路径
	private String layer1RawPath = "";// 第一层爬取的术语存放路径
	private String layer1Path = "";// 第1层筛选后术语存放路径
	private String layer01RawPath = "";// 0,1爬取的术语存放路径
	private double processId = 0;// 流程控制标签
	private String processIdFile = "";// 流程控制文件路径
	private String layer0DomainPath ="";// 领域页面地址
	//private String catPath="";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String basePath="f:/FacetedTaxonomy/";
		String DomainTerm="Data_structure";
		Layer1Extractor_old l1e=new Layer1Extractor_old(basePath,DomainTerm);
		Vector<String> v=SetUtil.getNoRepeatVector(l1e.checkNavbox(l1e.layer0DomainPath));
		System.out.println(v.size());
		for(String term :v){
			System.out.println(term);
		}
	}

	public Layer1Extractor_old(String basePath,String DomainTerm) {
		this.DTPath =basePath+DomainTerm+"/html";
		this.DomainTerm = DomainTerm;
		this.layer0Path = this.DTPath + "/layer0";
		this.layer1RawPath = this.DTPath + "/layer1-raw";
		this.layer1Path = this.DTPath + "/layer1";
		this.layer01RawPath = this.DTPath + "/layer01-raw";
		this.layer0DomainPath = layer0Path + "/" + DomainTerm + ".html";
		//this.catPath=basePath+DomainTerm+"/category";
		File f0 = new File(layer0Path);
		File f1raw = new File(layer1RawPath);
		File f1 = new File(layer1Path);
		File f01raw = new File(layer01RawPath);
		f0.mkdirs();
		f1raw.mkdirs();
		f1.mkdirs();
		f01raw.mkdirs();
		this.processIdFile = DTPath + "/processId.txt";
		File fProcess = new File(processIdFile);
		if (!fProcess.exists())
			FileUtil.writeStringFile("0", processIdFile);
	}

	public double getProcessId() {
		double id = Double.valueOf(FileUtil.readFile(processIdFile));
		processId = id;
		return processId;
	}

	public void setProcessId(double processId) {
		this.processId = processId;
		String id = String.valueOf(processId);
		FileUtil.writeStringFile(id, processIdFile);
	}
	
	public void extract() {
		WebCrawler wc = new WebCrawler();
		String layer0HrefPath = this.DTPath + "/layer0-href.txt";
		Vector<String> vHaveCrawl = new Vector<String>();
		String haveCrawlPath = this.DTPath + "/haveCrawl.txt";
		File fCrawl = new File(haveCrawlPath);
		if (fCrawl.exists())
			vHaveCrawl = SetUtil.readSetFromFile(haveCrawlPath);
		else
			SetUtil.writeSetToFile(vHaveCrawl, haveCrawlPath);
		// 爬取初始页面
		if (getProcessId() < 0.1) {
			System.out.println("正在爬取领域初始页面……");
			String dtUrl = "http://en.wikipedia.org/wiki/" + DomainTerm;// 领域初始路径
			while (!wc.crawlPageByUrl(dtUrl, layer0DomainPath))
				;
			vHaveCrawl.add(DomainTerm);
			setProcessId(0.1);
		}
		// 解析Wiki术语
		if (getProcessId() < 0.2) {
			System.out.println("正在解析领域初始页面中的维基术语……");
			WikiHrefProcess whp = new WikiHrefProcess();
			Vector<String> vWikiTerm = SetUtil.getNoRepeatVector(whp
					.getWikiTermFromFile(layer0DomainPath));
			vWikiTerm.addAll(checkNavbox(layer0DomainPath));// 添加NavBox的页面
			Vector<String> vHaveNotCrawl = SetUtil.getSubSet(vWikiTerm,
					vHaveCrawl);
			vHaveNotCrawl = SetUtil.getNoRepeatVector(vHaveNotCrawl);// 去除重复
			SetUtil.writeSetToFile(vHaveNotCrawl, layer0HrefPath);
			vHaveCrawl.addAll(vHaveNotCrawl);
			SetUtil.writeSetToFile(vHaveCrawl, haveCrawlPath);
			setProcessId(0.2);
		}
		// 爬取layer1-raw
		if (getProcessId() < 0.3) {
			System.out.println("正在爬取layer1-raw……");
			wc.crawlPageByList(layer0HrefPath, layer1RawPath, 10);
			setProcessId(0.3);
		}
		// 合并layer0,1 raw
		if (getProcessId() < 0.4) {
			System.out.println("正在合并0,1 raw……");
			try {
				FileUtil.copyDirectiory(layer1RawPath, layer01RawPath);
				FileUtil.copyFile(new File(layer0Path + "/" + DomainTerm
						+ ".html"), new File(layer01RawPath + "/" + DomainTerm
						+ ".html"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setProcessId(0.4);
		}
		// 向量化
		if (getProcessId() < 0.5) {
			System.out.println("正在向量化……");
			Vectorization vz = new Vectorization();
			vz.wikiVector(layer01RawPath);
			setProcessId(0.5);
		}
		// TF/IDF计算
		if (getProcessId() < 0.6) {
			System.out.println("正在计算TF/IDF……");
			TFIDF tfidf = new TFIDF();
			tfidf.computeTFIDF_wv(layer01RawPath + "_wikiVector");
			setProcessId(0.6);
		}
		// 构建领域页面中相关目录的术语
		if (getProcessId() < 0.7) {
			System.out.println("正在构建领域页面中相关目录的术语……");
			buildDomainPageCategory();
			setProcessId(0.7);
		}
	}

	/**
	 * 检测fileName里面是否含有包含fileName的Navbox，如果有，则返回里面的术语
	 * 
	 * @param fileName
	 * @return
	 */
	public Vector<String> checkNavbox(String fileName) {
		String term = "";
		String s = FileUtil.readFile(fileName);
		Vector<String> vWikiTerm = new Vector<String>();
		if (fileName.contains("\\"))
			term = fileName.substring(fileName.lastIndexOf("\\") + 1,
					fileName.indexOf(".html"));
		else
			term = fileName.substring(fileName.lastIndexOf("/") + 1,
					fileName.indexOf(".html"));
		NavboxExtractor ne = new NavboxExtractor();
		HashMap<String, Vector<String>> hm = ne.extractNavboxFromString(s);
		Iterator<String> it = hm.keySet().iterator();
		while (it.hasNext()) {
			String tableName = it.next();
			if (tableName.contains(term)) {
				Vector<String> v = hm.get(tableName);
				for (int i = 0; i < v.size(); i++) {
					String record[] = v.get(i).split(",");
					if (record[2].toLowerCase().equals("true")) {
						vWikiTerm.add(record[0]);
					}
					if (record[3].toLowerCase().equals("true")) {
						vWikiTerm.add(record[1]);
					}
				}
			}
		}
		return vWikiTerm;
	}

	/**
	 * 构建领域页面中相关目录的术语
	 * 
	 */
	public void buildDomainPageCategory() {
		WikiCategoryCrawler wcc = new WikiCategoryCrawler();
		Vector<String> vCat = wcc.extractSupCategory(FileUtil
				.readFile(layer0DomainPath));
		//String domainCat = "";// 领域目录，和领域术语相同或者多个s
		for (String cat : vCat) {
			if (cat.toLowerCase().equals(DomainTerm.toLowerCase())
					|| cat.toLowerCase().equals(DomainTerm.toLowerCase() + "s")) {
				//domainCat = cat;
				break;
			}
		}
        //wcc.buildCategoryTree(domainCat, catPath+"/layer0-category");
	}
	
}
