package function.netAnalysis.motif;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import function.util.FileUtil;
import function.util.StringUtil;

public class Motif {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName = "f:/DM.xls";
		String dumpFileName = fileName.substring(0, fileName.lastIndexOf(".")) + "_Edge.txt.OUT.dump";// fanmod生成的motif枚举文件
		Motif motif = new Motif();
		//motif.computeMotif3(fileName);
		HashMap<String, String[]> hmExample=motif.getMotif3Example(fileName, dumpFileName);
		Iterator<String> it=hmExample.keySet().iterator();
		while(it.hasNext()){
			String motifTag=it.next();
			String term[]=hmExample.get(motifTag);
			System.out.println(motifTag+":("+term[0]+","+term[1]+","+term[2]+")");
		}
		
	}

	/**
	 * 
	 * @param fileName
	 *            术语链接关系文件，至少包含sourceURLName和toURLName两列
	 */
	public void computeMotif3(String fileName) {
		String edgeFileName = fileName.substring(0, fileName.lastIndexOf("."))
				+ "_Edge.txt";// 对a.xls自动生成的a_Edge.txt文件
		String dumpFileName = edgeFileName + ".OUT.dump";// fanmod生成的motif枚举文件
		String htmlFileName = edgeFileName.substring(0,
				edgeFileName.lastIndexOf("."))
				+ "0.html";// 生成的网页文件
		File fEdge = new File(edgeFileName);
		File fDump = new File(dumpFileName);
		File fHtml = new File(htmlFileName);
		/************ Add srcToID ************/
		if (!fEdge.exists()) {
			SorToID sti = new SorToID(fileName);
			sti.run(sti);
		}
		/************ motif detection ************/
		if (!fDump.exists()) {
			FanmodCompute fmc = new FanmodCompute();
			fmc.findMotif3UseFanmod();
		}
		/************** add motif3 *****************/
		System.out.println("wait……");
		while (!fHtml.exists()) {
			fHtml = new File(htmlFileName);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}// 一直等到存在才计算
		if (fDump.exists()) {
			System.out.println("AddMotif3……");
			AddMotif3 add3 = new AddMotif3(fileName, dumpFileName);
			add3.run(add3);
		}
	}

	/**
	 * 获取产生的结果HTML内容
	 * 
	 * @param filePath
	 * @return
	 */
	public Vector<String[]> getHtmlContent(String filePath) {
		Vector<String[]> vResult = new Vector<String[]>();
		String s = FileUtil.readFile(filePath);
		int pos = s.indexOf(".png");
		String td = "<td>";
		String _td = "</td>";
		while (pos != -1) {
			int posA, posB;
			String record[] = new String[6];
			int posTemp = StringUtil.upIndexOf(s, td, pos);
			// ID
			posB = StringUtil.upIndexOf(s, _td, posTemp);
			posA = StringUtil.upIndexOf(s, td, posB);
			record[0] = s.substring(posA + td.length(), posB);
			// img
			posA = StringUtil.upIndexOf(s, "/", pos) + 1;
			posB = pos + 4;
			record[1] = s.substring(posA, posB);
			// frequency
			posA = s.indexOf(td, posB);
			posB = s.indexOf(_td, posA);
			record[2] = s.substring(posA + td.length(), posB);
			// Mean-Freq
			posA = s.indexOf(td, posB);
			posB = s.indexOf(_td, posA);
			record[3] = s.substring(posA + td.length(), posB);
			// Z-score
			posA = s.indexOf(td, posB);
			posB = s.indexOf(_td, posA);
			posA = s.indexOf(td, posB);
			posB = s.indexOf(_td, posA);
			record[4] = s.substring(posA + td.length(), posB);
			// P-value
			posA = s.indexOf(td, posB);
			posB = s.indexOf(_td, posA);
			record[5] = s.substring(posA + td.length(), posB);

			System.out.println("id:" + record[0] + "\t" + "img:" + record[1]
					+ "\t" + "frequency:" + record[2] + "\t"
					+ "mean-frequency:" + record[3] + "\t" +"z-score:" + record[4]
					+ "\t" + "p-value:" + record[5]);
			vResult.add(record);
			pos = s.indexOf(".png", pos + 4);
		}
		return vResult;
	}
	
	/**
	 * 获取motif3例子
	 * @param fileName
	 * @param m3EdgeFile
	 * @return
	 */
	public HashMap<String, String[]> getMotif3Example(String fileName,String m3DumpFile){
		Motif3Example m3e=new Motif3Example(fileName, m3DumpFile);
		m3e.run(m3e);
		HashMap<String, String[]> hmExample=m3e.hmExample;
		return hmExample;
	}

}
