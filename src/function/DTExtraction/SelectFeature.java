package function.DTExtraction;

import java.util.Vector;

import function.base.ExcelBase;
import function.util.ExcelUtil;
import function.util.SetUtil;

public class SelectFeature extends ExcelBase {

	String fileName = "";
	int sheetId = 0;
	String featureName = "";
	int frequencyThreshold = 0;
	double ratioThreshold = -1;
	String targetExcelName = "";
	int targetSheetId = 0;
	String exceptionFile = "";// 例外列表文件

	// 只考虑frequency阀值使用
	public SelectFeature(String fileName, int sheetId, String featureName,
			int frequencyThreshold, String targetExcelName, int targetSheetId) {
		super();
		this.fileName = fileName;
		this.sheetId = sheetId;
		this.featureName = featureName;
		this.frequencyThreshold = frequencyThreshold;
		this.targetExcelName = targetExcelName;
		this.targetSheetId = targetSheetId;
	}

	// frequency阀值和ratio阀值同时考虑
	public SelectFeature(String fileName, int sheetId, String featureName,
			int frequencyThreshold, double ratioThreshold,
			String targetExcelName, int targetSheetId) {
		super();
		this.fileName = fileName;
		this.sheetId = sheetId;
		this.featureName = featureName;
		this.frequencyThreshold = frequencyThreshold;
		this.ratioThreshold = ratioThreshold;
		this.targetExcelName = targetExcelName;
		this.targetSheetId = targetSheetId;
	}

	// frequency阀值和ratio阀值同时考虑
	public SelectFeature(String fileName, int sheetId, String featureName,
			int frequencyThreshold, double ratioThreshold,
			String targetExcelName, int targetSheetId, String exceptionFile) {
		super();
		this.fileName = fileName;
		this.sheetId = sheetId;
		this.featureName = featureName;
		this.frequencyThreshold = frequencyThreshold;
		this.ratioThreshold = ratioThreshold;
		this.targetExcelName = targetExcelName;
		this.targetSheetId = targetSheetId;
		this.exceptionFile = exceptionFile;
	}

	@Override
	public void process() throws Exception {
		// TODO Auto-generated method stub
		Vector<String> vFeature = new Vector<String>();
		Vector<Integer> vFeatureFrequency = new Vector<Integer>();
		Vector<Double> vFeatureRatio = new Vector<Double>();
		Vector<String> vException = new Vector<String>();
		if (exceptionFile.length() != 0)
			vException = SetUtil.readSetFromFile(exceptionFile);
		for (int i = 1; i < getRows(sheetId); i++) {
			String feature = getStringValue(sheetId, featureName, i);
			if (ratioThreshold < 0) {
				int frequency = getIntegerValue(sheetId, "frequency", i);
				if (frequency >= frequencyThreshold) {
					vFeature.add(feature);
					vFeatureFrequency.add(frequency);
				}
			}// 只考虑frequency阀值使用
			else if (exceptionFile.length() == 0) {
				int frequency = getIntegerValue(sheetId, "frequencyAll", i);
				double ratio = getDoubleValue(sheetId, "ratio", i);
				if (frequency >= frequencyThreshold && ratio >= ratioThreshold
						&& ratio != 1) {
					vFeature.add(feature);
					vFeatureFrequency.add(frequency);
					vFeatureRatio.add(ratio);
				}
			}// frequency阀值和ratio阀值同时考虑，第一层
			else {
				int frequency = getIntegerValue(sheetId, "frequencyAll", i);
				double ratio = getDoubleValue(sheetId, "ratio", i);
				if (frequency >= frequencyThreshold && ratio >= ratioThreshold
						&& !vException.contains(feature)) {
					vFeature.add(feature);
					vFeatureFrequency.add(frequency);
					vFeatureRatio.add(ratio);
				}
			}// frequency阀值和ratio阀值同时考虑，第二层
		}
		ExcelUtil.writeSetToExcel(vFeature, targetExcelName, targetSheetId,
				featureName);
		ExcelUtil.writeIntegerSetToExcel(vFeatureFrequency, targetExcelName,
				targetSheetId, "frequency");
		if (vFeatureRatio.size() != 0)
			ExcelUtil.writeDoubleSetToExcel(vFeatureRatio, targetExcelName,
					targetSheetId, "ratio");
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
