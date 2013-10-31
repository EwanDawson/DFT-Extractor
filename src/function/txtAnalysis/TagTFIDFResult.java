package function.txtAnalysis;

import java.io.File;
import java.util.Vector;

import function.util.SetUtil;

/**
 * 
 * @author MJ
 * @description 给tfidf计算的结果打领域术语标签
 */
public class TagTFIDFResult {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String termSetPath="F:\\Data\\术语抽取数据集\\DSDataSet\\DS_experiment\\DS_termset.txt";
		String tfidfDir="F:\\FacetedTaxonomy\\Data_structure\\html\\layer01-raw_wikiVector_tfidf";
		tagTFIDFResult(termSetPath, tfidfDir);
	}
	public static void tagTFIDFResult(String termSetPath,String tfidfDir){
		Vector<String> v=SetUtil.getLowerCaseSet(SetUtil.readSetFromFile(termSetPath));
		File f=new File(tfidfDir);
		File childs[]=f.listFiles();
		for(int i=0;i<childs.length;i++){
			String filePath=childs[i].getAbsolutePath();
			Vector<String> vTemp=SetUtil.readSetFromFile(filePath);
			String title=vTemp.get(0)+",termTag";
			vTemp.set(0, title);
			for(int j=1;j<vTemp.size();j++){
				String record=vTemp.get(j);
				String term=record.split(",")[0].toLowerCase();
				if(v.contains(term))
					record=record+",true";
				else
					record=record+",false";
				vTemp.set(j, record);
			}
			SetUtil.writeSetToFile(vTemp, filePath);
			System.out.println(i);
		}
	}

}
