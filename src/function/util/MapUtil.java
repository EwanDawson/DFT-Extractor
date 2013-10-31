package function.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class MapUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("d", 2);
		map.put("c", 1);
		map.put("b", 1);
		map.put("a", 3);
		Vector<String[]> v=sortMapValueDes(map);
		for(String s[] :v){
			System.out.println(s[0]+","+s[1]);
		}
	}

	/**
	 * 对hashMap按照key升序排列
	 * @param hm
	 * @return
	 */
	public static Vector<String[]> sortMapKeyAsc(HashMap<String, Integer> hm) {
		Vector<String[]> v = new Vector<String[]>();// 存放排好序的hashmap
		ArrayList<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(
				hm.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {   
		    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {      
		        return (o1.getKey()).toString().compareTo(o2.getKey());
		    }
		});
		//排序后
		for (int i = 0; i < infoIds.size(); i++) {
		    String id = infoIds.get(i).toString();
		    String record[]=id.split("=");
		    v.add(record);
		}
		return v;
	}
	
	/**
	 * 对hashMap按照key降序排列
	 * @param hm
	 * @return
	 */
	public static Vector<String[]> sortMapKeyDes(HashMap<String, Integer> hm) {
		Vector<String[]> v = new Vector<String[]>();// 存放排好序的hashmap
		ArrayList<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(
				hm.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {   
		    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {      
		        return (o2.getKey()).toString().compareTo(o1.getKey());
		    }
		});
		//排序后
		for (int i = 0; i < infoIds.size(); i++) {
		    String id = infoIds.get(i).toString();
		    String record[]=id.split("=");
		    v.add(record);
		}
		return v;
	}
	
	/**
	 * 对hashMap按照value升序排列
	 * @param hm
	 * @return
	 */
	public static Vector<String[]> sortMapValueAsc(HashMap<String, Integer> hm) {
		Vector<String[]> v = new Vector<String[]>();// 存放排好序的hashmap
		ArrayList<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(
				hm.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {   
		    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {      
		        return (o1.getValue() - o2.getValue()); 
		    }
		});
		//排序后
		for (int i = 0; i < infoIds.size(); i++) {
		    String id = infoIds.get(i).toString();
		    String record[]=id.split("=");
		    v.add(record);
		}
		return v;
	}
	
	/**
	 * 对hashMap按照value降序排列
	 * @param hm
	 * @return
	 */
	public static Vector<String[]> sortMapValueDes(HashMap<String, Integer> hm) {
		Vector<String[]> v = new Vector<String[]>();// 存放排好序的hashmap
		ArrayList<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(
				hm.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {   
		    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {      
		        return (o2.getValue() - o1.getValue()); 
		    }
		});
		//排序后
		for (int i = 0; i < infoIds.size(); i++) {
		    String id = infoIds.get(i).toString();
		    String record[]=id.split("=");
		    v.add(record);
		}
		return v;
	}
	
	/**
	 * 对double类型hashMap按照value升序排列
	 * @param hm
	 * @return
	 */
	public static Vector<String[]> sortMapDoubleValueAsc(HashMap<String, Double> hm) {
		Vector<String[]> v = new Vector<String[]>();// 存放排好序的hashmap
		ArrayList<Map.Entry<String, Double>> infoIds = new ArrayList<Map.Entry<String, Double>>(
				hm.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Double>>() {   
		    public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {   
		    	if(o1.getValue() - o2.getValue()>0)
		    		return 1;
		    	else if(o1.getValue() - o2.getValue()<0)
		    		return -1;
		    	else return 0;
		    }
		});
		//排序后
		for (int i = 0; i < infoIds.size(); i++) {
		    String id = infoIds.get(i).toString();
		    String record[]=id.split("=");
		    v.add(record);
		}
		return v;
	}
	
	/**
	 * 对double类型hashMap按照value降序排列
	 * @param hm
	 * @return
	 */
	public static Vector<String[]> sortMapDoubleValueDes(HashMap<String, Double> hm) {
		Vector<String[]> v = new Vector<String[]>();// 存放排好序的hashmap
		ArrayList<Map.Entry<String, Double>> infoIds = new ArrayList<Map.Entry<String, Double>>(
				hm.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Double>>() {   
		    public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {   
		    	if(o2.getValue() - o1.getValue()>0)
		    		return 1;
		    	else if(o2.getValue() - o1.getValue()<0)
		    		return -1;
		    	else return 0;
		    }
		});
		//排序后
		for (int i = 0; i < infoIds.size(); i++) {
		    String id = infoIds.get(i).toString();
		    String record[]=id.split("=");
		    v.add(record);
		}
		return v;
	}

}
