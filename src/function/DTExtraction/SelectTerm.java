package function.DTExtraction;

import java.util.HashMap;
import java.util.Vector;

import function.base.ExcelBase;
import function.util.MapUtil;
import function.util.SetUtil;

public class SelectTerm extends ExcelBase {

	// Data_mining Data_structure Computer_network
	String fileName = "F:\\FacetedTaxonomy\\Data_structure\\layer2.xls";
	String featureName = "editor";// 特征名称
	int featureVectorSheet = 4;// 选择的特征集合sheet
	int notSelectSheetId = 0;
	int selectSheetId = 1;
	String detailSavePath="f:/test.txt";//详情保存路径
	boolean haveTermTag = false;
	
	public SelectTerm(String fileName){
		this.fileName=fileName;
	}

	public SelectTerm(String fileName, String featureName,
			int featureVectorSheet, int notSelectSheetId, int selectSheetId,String detailSavePath,boolean haveTermTag) {
		super();
		this.fileName = fileName;
		this.featureName = featureName;
		this.featureVectorSheet = featureVectorSheet;
		this.notSelectSheetId = notSelectSheetId;
		this.selectSheetId = selectSheetId;
		this.detailSavePath=detailSavePath;
		this.haveTermTag = haveTermTag;
	}

	@Override
	public void process() throws Exception {
		// TODO Auto-generated method stub
		selectTerm(notSelectSheetId, selectSheetId);
		//addSelectTag(0,1);
	}

	/**
	 * 将符合指定特征的术语选择出来
	 * 
	 * @param notSelectSheetId
	 * @param selectSheetId
	 */
	public void selectTerm(int notSelectSheetId, int selectSheetId) {
		int recordId = getRows(selectSheetId);
		if (recordId <= 0)
			recordId = 1;
		HashMap<String,Integer> hmFeaturePos=new HashMap<String,Integer>();
		HashMap<String, Integer> hmFeature = new HashMap<String, Integer>();
		HashMap<String, Integer> hmTermNumber = new HashMap<String, Integer>();
		Vector<String> vFeature =new Vector<String>();
		for(int i=1;i<getRows(featureVectorSheet);i++){
			String feature=getStringValue(featureVectorSheet,featureName,i);
			hmFeaturePos.put(feature, i);
			vFeature.add(feature);
		}
		Vector<String> vHaveSelectTerm = new Vector<String>();
		for (int i = 1; i < getRows(selectSheetId); i++) {
			String term = getStringValue(selectSheetId, "term", i);
			vHaveSelectTerm.add(term);
		}
		int selectNumber = 0;
		int selectTermNumber = 0;
		for (int i = 1; i < getRows(notSelectSheetId); i++) {
			boolean selectTag = false;
			String feature = getStringValue(notSelectSheetId, featureName, i);
			String lowFeature[] = feature.toLowerCase().split(",");
			for (String featureTemp : vFeature) {
				String lowFeatureTemp = featureTemp.toLowerCase();
				for (int j = 0; j < lowFeature.length; j++) {
					if (lowFeature[j].equals(lowFeatureTemp)) {
						String term = getStringValue(notSelectSheetId, "term",
								i);
						String termTag = "false";
						if (haveTermTag) {
							termTag = getStringValue(notSelectSheetId,
									"termTag", i);
						}
						if (!selectTag) {
							selectNumber++;
							if (termTag.equals("true"))
								selectTermNumber++;
							if (!vHaveSelectTerm.contains(term)) {
								setStringValue(selectSheetId, "term", recordId,
										term);
								if (haveTermTag)
									setStringValue(selectSheetId, "termTag",
											recordId, termTag);
								setStringValue(selectSheetId, featureName,
										recordId++, feature);
								vHaveSelectTerm.add(term);
							}
							selectTag = true;
						}
						if (hmFeature.containsKey(featureTemp)) {
							int number = hmFeature.get(featureTemp) + 1;
							hmFeature.put(featureTemp, number);
						} else
							hmFeature.put(featureTemp, 1);
						if (termTag.equals("true")) {
							if (hmTermNumber.containsKey(featureTemp)) {
								int number = hmTermNumber.get(featureTemp) + 1;
								hmTermNumber.put(featureTemp, number);
							} else
								hmTermNumber.put(featureTemp, 1);
						}
						break;
					}
				}
			}
		}// end for
		Vector<String[]> vSortFeature = MapUtil.sortMapValueDes(hmFeature);
		Vector<String> vDetail=new Vector<String>();
		for (String temp[] : vSortFeature) {
			int termNumber = 0;
			if (hmTermNumber.containsKey(temp[0]))
				termNumber = hmTermNumber.get(temp[0]);
			int pos=hmFeaturePos.get(temp[0]);
			String detail="";
			int frequency=getIntegerValue(featureVectorSheet,"frequency",pos);
			
			if(featureName.equals("editor")){
				double ratio=getDoubleValue(featureVectorSheet,"ratio",pos);
				detail=temp[0] + "@@@"+frequency+"@@@"+ratio+"@@@"+temp[1]+"@@@"+termNumber;
			}
			else{
				detail=temp[0] + "@@@"+frequency+"@@@"+temp[1]+"@@@"+termNumber;
			}
			vDetail.add(detail);
			setIntegerValue(featureVectorSheet,"recall",pos,Integer.valueOf(temp[1]));
			setIntegerValue(featureVectorSheet,"recallIsTerm",pos,termNumber);
			System.out.println(detail);
		}
		SetUtil.writeSetToFile(vDetail, detailSavePath);
		System.out.println("选择情况：" + selectTermNumber + "/" + selectNumber);
	}

	/**
	 * 将符合指定组合特征的术语选择出来
	 * 
	 * @param notSelectSheetId
	 * @param selectSheetId
	 */
	public void selectCombineFeatureTerm(int notSelectSheetId, int selectSheetId) {
		int recordId = getRows(selectSheetId);
		if (recordId <= 0)
			recordId = 1;
		HashMap<String, Integer> hmFeature = new HashMap<String, Integer>();
		HashMap<String, Integer> hmTermNumber = new HashMap<String, Integer>();
		Vector<String> vFeatureCombine = getColumnContent(featureVectorSheet,
				featureName);// 混合feature
		for (int i = 1; i < getRows(notSelectSheetId); i++) {
			boolean selectTag = false;
			String feature = getStringValue(notSelectSheetId, featureName, i);
			String lowFeature = feature.toLowerCase();
			for (String featureTemp : vFeatureCombine) {
				String FeatureTempArray[] = featureTemp.split(",");
				boolean flag = true;
				for (int j = 0; j < FeatureTempArray.length; j++) {
					if (!lowFeature.contains(FeatureTempArray[j].toLowerCase())) {
						flag = false;
						break;
					}
				}
				if (flag) {
					String term = getStringValue(notSelectSheetId, "term", i);
					String termTag = getStringValue(notSelectSheetId,
							"termTag", i);
					if (!selectTag) {
						setStringValue(selectSheetId, "term", recordId, term);
						setStringValue(selectSheetId, "termTag", recordId,
								termTag);
						setStringValue(selectSheetId, featureName, recordId++,
								feature);
						selectTag = true;
					}
					if (hmFeature.containsKey(featureTemp)) {
						int number = hmFeature.get(featureTemp) + 1;
						hmFeature.put(featureTemp, number);
					} else
						hmFeature.put(featureTemp, 1);
					if (termTag.equals("true")) {
						if (hmTermNumber.containsKey(featureTemp)) {
							int number = hmTermNumber.get(featureTemp) + 1;
							hmTermNumber.put(featureTemp, number);
						} else
							hmTermNumber.put(featureTemp, 1);
					}
				}// end if
			}// end for
		}// end for
		Vector<String[]> vSortFeature = MapUtil.sortMapValueDes(hmFeature);
		for (String temp[] : vSortFeature) {
			int termNumber = 0;
			if (hmTermNumber.containsKey(temp[0]))
				termNumber = hmTermNumber.get(temp[0]);
			System.out.println(temp[0] + "——" + termNumber + "/" + temp[1]);
		}
	}

	/**
	 * 添加术语是否被选择的标签
	 * 
	 * @param notSelectSheetId
	 * @param selectSheetId
	 */
	public void addSelectTag(int notSelectSheetId, int selectSheetId) {
		Vector<String> vSelectTerm = getColumnContent(selectSheetId, "term");
		for (int i = 1; i < getRows(notSelectSheetId); i++) {
			String term = getStringValue(notSelectSheetId, "term", i);
			if (vSelectTerm.contains(term))
				setStringValue(notSelectSheetId, "selectTag", i, "true");
			else
				setStringValue(notSelectSheetId, "selectTag", i, "false");
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
		String fileName="F:\\extractTest\\Data_mining\\process\\layer1.xls";
		SelectTerm st=new SelectTerm(fileName);
		st.run(st);
	}

}
