package function.hypRelation;

import function.base.ExcelBase;

/**
 * 根据一些简单的规则纠正上下位
 * @author MJ
 * @description
 */
public class RectifyRelation extends ExcelBase{

	/**
	 * @param args
	 */
	String fileName="";
	int sheetId=1;
	String relationColumnName="";
	
	public RectifyRelation(String fileName, int sheetId,
			String relationColumnName) {
		super();
		this.fileName = fileName;
		this.sheetId = sheetId;
		this.relationColumnName = relationColumnName;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName="F:\\DOFT-data\\hyperExtraction\\DM\\html-hypRelation\\relation.xls";
		int sheetId=1;
		String relationColumnName="checkRelation";
		RectifyRelation rr=new RectifyRelation(fileName, sheetId, relationColumnName);
		rr.run(rr);
	}

	@Override
	public void process() throws Exception {
		// TODO Auto-generated method stub
		for(int i=1;i<getRows(sheetId);i++){
			String src=getStringValue(sheetId,"sourceURLName",i);
			String to=getStringValue(sheetId,"toURLName",i);
			String relation=getStringValue(sheetId,relationColumnName,i);
			if(relation.equals("Other")){
				String checkRelation=checkRelation(src,to);
				System.out.println(src+"->"+to+":"+checkRelation);
				setStringValue(sheetId,relationColumnName,i,checkRelation);
			}
		}
	}

	@Override
	public void run(ExcelBase eb) {
		// TODO Auto-generated method stub
		eb.go(fileName);
	}
	
	/**
	 * 根据给定的src和to确定关系
	 * 
	 * @param src
	 * @param to
	 * @return
	 */
	public String checkRelation(String src, String to) {
		String relation = "";
		src = src.toLowerCase();
		to = to.toLowerCase();
		String srcSuffix = src, toSuffix = to;
		if (src.contains("_"))
			srcSuffix = src.substring(src.lastIndexOf("_") + 1, src.length());
		if (to.contains("_"))
			toSuffix = to.substring(to.lastIndexOf("_") + 1, to.length());

		if (to.startsWith(srcSuffix))
			relation = "A is a B";
		else if (src.startsWith(toSuffix))
			relation = "B is a A";
		else if (srcSuffix.equals(toSuffix))
			relation = "Other";
		else
			relation = "Other";
		return relation;
	}

}
