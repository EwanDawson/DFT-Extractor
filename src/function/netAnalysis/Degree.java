package function.netAnalysis;

import java.util.HashMap;

import function.base.ExcelBase;

public class Degree extends ExcelBase{

	String fileName="";
	public Degree(String fileName){
		this.fileName=fileName;
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
		HashMap<String, int[]> hm = new HashMap<String, int[]>();//保存节点度关系,出——入——度
		for (int i = 1; i < getRows(); i++) {
			String src = getStringValue("sourceURLName", i);
			String to = getStringValue("toURLName", i);
			if(hm.containsKey(src)){
				int degree[]=hm.get(src);
				degree[0]++;//out
				degree[2]++;//degree
				hm.put(src, degree);
			}
			else{
				int degree[]=new int[3];
				degree[0]=1;//out
				degree[1]=0;//in
				degree[2]=1;//degree
				hm.put(src, degree);
			}
			if(hm.containsKey(to)){
				int degree[]=hm.get(to);
				degree[1]++;//in
				degree[2]++;//degree
				hm.put(to, degree);
			}
			else{
				int degree[]=new int[3];
				degree[0]=0;//out
				degree[1]=1;//in
				degree[2]=1;//degree
				hm.put(to, degree);
			}
		}// 计算度
		for (int i = 1; i < getRows(); i++) {
			String src = getStringValue("sourceURLName", i);
			String to = getStringValue("toURLName", i);
			int srcDegree[]=hm.get(src);
			int toDegree[]=hm.get(to);
			setIntegerValue("srcOutDegree",i,srcDegree[0]);
			setIntegerValue("srcInDegree",i,srcDegree[1]);
			setIntegerValue("srcDegree",i,srcDegree[2]);
			setIntegerValue("toOutDegree",i,toDegree[0]);
			setIntegerValue("toInDegree",i,toDegree[1]);
			setIntegerValue("toDegree",i,toDegree[2]);
		}
	}
	@Override
	public void run(ExcelBase eb) {
		// TODO Auto-generated method stub
		eb.go(fileName);
	}

}
