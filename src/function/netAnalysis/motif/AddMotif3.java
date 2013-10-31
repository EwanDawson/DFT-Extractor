package function.netAnalysis.motif;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

import function.base.ExcelBase;

/**
 * 
 * @author MJ
 * @description 计算motif3数值
 */
public class AddMotif3 extends ExcelBase{
	
	String fileName="";
	String m3EdgeFile="";
	
	public AddMotif3(String m3XlsFile,String m3EdgeFile){
		this.fileName=m3XlsFile;
		this.m3EdgeFile=m3EdgeFile;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AddMotif3 am=new AddMotif3("f:/test.xls","F:\\Data\\上下位数据集\\DM\\DM_motif\\DM2_m3\\DM2_Edge.txt.csv.dump");
		am.run(am);
	}
	
	@Override
	public void process() throws Exception {
		// TODO Auto-generated method stub
		HashMap<String, Integer> hmHead = new HashMap<String, Integer>();
		HashMap<String, Integer> hmSrcTo = new HashMap<String, Integer>();
		int size =getRows();
		int matrix[][] = new int[size - 1][15];
		/****** 以下是添加motifAtt3Name到hm中 ******/
		String[] m3Head = {"m000000110", "m000001100", "m000001110",
				"m000100100", "m000100110", "m000101110", "m001001110",
				"m001100110", "m010001100", "m010100100", "m010100110",
				"m010101110", "m011101110" };
		for (int i = 0; i < m3Head.length; i++) {
			System.out.println(m3Head[i]+"------"+i);
			hmHead.put(m3Head[i], i+2);
		}//
		/******以下是初始化每个三角形个数为0*******/
		for (int i = 0; i < size - 1; i++) {
			for (int j = 2; j < 15; j++) {
				matrix[i][j] = 0;
			}// 初始化
		}
		for (int i = 1; i < size; i++) {
			int srcID=getIntegerValue("srcID",i);
			int toID=getIntegerValue("toID",i);
			matrix[i - 1][0] = srcID;
			matrix[i - 1][1] = toID;
			hmSrcTo.put(srcID+"-"+toID, i-1);
		}// 拷贝source和to到矩阵里面
		HashMap<Integer, Integer> hmTag = new HashMap<Integer, Integer>();
		int tag[][] = { {0,1,2},{2,0,1},{1,2,0},{0,2,1},{2,1,0},{1,0,2} };// 保存顺序表
		int sum = 0,m=1;// 找不到的个数
		FileReader fR = new FileReader(m3EdgeFile);
		BufferedReader bR = new BufferedReader(fR);
		String contentStr = bR.readLine();
		contentStr = bR.readLine();//跳过前两行
		contentStr = bR.readLine();
		while (contentStr != null) {
			m++;
			String s[] = contentStr.split(",");
			String AttName = "m".concat(s[0]);
			int A = Integer.parseInt(s[1]);
			int B = Integer.parseInt(s[2]);
			int C = Integer.parseInt(s[3]);
			contentStr=bR.readLine();
			char c;
			int x, y;
			int src, to;
			int i;
			boolean continueTag=false;
			for (i = 0; i < 6; i++) {// 遍历每种顺序
				continueTag=false;
				int pos[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0};
				hmTag.clear();
				hmTag.put(tag[i][0], A);
				hmTag.put(tag[i][1], B);
				hmTag.put(tag[i][2], C);
				for (int j = 1; j < 10; j++) {// 针对每个字符
					c = AttName.charAt(j);
					if (c == '1') {
						x = (j - 1) / 3;
						y = (j - 1) % 3;
						src = hmTag.get(x);
						to = hmTag.get(y);
						String key=src+"-"+to;
						if(hmSrcTo.containsKey(key))
							pos[j - 1] =hmSrcTo.get(key);
						else{
							continueTag=true;
							break;// 一个没找到，推出整个顺序
						}
					}// end if
				}// end for针对每个字符
				if (continueTag)
					continue;
				else {
					for (int j = 1; j < 10; j++) {
						c = AttName.charAt(j);
						if (c == '1'){
							matrix[pos[j - 1]][hmHead.get(AttName)]++;
						}
							
					}
					break;
				}
			}// end for遍历每种顺序
			if (i == 6) {
				System.out.println("找不到");
				sum++;
			}
		}//end while
		/*********** 下面是写入个数 ************/
		for (int i = 1; i < size; i++) {
			for (int j = 0; j < 13; j++) {
				setIntegerValue(m3Head[j],i,matrix[i - 1][j +2]);
			}
		}
		System.out.print("共"+m+"个，找不到个数："+sum);
		fR.close();
		bR.close();
		/************** 关闭操作 ****************/
		System.out.println("处理完毕！");
	}
	
	@Override
	public void run(ExcelBase eb) {
		// TODO Auto-generated method stub
		eb.go(fileName);
	}
}
