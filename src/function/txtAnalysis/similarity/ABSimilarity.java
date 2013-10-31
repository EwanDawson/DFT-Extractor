package function.txtAnalysis.similarity;

import java.io.File;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ABSimilarity {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ABSimilarity as=new ABSimilarity();
		try{
			Workbook wb = Workbook.getWorkbook(new File(
			"f:/快盘/datamining/pos-1212.xls"));
	        Sheet sheet = wb.getSheet(0);
			WritableWorkbook wbook = Workbook.createWorkbook(new File(
			"f:/快盘/datamining/pos-1212.xls"), wb);
	        WritableSheet wsheet = wbook.getSheet(0);
		    int size=sheet.getRows();
	    	for(int i=1;i<size;i++){
	    		Cell c1=sheet.getCell(1, i);
	    		Cell c2=sheet.getCell(2, i);
	    		String c1String=c1.getContents().toLowerCase();
	    		String c2String=c2.getContents().toLowerCase();
	    		/*********下面计算相似度*********/
	    		double similarity=0.0;
	    		similarity=as.ABSimilarityCompute(c1String, c2String);
	    		Number number=new Number(5,i,similarity);
	    		wsheet.addCell(number);
	    	}
		    wbook.write();
		    wbook.close();
		    wb.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("计算成功！");
	}
	
	public double ABSimilarityCompute(String s1,String s2){
		int len1=s1.length();
		int len2=s2.length();
		int max=0;
		int p=0,q=0;
		double similarity=0.0;
		for(int k=0;k<len1;k++){
			p=k;
			for(int j=0;j<len2;j++){
				q=j;
				while(p<len1&&q<len2&&s1.charAt(p)==s2.charAt(q)){
					p++;
					q++;
				}
				max=q-j>max?q-j:max;
			}//end for
		}//end for
		similarity=((double)max)*2/(len1+len2);
		return similarity;
	}

}
