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
		/***************** ���洴��Ŀ��·�� ********************/
		String desDir="";
		if(desPath.contains("/"))
			desDir=desPath.substring(0, desPath.lastIndexOf("/"));
		else
			desDir=desPath.substring(0, desPath.lastIndexOf("\\"));
		File fDes=new File(desDir);
		fDes.mkdirs();
		/***************** �����ǰ����ֱ��浽�������� ********************/
		File f = new File(srcPath);
		File[] childs = f.listFiles();
		int size = childs.length;// �ļ��ĸ���
		int recordSum = 0;// sheet�е�ӛ䛔�
		String[] nameArray = new String[size];// ���������ļ���������
		for (int i = 0; i < size; i++) {// ����ѭ�����������ļ�����
			String fileName = childs[i].getName();
			String realName = fileName.substring(0, fileName.length() - 5);
			nameArray[i] = realName;
		}
		System.out.println("�ļ���������ϡ���");
		System.out.println("��" + size + "��");
		/************** ���潫����StringBuffer������ÿ���������ļ� *************/
		int pos = 0;// ��¼href��λ��
		int maohaoIndex = 0;// ð��λ��
		int leftIndex = 0;// �������λ��
		int rightIndex = 0;// �Ҽ�����λ��
		int nextLeftIndex = 0;// �������λ��
		int nextRightIndex = 0;// �Ҽ�����λ��
		int k = 0;
		String wikiTemp;
		String noteTemp;// ���Դ���
		String addText;// ���Ӵ�
		String compareURL;
		/************ ����ı�������Ҫ�浽Excel�е� ****************/
		String sourceURLName;// ����Դ��ַ����
		String toURLName;// ����Ŀ���ַ����
		String toURLNameInc;// ���������ַ���
		int posInHtml;// ��html�е�λ��
		String anchorText;// ê�ı�
		Boolean linkSameAhchor;// �ж������ı���ê�ı��Ƿ�һ��
		/********************** �ؼ�����������ֹ ********************/
		/****************** Excel��ʼ�� ******************/
		try {
			// ���ļ�
			
			//WritableWorkbook book = Workbook.createWorkbook(new File(excelFilePath));
			// ������Ϊ��sheet1���Ĺ�����������0��ʾ���ǵ�һҳ
			//WritableSheet sheet = book.createSheet("DM_layer23", 0);
			// ��Label����Ĺ�������ָ����Ԫ��λ���ǵ�һ�е�һ��(0,0),��Ԫ������Ϊstring
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
			// ������õĵ�Ԫ�����ӵ���������
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
					System.out.println("��ȡ�ļ��ɹ�����");
					// ������к���ƥ��
					int sbSize = sb.length();
					int wikiNum = 0;
					int linkNum = 0;
					System.out.println("sb��С" + sbSize);
					for (pos = 0; pos < sbSize; pos += 4) {
						pos = sb.indexOf("href", pos);
						if (pos == -1)
							break;// ���������ĩβ������
						else {
							/************************ ���Ĵ����� **********************/
							if (pos + 11 >= sbSize)
								break;
							wikiTemp = sb.substring(pos + 7, pos + 11);// ��ǰ����wiki
							if (wikiTemp.equals("wiki")) {// �ж��Ƿ�����Ҫ��
								maohaoIndex = sb.indexOf("\"", pos + 11);// ����ð��λ��
								leftIndex = sb.indexOf("<", pos + 11);// �����������λ��
								rightIndex = sb.indexOf(">", pos + 11);// �����Ҽ�����λ��
								if (maohaoIndex == -1 || leftIndex == -1
										|| rightIndex == -1)
									break;
								if (leftIndex >= sbSize || rightIndex >= sbSize
										|| maohaoIndex >= sbSize)
									break;
								noteTemp = sb.substring(pos + 12, maohaoIndex);// ����ȡ�ı�
								wikiNum++;
								/******** ������ƥ���������ʽ��Ĵ��� ********/
								if (noteTemp
										.matches("^((\\()?[a-z0-9A-Z]?(-)?(_)?(\\))?)*$")) {
									for (int j = 0; j < size; j++) {
										if (noteTemp
												.equals(nameArray[j])
												&& !noteTemp
														.equals(sourceURLName)) {
											// ����
											recordSum++;// ӛ䛿���
											linkNum++;
											nextRightIndex = sb.indexOf(">",
													leftIndex);// ������һ���Ҽ�����λ��
											nextLeftIndex = sb.indexOf("<",
													nextRightIndex);// ������һ���������λ��
											if (nextRightIndex >= sbSize
													|| nextLeftIndex >= sbSize)
												break;
											if (nextRightIndex == -1
													|| nextLeftIndex == -1)
												break;
											if (nextLeftIndex - nextRightIndex <= 20)
												addText = sb.substring(
														nextRightIndex + 1,
														nextLeftIndex);// �����ַ���
											else
												addText = sb.substring(
														nextRightIndex + 1,
														nextRightIndex + 21);
											anchorText = sb.substring(
													rightIndex + 1, leftIndex);// ê�ı�
											toURLNameInc = anchorText
													.concat(addText);// ���������ַ���
											posInHtml = pos + 12;
											toURLName = noteTemp;// ����Ŀ���ַ����
											compareURL = toURLName.replace('_',
													' ');
											linkSameAhchor = compareURL
													.equalsIgnoreCase(anchorText);
											System.out.println("��" + linkNum
													+ "���ϸ�����");
											System.out.println("ԴURL����Ϊ��"
													+ sourceURLName);
											System.out.println("Ŀ��URL����Ϊ��"
													+ toURLName);
											System.out.println("��html�е�λ�ã�"
													+ posInHtml);
											System.out.println("ê�ı�Ϊ��"
													+ anchorText);
											System.out.println("�����ַ���Ϊ��"
													+ toURLNameInc);
											System.out.println("�Ƿ���ͬ��"
													+ linkSameAhchor);
											/************** д�뵽Excel�� **************/
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
											// ������õĵ�Ԫ�����ӵ���������
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
											
											
											
											
											/**************** д����� *************/
											break;
										}
									}
								}
								/************** ������β **************/
							}
							/******************** ���Ĵ�����β ************************/
						}
					}
					System.out.println("wiki������" + wikiNum);
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
		System.out.println("ȫ���ɹ�����" + recordSum + "����¼");
		System.out.println("�����" + k + "���ļ�������");
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}