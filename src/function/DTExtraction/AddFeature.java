package function.DTExtraction;

import java.util.HashMap;

import function.base.ExcelBase;

public class AddFeature extends ExcelBase {

	// Data_mining Data_structure Computer_network
	String fileName = "F:\\FacetedTaxonomy\\Data_mining\\layer2.xls";
	int srcSheet=0;
	int targetSheet=0;
	String srcColumnName="";
	String targetColumnName="";
	String featureType="";
	public AddFeature(String fileName,int srcSheet,int targetSheet,String srcColumnName,String targetColumnName,String featureType){
		this.fileName=fileName;
		this.srcSheet=srcSheet;
		this.targetSheet=targetSheet;
		this.srcColumnName=srcColumnName;
		this.targetColumnName=targetColumnName;
		this.featureType=featureType;
	}

	@Override
	public void process() throws Exception {
		// TODO Auto-generated method stub
		if(featureType.equals("Integer"))
			addIntegerFeature(srcSheet,targetSheet,srcColumnName,targetColumnName);
		else if(featureType.equals("String"))
			addStringFeature(srcSheet,targetSheet,srcColumnName,targetColumnName);
		else if(featureType.equals("Double"))
			addDoubleFeature(srcSheet,targetSheet,srcColumnName,targetColumnName);
	}

	/**
	 * 把srcSheetID中的srcTitle一列的属性添加到toSheetID对应的toName列（限整数属性）,默认第一列是对应的词条列
	 * 
	 * @param srcSheetID
	 * @param toSheetID
	 * @param srcTitle
	 * @param toName
	 */
	public void addIntegerFeature(int srcSheetID, int toSheetID,
			String srcTitle, String toTitle) {
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		for (int i = 1; i < getRows(srcSheetID); i++) {
			String srcName = getStringValue(srcSheetID, 0, i);
			int srcAtt = getIntegerValue(srcSheetID, srcTitle, i);
			hm.put(srcName, srcAtt);
		}
		for (int i = 1; i < getRows(toSheetID); i++) {
			String toName = getStringValue(toSheetID, 0, i);
			if (hm.containsKey(toName)) {
				int toAtt = hm.get(toName);
				setIntegerValue(toSheetID, toTitle, i, toAtt);
			} else
				setStringValue(toSheetID, toTitle, i, "no");
		}
	}

	/**
	 * 把srcSheetID中的srcTitle一列的属性添加到toSheetID对应的toName列（限浮点属性）,默认第一列是对应的词条列
	 * 
	 * @param srcSheetID
	 * @param toSheetID
	 * @param srcTitle
	 * @param toName
	 */
	public void addDoubleFeature(int srcSheetID, int toSheetID,
			String srcTitle, String toTitle) {
		HashMap<String, Double> hm = new HashMap<String, Double>();
		for (int i = 1; i < getRows(srcSheetID); i++) {
			String srcName = getStringValue(srcSheetID, 0, i);
			double srcAtt = getDoubleValue(srcSheetID, srcTitle, i);
			hm.put(srcName, srcAtt);
		}
		for (int i = 1; i < getRows(toSheetID); i++) {
			String toName = getStringValue(toSheetID, 0, i);
			if (hm.containsKey(toName)) {
				double toAtt = hm.get(toName);
				setDoubleValue(toSheetID, toTitle, i, toAtt);
			} else
				setDoubleValue(toSheetID, toTitle, i, -1);
		}
	}

	/**
	 * 把srcSheetID中的srcTitle一列的属性添加到toSheetID对应的toName列（限整数类型）,默认第一列是对应的词条列
	 * 
	 * @param srcSheetID
	 * @param toSheetID
	 * @param srcTitle
	 * @param toName
	 */
	public void addStringFeature(int srcSheetID, int toSheetID,
			String srcTitle, String toTitle) {
		HashMap<String, String> hm = new HashMap<String, String>();
		for (int i = 1; i < getRows(srcSheetID); i++) {
			String srcName = getStringValue(srcSheetID, 0, i);
			String srcAtt = getStringValue(srcSheetID, srcTitle, i);
			hm.put(srcName, srcAtt);
		}
		for (int i = 1; i < getRows(toSheetID); i++) {
			String toName = getStringValue(toSheetID, 0, i);
			if (hm.containsKey(toName)) {
				String toAtt = hm.get(toName);
				setStringValue(toSheetID, toTitle, i, toAtt);
			} else
				setStringValue(toSheetID, toTitle, i, "false");
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
