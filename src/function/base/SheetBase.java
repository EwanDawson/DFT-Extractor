/**
 * 
 */
package function.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * 1、完成Excel的基本读写，建立的Sheet可读写。
 * 待改进：目前无法实现一个sheet用于读，另一个sheet用于写。
 * 可以先读入可写的sheet，用该sheet创建可写对象。然后再打开只读的sheet。
 * 2、使用方法：
 * 1）继承该类。2）编写processing()函数。3)创建对象，初始化文件，执行go函数。
 *      SheetBase ss;
 * 		ss = new DestPageInfo();
 *      String RdWrFile="d:/dir/xxx.xls";
 *		ss.init(RdWrFile);
 *		ss.go();
 *
 */
public abstract class SheetBase {
	protected Logger log = Logger.getLogger( this.getClass().getName()); 
	
	Workbook SrcBook;
	Sheet SrcSheet;
	WritableWorkbook SinkBook;
	WritableSheet SinkSheet;
	public int startLine = 1;
	
	private String SrcFile=null;
	private String SinkFile=null;
	
	//存放Title的结构
	Map<String,Integer> srcTitle=null;
	Map<String,Integer> sinkTitle=null;
	
	//兼容马健代码
	Sheet sheet;
	WritableSheet wsheet;
	
	public void init(){
		assert(this.SrcFile != null && this.SinkFile!=null);
		init(this.SrcFile, this.SinkFile);
	}
	public void init(String file){
		init(file,file);
	}

	public void init(String SrcFile, String SinkFile) {

		this.SrcFile = SrcFile;
		this.SinkFile = SinkFile;
		try {
			SrcBook = Workbook.getWorkbook(new File(SrcFile));
			SrcSheet = SrcBook.getSheet(0);

			SinkBook = Workbook.createWorkbook(new File(SinkFile), SrcBook);			
			SinkSheet = SinkBook.getSheet(0);

		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//兼容马健代码
		sheet =SrcSheet;
		wsheet = SinkSheet;
		
		initTitle();
	}
	
	public void initTitle(){
		srcTitle = new HashMap<String, Integer>();
		sinkTitle = new HashMap<String, Integer>();
		for(int i=0;i<SrcSheet.getColumns();i++){
			String title = getStringValue(i,0);
			srcTitle.put(title, i);
		}
		for(int i=0;i<SinkSheet.getColumns();i++){
			String title = getStringValue(i,0);
			sinkTitle.put(title, i);
		}
	}
	

	
	public void close() {
		try {
			SinkBook.write();
			SinkBook.close();
			SrcBook.close();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void go(){
		printConfigInfo();
		printSpecificInfo();
		try {

				processing();
	
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		close();
		System.out.println("go() end");
		
	}
	
	protected void printConfigInfo(){
		System.out.println("startLine=" + startLine);
		System.out.println("SrcFile=" + SrcFile);
		System.out.println("SinkFile=" + SinkFile);
		System.out.println(srcTitle);
		System.out.println(sinkTitle);		
	}

	abstract public void processing() throws RowsExceededException, WriteException, FileNotFoundException, IOException;
	
	public void printSpecificInfo(){
		
	}
	
	public void printTitle(){
		//int col=sheet.getColumns();
		
	}
	
	//设置及获取cell的内容，分别为字符串和整数索引
	public void setNumberValue(int col,int row, double value){
		Number number = new Number(col, row, value);
		try {
			SinkSheet.addCell(number);
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setNumberValue(String colStr,int row, double value){
		Integer col = sinkTitle.get(colStr);
		assert(col != null);
		setNumberValue(col, row,value);
	}
	
	public void setNumberValue(int col,int row, int value){
		Number number = new Number(col, row, value);
		try {
			SinkSheet.addCell(number);
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void setNumberValue(String colStr,int row, int value){
		Integer col = sinkTitle.get(colStr);
		assert(col != null);
		setNumberValue(col, row,value);
	}
	
	public void setStringValue(int col,int row, String str){
		Label label = new Label(col, row, str);
		try {
			SinkSheet.addCell(label);
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setStringValue(String colStr,int row, String str){
		Integer col = sinkTitle.get(colStr);
		assert(col != null);
		setStringValue(col, row,str);
	}
	public void setStringValue(String colStr,int row, boolean bool){
		Boolean Bool=new Boolean(bool);
		setStringValue(colStr, row,Bool.toString());
	}
	
	public double getDoubleValue(int col,int row){
		Cell cell = SrcSheet.getCell(col, row);
		// System.out.println(c11.getContents());
		double value = Double.parseDouble(cell.getContents());
		return value;
	}
	
	public double getDoubleValue(String colStr,int row){
		Integer col = srcTitle.get(colStr);
		assert(col != null);
		return getDoubleValue(col, row);
	}
	
	public Integer getIntegerValue(int col,int row){
		Cell cell = SrcSheet.getCell(col, row);
		// System.out.println(c11.getContents());
		Integer value = Integer.parseInt(cell.getContents());
		return value;
	}
	
	public Integer getIntegerValue(String colStr,int row){
		Integer col = srcTitle.get(colStr);
		assert(col != null);
		return getIntegerValue(col, row);
	}
	
	public String getStringValue(int col,int row){
		Cell cell = SrcSheet.getCell(col, row);
		// System.out.println(c11.getContents());
		String str = cell.getContents();
		return str;
	}
	
	public String getStringValue(String colStr,int row){
		Integer col = srcTitle.get(colStr);
		assert(col != null);
		return getStringValue(col, row);
	}
	
	
	//小功能
	public String modifyCategory(int col,int row){
		Cell cell = SrcSheet.getCell(col, row);
		// System.out.println(c11.getContents());
		String str = cell.getContents();
		if(str.equalsIgnoreCase("Other1")||str.equalsIgnoreCase("Other2")||str.equalsIgnoreCase("Other3"))
			str="Other";
		if(str.equalsIgnoreCase("A is a B")||str.equalsIgnoreCase("B is a A"))
			str="Hypo";
		setStringValue(col, row,str);
		return str;
	}
	public String modifyCategory(String colStr,int row){
		Integer col = srcTitle.get(colStr);
		return modifyCategory(col,row);
	}
	
	
	/**
	 * 
	 */
	public SheetBase() {
		// TODO Auto-generated constructor stub
	}

	public String getSrcFile() {
		return SrcFile;
	}

	public void setSrcFile(String srcFile) {
		SrcFile = srcFile;
	}

	public String getSinkFile() {
		return SinkFile;
	}

	public void setSinkFile(String sinkFile) {
		SinkFile = sinkFile;
	}
	
	public int getRows(){
		return SrcSheet.getRows();
	}
	public int getDesRows(){
		return SinkSheet.getRows();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
