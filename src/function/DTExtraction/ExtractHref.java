package function.DTExtraction;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import function.base.ExcelBase;
import function.util.SetUtil;

/**
 * 
 * @author MJ
 * @description 抽取历史编辑信息
 */
public class ExtractHref extends ExcelBase {

	String xlsFileName = "";
	String htmlPath = "";// 网页文件夹

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Data_mining Data_structure Computer_network
		String htmlPath = "F:\\FacetedTaxonomy\\Data_structure\\html\\layer1-select";
		String xlsFileName = "F:\\FacetedTaxonomy\\Data_structure\\process\\layer1-select-href.xls";
		ExtractHref ec = new ExtractHref(htmlPath,
				xlsFileName);
		ec.run(ec);
	}

	// 构造函数
	public ExtractHref(String htmlPath, String xlsFileName) {
		this.xlsFileName = xlsFileName;
		this.htmlPath = htmlPath;
	}

	public void extractHref() {
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		File f = new File(htmlPath);
		File childs[] = f.listFiles();
		int recordID = 0;
		int stringRecordID = 1;
		for (int i = 0; i < childs.length; i++) {// 遍历每个文件
			System.out.println(i);
			String fileName = childs[i].getName();
			String filePath = childs[i].getAbsolutePath();
			WikiHrefProcess whp = new WikiHrefProcess();
			Vector<String> hisV = whp.getWikiTermFromFile(filePath);// 获取每个文件的href
			String superTermString = "";
			String term = fileName.substring(0, fileName.length() - 5);
			for (int j = 0; j < hisV.size(); j++) {
				String superTerm = hisV.get(j);
				superTermString += superTerm + ",";
				if (hm.containsKey(superTerm)) {
					int n = hm.get(superTerm);
					n++;
					hm.put(superTerm, n);
				} else
					hm.put(superTerm, 1);
				setStringValue(0, "term", ++recordID, term);
				setStringValue(0, "href", recordID, superTerm);
			}
			if (superTermString.length() >= 1)
				superTermString = superTermString.substring(0,
						superTermString.length() - 1);
			setStringValue(1, "term", stringRecordID, term);
			setStringValue(1, "href", stringRecordID++, superTermString);
		}// end for
		Iterator<String> it = hm.keySet().iterator();
		int number = 0;
		while (it.hasNext()) {
			String superTerm = it.next();
			int n = hm.get(superTerm);
			setStringValue(2, "href", ++number, superTerm);
			setIntegerValue(2, "frequency", number, n);
		}
	}
	
	/**
	 * 根据抽取的重要herf，统计术语出现的重要href频率
	 */
	public void statisticHref(){
		HashMap<String,Integer> hm=new HashMap<String,Integer>();
		for(int i=1;i<getRows(0);i++){
			String term=getStringValue(0,"term",i);
			hm.put(term, i);
		}
		Vector<String> vHref=getColumnContent(6, "href");
		String htmlDir="F:\\FacetedTaxonomy\\Data_structrue\\html\\layer1-select";
		File f=new File(htmlDir);
		File childs[]=f.listFiles();
		WikiHrefProcess whp = new WikiHrefProcess();
		for(int i=0;i<childs.length;i++){
			String path=childs[i].getAbsolutePath();
			String term=childs[i].getName().replace(".html", "");
			Vector<String> hisV = whp.getWikiTermFromFile(path);// 获取每个文件的href
			Vector<String> interV=SetUtil.getNoRepeatVector(SetUtil.getInterSet(hisV, vHref));
			int interNumber=interV.size();
			if(hm.containsKey(term)){
				int id=hm.get(term);
				setIntegerValue(0,"hrefInterNumber",id,interNumber);
				setStringValue(0,"hrefInter",id,interV.toString());
			}
			System.out.println(i+"-"+term+":"+interV);
		}
	}

	@Override
	public void process() throws Exception {
		// TODO Auto-generated method stub
		extractHref();
		//statisticHref();
	}

	@Override
	public void run(ExcelBase eb) {
		// TODO Auto-generated method stub
		eb.go(xlsFileName);
	}
}
