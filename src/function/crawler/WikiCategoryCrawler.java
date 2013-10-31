/**
 * 
 */
package function.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import function.txtAnalysis.util.OddComplexReplace;
import function.util.SetUtil;

/** 
 * 功能1：从指定的若干分类页面开始，爬取分类结构，保存到csv文件。
 * 功能2：读取本地一般wiki页面，得到父类。
 * 
 * 1、分类信息来源于两类页面：一般wiki页面和专用的wiki分类页面。
 *   一般wiki页面只有父类。
 *   wiki分类页面包括3类：父类，子类，子页面。
 * 2、分类关系可能循环。比如wiki分类页面的子类本身就循环。
 * 3、2种工作方式：cvs文件以追加方式打开。
 *   1）全新构建。cvs文件清空，分类根为全部。
 *   2）增量构建，cvs文件不清空，去掉爬取过的根，只加入新的根。
 *
 */
public class WikiCategoryCrawler {

	public URL dicURL=getClass().getResource("/resources/standardspellings.txt");
	public Vector<String> vDic = SetUtil.readSetFromFile(dicURL.getPath().replace("%20", " "));//字典
	public String crawlPageName="";//保存正在爬取的頁面
	public HashSet<String> hsHaveStore=new HashSet<String>();//保存已经写过的记录
	String cvsCategoryFile = "";
	FileWriter cvsWriter;

	/**
	 * 
	 */
	public WikiCategoryCrawler() {
		// TODO Auto-generated constructor stub
		System.out.println("cvsCategoryFile:"+cvsCategoryFile);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub


		WikiCategoryCrawler wcc=new WikiCategoryCrawler();
		String catTermPath="C:\\Users\\Lenovo\\Desktop\\category.txt";
		wcc.buildCategoryTree(catTermPath,1);
	}
	
	/**
	 * 按照指定的集合将目录爬取到指定的路径
	 * @param vCat
	 * @param desCatPath
	 */
	public void buildCategoryTree(Vector<String> vCat,String desCatDir,int depth){
		for(String s:vCat){
			buildCategoryTree(s,desCatDir,depth);
		}
	}
	
	/**
	 * 按照指定根爬取目录到指定的文件夹里
	 * @param catTerm
	 * @param desCatDir
	 * @param depth 深度
	 */
	public void buildCategoryTree(String catTerm,String desCatDir,int depth){
		WikiCategoryCrawler wps = new WikiCategoryCrawler();
		Vector<String> cat = new Vector<String>();
		cat.add(catTerm);
		desCatDir=desCatDir.replace("\\", "/");
		File fCatDataDir=new File(desCatDir);
		fCatDataDir.mkdirs();
	    cvsCategoryFile=desCatDir+"/"+catTerm+"_category.csv";
		wps.init(cvsCategoryFile);
		wps.travelCategoryPages(cat, depth);
		System.out.println("end");
	}

	/*// 从本地目录读取该目录所有一般页面，获得父类。
	//从本地目录读取该目录所有wiki分类页面，获得父类，子类，子页面。
	public static void testPagesCategoryFromLocal() {
		WikiCategoryScrawl wps = new WikiCategoryScrawl();
		
		LocalCorpus corpus = new LocalCorpus();
		String dir= "F:/Data/wei/DM/Category";
		Vector<String> fileNames=corpus.getFileNamesFromDirectory(dir);

		Vector<String> cat = null;
		for (String name: fileNames) {
			StringBuffer buffer = corpus.getHtmlFileBuffer(name);
			buffer = corpus.getHtmlFileBuffer(name);
			cat = wps.extractSupCategory(buffer);
			cat = wps.extractSubPages(buffer);
			System.out.println(cat);
		}
	}*/

	// 从特定的分类页面节点开始，在线获取父类，子类，及子节点。然后遍历子类。
	// 但是不遍历父类。可以增量添加节点。 结果打印到屏幕和保存到文件
	//可全新，可增量构建
	public void buildCategoryTree(String catTermPath,int depth) {

		WikiCategoryCrawler wps = new WikiCategoryCrawler();
		Vector<String> vTerm = SetUtil.readSetFromFile(catTermPath);
		for(String s:vTerm){
			Vector<String> cat = new Vector<String>();
			cat.add(s);
			catTermPath=catTermPath.replace("\\", "/");
			String catDataDir=catTermPath.substring(0, catTermPath.lastIndexOf("/")+1)+"category_data";
			File fCatDataDir=new File(catDataDir);
			fCatDataDir.mkdirs();
		    cvsCategoryFile=catDataDir+"/"+s+"_category.csv";
		    File f=new File(cvsCategoryFile);
		    if(!f.exists()){
		    	wps.init(cvsCategoryFile);
				wps.travelCategoryPages(cat, depth);
				System.out.println("end");
		    }
		}
		// 已经处理了
		/**
		 * DM的术语条目
		 */
		/*cat.add("Data_mining");
		cat.add("Data_analysis");
		cat.add("Machine_learning");
		cat.add("Computational_statistics");
		cat.add("Data_management");*/
		/*cat.add("Artificial_intelligence");
		cat.add("Algorithms");
		cat.add("Statistical_theory");
		cat.add("Knowledge");
		cat.add("Business_intelligence");*/
		
		/*cat.add("Search_algorithms");
		cat.add("Statistical_models");
		cat.add("Trees_(structure)");*/
		/**
		 * CN的术语条目
		 */
		/*cat.add("Computer_networks");
		cat.add("Local_area_networks");
		cat.add("Metropolitan_area_networks");
		cat.add("Personal_area_networks");
		cat.add("Wide_area_networks");
		cat.add("Internet");
		cat.add("Internet_terminology");
		cat.add("World_Wide_Web");
		cat.add("Mobile_Web");
		cat.add("Internet_architecture");
		cat.add("Wireless_networking");
		cat.add("Wi-Fi");*/
		/*cat.add("Data_transmission");
		cat.add("Telecommunications_equipment");
		cat.add("Telecommunications_standards");
		cat.add("Internet access");*/
		/*cat.add("Interoperability");
		cat.add("Radio_spectrum");
		cat.add("Electromagnetic_radiation");
		cat.add("Cables");*/
		//cat.add("Network_protocols");
		//cat.add("Servers_(computing)");
		/**
		 * DS的术语条目
		 */
		/*cat.add("Trees_(data_structures)");
		cat.add("Sorting_algorithms");
		cat.add("Binary_trees");
		cat.add("Graph_families");
		cat.add("Search_algorithms");
		cat.add("Algorithms_and_data_structures_stubs");
		cat.add("Data_types");
		cat.add("Comparison_sorts");
		cat.add("Graph_theory");
		cat.add("Graph_algorithms");
		cat.add("Abstract_data_types");
		cat.add("Data_structures");*/
		
		/**
		 * DSnew
		 *//*
		cat.add("Data_structures");
		cat.add("Graphs");	
		cat.add("Graph_algorithms");		
		cat.add("Abstract_data_types");
		cat.add("Algorithms");
		cat.add("Machine_learning");*/

		// IR分类体系有问题，循环，会跳到操作系统，软件等领域
		// cat.add("Information_retrieval");
	}

	

	// 遍历分类页面,递归子分类，不递归父分类
	// 递归异常：Microsoft server software与 Microsoft server technology循环
	public void travelCategoryPages(Vector<String> urlTerms, int level) {
		if (urlTerms.isEmpty())
			return;
		if (level-- < 1)
			return;

		Vector<String> cat = null;
		for (String term : urlTerms) {
			
			//从网上得到wiki分类页面
			String url = buildCategoryUrl(term);			
			StringBuffer buffer =getPageBufferFromWeb(url);
			
			/*//抽取父类
			cat = extractSupCategory(buffer);
			System.out.println("parent category=" + cat);
			cvsStore(term, 1, cat);*/
			
			//抽取子页面
			cat = extractSubPages(buffer);
			System.out.println("sub pages=" + cat);
			cvsStore(term, 0, cat);
			
			//抽取子类
			cat = extractSubCategory(buffer);
			System.out.println("sub category=" + cat);
			cvsStore(term, 0, cat);
			filterSubCategory(cat);
			
			//递归遍历子类
			travelCategoryPages(cat, level);
		}
	}
	
	// 过滤一些无用的term
	public void filterSubCategory(Vector<String> urlTerms) {
		Set<String> delTerms = new HashSet<String>();
		delTerms.add("Microsoft");
		delTerms.add("Windows");
		for (Iterator<String> i = urlTerms.iterator(); i.hasNext();) {
			String str = i.next();
			if (str.toLowerCase().indexOf("internet by country") != -1) {
				i.remove();
				continue;
			}
			if (str.toLowerCase().indexOf("internet by continent") != -1) {
				i.remove();
				continue;
			}
			if (str.toLowerCase().indexOf("internet-related lists") != -1) {
				i.remove();
				continue;
			}
			if (str.toLowerCase().indexOf("companies") != -1) {
				i.remove();
				continue;
			}
			if (str.toLowerCase().indexOf("researcher") != -1) {
				i.remove();
				continue;
			}
			if (str.toLowerCase().indexOf("tool") != -1) {
				i.remove();
				continue;
			}
			if (str.toLowerCase().indexOf("microsoft") != -1) {
				i.remove();
				continue;
			}
			if (str.toLowerCase().indexOf("windows") != -1) {
				i.remove();
				continue;
			}
			if (str.toLowerCase().indexOf("free_") != -1) {
				i.remove();
				continue;
			}
			if (str.toLowerCase().indexOf("_software") != -1) {
				i.remove();
				continue;
			}
		}
	}


	// 过滤一些无用的term
	public void filterSubCategory2(Vector<String> urlTerms) {
		Set<String> delTerms = new HashSet<String>();
		delTerms.add("Microsoft");
		delTerms.add("Windows");
		for (Iterator<String> i = urlTerms.iterator(); i.hasNext();) {
			String str = i.next();
			if (str.toLowerCase().indexOf("microsoft") != -1) {
				i.remove();
				continue;
			}
			if (str.toLowerCase().indexOf("windows") != -1) {
				i.remove();
				continue;
			}
			if (str.toLowerCase().indexOf("free_") != -1) {
				i.remove();
				continue;
			}
			if (str.toLowerCase().indexOf("_software") != -1) {
				i.remove();
				continue;
			}
			if (str.toLowerCase().indexOf("researcher") != -1) {
				i.remove();
				continue;
			}
			if (str.toLowerCase().indexOf("tool") != -1) {
				i.remove();
				continue;
			}
			if (str.toLowerCase().indexOf("google") != -1) {
				i.remove();
				continue;
			}
		}
	}

	// 抽取分类页面的子类，针对分类页面。区分子页面和子类。
	public Vector<String> extractSubCategory(StringBuffer buffer) {
		/*
		 * <a href="/wiki/Category:Classification_algorithms"
		 * title="Category:Classification algorithms">Classification
		 * algorithms</a>
		 */
		String startFeature0 = "mw-subcategories";
		String startFeature = "Subcategories";
		String feature = "href=\"/wiki/Category:";
		int delta = feature.length();
		String endFeature = "mw-pages";

		int startPos0 = buffer.indexOf(startFeature0);
		int startPos = buffer.indexOf(startFeature, startPos0);
		if (startPos == -1)
			startPos = Integer.MAX_VALUE;
		int endPos = buffer.indexOf(endFeature, startPos);

		int pos = buffer.indexOf(feature, startPos);

		Vector<String> cat = new Vector<String>();
		while (pos != -1) {
			if (pos < startPos)
				break;
			if (pos > endPos)
				break;

			int end = buffer.indexOf("\"", pos + delta);
			cat.add(buffer.substring(pos + delta, end));
			pos = buffer.indexOf(feature, pos + delta);
		}
		return cat;
	}

	// 抽取分类页面的子页面，针对分类页面。区分子页面和子类。
	public Vector<String> extractSubPages(StringBuffer buffer) {
		/*
		 * <a href="/wiki/Category:Classification_algorithms"
		 * title="Category:Classification algorithms">Classification
		 * algorithms</a>
		 */
		String startFeature0 = "Pages in category";
		String startFeature = "mw-content-ltr";
		String feature = "href=\"/wiki/";
		int delta = feature.length();
		String endFeature = "</div></div></div>";

		int startPos0 = buffer.indexOf(startFeature0);
		int startPos = buffer.indexOf(startFeature, startPos0);
		if (startPos == -1)
			startPos = Integer.MAX_VALUE;
		int endPos = buffer.indexOf(endFeature, startPos);

		int pos = buffer.indexOf(feature, startPos);

		Vector<String> cat = new Vector<String>();
		while (pos != -1) {
			if (pos < startPos)
				break;
			if (pos > endPos)
				break;

			int end = buffer.indexOf("\"", pos + delta);
			cat.add(buffer.substring(pos + delta, end));
			pos = buffer.indexOf(feature, pos + delta);
		}
		return cat;
	}
	
	// 抽取页面的父类，针对一般页面和分类页面
	public Vector<String> extractSupCategory(String s) {
		/*
		 * <a href="/wiki/Category:Classification_algorithms"
		 * title="Category:Classification algorithms">Classification
		 * algorithms</a>
		 */
		String startFeature0 = "catlinks";
		String startFeature = "mw-normal-catlinks";
		String feature = "href=\"/wiki/Category:";
		int delta = "href=\"/wiki/Category:".length();
		String endFeature = "</div></div>";

		int startPos0 = s.indexOf(startFeature0);
		int startPos = s.indexOf(startFeature, startPos0);
		// 异常处理：在分类后可能还有一些隐藏的，无用的分类标签
		String exceptFeature = "mw-hidden-catlinks";
		//String exceptFeature2 = "mw-hidden-cats-hidden";
		int exceptPos = s.indexOf(exceptFeature);
		if (exceptPos == -1)
			exceptPos = Integer.MAX_VALUE;
		int endPos = s.indexOf(endFeature, startPos);

		int pos = s.indexOf(feature, startPos);

		Vector<String> cat = new Vector<String>();
		while (pos != -1) {
			if (pos > endPos)
				break;
			if (pos > exceptPos)
				break;

			int end = s.indexOf("\"", pos + delta);
			cat.add(s.substring(pos + delta, end));
			pos = s.indexOf(feature, pos + delta);
		}
		return cat;
	}

	// 抽取页面的父类，针对一般页面和分类页面
	public Vector<String> extractSupCategory(StringBuffer buffer) {
		/*
		 * <a href="/wiki/Category:Classification_algorithms"
		 * title="Category:Classification algorithms">Classification
		 * algorithms</a>
		 */
		String startFeature0 = "catlinks";
		String startFeature = "mw-normal-catlinks";
		String feature = "href=\"/wiki/Category:";
		int delta = "href=\"/wiki/Category:".length();
		String endFeature = "</div></div>";

		int startPos0 = buffer.indexOf(startFeature0);
		int startPos = buffer.indexOf(startFeature, startPos0);
		// 异常处理：在分类后可能还有一些隐藏的，无用的分类标签
		String exceptFeature = "mw-hidden-catlinks";
		//String exceptFeature2 = "mw-hidden-cats-hidden";
		int exceptPos = buffer.indexOf(exceptFeature);
		if (exceptPos == -1)
			exceptPos = Integer.MAX_VALUE;
		int endPos = buffer.indexOf(endFeature, startPos);

		int pos = buffer.indexOf(feature, startPos);

		Vector<String> cat = new Vector<String>();
		while (pos != -1) {
			if (pos > endPos)
				break;
			if (pos > exceptPos)
				break;

			int end = buffer.indexOf("\"", pos + delta);
			cat.add(buffer.substring(pos + delta, end));
			pos = buffer.indexOf(feature, pos + delta);
		}
		return cat;
	}
	
	
	
	// 构建分类页面的URL
	public Vector<String> buildCategoryUrls(Vector<String> urls) {
		Vector<String> fullUrls = new Vector<String>();
		for (String url : urls) {
			fullUrls.add(buildCategoryUrl(url));
		}
		return fullUrls;
	}

	public String buildCategoryUrl(String url) {
		return "http://en.wikipedia.org/wiki/Category:" + url;
	}

	// 以追加方式打开文件
	public void init(String file) {
		System.out.println("CSV file open... " + file);
		try {
			cvsWriter = new FileWriter(file, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// cvs文件写入。参数type=1表示是父类，如果是0表示子类
	public void cvsStore(String node, int type, Vector<String> lines) {
		try {
			node = getOdd(node);
			for (String line : lines) {
				line =getOdd(line);
				if (line.equals(node))
					continue;
				String record="";
				if (1 == type) {
					record=line + "," + node + "\n";
				} else {
					record=node + "," + line + "\n";
				}
				if(!hsHaveStore.contains(record)){
					cvsWriter.write(record);
					hsHaveStore.add(record);
				}
			}
			cvsWriter.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 根据ULR从网上获取HTML文件
	public static StringBuffer getPageBufferFromWeb(String url) {
		return getPageBufferFromWeb2(url,null);
	}	

	//得到页面的内容，返回值删掉了回车，传出参数未删除回车
	public static StringBuffer getPageBufferFromWeb2(String url,StringBuffer outBuffer) {
		StringBuffer sb = new StringBuffer();
		try {
			URL my_url = new URL(url);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					my_url.openStream()));
			
			String strTemp = "";
			while (null != (strTemp = br.readLine())) {
				sb.append(strTemp);
				if(null != outBuffer) outBuffer.append(strTemp+"\r\n");
			}
			System.out.println("get page successful form " + url);
			if(null != outBuffer) outBuffer.append(br.toString());
		} catch (Exception ex) {
			//ex.printStackTrace();
                        System.err.println("page not found: " + url);
		}

		return sb;
	}
	
	/**
	 * 
	 * @param str 某个术语
	 * @return str的单数形式
	 */
	public String getOdd(String str){
		String a="",b=str;
		if(str.contains("_")){
			a=str.substring(0, str.lastIndexOf("_")+1);
			b=str.substring(str.lastIndexOf("_")+1,str.length());
		}
		String result=a+new OddComplexReplace().replace(b, vDic);
		return result;
	}
}
