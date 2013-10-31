package function.DTExtraction;

import java.util.HashMap;
import java.util.Vector;

import function.base.ExcelBase;
import function.util.MapUtil;
import function.util.SetUtil;

/**
 * 根据目录特征文件选择根目录
 * @author MJ
 * @description
 */
public class SelectCategory extends ExcelBase {

	String fileName="F:\\extractTest\\Data_mining\\process\\layer12-select-cat.xls";
	int sheetId=2;//特征统计sheet
	int topNumber=6;
	String desCatPath="F:\\extractTest\\Data_mining\\process\\select-category.txt";//选择的目录存放文件
	
	public SelectCategory(){
		
	}
	
	public SelectCategory(String fileName, int sheetId, int topNumber,
			String desCatPath) {
		super();
		this.fileName = fileName;
		this.sheetId = sheetId;
		this.topNumber = topNumber;
		this.desCatPath = desCatPath;
	}

	@Override
	public void process() throws Exception {
		// TODO Auto-generated method stub
		HashMap<String,Integer> hm=new HashMap<String,Integer>();
		for(int i=1;i<getRows(sheetId);i++){
			String category=getStringValue(sheetId,"category",i);
			int frequency=getIntegerValue(sheetId,"frequency",i);
			hm.put(category, frequency);
		}
		Vector<String[]> vSortCategory=MapUtil.sortMapValueDes(hm);
		Vector<String> vSelectCategory=new Vector<String>();
		for(int i=0;i<topNumber;i++){
			String category=vSortCategory.get(i)[0];
			System.out.println("selectCategory:"+category);
			vSelectCategory.add(category);
		}
		SetUtil.writeSetToFile(vSelectCategory, desCatPath);
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
		SelectCategory sc=new SelectCategory();
		sc.run(sc);
	}

}
