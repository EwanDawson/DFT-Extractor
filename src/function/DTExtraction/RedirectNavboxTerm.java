package function.DTExtraction;

import java.util.HashMap;
import java.util.Vector;

import function.base.ExcelBase;
import function.util.ExcelUtil;

/**
 * 获取重定向的术语
 * @author MJ
 * @description
 */
public class RedirectNavboxTerm extends ExcelBase{

	String fileName="";
	Vector<String> vNavboxTerm=new Vector<String>();
	String desXlsFile="";
	int desXlsSheet=0;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void process() throws Exception {
		// TODO Auto-generated method stub
		HashMap<String,String> hmRedirect=new HashMap<String,String>();
		Vector<String> vTerm=new Vector<String>();
		for(int i=1;i<getRows();i++){
			String term=getStringValue(0,"term",i);
			String title=getStringValue(0,"title",i);
			hmRedirect.put(term,title);
		}
		for(String term:vNavboxTerm){
			if(hmRedirect.containsKey(term))
				term=hmRedirect.get(term);
			if(!vTerm.contains(term))
				vTerm.add(term);
		}
		ExcelUtil.writeSetToExcel(vTerm, desXlsFile, desXlsSheet, "term");
	}

	@Override
	public void run(ExcelBase eb) {
		// TODO Auto-generated method stub
		eb.go(fileName);
	}

	public RedirectNavboxTerm(String fileName, Vector<String> vNavboxTerm,
			String desXlsFile, int desXlsSheet) {
		super();
		this.fileName = fileName;
		this.vNavboxTerm = vNavboxTerm;
		this.desXlsFile = desXlsFile;
		this.desXlsSheet = desXlsSheet;
	}

}
