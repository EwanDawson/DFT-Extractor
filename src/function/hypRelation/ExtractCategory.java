package function.hypRelation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import function.base.ExcelBase;
import function.crawler.WikiCategoryCrawler;

/**
 * 
 * @author MJ
 * @description 抽取统计术语的category标签
 */
public class ExtractCategory extends ExcelBase {

	String xlsFileName = "";
	String htmlPath = "";// 网页文件夹

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Data_mining Data_structure Computer_network
		String htmlPath = "F:\\extractTest\\Data_mining\\html\\layer12-select";
		String xlsFileName = "F:\\extractTest\\Data_mining\\process\\layer12-select-cat.xls";
		ExtractCategory ec = new ExtractCategory(htmlPath, xlsFileName);
		ec.run(ec);
	}

	// 构造函数
	public ExtractCategory(String htmlPath, String xlsFileName) {
		this.xlsFileName = xlsFileName;
		this.htmlPath = htmlPath;
	}

	public void extractCategory() {
		WikiCategoryCrawler wcs = new WikiCategoryCrawler();
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		File f = new File(htmlPath);
		File childs[] = f.listFiles();
		int recordID = 0;
		int stringRecordID = 1;
		try {
			for (int i = 0; i < childs.length; i++) {// 遍历每个文件
				System.out.println(i);
				String fileName = childs[i].getName();
				FileReader fr = new FileReader(htmlPath + "/" + fileName);
				BufferedReader br = new BufferedReader(fr);
				StringBuffer sb = new StringBuffer();
				String s = br.readLine();
				while (s != null) {
					sb.append(s);
					s = br.readLine();
				}
				Vector<String> superV = wcs.extractSupCategory(sb);// 获取每个文件的supterm
				String superTermString = "";
				String term = fileName.substring(0, fileName.length() - 5);
				for (int j = 0; j < superV.size(); j++) {
					String superTerm = superV.get(j);
					superTermString += superTerm + ",";
					if (hm.containsKey(superTerm)) {
						int n = hm.get(superTerm);
						n++;
						hm.put(superTerm, n);
					} else
						hm.put(superTerm, 1);
					setStringValue(0, "term", ++recordID, term);
					setStringValue(0, "category", recordID, superTerm);
				}
				if (superTermString.length() >= 1)
					superTermString = superTermString.substring(0,
							superTermString.length() - 1);
				setStringValue(1, "term", stringRecordID, term);
				setStringValue(1, "category", stringRecordID++, superTermString);
				br.close();
				fr.close();
			}// end for
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Iterator<String> it = hm.keySet().iterator();
		int number = 0;
		while (it.hasNext()) {
			String superTerm = it.next();
			int n = hm.get(superTerm);
			setStringValue(2, "category", ++number, superTerm);
			setIntegerValue(2, "frequency", number, n);
		}
	}

	@Override
	public void process() throws Exception {
		// TODO Auto-generated method stub
		extractCategory();
	}

	@Override
	public void run(ExcelBase eb) {
		// TODO Auto-generated method stub
		eb.go(xlsFileName);
	}
}
