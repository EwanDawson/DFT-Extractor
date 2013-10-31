package function.linkAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
/*
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;*/

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;



public class URLtoExcel07 {

	/**
	 * @param args
	 */
	public void extractUrlToExcel(String srcPath,String desPath){
		/***************** 下面创建目标路径 ********************/
		String desDir="";
		if(desPath.contains("/"))
			desDir=desPath.substring(0, desPath.lastIndexOf("/"));
		else
			desDir=desPath.substring(0, desPath.lastIndexOf("\\"));
		File fDes=new File(desDir);
		fDes.mkdirs();
		/***************** 下面是把名字保存到数组里面 ********************/
		File f = new File(srcPath);
		File[] childs = f.listFiles();
		int size = childs.length;// 文件的个数
		int recordSum = 0;// sheet中的記錄數
		String[] nameArray = new String[size];// 用来保存文件名的数组
		for (int i = 0; i < size; i++) {// 下面循环用来保存文件名字
			String fileName = childs[i].getName();
			String realName = fileName.substring(0, fileName.length() - 5);
			nameArray[i] = realName;
		}
		System.out.println("文件名保存完毕……");
		System.out.println("共" + size + "个");
		/************** 下面将利用StringBuffer来保存每个读到的文件 *************/
		int pos = 0;// 记录href的位置
		int maohaoIndex = 0;// 冒号位置
		int leftIndex = 0;// 左尖括号位置
		int rightIndex = 0;// 右尖括号位置
		int nextLeftIndex = 0;// 左尖括号位置
		int nextRightIndex = 0;// 右尖括号位置
		int k = 0;
		String wikiTemp;
		String noteTemp;// 测试词条
		String addText;// 附加串
		String compareURL;
		/************ 下面的变量是需要存到Excel中的 ****************/
		String sourceURLName;// 连接源地址名字
		String toURLName;// 连接目标地址名字
		String toURLNameInc;// 连接增量字符串
		int posInHtml;// 在html中的位置
		String anchorText;// 锚文本
		Boolean linkSameAhchor;// 判断连接文本和锚文本是否一致
		/********************** 关键变量定义终止 ********************/
		/****************** Excel初始化 ******************/
		try {
			// 打开文件
			
			//WritableWorkbook book = Workbook.createWorkbook(new File(excelFilePath));
			// 生成名为“sheet1”的工作表，参数0表示这是第一页
			//WritableSheet sheet = book.createSheet("DM_layer23", 0);
			// 在Label对象的构造子中指名单元格位置是第一列第一行(0,0),单元格内容为string
			/*Label label1 = new Label(0, 0, "sourceURLName");
			Label label2 = new Label(1, 0, "toURLName");
			Label label3 = new Label(2, 0, "toURLNameInc");
			Label label4 = new Label(3, 0, "posInHtml");
			Label label5 = new Label(4, 0, "anchorText");
			Label label6 = new Label(5, 0, "linkSameAhchor");
			*/
			FileOutputStream fileout = new FileOutputStream(desPath);
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet("urlRelation");
			XSSFRow row = sheet.createRow(0);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue("sourceURLName");
			cell = row.createCell(1);
			cell.setCellValue("toURLName");
			cell = row.createCell(2);
			cell.setCellValue("toURLNameInc");
			cell = row.createCell(3);
			cell.setCellValue("posInHtml");
			cell = row.createCell(4);
			cell.setCellValue("anchorText");
			cell = row.createCell(5);
			cell.setCellValue("linkSameAhchor");
			// 将定义好的单元格添加到工作表中
			/*sheet.addCell(label1);
			sheet.addCell(label2);
			sheet.addCell(label3);
			sheet.addCell(label4);
			sheet.addCell(label5);
			sheet.addCell(label6);*/
			try {
				for (k = 0; k < size; k++) {
					FileReader fr = new FileReader(childs[k]);
					BufferedReader br = new BufferedReader(fr);
					StringBuffer sb = new StringBuffer();
					String fileName = childs[k].getName();
					sourceURLName = fileName
							.substring(0, fileName.length() - 5);
					String s;
					while ((s = br.readLine()) != null) {
						sb.append(s);
					}
					System.out.println("读取文件成功……");
					// 下面进行核心匹配
					int sbSize = sb.length();
					int wikiNum = 0;
					int linkNum = 0;
					System.out.println("sb大小" + sbSize);
					for (pos = 0; pos < sbSize; pos += 4) {
						pos = sb.indexOf("href", pos);
						if (pos == -1)
							break;// 如果搜索到末尾则跳出
						else {
							/************************ 核心代码区 **********************/
							if (pos + 11 >= sbSize)
								break;
							wikiTemp = sb.substring(pos + 7, pos + 11);// 超前查找wiki
							if (wikiTemp.equals("wiki")) {// 判断是否是所要的
								maohaoIndex = sb.indexOf("\"", pos + 11);// 返回冒号位置
								leftIndex = sb.indexOf("<", pos + 11);// 返回左尖括号位置
								rightIndex = sb.indexOf(">", pos + 11);// 返回右尖括号位置
								if (maohaoIndex == -1 || leftIndex == -1
										|| rightIndex == -1)
									break;
								if (leftIndex >= sbSize || rightIndex >= sbSize
										|| maohaoIndex >= sbSize)
									break;
								noteTemp = sb.substring(pos + 12, maohaoIndex);// 待提取文本
								wikiNum++;
								/******** 以下是匹配正则表达式后的代码 ********/
								if (noteTemp
										.matches("^((\\()?[a-z0-9A-Z]?(-)?(_)?(\\))?)*$")) {
									for (int j = 0; j < size; j++) {
										if (noteTemp
												.equals(nameArray[j])
												&& !noteTemp
														.equals(sourceURLName)) {
											// 命中
											recordSum++;// 記錄總數
											linkNum++;
											nextRightIndex = sb.indexOf(">",
													leftIndex);// 返回下一个右尖括号位置
											nextLeftIndex = sb.indexOf("<",
													nextRightIndex);// 返回下一个左尖括号位置
											if (nextRightIndex >= sbSize
													|| nextLeftIndex >= sbSize)
												break;
											if (nextRightIndex == -1
													|| nextLeftIndex == -1)
												break;
											if (nextLeftIndex - nextRightIndex <= 20)
												addText = sb.substring(
														nextRightIndex + 1,
														nextLeftIndex);// 增量字符串
											else
												addText = sb.substring(
														nextRightIndex + 1,
														nextRightIndex + 21);
											anchorText = sb.substring(
													rightIndex + 1, leftIndex);// 锚文本
											toURLNameInc = anchorText
													.concat(addText);// 连接增量字符串
											posInHtml = pos + 12;
											toURLName = noteTemp;// 连接目标地址名字
											compareURL = toURLName.replace('_',
													' ');
											linkSameAhchor = compareURL
													.equalsIgnoreCase(anchorText);
											System.out.println("第" + linkNum
													+ "个合格连接");
											System.out.println("源URL名字为："
													+ sourceURLName);
											System.out.println("目标URL名字为："
													+ toURLName);
											System.out.println("在html中的位置："
													+ posInHtml);
											System.out.println("锚文本为："
													+ anchorText);
											System.out.println("增量字符串为："
													+ toURLNameInc);
											System.out.println("是否相同："
													+ linkSameAhchor);
											/************** 写入到Excel中 **************/
											/*Label label10 = new Label(0,
													recordSum, sourceURLName);
											Label label11 = new Label(1,
													recordSum, toURLName);
											Label label12 = new Label(2,
													recordSum, toURLNameInc);
											Number number13 = new Number(3,
													recordSum, posInHtml);
											Label label14 = new Label(4,
													recordSum, anchorText);
											Label label15 = new Label(5,
													recordSum,
													linkSameAhchor.toString());
											// 将定义好的单元格添加到工作表中
											sheet.addCell(label10);
											sheet.addCell(label11);
											sheet.addCell(label12);
											sheet.addCell(number13);
											sheet.addCell(label14);
											sheet.addCell(label15);*/
											row = sheet.createRow(recordSum);
											cell = row.createCell(0);
											cell.setCellValue(sourceURLName);
											cell = row.createCell(1);
											cell.setCellValue(toURLName);
											cell = row.createCell(2);
											cell.setCellValue(toURLNameInc);
											cell = row.createCell(3);
											cell.setCellValue(posInHtml);
											cell = row.createCell(4);
											cell.setCellValue(anchorText);
											cell = row.createCell(5);
											cell.setCellValue(linkSameAhchor.toString());
											
											
											
											
											/**************** 写入完毕 *************/
											break;
										}
									}
								}
								/************** 正则收尾 **************/
							}
							/******************** 核心代码收尾 ************************/
						}
					}
					System.out.println("wiki总数：" + wikiNum);
					br.close();
					fr.close();
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			wb.write(fileout);
			fileout.close(); 
		} catch (Exception e) {
			System.out.println(e);
		}
		System.out.println("全部成功，共" + recordSum + "条记录");
		System.out.println("共完成" + k + "个文件分析。");
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
