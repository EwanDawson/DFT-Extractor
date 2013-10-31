package function.DTExtraction;

import java.util.Vector;

import function.util.ExcelUtil;
import function.util.SetUtil;

public class Function {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Data_mining Data_structure Computer_network
		String domain="Data_structure";
		String termSetName="DS_termset.txt";
		String layer2XlsPath="F:\\FacetedTaxonomy\\"+domain+"\\layer2.xls";
		String layer1SelectPath="F:\\FacetedTaxonomy\\"+domain+"\\process\\layer1-select.txt";
		String termSetPath="F:\\FacetedTaxonomy\\"+domain+"\\"+termSetName;
		String remainPath="F:\\FacetedTaxonomy\\"+domain+"\\process\\remainTerm.txt";
		getRemainTerm(layer2XlsPath,layer1SelectPath,termSetPath,remainPath);
	}
	
	public static void getRemainTerm(String layer2XlsPath,String layer1SelectPath,String termSetPath,String remainPath){
		Vector<String> vSelectTerm=ExcelUtil.readSetFromExcel(layer2XlsPath, 1, "term");
		Vector<String> vLayer1Select=SetUtil.readSetFromFile(layer1SelectPath);
		Vector<String> vTerm=SetUtil.readSetFromFile(termSetPath);
		for(String s:vTerm)
			System.out.println(s);
		System.out.println("---------------");
		Vector<String> vRemain=SetUtil.getSubSet(SetUtil.getSubSet(vTerm, vSelectTerm),vLayer1Select);
		SetUtil.writeSetToFile(vRemain, remainPath);
		for(String s:vRemain)
			System.out.println(s);
	}
}
