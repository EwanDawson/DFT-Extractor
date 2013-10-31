package function.netAnalysis.motif;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;

import function.base.ExcelBase;

/**
 * 
 * @author MJ
 * @description 生成excel文件中Src和To的对应ID号，并生成一份fanmod软件需要 的文本格式
 * 
 */
public class SorToID extends ExcelBase {

	String fileName = "";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName="f:\\test.xls";
		SorToID sti=new SorToID(fileName);
		sti.run(sti);
	}
	
	public SorToID(String fileName){
		this.fileName=fileName;
	}

	@Override
	public void process() throws Exception {
		// TODO Auto-generated method stub
		HashSet<String> hs = new HashSet<String>();
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		String edgeFileName = fileName.substring(0, fileName.lastIndexOf("."))
				+ "_Edge.txt";// 对a.xls自动生成a_Edge.txt文件
		FileWriter fw3 = new FileWriter(edgeFileName);
		BufferedWriter bw3 = new BufferedWriter(fw3);
		bw3.flush();
		int nameNumber = 0;
		for (int i = 1; i < getRows(); i++) {
			String src = getStringValue("sourceURLName", i);
			String to = getStringValue("toURLName", i);
			if (!hs.contains(src)) {
				hs.add(src);
				hm.put(src, nameNumber++);
			}
			if (!hs.contains(to)) {
				hs.add(to);
				hm.put(to, nameNumber++);
			}
		}
		for (int i = 1; i < getRows(); i++) {
			String srcName = getStringValue("sourceURLName", i);
			String toName = getStringValue("toURLName", i);
			int srcID = 0;
			if (hm.get(srcName) != null) {
				srcID = hm.get(srcName);
				setIntegerValue("srcID", i, srcID);
			}
			int toID = 0;
			if (hm.get(toName) != null) {
				toID = hm.get(toName);
				setIntegerValue("toID", i, toID);
			}
			bw3.write(srcID + " " + toID);
			bw3.newLine();
			System.out.println(i);
		}
		bw3.flush();
		bw3.close();
		fw3.close();
	}

	@Override
	public void run(ExcelBase eb) {
		// TODO Auto-generated method stub
		eb.go(fileName);
	}

}
