package function.hypRelation;

import java.util.Vector;

import function.base.ExcelBase;

public class Annotator extends ExcelBase{

	String xlsFileName="";
	int relationSheetID=0;
	TermGraph tg=null;
	Vector<String> vGraphRoot=null;
	
	public Annotator(String xlsFileName,int relationSheetID,TermGraph tg,Vector<String> vGraphRoot){
		this.xlsFileName=xlsFileName;
		this.relationSheetID=relationSheetID;
		this.tg=tg;
		this.vGraphRoot=vGraphRoot;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void process() throws Exception {
		// TODO Auto-generated method stub
		for(int i=1;i<getRows(relationSheetID);i++){
			String sourceURLName=getStringValue(relationSheetID,"sourceURLName",i);
			String toURLName=getStringValue(relationSheetID,"toURLName",i);
			int relationId=tg.getRelation(sourceURLName, toURLName);
			String relation="";
			if(relationId==1)
				relation="A is a B";
			else if(relationId==2)
				relation="B is a A";
			else if(relationId==-1){
				relation="noExist";
			}
			else{
				int minValue=Integer.MAX_VALUE;
				for(int j=0;j<vGraphRoot.size();j++){
					String root=vGraphRoot.get(j);
					int distance=tg.getCommonParentNode(root, sourceURLName, toURLName);
					if(distance<minValue)
						minValue=distance;
				}
				if(minValue>2)
					relation="Other";
				else
					relation="noExist";
			}
			setStringValue(relationSheetID,"relation",i,relation);
			System.out.println("±ê×¢£º"+sourceURLName+","+toURLName+","+relation);
		}
	}

	@Override
	public void run(ExcelBase eb) {
		// TODO Auto-generated method stub
		eb.go(xlsFileName);
	}

}
