package function.DTExtraction;

import java.util.Vector;

import function.base.ExcelBase;
import function.util.SetUtil;

public class AddTermTag extends ExcelBase {

	//Data_mining Data_structure Computer_network
	String fileName="";
	String termPath="";
	int sheetId=0;
	public AddTermTag(String fileName,String termPath,int sheetId){
		this.fileName=fileName;
		this.termPath=termPath;
		this.sheetId=sheetId;
	}
	@Override
	public void process() throws Exception {
		// TODO Auto-generated method stub
		Vector<String> vTerm=SetUtil.readSetFromFile(termPath);
		for(int i=1;i<getRows(sheetId);i++){
			String term=getStringValue(sheetId,"term",i);
			if(vTerm.contains(term))
				setStringValue(sheetId,"termTag",i,"true");
			else
				setStringValue(sheetId,"termTag",i,"false");
		}
	}

	@Override
	public void run(ExcelBase eb) {
		// TODO Auto-generated method stub
		eb.go(fileName);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName="F:\\extractTest\\Data_structure\\process\\layer1.xls";
		String termPath="F:\\extractTest\\Data_structure\\process\\DS_termset.txt";
		int sheetId=5;
		ExcelBase eb=new AddTermTag(fileName,termPath,sheetId);
		eb.run(eb);
	}

}
