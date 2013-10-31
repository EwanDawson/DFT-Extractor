package function.hypRelation;

import java.util.HashMap;

import function.base.ExcelBase;


/**
 * 将预测的关系添加到关系表中
 * @author MJ
 * @description
 */
public class AddPredirectRelation extends ExcelBase {

	String fileName="";
	HashMap<Integer,String> hmPrediction=null;
	
	public AddPredirectRelation(String fileName,HashMap<Integer,String> hmPrediction){
		this.fileName=fileName;
		this.hmPrediction=hmPrediction;
	}
	@Override
	public void process() throws Exception {
		// TODO Auto-generated method stub
		for(int i=1;i<getRows(0);i++){
			int id=Integer.valueOf(getStringValue(0,"id",i));
			String sourceURLName=getStringValue(0,"sourceURLName",i);
			String toURLName=getStringValue(0,"toURLName",i);
			String relation=getStringValue(0,"relation",i);
			String predictRelation=relation;
			if(hmPrediction.containsKey(id))
				predictRelation=hmPrediction.get(id);
			setIntegerValue(1,"id",i,id);
			setStringValue(1,"sourceURLName",i,sourceURLName);
			setStringValue(1,"toURLName",i,toURLName);
			setStringValue(1,"relation",i,relation);
			setStringValue(1,"predictRelation",i,predictRelation);
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

	}

}
