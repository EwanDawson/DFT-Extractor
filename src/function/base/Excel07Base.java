package function.base;

import java.io.*;

import java.util.*;


import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell; 

public abstract class Excel07Base {
	
	public static String basePath = "f:/Data/";
	private XSSFWorkbook wb = null;
	private HashMap<String, Integer> hmTitle = null;
	private Vector<HashMap<String, Integer>> vhmTitle = null;
	
	public abstract void process() throws Exception;

	public abstract void run(Excel07Base eb);
	
	public void open(String fileName) {
		System.out.println("正在打开文件" + fileName + "……");
		try {
			FileInputStream filein = new FileInputStream(basePath + fileName);
			wb = new XSSFWorkbook(filein);
			filein.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void createAndOpen(String fileName) {
		System.out.println("正在创建文件" + fileName + "……");
		wb = new XSSFWorkbook();
	}
	
	
	public void close(String fileName) {
		System.out.println("关闭文件，释放资源……");
		try {
			FileOutputStream fileout = new FileOutputStream(basePath + fileName);
			wb.write(fileout);
		    fileout.close();  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void go(String fileName) {
		System.out.println("开始执行……");
		long start = System.currentTimeMillis();
		File f = new File(basePath + fileName);
		if (f.exists())
			open(fileName);
		else
			createAndOpen(fileName);
		try {
			System.out.println("-------------------操作进行中---------------------");
			process();
			System.out
					.println("--------------------操作完毕----------------------");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		close(fileName);
		System.out.println("执行完毕！");
		long end = System.currentTimeMillis();
		System.out.println("花费：" + (end - start) + "毫秒");
	}
	
	public String getStringValue(int Column, int Row) {
		XSSFSheet sheet = null;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber <= 0) {
			System.err.println("没有sheet可以取了……");
			return null;
		} else
		sheet = wb.getSheetAt(0);
		XSSFRow row = sheet.getRow(Row);
		if(row == null) {
			System.err.println("没有该行……");
			return null;
		}
		XSSFCell cell = row.getCell(Column);
		if(cell == null) {
			System.err.println("没有该单元……");
			return null;
		}
		String s = null;
		if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
			s = String.valueOf((int)cell.getNumericCellValue());
		else
			s = cell.getStringCellValue();
		return s;
	}
	
	public void setStringValue(int Column, int Row, String value) {
		XSSFSheet sheet = null;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber <= 0)
			sheet = wb.createSheet("sheet1");
		else
			sheet = wb.getSheetAt(0);
		
		XSSFRow row = sheet.getRow(Row);
		if(row == null) 
			row = sheet.createRow(Row);
		XSSFCell cell = row.getCell(Column);
		if(cell == null) 
			cell = row.createCell(Column);
		cell.setCellValue(value);
	}
	
	public String getStringValue(String ColumnName, int Row) {
		XSSFSheet sheet = null;
		int columnID = 0;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber <= 0) {
			System.err.println("没有sheet可以取了……");
			return null;
		} else
			sheet = wb.getSheetAt(0);
		if (hmTitle == null)
			columnID = getColumnID(ColumnName, sheet);
		else if (hmTitle.get(ColumnName) == null) 
			columnID = -1;
		else
			columnID = hmTitle.get(ColumnName);
		if (columnID == -1) {
			System.out.println("没有标题为\"" + ColumnName + "\"的列啦");
			return null;
		}
		
		String s = getStringValue(columnID,Row);
		return s;
	}
	
	public void setStringValue(String ColumnName, int Row, String value) {
		XSSFSheet wst = null;
		int sheetNumber = wb.getNumberOfSheets();
		int columnID = 0;
		if (sheetNumber <= 0)
			wst = wb.createSheet("sheet1");
		else
			wst = wb.getSheetAt(0);
		if (hmTitle == null)
			columnID = getColumnID(ColumnName, wst);
		else if (hmTitle.get(ColumnName) == null)
			columnID = -1;
		else
			columnID = hmTitle.get(ColumnName);
		
		if (columnID == -1) {
			XSSFRow row = wst.getRow(0);
			if(row == null)
				row= wst.createRow(0);
			columnID = row.getLastCellNum();
			if(columnID == -1)
				columnID = 0;
		    setStringValue(columnID,0,ColumnName);
		}
		columnID = getColumnID(ColumnName, wst);
		setStringValue(columnID, Row, value);
		
	}
	
	
	
	
	
	
	
	
	public double getDoubleValue(int Column, int Row) {
		XSSFSheet sheet = null;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber <= 0) {
			System.err.println("没有sheet可以取了……");
			System.exit(0);
		} else
		sheet = wb.getSheetAt(0);
		XSSFRow row = sheet.getRow(Row);
		if(row == null) {
			System.err.println("没有该行……");
			System.exit(0);
		}
		XSSFCell cell = row.getCell(Column);
		if(cell == null) {
			System.err.println("没有该单元……");
			System.exit(0);
		}
		double s = cell.getNumericCellValue();
		return s;
	}
	
	
	public void setDoubleValue(int Column, int Row, double value) {
		XSSFSheet sheet = null;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber <= 0)
			sheet = wb.createSheet("sheet1");
		else
			sheet = wb.getSheetAt(0);
		
		XSSFRow row = sheet.getRow(Row);
		if(row == null) 
			row = sheet.createRow(Row);
		XSSFCell cell = row.getCell(Column);
		if(cell == null) 
			cell = row.createCell(Column);
		cell.setCellValue(value);
	}
	
	public double getDoubleValue(String ColumnName, int Row) {
		XSSFSheet sheet = null;
		int columnID = 0;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber <= 0) {
			System.err.println("没有sheet可以取了……");
			System.exit(0);
		} else
			sheet = wb.getSheetAt(0);
		if (hmTitle == null)
			columnID = getColumnID(ColumnName, sheet);
		else if (hmTitle.get(ColumnName) == null) 
			columnID = -1;
		else
			columnID = hmTitle.get(ColumnName);
		if (columnID == -1) {
			System.out.println("没有标题为\"" + ColumnName + "\"的列啦");
			System.exit(0);
		}
		
		double s = getDoubleValue(columnID,Row);
		return s;
	}
	
	public void setDoubleValue(String ColumnName, int Row, double value) {
		XSSFSheet wst = null;
		int sheetNumber = wb.getNumberOfSheets();
		int columnID = 0;
		if (sheetNumber <= 0)
			wst = wb.createSheet("sheet1");
		else
			wst = wb.getSheetAt(0);
		if (hmTitle == null)
			columnID = getColumnID(ColumnName, wst);
		else if (hmTitle.get(ColumnName) == null)
			columnID = -1;
		else
			columnID = hmTitle.get(ColumnName);
		
		if (columnID == -1) {
			XSSFRow row = wst.getRow(0);
			if(row == null)
				row= wst.createRow(0);
			columnID = row.getLastCellNum();
			if(columnID == -1)
				columnID = 0;
		    setStringValue(columnID,0,ColumnName);
		}
		columnID = getColumnID(ColumnName, wst);
		setDoubleValue(columnID, Row, value);
		
	}
	
	
	
	
	
	
	
	public int getIntegerValue(int Column, int Row) {
		String s = getStringValue(Column, Row);
		return Integer.valueOf(s);
	}
	
	public void setIntegerValue(int Column, int Row, int value) {
		XSSFSheet sheet = null;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber <= 0)
			sheet = wb.createSheet("sheet1");
		else
			sheet = wb.getSheetAt(0);
		
		XSSFRow row = sheet.getRow(Row);
		if(row == null) 
			row = sheet.createRow(Row);
		XSSFCell cell = row.getCell(Column);
		if(cell == null) 
			cell = row.createCell(Column);
		cell.setCellValue(value);
	}
	
	public int getIntegerValue(String ColumnName, int Row) {
		String s = getStringValue(ColumnName, Row);
		return Integer.valueOf(s);
	}
	
	public void setIntegerValue(String ColumnName, int Row, int value) {
		XSSFSheet wst = null;
		int sheetNumber = wb.getNumberOfSheets();
		int columnID = 0;
		if (sheetNumber <= 0)
			wst = wb.createSheet("sheet1");
		else
			wst = wb.getSheetAt(0);
		if (hmTitle == null)
			columnID = getColumnID(ColumnName, wst);
		else if (hmTitle.get(ColumnName) == null)
			columnID = -1;
		else
			columnID = hmTitle.get(ColumnName);
		
		if (columnID == -1) {
			XSSFRow row = wst.getRow(0);
			if(row == null)
				row= wst.createRow(0);
			columnID = row.getLastCellNum();
			if(columnID == -1)
				columnID = 0;
		    setStringValue(columnID,0,ColumnName);
		}
		columnID = getColumnID(ColumnName, wst);
		setIntegerValue(columnID, Row, value);
		
	}
	
	
	public String getStringValue(int sheetID, int Column, int Row) {
		XSSFSheet sheet = null;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber <= sheetID) {
			System.err.println("编号为" + sheetID + "的sheet超范围了……");
			return null;
		} else
			sheet = wb.getSheetAt(sheetID);
		XSSFRow row = sheet.getRow(Row);
		if(row == null) {
			System.err.println("没有该行……");
			return null;
		}
		XSSFCell cell = row.getCell(Column);
		if(cell == null) {
			System.err.println("没有该单元……");
			return null;
		}
		String s = null;
		if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
			s = String.valueOf((int)cell.getNumericCellValue());
		else
			s = cell.getStringCellValue();
		return s;
	}
	
	public void setStringValue(int sheetID,int Column, int Row, String value) {
		XSSFSheet sheet = null;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber < sheetID) {
			System.err.println("一共才" + sheetNumber + "个sheet，创建编号为" + sheetID
					+ "的sheet失败……");
			return;
		} else if (sheetNumber == sheetID)
			sheet = wb.createSheet("sheet" + (sheetID + 1));
		else
			sheet = wb.getSheetAt(sheetID);
		XSSFRow row = sheet.getRow(Row);
		if(row == null) 
			row = sheet.createRow(Row);
		XSSFCell cell = row.getCell(Column);
		if(cell == null) 
			cell = row.createCell(Column);
		cell.setCellValue(value);
	}
	
	public String getStringValue(int sheetID, String ColumnName, int Row) {
		XSSFSheet sheet = null;
		int columnID = 0;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber <= sheetID) {
			System.err.println("编号为" + sheetID + "的sheet超范围了……");
			return null;
		} else
			sheet = wb.getSheetAt(sheetID);
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
		XSSFRow row = sheet.getRow(Row);
		XSSFCell cell = row.getCell(columnID);
		String s = null;
		if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
			s = String.valueOf((int)cell.getNumericCellValue());
		else
			s = cell.getStringCellValue();
		return s;
	}
	
	public void setStringValue(int sheetID, String ColumnName, int Row, String value) {
		XSSFSheet sheet = null;
		int columnID = 0;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber < sheetID) {
			System.err.println("一共才" + sheetNumber + "个sheet，创建编号为" + sheetID
					+ "的sheet失败……");
			return;
		} else if (sheetNumber == sheetID)
			sheet = wb.createSheet("sheet" + (sheetID + 1));
		else
			sheet = wb.getSheetAt(sheetID);
		if (vhmTitle == null)
			columnID = getColumnID(ColumnName, sheetID);
		else if(vhmTitle.size()<=sheetID)columnID=-1;
		else if (vhmTitle.get(sheetID).get(ColumnName) == null)
			columnID = -1;
		else
			columnID = vhmTitle.get(sheetID).get(ColumnName);
		if (columnID == -1) {
			XSSFRow row = sheet.getRow(0);
			if(row == null)
				row= sheet.createRow(0);
			columnID = row.getLastCellNum();
			if(columnID == -1)
				columnID = 0;
		    setStringValue(sheetID,columnID,0,ColumnName);
		}
		columnID = getColumnID(ColumnName, sheetID);
		setStringValue(sheetID,columnID, Row, value);
		
	}
	
	
	public double getDoubleValue(int sheetID, int Column, int Row) {
		XSSFSheet sheet = null;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber <= sheetID) {
			System.err.println("编号为" + sheetID + "的sheet超范围了……");
			System.exit(0);
		} else
			sheet = wb.getSheetAt(sheetID);
		XSSFRow row = sheet.getRow(Row);
		if(row == null) {
			System.err.println("没有该行……");
			System.exit(0);
		}
		XSSFCell cell = row.getCell(Column);
		if(cell == null) {
			System.err.println("没有该单元……");
			System.exit(0);
		}
		double s = cell.getNumericCellValue();
		return s;
	}
	
	public void setDoubleValue(int sheetID, int Column, int Row, double value) {
		XSSFSheet sheet = null;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber < sheetID) {
			System.err.println("一共才" + sheetNumber + "个sheet，创建编号为" + sheetID
					+ "的sheet失败……");
			return;
		} else if (sheetNumber == sheetID)
			sheet = wb.createSheet("sheet" + (sheetID + 1));
		else
			sheet = wb.getSheetAt(sheetID);
		XSSFRow row = sheet.getRow(Row);
		if(row == null) 
			row = sheet.createRow(Row);
		XSSFCell cell = row.getCell(Column);
		if(cell == null) 
			cell = row.createCell(Column);
		cell.setCellValue(value);
	}
	
	public double getDoubleValue(int sheetID, String ColumnName, int Row) {
		XSSFSheet sheet = null;
		int columnID = 0;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber <= sheetID) {
			System.err.println("编号为" + sheetID + "的sheet超范围了……");
			System.exit(0);
		} else
			sheet = wb.getSheetAt(sheetID);
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
			System.exit(0);
		}
		XSSFRow row = sheet.getRow(Row);
		XSSFCell cell = row.getCell(columnID);
		double s = cell.getNumericCellValue();
		return s;
	}
	
	public void setDoubleValue(int sheetID, String ColumnName, int Row, double value) {
		XSSFSheet sheet = null;
		int columnID = 0;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber < sheetID) {
			System.err.println("一共才" + sheetNumber + "个sheet，创建编号为" + sheetID
					+ "的sheet失败……");
			return;
		} else if (sheetNumber == sheetID)
			sheet = wb.createSheet("sheet" + (sheetID + 1));
		else
			sheet = wb.getSheetAt(sheetID);
		if (vhmTitle == null)
			columnID = getColumnID(ColumnName, sheetID);
		else if(vhmTitle.size()<=sheetID)columnID=-1;
		else if (vhmTitle.get(sheetID).get(ColumnName) == null)
			columnID = -1;
		else
			columnID = vhmTitle.get(sheetID).get(ColumnName);
		if (columnID == -1) {
			XSSFRow row = sheet.getRow(0);
			if(row == null)
				row= sheet.createRow(0);
			columnID = row.getLastCellNum();
			if(columnID == -1)
				columnID = 0;
		    setStringValue(sheetID,columnID,0,ColumnName);
		}
		columnID = getColumnID(ColumnName, sheetID);
		setDoubleValue(sheetID,columnID, Row, value);
	}
	
	public int getIntegerValue(int sheetID, int Column, int Row) {
		String s = getStringValue(sheetID, Column, Row);
		return Integer.valueOf(s);
	}
	
	public void setIntegerValue(int sheetID, int Column, int Row, int value) {
		setDoubleValue(sheetID,Column,Row,value);
	}
	
	public int getIntegerValue(int sheetID, String ColumnName, int Row) {
		String s = getStringValue(sheetID, ColumnName, Row);
		return Integer.valueOf(s);
	}
	
	public void setIntegerValue(int sheetID, String ColumnName, int Row, int value) {
		setDoubleValue(sheetID,ColumnName,Row,value);
	}
	
	public int getColumnID(String ColumnName, XSSFSheet sheet) {
		hmTitle = new HashMap<String, Integer>();
		XSSFRow row = sheet.getRow(0);
		if(row == null)
			return -1;
		Iterator<Cell> it = row.cellIterator(); 
		Cell cell = null;
		for (int i = 0; it.hasNext(); i++) {
			cell = it.next();
			String titlei = cell.getStringCellValue();
			hmTitle.put(titlei, i);
		}
		if (hmTitle.get(ColumnName) == null)
			return -1;
		else
			return hmTitle.get(ColumnName);
	}
	
	public int getColumnID(String ColumnName, int sheetID) {
		vhmTitle = new Vector<HashMap<String, Integer>>();
		int sheetNumber = wb.getNumberOfSheets();
		for (int i = 0; i < sheetNumber; i++) {
			HashMap<String, Integer> hm = new HashMap<String, Integer>();
			XSSFSheet sheet = wb.getSheetAt(i);
			XSSFRow row = sheet.getRow(0);
			if(row != null) {
				Iterator<Cell> it = row.cellIterator(); 
				Cell cell = null;
				for (int j = 0; it.hasNext(); j++) {
					cell = it.next();
					String titlei = cell.getStringCellValue();
					hm.put(titlei, j);
				}
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
		
		
		/*XSSFSheet sheet = wb.getSheetAt(sheetID);
		
			return getColumnID(ColumnName,sheet);*/
	}

	
	public int getRows() {    
		XSSFSheet sheet = null;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber <= 0) {
			System.err.println("没有sheet可以取了……");
			return -1;
		} else
			sheet = wb.getSheetAt(0);
		return (sheet.getLastRowNum() + 1 ) ;
	}
	
	public int getRows(int sheetID) {
		XSSFSheet sheet = null;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber <= sheetID) {
			System.err.println("编号为" + sheetID + "的sheet超范围了……");
			return -1;
		} else
			sheet = wb.getSheetAt(sheetID);
		return (sheet.getLastRowNum() + 1 ) ;
	}
	
	
	public Vector<String> getColumnContent(int Column) {
		Vector<String> v = new Vector<String>();
		XSSFSheet sheet = null;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber <= 0) {
			System.err.println("没有sheet可以取了……");
			return null;
		} else
			sheet = wb.getSheetAt(0);
		for(int i = 0; i <= sheet.getLastRowNum(); i++) {
			v.add(getStringValue(Column,i));
		}
		return v;
	}
	
	public Vector<String> getColumnContent(String ColumnName) {
		Vector<String> v = new Vector<String>();
		XSSFSheet sheet = null;
		int columnID = 0;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber <= 0) {
			System.err.println("没有sheet可以取了……");
			return null;
		} else
			sheet = wb.getSheetAt(0);
		if (hmTitle == null)
			columnID = getColumnID(ColumnName, sheet);
		else if (hmTitle.get(ColumnName) == null)
			columnID = -1;
		else
			columnID = hmTitle.get(ColumnName);
		if (columnID == -1) {
			System.out.println("没有标题为\"" + ColumnName + "\"的列啦");
			return null;
		} else {
			for(int i = 0; i <= sheet.getLastRowNum(); i++) {
				v.add(getStringValue(columnID,i));
			}
			return v;
		}
	}
	
	public Vector<String> getRowContent(int Row) {
		Vector<String> v = new Vector<String>();
		XSSFSheet sheet = null;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber <= 0) {
			System.err.println("没有sheet可以取了……");
			return null;
		} else
			sheet = wb.getSheetAt(0);
		XSSFRow row = sheet.getRow(Row);
		if(row != null && row.getLastCellNum() != -1) {
			for(int i = 0;i < row.getLastCellNum();i++)
				v.add(row.getCell(i).getStringCellValue());
		}
		return v;
	}
	
	
	public Vector<String> getColumnContent(int sheetID, int Column) {
		Vector<String> v = new Vector<String>();
		XSSFSheet sheet = null;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber <= sheetID) {
			System.err.println("编号为" + sheetID + "的sheet超范围了……");
			return null;
		} else
			sheet = wb.getSheetAt(sheetID);
		for(int i = 0; i <= sheet.getLastRowNum(); i++) {
			v.add(getStringValue(sheetID,Column,i));
		}
		return v;
	}
	
	public Vector<String> getColumnContent(int sheetID, String ColumnName) {
		Vector<String> v = new Vector<String>();
		XSSFSheet sheet = null;
		int columnID = 0;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber <= sheetID) {
			System.err.println("编号为" + sheetID + "的sheet超范围了……");
			return null;
		} else
			sheet = wb.getSheetAt(sheetID);
		if (hmTitle == null)
			columnID = getColumnID(ColumnName, sheet);
		else if (hmTitle.get(ColumnName) == null)
			columnID = -1;
		else
			columnID = hmTitle.get(ColumnName);
		if (columnID == -1) {
			System.out.println("没有标题为\"" + ColumnName + "\"的列啦");
			return null;
		} else {
			for(int i = 0; i <= sheet.getLastRowNum(); i++) {
				v.add(getStringValue(sheetID,columnID,i));
			}
			return v;
		}
	}
	
	
	public Vector<String> getRowContent(int sheetID, int Row) {
		Vector<String> v = new Vector<String>();
		XSSFSheet sheet = null;
		int sheetNumber = wb.getNumberOfSheets();
		if (sheetNumber <= sheetID) {
			System.err.println("编号为" + sheetID + "的sheet超范围了……");
			return null;
		} else
			sheet = wb.getSheetAt(sheetID);
		XSSFRow row = sheet.getRow(Row);
		if(row != null && row.getLastCellNum() != -1) {
			for(int i = 0;i < row.getLastCellNum();i++)
				v.add(row.getCell(i).getStringCellValue());
		}
		return v;
	}
	
	
}
