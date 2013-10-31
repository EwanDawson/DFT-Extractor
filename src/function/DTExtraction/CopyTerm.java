package function.DTExtraction;

import java.util.HashMap;
import java.util.Vector;

import function.base.ExcelBase;
import function.util.SetUtil;

public class CopyTerm extends ExcelBase {

	String fileName="F:\\extractTest\\Data_mining\\process\\layer1.xls";
	String sameColumnName="term";//关键列
	int srcSheet=1;
	int desSheet=5;
	String columnNames[]={"category","FSWikiTerm","editor"};
	boolean repeatTag=false;//是否保留重复
	
	public CopyTerm(){
		
	}
	
	public CopyTerm(String fileName, String sameColumnName, int srcSheet,
			int desSheet, String[] columnNames, boolean repeatTag) {
		super();
		this.fileName = fileName;
		this.sameColumnName = sameColumnName;
		this.srcSheet = srcSheet;
		this.desSheet = desSheet;
		this.columnNames = columnNames;
		this.repeatTag = repeatTag;
	}

	@Override
	public void process() throws Exception {
		// TODO Auto-generated method stub
		HashMap<String,Integer> hmPos=new HashMap<String,Integer>();
		Vector<String> vSrc=new Vector<String>();
		Vector<String> vDes=new Vector<String>();
		for(int i=1;i<getRows(srcSheet);i++){
			String key=getStringValue(srcSheet,sameColumnName,i);
			vSrc.add(key);
			hmPos.put(key, i);
		}
		setStringValue(desSheet,sameColumnName,0,sameColumnName);
		for(int i=1;i<getRows(desSheet);i++){
			String key=getStringValue(desSheet,sameColumnName,i);
			vDes.add(key);
		}
		if(!repeatTag)
		   vSrc=SetUtil.getSubSet(vSrc, vDes);
		int recordId=getRows(desSheet);
		for(int i=0;i<vSrc.size();i++){
			String key=vSrc.get(i);
			int pos=hmPos.get(key);
			setStringValue(desSheet,sameColumnName,recordId,key);
			for(int j=0;j<columnNames.length;j++){
				String value=getStringValue(srcSheet,columnNames[j],pos);
				if(j<columnNames.length-1)
					setStringValue(desSheet,columnNames[j],recordId,value);
				else
					setStringValue(desSheet,columnNames[j],recordId++,value);
			}
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
		CopyTerm ct=new CopyTerm();
		ct.run(ct);
	}

}
