package function.DTExtraction;

import function.base.ExcelBase;

/**
 * Excel表格数据的计算
 * 
 * @author MJ
 * @description
 */
public class ComputeFeature extends ExcelBase {

	String fileName = "";
	int sheet = 0;
	String columnName1 = "";
	String columnName2 = "";
	String operation = "";
	String targetColumnName = "";

	public ComputeFeature(String fileName, int sheet, String columnName1,
			String columnName2, String operation, String targetColumnName) {
		super();
		this.fileName = fileName;
		this.sheet = sheet;
		this.columnName1 = columnName1;
		this.columnName2 = columnName2;
		this.operation = operation;
		this.targetColumnName = targetColumnName;
	}

	@Override
	public void process() throws Exception {
		// TODO Auto-generated method stub
		for (int i = 1; i < getRows(sheet); i++) {
			double a = getDoubleValue(sheet, columnName1, i);
			double b = getDoubleValue(sheet, columnName2, i);
			if (operation.equals("+"))
				setDoubleValue(sheet, targetColumnName, i, a + b);
			else if (operation.equals("-"))
				setDoubleValue(sheet, targetColumnName, i, a - b);
			else if (operation.equals("*"))
				setDoubleValue(sheet, targetColumnName, i, a * b);
			else if (operation.equals("/"))
				setDoubleValue(sheet, targetColumnName, i, a / b);
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
		String fileName = "F:\\extractTest\\Data_mining\\process\\layer1-DTSelect-history.xls";
		int sheet = 2;
		String columnName1 = "frequency";
		String columnName2 = "frequencyAll";
		String operation = "+";
		String targetColumnName = "ratio2";
		ComputeFeature cf=new ComputeFeature(fileName, sheet, columnName1, columnName2, operation, targetColumnName);
		cf.run(cf);
	}

}
