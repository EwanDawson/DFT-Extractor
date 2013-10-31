package function.txtAnalysis.similarity;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import function.txtAnalysis.TFIDF;
import function.txtAnalysis.Vectorization;
import function.util.SetUtil;

public class Similarity {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String base = "F:\\Data\\术语抽取数据集\\DMDataSet\\DM_experiment\\removeNoise\\";
		String vectorDir = base + "layer3-new-txt_wordVector";
		Similarity cs = new Similarity();
		String listAPath = base + "listA.txt";
		String listBPath = base + "listB.txt";
		Vector<String> vListA = SetUtil.readSetFromFile(listAPath);
		Vector<String> vListB = SetUtil.readSetFromFile(listBPath);
		cs.computeSimilarity(vectorDir, 2, vListA, vListB);
	}

	/**
	 * 
	 * @param txtHtmlDir 文本或网页路径
	 * @param listAPath 列表A的路径
	 * @param listBPath 列表B的路径
	 * @param tfidfTag 权重方法
	 * @param vectorMethod 向量化的方法,0表示词向量化，1表示术语向量化
	 * @return
	 */
	public HashMap<String[], Double> computeSimilarity(String txtHtmlDir,
			String listAPath, String listBPath, int tfidfTag, int vectorMethod) {
		/************ 向量化 *******************/
		Vectorization vz = new Vectorization();
		String wordVectorPath = "";
		if (vectorMethod == 0)
			wordVectorPath = vz.wordVector(txtHtmlDir);
		else if (vectorMethod == 1)
			wordVectorPath = vz.wikiVector(txtHtmlDir);
		/************ 获取list列表 *******************/
		Vector<String> vAllList = new Vector<String>();
		Vector<String> vAList = null;
		Vector<String> vBList = null;
		File f = new File(wordVectorPath);
		File childs[] = f.listFiles();
		for (int i = 0; i < childs.length; i++) {
			String fileName = childs[i].getName();
			String term = fileName.substring(0, fileName.lastIndexOf("."));
			vAllList.add(term);
		}
		if (listAPath.length() == 0 || listAPath == null) {
			vAList = new Vector<String>();
			vAList.addAll(vAllList);
		}// 没有指定列表就是全部
		else
			vAList = SetUtil.readSetFromFile(listAPath);
		if (listBPath.length() == 0 || listBPath == null) {
			vBList = new Vector<String>();
			vBList.addAll(vAllList);
		} else
			vBList = SetUtil.readSetFromFile(listBPath);
		/************ 相似度计算 *******************/
		HashMap<String[], Double> hmResult = computeSimilarity(wordVectorPath,
				tfidfTag, vAList, vBList);
		return hmResult;
	}

	/**
	 * 
	 * @param vectorDir
	 *            向量文件夹
	 * @param tfidfTag
	 *            权重标记，0表示tf，1表示idf，2表示tf*idf
	 * @param vListA
	 *            ，vListB 需要计算的列表A和B
	 * @return 同时把结果写到了同层目录的-similarity.csv里
	 */
	public HashMap<String[], Double> computeSimilarity(String vectorDir,
			int tfidfTag, Vector<String> vListA, Vector<String> vListB) {
		String desPath = vectorDir + "-similarity.csv";
		Vector<String> vResult = new Vector<String>();
		vResult.add("termA,termB,similarity");
		HashMap<String[], Double> hmResult = new HashMap<String[], Double>();
		// 所有word
		Vector<String> vAllWord = new Vector<String>();
		HashSet<String> hsAllWord = new HashSet<String>();
		TFIDF ti = new TFIDF();
		// 文件和（词——权重）的对应关系
		System.out.println("计算词向量权重……");
		HashMap<String, HashMap<String, double[]>> hmFileWordWeight = ti
				.computeTFIDF_app(vectorDir);
		// 读取文件并保存word和位置的对应关系
		System.out.println("读取文件并保存word和位置的对应关系……");
		File f = new File(vectorDir);
		File childs[] = f.listFiles();
		for (int i = 0; i < childs.length; i++) {
			String fileName = childs[i].getName();
			String filePath = childs[i].getAbsolutePath();
			Vector<String> vWord = SetUtil.readSetFromFile(filePath);
			hsAllWord.addAll(vWord);
			System.out.println(fileName + "——" + i + "/" + childs.length);
		}// end for
		vAllWord.addAll(hsAllWord);
		System.out.println("计算相似度……");
		int vectorSize = vAllWord.size();
		Iterator<String> iti = hmFileWordWeight.keySet().iterator();
		while (iti.hasNext()) {
			Iterator<String> itj = hmFileWordWeight.keySet().iterator();
			String titlei = iti.next();
			String termi = titlei.substring(0, titlei.lastIndexOf("."));
			if (!vListA.contains(termi))
				continue;
			HashMap<String, double[]> hmVectori = hmFileWordWeight.get(titlei);
			double weighti[] = new double[vectorSize];
			for (int i = 0; i < vectorSize; i++) {
				String word = vAllWord.get(i);
				if (hmVectori.containsKey(word))
					weighti[i] = hmVectori.get(word)[tfidfTag];
				else
					weighti[i] = 0;
			}
			while (itj.hasNext()) {
				String titlej = itj.next();
				String termj = titlej.substring(0, titlej.lastIndexOf("."));
				if (!vListB.contains(termj) || termj.equals(termi))
					continue;
				HashMap<String, double[]> hmVectorj = hmFileWordWeight
						.get(titlej);
				double weightj[] = new double[vectorSize];
				for (int i = 0; i < vectorSize; i++) {
					String word = vAllWord.get(i);
					if (hmVectorj.containsKey(word))
						weightj[i] = hmVectorj.get(word)[tfidfTag];
					else
						weightj[i] = 0;
				}
				double similarity = cosSimilarity(
						new WordVector(termi, weighti), new WordVector(termj,
								weightj));
				System.out.println(termi + "——" + termj + ":" + similarity);
				vResult.add(termi + "," + termj + "," + similarity);
				String term[]={termi,termj};
				hmResult.put(term, similarity);
			}
		}// end while
		SetUtil.writeSetToFile(vResult, desPath);
		System.out.println("OK");
		return hmResult;
	}

	/**
	 * 
	 * @param vA
	 * @param vB
	 * @return 给定词向量，计算余弦相似度
	 */
	public double cosSimilarity(WordVector vA, WordVector vB) {
		double weightA[] = vA.weight;
		double weightB[] = vB.weight;
		double result = 0;
		for (int i = 0; i < weightA.length; i++) {
			result += weightA[i] * weightB[i];
		}
		result = result / (vA.mod * vB.mod);
		return result;
	}

	/**
	 * 
	 * @author MJ
	 * @description 词向量内部类
	 */
	public class WordVector {
		String term = "";// 词条名称
		double weight[] = {};// 权重
		double mod = 0;// 权重的模

		public WordVector(String term, double weight[]) {
			this.term = term;
			this.weight = weight;
			for (int i = 0; i < weight.length; i++) {
				this.mod += weight[i] * weight[i];
			}
			this.mod = Math.sqrt(this.mod);
		}
	}
}
