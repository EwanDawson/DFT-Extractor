package function.DTExtraction;

import java.io.File;
import java.util.Vector;

import function.util.SetUtil;

public class CategoryAnalysis {

	String selectTermPath="F:\\extractTest\\Data_mining\\process\\layer12-select.txt";//已经选择的术语列表路径
	String termSetPath="F:\\extractTest\\Data_mining\\process\\DM_termset.txt";
	String categoryDir="C:\\Users\\Lenovo\\Desktop\\category_data";
	String desTermListPath="";//category最终选择的术语放置位置
	
	public CategoryAnalysis(String selectTermPath,String categoryDir,String desTermListPath) {
		super();
		this.selectTermPath = selectTermPath;
		this.categoryDir = categoryDir;
		this.desTermListPath=desTermListPath;
	}
	
	public CategoryAnalysis(String selectTermPath, String termSetPath,
			String categoryDir,String desTermListPath) {
		super();
		this.selectTermPath = selectTermPath;
		this.termSetPath = termSetPath;
		this.categoryDir = categoryDir;
		this.desTermListPath=desTermListPath;
	}
	
	public void getCategoryTerm(){
		Vector<String> vSelectTerm=SetUtil.readSetFromFile(selectTermPath);
		Vector<String> vCatRecallTerm=new Vector<String>();
		File f=new File(categoryDir);
		File childs[]=f.listFiles();
		for(int i=0;i<childs.length;i++){
			String path=childs[i].getAbsolutePath();
			String name=childs[i].getName();
			String category=name.substring(0, name.lastIndexOf("_"));
			Vector<String> vCatString=SetUtil.readSetFromFile(path);
			Vector<String> vCatTerm=new Vector<String>();
			for(String temp:vCatString){
				String term[]=temp.split(",");
				if(!term[0].contains(":")&&!term[0].contains("/"))
				vCatTerm.add(term[0]);
				if(!term[1].contains(":")&&!term[1].contains("/"))
				vCatTerm.add(term[1]);
			}
			vCatTerm=SetUtil.getNoRepeatVector(vCatTerm);
			int sum=vCatTerm.size();
			Vector<String> vSelectCatTerm=SetUtil.getInterSet(vSelectTerm, vCatTerm);//选择的
			int selectCatTermNumber=vSelectCatTerm.size();//选择的总数
			int selectCatIsTermNumber=0;//选择的是术语的数量
			Vector<String> vNoSelectCatTerm=SetUtil.getSubSet(vCatTerm, vSelectCatTerm);//没有选择的
			int noSelectCatTermNumber=vNoSelectCatTerm.size();//没选择的总数
			int noSelectCatIsTermNumber=0;//没选择的是术语的数量
			vCatRecallTerm.addAll(vNoSelectCatTerm);
			System.out.println(category+":("+sum+"),("+selectCatIsTermNumber+"/"+selectCatTermNumber+"),("+noSelectCatIsTermNumber+"/"+noSelectCatTermNumber+")");
		}
		vCatRecallTerm=SetUtil.getNoRepeatVector(vCatRecallTerm);
		int recallNumber=vCatRecallTerm.size();
		int recallIsTermNumber=0;
		System.out.println("召回情况："+recallIsTermNumber+"/"+recallNumber);
		SetUtil.writeSetToFile(vCatRecallTerm, desTermListPath);
	}
	
	/**
	 * 目录中术语、已筛选术语、标注领域术语的交集
	 */
	public void InterAnalysis(){
		Vector<String> vSelectTerm=SetUtil.readSetFromFile(selectTermPath);
		Vector<String> vTerm=SetUtil.readSetFromFile(termSetPath);
		Vector<String> vCatRecallTerm=new Vector<String>();
		File f=new File(categoryDir);
		File childs[]=f.listFiles();
		for(int i=0;i<childs.length;i++){
			String path=childs[i].getAbsolutePath();
			String name=childs[i].getName();
			String category=name.substring(0, name.lastIndexOf("_"));
			Vector<String> vCatString=SetUtil.readSetFromFile(path);
			Vector<String> vCatTerm=new Vector<String>();
			for(String temp:vCatString){
				String term[]=temp.split(",");
				vCatTerm.add(term[0]);
				vCatTerm.add(term[1]);
			}
			vCatTerm=SetUtil.getNoRepeatVector(vCatTerm);
			int sum=vCatTerm.size();
			Vector<String> vSelectCatTerm=SetUtil.getInterSet(vSelectTerm, vCatTerm);//选择的
			int selectCatTermNumber=vSelectCatTerm.size();//选择的总数
			int selectCatIsTermNumber=SetUtil.getInterSet(vSelectCatTerm, vTerm).size();//选择的是术语的数量
			Vector<String> vNoSelectCatTerm=SetUtil.getSubSet(vCatTerm, vSelectCatTerm);//没有选择的
			int noSelectCatTermNumber=vNoSelectCatTerm.size();//没选择的总数
			int noSelectCatIsTermNumber=SetUtil.getInterSet(vNoSelectCatTerm, vTerm).size();//没选择的是术语的数量
			vCatRecallTerm.addAll(vNoSelectCatTerm);
			System.out.println(category+":("+sum+"),("+selectCatIsTermNumber+"/"+selectCatTermNumber+"),("+noSelectCatIsTermNumber+"/"+noSelectCatTermNumber+")");
		}
		vCatRecallTerm=SetUtil.getNoRepeatVector(vCatRecallTerm);
		int recallNumber=vCatRecallTerm.size();
		int recallIsTermNumber=SetUtil.getInterSet(vCatRecallTerm, vTerm).size();
		System.out.println("召回情况："+recallIsTermNumber+"/"+recallNumber);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
