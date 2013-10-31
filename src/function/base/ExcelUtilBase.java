package function.base;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import jxl.Cell;
import jxl.CellType;
import jxl.NumberCell;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public abstract class ExcelUtilBase {

	public static String basePath = "";
	// 读写单个excel文件变量
	private Workbook wb = null;
	private WritableWorkbook wwb = null;
	private HashMap<String, Integer> hmTitle = null;
	private Vector<HashMap<String, Integer>> vhmTitle = null;
	private long start =0,end=0;//记录开始结束时间

	public void open(String fileName) {
		System.out.println("正在打开文件" + fileName + "……");
		try {
			File f = new File(basePath + fileName);
			wb = Workbook.getWorkbook(f);
			wwb = Workbook.createWorkbook(f, wb);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createAndOpen(String fileName) {
		System.out.println("正在创建文件" + fileName + "……");
		try {
			File f = new File(basePath + fileName);
			wwb = Workbook.createWorkbook(f);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() {
		System.out.println("关闭文件，释放资源……");
		try {
			wwb.write();
			wwb.close();
			if (wb != null)
				wb.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void begin(String fileName){
		System.out.println("开始执行……");
		start = System.currentTimeMillis();
		File f = new File(basePath + fileName);
		if (f.exists())
			open(fileName);
		else
			createAndOpen(fileName);
	}
	
	public void end(){
		close();
		System.out.println("执行完毕！");
		end = System.currentTimeMillis();
		System.out.println("花费：" + (end - start) + "毫秒");
	}

	public String getStringValue(int Column, int Row) {
		WritableSheet wst = null;
		int sheetNumber = wwb.getNumberOfSheets();
		if (sheetNumber <= 0) {
			System.err.println("没有sheet可以取了……");
			return null;
		} else
			wst = wwb.getSheet(0);
		Cell c = wst.getCell(Column, Row);
		String s="";
		if(c.getType()==CellType.NUMBER){
			s=String.valueOf(((NumberCell)c).getValue());
		}
		else s = c.getContents();
		return s;
	}

	public void setStringValue(int Column, int Row, String value) {
		WritableSheet wst = null;
		int sheetNumber = wwb.getNumberOfSheets();
		if (sheetNumber <= 0)
			wst = wwb.createSheet("sheet1", 0);
		else
			wst = wwb.getSheet(0);
		try {
			wst.addCell(new Label(Column, Row, value));
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	public String getStringValue(String ColumnName, int Row) {
		WritableSheet wst = null;
		int columnID = 0;
		int sheetNumber = wwb.getNumberOfSheets();
		if (sheetNumber <= 0) {
			System.err.println("没有sheet可以取了……");
			return null;
		} else
			wst = wwb.getSheet(0);
		if (hmTitle == null)
			columnID = getColumnID(ColumnName, wst);
		else if (hmTitle.get(ColumnName) == null)
			columnID = -1;
		else
			columnID = hmTitle.get(ColumnName);
		if (columnID == -1) {
			System.out.println("没有标题为\"" + ColumnName + "\"的列啦");
			return null;
		}
		Cell c = wst.getCell(columnID, Row);
		String s="";
		if(c.getType()==CellType.NUMBER){
			s=String.valueOf(((NumberCell)c).getValue());
		}
		else s = c.getContents();
		return s;
	}

	public void setStringValue(String ColumnName, int Row, String value) {
		WritableSheet wst = null;
		int sheetNumber = wwb.getNumberOfSheets();
		int columnID = 0;
		if (sheetNumber <= 0)
			wst = wwb.createSheet("sheet1", 0);
		else
			wst = wwb.getSheet(0);
		if (hmTitle == null)
			columnID = getColumnID(ColumnName, wst);
		else if (hmTitle.get(ColumnName) == null)
			columnID = -1;
		else
			columnID = hmTitle.get(ColumnName);
		try {
			if (columnID == -1) {
				int ColumnNumber = wst.getColumns();
				wst.addCell(new Label(ColumnNumber, 0, ColumnName));
				columnID = getColumnID(ColumnName, wst);
			}
			wst.addCell(new Label(columnID, Row, value));
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	public int getIntegerValue(int Column, int Row) {
		String s = getStringValue(Column, Row);
		return Double.valueOf(s).intValue();
	}

	public void setIntegerValue(int Column, int Row, int value) {
		WritableSheet wst = null;
		int sheetNumber = wwb.getNumberOfSheets();
		if (sheetNumber <= 0)
			wst = wwb.createSheet("sheet1", 0);
		else
			wst = wwb.getSheet(0);
		try {
			wst.addCell(new Number(Column, Row, value));
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	public int getIntegerValue(String ColumnName, int Row) {
		String s = getStringValue(ColumnName, Row);
		return Double.valueOf(s).intValue();
	}

	public void setIntegerValue(String ColumnName, int Row, int value) {
		WritableSheet wst = null;
		int sheetNumber = wwb.getNumberOfSheets();
		int columnID = 0;
		if (sheetNumber <= 0)
			wst = wwb.createSheet("sheet1", 0);
		else
			wst = wwb.getSheet(0);
		if (hmTitle == null)
			columnID = getColumnID(ColumnName, wst);
		else if (hmTitle.get(ColumnName) == null)
			columnID = -1;
		else
			columnID = hmTitle.get(ColumnName);
		try {
			if (columnID == -1) {
				int ColumnNumber = wst.getColumns();
				wst.addCell(new Label(ColumnNumber, 0, ColumnName));
				columnID = getColumnID(ColumnName, wst);
			}
			wst.addCell(new Number(columnID, Row, value));
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	public double getDoubleValue(int Column, int Row) {
		String s = getStringValue(Column, Row);
		return Double.valueOf(s);
	}

	public void setDoubleValue(int Column, int Row, double value) {
		WritableSheet wst = null;
		int sheetNumber = wwb.getNumberOfSheets();
		if (sheetNumber <= 0)
			wst = wwb.createSheet("sheet1", 0);
		else
			wst = wwb.getSheet(0);
		try {
			wst.addCell(new Number(Column, Row, value));
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	public double getDoubleValue(String ColumnName, int Row) {
		String s = getStringValue(ColumnName, Row);
		return Double.valueOf(s);
	}

	public void setDoubleValue(String ColumnName, int Row, double value) {
		WritableSheet wst = null;
		int sheetNumber = wwb.getNumberOfSheets();
		int columnID = 0;
		if (sheetNumber <= 0)
			wst = wwb.createSheet("sheet1", 0);
		else
			wst = wwb.getSheet(0);
		if (hmTitle == null)
			columnID = getColumnID(ColumnName, wst);
		else if (hmTitle.get(ColumnName) == null)
			columnID = -1;
		else
			columnID = hmTitle.get(ColumnName);
		try {
			if (columnID == -1) {
				int ColumnNumber = wst.getColumns();
				wst.addCell(new Label(ColumnNumber, 0, ColumnName));
				columnID = getColumnID(ColumnName, wst);
			}
			wst.addCell(new Number(columnID, Row, value));
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	public String getStringValue(int sheetID, int Column, int Row) {
		WritableSheet wst = null;
		int sheetNumber = wwb.getNumberOfSheets();
		if (sheetNumber <= sheetID) {
			System.err.println("编号为" + sheetID + "的sheet超范围了……");
			return null;
		} else
			wst = wwb.getSheet(sheetID);
		Cell c = wst.getCell(Column, Row);
		String s="";
		if(c.getType()==CellType.NUMBER){
			s=String.valueOf(((NumberCell)c).getValue());
		}
		else s = c.getContents();
		return s;
	}

	public void setStringValue(int sheetID, int Column, int Row, String value) {
		WritableSheet wst = null;
		int sheetNumber = wwb.getNumberOfSheets();
		if (sheetNumber < sheetID) {
			System.err.println("一共才" + sheetNumber + "个sheet，创建编号为" + sheetID
					+ "的sheet失败……");
			return;
		} else if (sheetNumber == sheetID)
			wst = wwb.createSheet("sheet" + (sheetID + 1), sheetID);
		else
			wst = wwb.getSheet(sheetID);
		try {
			wst.addCell(new Label(Column, Row, value));
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	public String getStringValue(int sheetID, String ColumnName, int Row) {
		WritableSheet wst = null;
		int columnID = 0;
		int sheetNumber = wwb.getNumberOfSheets();
		if (sheetNumber <= sheetID) {
			System.err.println("编号为" + sheetID + "的sheet超范围了……");
			return null;
		} else
			wst = wwb.getSheet(sheetID);
		if (vhmTitle == null)
			columnID = getColumnID(ColumnName, sheetID);
		else if(vhmTitle.size()<=sheetID)columnID=-1;
		else if (vhmTitle.get(sheetID).get(ColumnName) == null)
			columnID = -1;
		else
			columnID = vhmTitle.get(sheetID).get(ColumnName);
		if (columnID == -1) {
			System.out.println("在sheet" + (sheetID + 1) + "中没有标题为\""
					+ ColumnName + "\"的列啦");
			return null;
		}
		Cell c = wst.getCell(columnID, Row);
		String s="";
		if(c.getType()==CellType.NUMBER){
			s=String.valueOf(((NumberCell)c).getValue());
		}
		else s = c.getContents();
		return s;
	}

	public void setStringValue(int sheetID, String ColumnName, int Row,
			String value) {
		WritableSheet wst = null;
		int columnID = 0;
		int sheetNumber = wwb.getNumberOfSheets();
		if (sheetNumber < sheetID) {
			System.err.println("一共才" + sheetNumber + "个sheet，创建编号为" + sheetID
					+ "的sheet失败……");
			return;
		} else if (sheetNumber == sheetID)
			wst = wwb.createSheet("sheet" + (sheetID + 1), sheetID);
		else
			wst = wwb.getSheet(sheetID);
		if (vhmTitle == null)
			columnID = getColumnID(ColumnName, sheetID);
		else if(vhmTitle.size()<=sheetID)columnID=-1;
		else if (vhmTitle.get(sheetID).get(ColumnName) == null)
			columnID = -1;
		else
			columnID = vhmTitle.get(sheetID).get(ColumnName);
		try {
			if (columnID == -1) {
				int ColumnNumber = wst.getColumns();
				wst.addCell(new Label(ColumnNumber, 0, ColumnName));
				columnID = getColumnID(ColumnName, sheetID);
			}
			wst.addCell(new Label(columnID, Row, value));
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	public int getIntegerValue(int sheetID, int Column, int Row) {
		String s = getStringValue(sheetID, Column, Row);
		return Double.valueOf(s).intValue();
	}

	public void setIntegerValue(int sheetID, int Column, int Row, int value) {
		WritableSheet wst = null;
		int sheetNumber = wwb.getNumberOfSheets();
		if (sheetNumber < sheetID) {
			System.err.println("一共才" + sheetNumber + "个sheet，创建编号为" + sheetID
					+ "的sheet失败……");
			return;
		} else if (sheetNumber == sheetID)
			wst = wwb.createSheet("sheet" + (sheetID + 1), sheetID);
		else
			wst = wwb.getSheet(sheetID);
		try {
			wst.addCell(new Number(Column, Row, value));
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	public int getIntegerValue(int sheetID, String ColumnName, int Row) {
		String s = getStringValue(sheetID, ColumnName, Row);
		return Double.valueOf(s).intValue();
	}

	public void setIntegerValue(int sheetID, String ColumnName, int Row,
			int value) {
		WritableSheet wst = null;
		int columnID = 0;
		int sheetNumber = wwb.getNumberOfSheets();
		if (sheetNumber < sheetID) {
			System.err.println("一共才" + sheetNumber + "个sheet，创建编号为" + sheetID
					+ "的sheet失败……");
			return;
		} else if (sheetNumber == sheetID)
			wst = wwb.createSheet("sheet" + (sheetID + 1), sheetID);
		else
			wst = wwb.getSheet(sheetID);
		if (vhmTitle == null)
			columnID = getColumnID(ColumnName, sheetID);
		else if(vhmTitle.size()<=sheetID)columnID=-1;
		else if (vhmTitle.get(sheetID).get(ColumnName) == null)
			columnID = -1;
		else
			columnID = vhmTitle.get(sheetID).get(ColumnName);
		try {
			if (columnID == -1) {
				int ColumnNumber = wst.getColumns();
				wst.addCell(new Label(ColumnNumber, 0, ColumnName));
				columnID = getColumnID(ColumnName, sheetID);
			}
			wst.addCell(new Number(columnID, Row, value));
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	public double getDoubleValue(int sheetID, int Column, int Row) {
		String s = getStringValue(sheetID, Column, Row);
		return Double.valueOf(s);
	}

	public void setDoubleValue(int sheetID, int Column, int Row, double value) {
		WritableSheet wst = null;
		int sheetNumber = wwb.getNumberOfSheets();
		if (sheetNumber < sheetID) {
			System.err.println("一共才" + sheetNumber + "个sheet，创建编号为" + sheetID
					+ "的sheet失败……");
			return;
		} else if (sheetNumber == sheetID)
			wst = wwb.createSheet("sheet" + (sheetID + 1), sheetID);
		else
			wst = wwb.getSheet(sheetID);
		try {
			wst.addCell(new Number(Column, Row, value));
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	public double getDoubleValue(int sheetID, String ColumnName, int Row) {
		String s = getStringValue(sheetID, ColumnName, Row);
		return Double.valueOf(s);
	}

	public void setDoubleValue(int sheetID, String ColumnName, int Row,
			double value) {
		WritableSheet wst = null;
		int columnID = 0;
		int sheetNumber = wwb.getNumberOfSheets();
		if (sheetNumber < sheetID) {
			System.err.println("一共才" + sheetNumber + "个sheet，创建编号为" + sheetID
					+ "的sheet失败……");
			return;
		} else if (sheetNumber == sheetID)
			wst = wwb.createSheet("sheet" + (sheetID + 1), sheetID);
		else
			wst = wwb.getSheet(sheetID);
		if (vhmTitle == null)
			columnID = getColumnID(ColumnName, sheetID);
		else if(vhmTitle.size()<=sheetID)columnID=-1;
		else if (vhmTitle.get(sheetID).get(ColumnName) == null)
			columnID = -1;
		else
			columnID = vhmTitle.get(sheetID).get(ColumnName);
		try {
			if (columnID == -1) {
				int ColumnNumber = wst.getColumns();
				wst.addCell(new Label(ColumnNumber, 0, ColumnName));
				columnID = getColumnID(ColumnName, sheetID);
			}
			wst.addCell(new Number(columnID, Row, value));
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	public int getColumnID(String ColumnName, WritableSheet wst) {
		hmTitle = new HashMap<String, Integer>();
		Cell[] c = wst.getRow(0);
		for (int i = 0; i < c.length; i++) {
			String titlei = c[i].getContents();
			hmTitle.put(titlei, i);
		}
		if (hmTitle.get(ColumnName) == null)
			return -1;
		else
			return hmTitle.get(ColumnName);
	}

	public int getColumnID(String ColumnName, int sheetID) {
		vhmTitle = new Vector<HashMap<String, Integer>>();
		int sheetNumber = wwb.getNumberOfSheets();
		for (int i = 0; i < sheetNumber; i++) {
			HashMap<String, Integer> hm = new HashMap<String, Integer>();
			WritableSheet wst = wwb.getSheet(i);
			Cell[] c = wst.getRow(0);
			for (int j = 0; j < c.length; j++) {
				String titlej = c[j].getContents();
				hm.put(titlej, j);
			}
			if (vhmTitle.size() <= i)
				vhmTitle.add(hm);
			else
				vhmTitle.set(i, hm);
		}
		HashMap<String, Integer> hmTemp = vhmTitle.get(sheetID);
		if (hmTemp.get(ColumnName) == null)
			return -1;
		else
			return hmTemp.get(ColumnName);
	}

	public int getRows() {
		WritableSheet wst = null;
		int sheetNumber = wwb.getNumberOfSheets();
		if (sheetNumber <= 0) {
			System.err.println("没有sheet可以取了……");
			return -1;
		} else
			wst = wwb.getSheet(0);
		return wst.getRows();
	}

	public int getRows(int sheetID) {
		WritableSheet wst = null;
		int sheetNumber = wwb.getNumberOfSheets();
		if (sheetNumber <= sheetID) {
			System.err.println("编号为" + sheetID + "的sheet超范围了……");
			return -1;
		} else
			wst = wwb.getSheet(sheetID);
		return wst.getRows();
	}

	public Vector<String> getColumnCotent(int Column) {
		Vector<String> v = new Vector<String>();
		WritableSheet wst = null;
		int sheetNumber = wwb.getNumberOfSheets();
		if (sheetNumber <= 0) {
			System.err.println("没有sheet可以取了……");
			return null;
		} else
			wst = wwb.getSheet(0);
		Cell c[] = wst.getColumn(Column);
		for (int i = 0; i < c.length; i++) {
			v.add(c[i].getContents());
		}
		return v;
	}

	public Vector<String> getColumnContent(String ColumnName) {
		Vector<String> v = new Vector<String>();
		WritableSheet wst = null;
		int columnID = 0;
		int sheetNumber = wwb.getNumberOfSheets();
		if (sheetNumber <= 0) {
			System.err.println("没有sheet可以取了……");
			return null;
		} else
			wst = wwb.getSheet(0);
		if (hmTitle == null)
			columnID = getColumnID(ColumnName, wst);
		else if (hmTitle.get(ColumnName) == null)
			columnID = -1;
		else
			columnID = hmTitle.get(ColumnName);
		if (columnID == -1) {
			System.out.println("没有标题为\"" + ColumnName + "\"的列啦");
			return null;
		} else {
			Cell c[] = wst.getColumn(columnID);
			for (int i = 1; i < c.length; i++) {
				v.add(c[i].getContents());
			}
			return v;
		}
	}

	public Vector<String> getRowContent(int Row) {
		Vector<String> v = new Vector<String>();
		WritableSheet wst = null;
		int sheetNumber = wwb.getNumberOfSheets();
		if (sheetNumber <= 0) {
			System.err.println("没有sheet可以取了……");
			return null;
		} else
			wst = wwb.getSheet(0);
		Cell c[] = wst.getRow(Row);
		for (int i = 0; i < c.length; i++) {
			v.add(c[i].getContents());
		}
		return v;
	}

	public Vector<String> getColumnContent(int sheetID, int Column) {
		Vector<String> v = new Vector<String>();
		WritableSheet wst = null;
		int sheetNumber = wwb.getNumberOfSheets();
		if (sheetNumber <= sheetID) {
			System.err.println("编号为" + sheetID + "的sheet超范围了……");
			return null;
		} else
			wst = wwb.getSheet(sheetID);
		Cell c[] = wst.getColumn(Column);
		for (int i = 0; i < c.length; i++) {
			v.add(c[i].getContents());
		}
		return v;
	}

	public Vector<String> getColumnContent(int sheetID, String ColumnName) {
		Vector<String> v = new Vector<String>();
		WritableSheet wst = null;
		int columnID = 0;
		int sheetNumber = wwb.getNumberOfSheets();
		if (sheetNumber <= sheetID) {
			System.err.println("编号为" + sheetID + "的sheet超范围了……");
			return null;
		} else
			wst = wwb.getSheet(sheetID);
		if (vhmTitle == null)
			columnID = getColumnID(ColumnName, sheetID);
		else if(vhmTitle.size()<=sheetID)columnID=-1;
		else if (vhmTitle.get(sheetID).get(ColumnName) == null)
			columnID = -1;
		else
			columnID = vhmTitle.get(sheetID).get(ColumnName);
		if (columnID == -1) {
			System.out.println("在sheet" + (sheetID + 1) + "中没有标题为\""
					+ ColumnName + "\"的列啦");
			return null;
		} else {
			Cell c[] = wst.getColumn(columnID);
			for (int i = 1; i < c.length; i++) {
				v.add(c[i].getContents());
			}
			return v;
		}
	}

	public Vector<String> getRowContent(int sheetID, int Row) {
		Vector<String> v = new Vector<String>();
		WritableSheet wst = null;
		int sheetNumber = wwb.getNumberOfSheets();
		if (sheetNumber <= sheetID) {
			System.err.println("编号为" + sheetID + "的sheet超范围了……");
			return null;
		} else
			wst = wwb.getSheet(sheetID);
		Cell c[] = wst.getRow(Row);
		for (int i = 0; i < c.length; i++) {
			v.add(c[i].getContents());
		}
		return v;
	}
}
