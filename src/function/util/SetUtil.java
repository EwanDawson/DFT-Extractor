package function.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

/**
 * 
 * @author MJ
 * @description 求集合的若干操作，包括: 1).求两个集合的交集 2).求两个集合忽略大小写的交集 3).求两个集合的并集
 *              4).求两个集合忽略大小写的并集 5).求两个集合的差集 6).将某个集合转换为小写 7).从指定路径读取集合
 *              8).将集合写入到指定路径 9).求Vector集合的不重複集合 10).将HashSet转换成Vector
 */
public class SetUtil {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*Vector<String> v1 = new Vector<String>();
		Vector<String> v2 = new Vector<String>();
		v1.add("MaJian");
		v1.add("ZhaoTing");
		v1.add("baobao");
		v2.add("Majian");
		v2.add("Zhaoting");
		v2.add("baobao");
		Vector<String> inter = getInterSet(v1, v2);
		System.out.println("交集：");
		for (String s : inter) {
			System.out.println(s);
		}
		Vector<String> interIgnoreCase = getInterSetIgnoreCase(v1, v2);
		System.out.println("交集(忽略大小写)：");
		for (String s : interIgnoreCase) {
			System.out.println(s);
		}
		Vector<String> union = getUnionSet(v1, v2);
		System.out.println("并集：");
		for (String s : union) {
			System.out.println(s);
		}
		Vector<String> unionIgnoreCase = getInterSetIgnoreCase(v1, v2);
		System.out.println("并集(忽略大小写)：");
		for (String s : unionIgnoreCase) {
			System.out.println(s);
		}
		writeSetToFile(unionIgnoreCase, "f:/testSet.txt");*/
		Vector<String> select = readSetFromFile("F://layer1.txt");
		Vector<String> wcc_select = readSetFromFile("F://layer2.txt");
		Vector<String> interIgnoreCase = getSubSet(wcc_select,select);
		writeSetToFile(interIgnoreCase,"f://2.txt");
		System.out.print(interIgnoreCase);

	}
	
	/**
	 * 
	 * @param v1
	 *            集合V1
	 * @param v2
	 *            集合V2
	 * @return V1和V2的交集
	 */
	public static Vector<String> getInterSet(Vector<String> v1,
			Vector<String> v2) {
		Vector<String> interV = new Vector<String>();
		for (String s1 : v1) {
			if (v2.contains(s1) && !interV.contains(s1))
				interV.add(s1);
		}
		return interV;
	}

	/**
	 * 
	 * @param v1集合V1
	 * @param v2集合V2
	 * @return V1和V2忽略大小写的交集
	 */
	public static Vector<String> getInterSetIgnoreCase(Vector<String> v1,
			Vector<String> v2) {
		Vector<String> v1L = getLowerCaseSet(v1);
		Vector<String> v2L = getLowerCaseSet(v2);
		Vector<String> interV = new Vector<String>();
		for (String s1 : v1L) {
			if (v2L.contains(s1) && !interV.contains(s1))
				interV.add(s1);
		}
		return interV;
	}

	/**
	 * 
	 * @param v1
	 *            集合V1
	 * @param v2
	 *            集合V2
	 * @return V1和V2的并集
	 */
	public static Vector<String> getUnionSet(Vector<String> v1,
			Vector<String> v2) {
		Vector<String> unionV = new Vector<String>();
		for (String s1 : v1) {
			if (!unionV.contains(s1))
				unionV.add(s1);
		}
		for (String s2 : v2) {
			if (!unionV.contains(s2))
				unionV.add(s2);
		}
		return unionV;
	}

	/**
	 * 
	 * @param v1
	 *            集合V1
	 * @param v2
	 *            集合V2
	 * @return V1和V2忽略大小写的并集
	 */
	public static Vector<String> getUnionSetIgnoreCase(Vector<String> v1,
			Vector<String> v2) {
		Vector<String> v1L = getLowerCaseSet(v1);
		Vector<String> v2L = getLowerCaseSet(v2);
		Vector<String> unionV = new Vector<String>();
		for (String s1 : v1L) {
			if (!unionV.contains(s1))
				unionV.add(s1);
		}
		for (String s2 : v2L) {
			if (!unionV.contains(s2))
				unionV.add(s2);
		}
		return unionV;
	}

	/**
	 * 
	 * @param v1
	 *            集合v1
	 * @param v2
	 *            集合v2
	 * @return v1和v2的差集，即在v1中但不在v2中
	 */
	public static Vector<String> getSubSet(Vector<String> v1, Vector<String> v2) {
		Vector<String> subV = new Vector<String>();
		for (String s1 : v1) {
			if (!v2.contains(s1))
				subV.add(s1);
		}
		return subV;
	}

	/**
	 * 
	 * @param v
	 *            要转换的集合
	 * @return v全部变小写后的集合
	 */
	public static Vector<String> getLowerCaseSet(Vector<String> v) {
		Vector<String> lowerV = new Vector<String>();
		for (String s : v) {
			lowerV.add(s.toLowerCase());
		}
		return lowerV;
	}

	/**
	 * 
	 * @param filePath
	 *            文件路徑
	 * @return 从该文件读取的字符串集合
	 */
	public static Vector<String> readSetFromFile(String filePath) {
		Vector<String> v = new Vector<String>();
		FileReader fr;
		try {
			fr = new FileReader(filePath);
			BufferedReader br = new BufferedReader(fr);
			String s = br.readLine();
			while (s != null) {
				v.add(s);
				s = br.readLine();
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return v;
	}

	/**
	 * 
	 * @param v
	 *            要写入的集合
	 * @param filePath
	 *            写入的文件路径
	 */
	public static void writeSetToFile(Vector<String> v, String filePath) {
		try {
			File f=new File(filePath);
			File fParent=f.getParentFile();
			fParent.mkdirs();
			FileWriter fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);
			for (String s : v) {
				bw.write(s);
				bw.newLine();
			}
			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param v
	 * @param filePath 将v中的内容追加到指定的文件中
	 */
	public static void appendSetToFile(Vector<String> v, String filePath) {
		try {
			FileWriter fw = new FileWriter(filePath,true);
			BufferedWriter bw = new BufferedWriter(fw);
			for (String s : v) {
				bw.write(s);
				bw.newLine();
			}
			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param v
	 *            需要求不重复的Vector集合
	 * @return v不重复的集合
	 */
	public static Vector<String> getNoRepeatVector(Vector<String> v) {
		Vector<String> vNoRepeat = new Vector<String>();
		for (int i = 0; i < v.size(); i++) {
			String s = v.get(i);
			if (!vNoRepeat.contains(s))
				vNoRepeat.add(s);
		}
		return vNoRepeat;
	}
	
	/**
	 * 
	 * @param v
	 *            需要求不重复的Vector集合
	 * @return v 忽略大小写不重复的集合
	 */
	public static Vector<String> getNoRepeatVectorIgnoreCase(Vector<String> v) {
		Vector<String> vNoRepeat = new Vector<String>();
		Vector<String> vIgnoreCase=new Vector<String>();
		for (int i = 0; i < v.size(); i++) {
			String s = v.get(i);
			if (!vNoRepeat.contains(s)&&!vIgnoreCase.contains(s.toLowerCase())){
				vNoRepeat.add(s);
				vIgnoreCase.add(s.toLowerCase());
			}
		}
		return vNoRepeat;
	}
	
	/**
	 * 
	 * @param hs
	 * @return 将hs转换成Vector
	 */
	public static Vector<String> getVectorFromHashSet(HashSet<String> hs){
		Vector<String> v=new Vector<String>();
		Iterator<String> it=hs.iterator();
		while(it.hasNext()){
			v.add(it.next());
		}
		return v;
	}

}
