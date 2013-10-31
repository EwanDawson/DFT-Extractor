package function.util;

import java.io.File;
import java.io.Serializable;
import java.util.Vector;

import function.base.ExcelUtilBase;

public class ExcelUtil extends ExcelUtilBase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String xlsFileName = "F:\\Data\\上下位数据集\\DM\\DM_html-hypRelation\\category.xls";
		int sheetID = 1;
		String ColumnName = "superTerm";
		String desxlsFileName = "F:\\Data\\上下位数据集\\DM\\DM_html-hypRelation\\relation.xls";
		int dessheetID = 3;
		String desColumnName = "superTerm";
		Vector<String> v = readSetFromExcel(xlsFileName, sheetID, ColumnName);
		writeSetToExcel(v, desxlsFileName, dessheetID, desColumnName);
	}
	
	/**
	 * 从xlsFileName中sheetID的ColumnName列获取整型内容
	 * 
	 * @param xlsFileName
	 * @param sheetID
	 * @param ColumnName
	 * @return
	 */
	public static Vector<Integer> readIntegerSetFromExcel(String xlsFileName,
			int sheetID, String ColumnName) {
		System.out.println("读取集合……");
		ExcelUtil eu = new ExcelUtil();
		Vector<Integer> v = new Vector<Integer>();
		eu.begin(xlsFileName);
		for (int i = 1; i < eu.getRows(sheetID); i++) {
			int record = eu.getIntegerValue(sheetID, ColumnName, i);
			v.add(record);
		}
		eu.end();
		return v;
	}

	/**
	 * 从xlsFileName中sheetID的ColumnName列获取内容
	 * 
	 * @param xlsFileName
	 * @param sheetID
	 * @param ColumnName
	 * @return
	 */
	public static Vector<String> readSetFromExcel(String xlsFileName,
			int sheetID, String ColumnName) {
		System.out.println("读取集合……");
		ExcelUtil eu = new ExcelUtil();
		Vector<String> v = new Vector<String>();
		eu.begin(xlsFileName);
		for (int i = 1; i < eu.getRows(sheetID); i++) {
			String record = eu.getStringValue(sheetID, ColumnName, i);
			v.add(record);
		}
		eu.end();
		return v;
	}

	/**
	 * 从xlsFileName中sheetID的ColumnID列获取内容
	 * 
	 * @param xlsFileName
	 * @param sheetID
	 * @param ColumnID
	 * @return
	 */
	public static Vector<String> readSetFromExcel(String xlsFileName,
			int sheetID, int ColumnID) {
		System.out.println("读取集合……");
		ExcelUtil eu = new ExcelUtil();
		Vector<String> v = new Vector<String>();
		eu.begin(xlsFileName);
		for (int i = 0; i < eu.getRows(sheetID); i++) {
			String record = eu.getStringValue(sheetID, ColumnID, i);
			v.add(record);
		}
		eu.end();
		return v;
	}

	/**
	 * 从xlsFileName中sheetID的ColumnNames多列获取内容
	 * 
	 * @param xlsFileName
	 * @param sheetID
	 * @param ColumnNames
	 * @return
	 */
	public static Vector<String[]> readSetFromExcel(String xlsFileName,
			int sheetID, String ColumnNames[]) {
		System.out.println("读取集合……");
		ExcelUtil eu = new ExcelUtil();
		Vector<String[]> v = new Vector<String[]>();
		eu.begin(xlsFileName);
		int columnNumber = ColumnNames.length;
		for (int i = 1; i < eu.getRows(sheetID); i++) {
			String record[] = new String[columnNumber];
			for (int j = 0; j < columnNumber; j++) {
				record[j] = eu.getStringValue(sheetID, ColumnNames[j], i);
			}
			v.add(record);
		}
		eu.end();
		return v;
	}

	/**
	 * 从xlsFileName中sheetID的ColumnIDs多列获取内容
	 * 
	 * @param xlsFileName
	 * @param sheetID
	 * @param ColumnIDs
	 * @return
	 */
	public static Vector<String[]> readSetFromExcel(String xlsFileName,
			int sheetID, int ColumnIDs[]) {
		System.out.println("读取集合……");
		ExcelUtil eu = new ExcelUtil();
		Vector<String[]> v = new Vector<String[]>();
		eu.begin(xlsFileName);
		int columnNumber = ColumnIDs.length;
		for (int i = 0; i < eu.getRows(sheetID); i++) {
			String record[] = new String[columnNumber];
			for (int j = 0; j < columnNumber; j++) {
				record[j] = eu.getStringValue(sheetID, ColumnIDs[j], i);
			}
			v.add(record);
		}
		eu.end();
		return v;
	}

	/**
	 * 把集合写入到指定的xls文件列
	 * 
	 * @param v
	 * @param xlsFileName
	 * @param sheetID
	 * @param ColumnName
	 */
	public static void writeSetToExcel(Vector<String> v, String xlsFileName,
			int sheetID, String ColumnName) {
		File f=new File(xlsFileName);
		File fParent=f.getParentFile();
		fParent.mkdirs();
		System.out.println("写入集合……");
		ExcelUtil eu = new ExcelUtil();
		eu.begin(xlsFileName);
		for (int i = 0; i < v.size(); i++) {
			eu.setStringValue(sheetID, ColumnName, i + 1, v.get(i));
		}
		eu.end();
	}
	
	/**
	 * 把集合写入到指定的xls文件列
	 * 
	 * @param v
	 * @param xlsFileName
	 * @param sheetID
	 * @param ColumnName
	 */
	public static void writeIntegerSetToExcel(Vector<Integer> v, String xlsFileName,
			int sheetID, String ColumnName) {
		File f=new File(xlsFileName);
		File fParent=f.getParentFile();
		fParent.mkdirs();
		System.out.println("写入集合……");
		ExcelUtil eu = new ExcelUtil();
		eu.begin(xlsFileName);
		for (int i = 0; i < v.size(); i++) {
			eu.setIntegerValue(sheetID, ColumnName, i + 1, v.get(i));
		}
		eu.end();
	}
	
	/**
	 * 把集合写入到指定的xls文件列
	 * 
	 * @param v
	 * @param xlsFileName
	 * @param sheetID
	 * @param ColumnName
	 */
	public static void writeDoubleSetToExcel(Vector<Double> v, String xlsFileName,
			int sheetID, String ColumnName) {
		File f=new File(xlsFileName);
		File fParent=f.getParentFile();
		fParent.mkdirs();
		System.out.println("写入集合……");
		ExcelUtil eu = new ExcelUtil();
		eu.begin(xlsFileName);
		for (int i = 0; i < v.size(); i++) {
			eu.setDoubleValue(sheetID, ColumnName, i + 1, v.get(i));
		}
		eu.end();
	}

	/**
	 * 把集合写入到指定的xls文件列
	 * 
	 * @param v
	 * @param xlsFileName
	 * @param sheetID
	 * @param ColumnID
	 */
	public static void writeSetToExcel(Vector<String> v, String xlsFileName,
			int sheetID, int ColumnID) {
		File f=new File(xlsFileName);
		File fParent=f.getParentFile();
		fParent.mkdirs();
		System.out.println("写入集合……");
		ExcelUtil eu = new ExcelUtil();
		eu.begin(xlsFileName);
		for (int i = 0; i < v.size(); i++) {
			eu.setStringValue(sheetID, ColumnID, i, v.get(i));
		}
		eu.end();
	}

	/**
	 * 把集合写入到指定的xls文件列(多列)
	 * 
	 * @param v
	 * @param xlsFileName
	 * @param sheetID
	 * @param ColumnNames
	 */
	public static void writeSetToExcel(Vector<Vector<Serializable>> v,
			String xlsFileName, int sheetID, String ColumnNames[]) {
		File f=new File(xlsFileName);
		File fParent=f.getParentFile();
		fParent.mkdirs();
		System.out.println("写入集合……");
		ExcelUtil eu = new ExcelUtil();
		eu.begin(xlsFileName);
		for (int i = 0; i < v.size(); i++) {
			Vector<Serializable> record = v.get(i);
			int columnNumber = ColumnNames.length;
			for (int j = 0; j < columnNumber; j++) {
				if (record.get(j).getClass().toString()
						.equals("class java.lang.String"))
					eu.setStringValue(sheetID, ColumnNames[j], i + 1, record
							.get(j).toString());
				else if (record.get(j).getClass().toString()
						.equals("class java.lang.Integer"))
					eu.setIntegerValue(sheetID, ColumnNames[j], i + 1,
							(Integer) record.get(j));
				else if (record.get(j).getClass().toString()
						.equals("class java.lang.Double"))
					eu.setDoubleValue(sheetID, ColumnNames[j], i + 1,
							(Double) record.get(j));
			}
		}
		eu.end();
	}

	/**
	 * 把集合写入到指定的xls文件列(多列)
	 * 
	 * @param v
	 * @param xlsFileName
	 * @param sheetID
	 * @param ColumnIDs
	 */
	public static void writeSetToExcel(Vector<Vector<Serializable>> v,
			String xlsFileName, int sheetID, int ColumnIDs[]) {
		File f=new File(xlsFileName);
		File fParent=f.getParentFile();
		fParent.mkdirs();
		System.out.println("写入集合……");
		ExcelUtil eu = new ExcelUtil();
		eu.begin(xlsFileName);
		for (int i = 0; i < v.size(); i++) {
			Vector<Serializable> record = v.get(i);
			int columnNumber = ColumnIDs.length;
			for (int j = 0; j < columnNumber; j++) {
				if (record.get(j).getClass().toString()
						.equals("class java.lang.String"))
					eu.setStringValue(sheetID, ColumnIDs[j], i, record.get(j)
							.toString());
				else if (record.get(j).getClass().toString()
						.equals("class java.lang.Integer"))
					eu.setIntegerValue(sheetID, ColumnIDs[j], i,
							(Integer) record.get(j));
				else if (record.get(j).getClass().toString()
						.equals("class java.lang.Double"))
					eu.setDoubleValue(sheetID, ColumnIDs[j], i,
							(Double) record.get(j));
			}
		}
		eu.end();
	}
}
