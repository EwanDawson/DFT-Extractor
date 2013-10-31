package function.util;

import java.io.File;

public class StringUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String dir="F:\\实验\\术语抽取baseline\\2数据预处理\\text_select2";
		String sub="programming language";
		find(dir,sub);
	}
	
	/**
	 * 在s中查找pos之前的str位置
	 * 
	 * @param s
	 * @param str
	 * @param pos
	 * @return
	 */
	public static int upIndexOf(String s, String str, int pos) {
		String subS = s.substring(0, pos);
		return subS.lastIndexOf(str, pos);
	}
	
	/**
	 * 将s在size尺寸中居中
	 * @param s
	 * @param size
	 * @return
	 */
	public static String getCenterString(String s, int size) {
		String centerS=s;
		int length=s.length();
		if(length>=size)
			return s;
		else{
			int spaceNum=(size-length)/2;
			for(int i=0;i<spaceNum;i++){
				centerS=" "+centerS+" ";
			}
		}
		return centerS;
	}
	
	/**
	 * 找出含有subStr的文件
	 * @param dir
	 * @param subStr
	 */
	 public static void find(String dir,String subStr){
    	 File f=new File(dir);
    	 File childs[]=f.listFiles();
    	 for(int i=0;i<childs.length;i++){
    		 if(childs[i].isDirectory())
    			 find(childs[i].getAbsolutePath(),subStr);
    		 else{
    			 String filePath=childs[i].getAbsolutePath();
    		     String s=FileUtil.readFile(filePath);
    		     if(s.toLowerCase().contains(subStr.toLowerCase()))
    		    	 System.out.println(filePath);
    		 }
    	 }
     }
}
