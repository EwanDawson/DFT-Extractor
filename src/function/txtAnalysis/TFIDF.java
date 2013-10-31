package function.txtAnalysis;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import function.util.SetUtil;

public class TFIDF {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String htmlDir="F:\\Data\\术语抽取数据集\\DMDataSet\\DM_experiment\\removeNoise\\layer3-new";
		TFIDF tfidf=new TFIDF();
		tfidf.computeTFIDF(htmlDir);
	}
	
	/**
	 * 根据给定的vectorDir文件夹计算tf/idf
	 * @param vectorDir
	 * @return 文件和（词——权重）的对应关系
	 */
	public HashMap<String,HashMap<String,double[]>> computeTFIDF_app(String vectorDir){
		//文件和（词——权重）的对应关系
		HashMap<String, HashMap<String, double[]>> hmFileWordWeight = new HashMap<String, HashMap<String, double[]>>();
		Vector<Vector<String>> vvTerm = new Vector<Vector<String>>();
		File f = new File(vectorDir);
		File childs[] = f.listFiles();
		int size=childs.length;
		for (int i = 0; i < size; i++) {
			String filePath = childs[i].getAbsolutePath();
			Vector<String> vTerm = SetUtil.readSetFromFile(filePath);
			vvTerm.add(vTerm);
		}
		HashMap<String, Double> hmIdf = computeIDF(vvTerm);
		for (int i = 0; i < size; i++) {
			HashMap<String,double[]> hmResult = new HashMap<String,double[]>();
			String fileName = childs[i].getName();
			String filePath=childs[i].getAbsolutePath();
			Vector<String> vTerm = SetUtil.readSetFromFile(filePath);
			HashMap<String, Double> hmTf = computeTF(vTerm);
			Iterator<String> it = hmTf.keySet().iterator();
			while (it.hasNext()) {
				String term = it.next();
				double dresult[]=new double[3];
				double tf = hmTf.get(term);
				double idf = hmIdf.get(term);
				double tfidf = tf * idf;
				dresult[0]=tf;
				dresult[1]=idf;
				dresult[2]=tfidf;
				hmResult.put(term, dresult);
			}
			hmFileWordWeight.put(fileName, hmResult);
			System.out.println("tf/idf计算："+fileName+"--------("+(i+1)+"/"+size+")");
		}
		return hmFileWordWeight;
	}

	/**
	 * 根据给定的html文件夹计算tf/idf
	 * @param htmlDir
	 */
	public void computeTFIDF(String htmlDir) {
		Vectorization vectorization=new Vectorization();
		String vectorDir = vectorization.wikiVector(htmlDir);
		computeTFIDF_wv(vectorDir);
	}

	/**
	 * 根据给定的文本向量文件夹计算tf/idf
	 * @param vectorDir
	 */
	public void computeTFIDF_wv(String vectorDir) {
		String tfidfDir = vectorDir + "_tfidf";
		File fTfidf = new File(tfidfDir);
		fTfidf.mkdirs();
		Vector<Vector<String>> vvTerm = new Vector<Vector<String>>();
		File f = new File(vectorDir);
		File childs[] = f.listFiles();
		int size=childs.length;
		for (int i = 0; i < size; i++) {
			String filePath = childs[i].getAbsolutePath();
			Vector<String> vTerm = SetUtil.readSetFromFile(filePath);
			vvTerm.add(vTerm);
		}
		HashMap<String, Double> hmIdf = computeIDF(vvTerm);
		
		for (int i = 0; i < size; i++) {
			Vector<String> vResult = new Vector<String>();
			vResult.add("term,tf,idf,tf/idf");
			String fileName = childs[i].getName();
			String filePath=childs[i].getAbsolutePath();
			String savePath = tfidfDir+"/"+fileName.substring(0, fileName.indexOf("."))
					+ "_tfidf.csv";
			Vector<String> vTerm = SetUtil.readSetFromFile(filePath);
			HashMap<String, Double> hmTf = computeTF(vTerm);
			Iterator<String> it = hmTf.keySet().iterator();
			while (it.hasNext()) {
				String term = it.next();
				double tf = hmTf.get(term);
				double idf = hmIdf.get(term);
				double tfidf = tf * idf;
				String record = term + "," + tf + "," + idf + "," + tfidf;
				vResult.add(record);
			}
			System.out.println("tf/idf计算："+fileName+"--------("+(i+1)+"/"+size+")");
			SetUtil.writeSetToFile(vResult, savePath);
		}
	}

	/**
	 * 
	 * @param vTerm
	 * @return 计算一个向量的tf
	 */
	public HashMap<String, Double> computeTF(Vector<String> vTerm) {
		HashMap<String, Double> hm = new HashMap<String, Double>();
		for (String s : vTerm) {
			if (!hm.containsKey(s))
				hm.put(s, 1.0);
			else {
				double curFre = hm.get(s) + 1;
				hm.put(s, curFre);
			}
		}
		Iterator<String> it = hm.keySet().iterator();
		int size = vTerm.size();
		while (it.hasNext()) {
			String term = it.next();
			double tf = hm.get(term) / size;
			hm.put(term, tf);
		}
		return hm;
	}

	/**
	 * 计算一个向量集合的idf
	 * @param vvTerm
	 * @return 
	 */
	public HashMap<String, Double> computeIDF(Vector<Vector<String>> vvTerm) {
		HashMap<String, Double> hm = new HashMap<String, Double>();
		for (Vector<String> v : vvTerm) {
			HashSet<String> hs = new HashSet<String>();
			for (String s : v) {
				if (!hs.contains(s)) {
					hs.add(s);
					if (!hm.containsKey(s))
						hm.put(s, 1.0);
					else {
						double curFre = hm.get(s) + 1;
						hm.put(s, curFre);
					}
				}
			}
		}// end for
		int D = vvTerm.size();
		Iterator<String> it = hm.keySet().iterator();
		while (it.hasNext()) {
			String term = it.next();
			double idf = Math.log(D / hm.get(term));
			hm.put(term, idf);
		}
		return hm;
	}
}
