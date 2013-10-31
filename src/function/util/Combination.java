package function.util;

import java.util.ArrayList; 
import java.util.BitSet; 
/** 
   * java组合算法的实现 
   * 
   */
public class Combination { 
  
    private ArrayList<String> combList= new ArrayList<String>(); 
/** 
   *mn方法的实现 
   * 
   */
    public void mn(String[] array, int n) { 
        int m = array.length; 
        if (m < n) 
            throw new IllegalArgumentException("Error   m   <   n"); 
        BitSet bs = new BitSet(m); 
        for (int i = 0; i < n; i++) { 
            bs.set(i, true); 
        } 
        do { 
            printAll(array, bs); 
        } while (moveNext(bs, m)); 
  
    } 
    /**说明 这个方法不太容易理解，也是组合的核心算法,下面进行简单的讲解. 
     * 1、start是从第一个true片段作为起始位，end作为截止位 
     * 2、第一个true片段都设置成false 
     * 3、数组从0下标起始到以第一个true片段元素数量减一为下标的位置都置true 
     * 4、把第一个true片段end截止位置true 
     * 
     * @param bs 数组是否显示的标志位 
     * @param m 数组长度 
     * @return boolean 是否还有其他组合 
     */
    private boolean moveNext(BitSet bs, int m) { 
        int start = -1; 
        while (start < m) 
            if (bs.get(++start)) 
                break; 
        if (start >= m) 
            return false; 
  
        int end = start; 
        while (end < m) 
            if (!bs.get(++end)) 
                break; 
        if (end >= m) 
            return false; 
        for (int i = start; i < end; i++) 
            bs.set(i, false); 
        for (int i = 0; i < end - start - 1; i++) 
            bs.set(i); 
        bs.set(end); 
        return true; 
    } 
  
    /** 
     * 输出打印结果 
     * 
     * @param array 数组 
     * @param bs 数组元素是否显示的标志位集合 
     */
    private void printAll(String[] array, BitSet bs) { 
        StringBuffer sb = new StringBuffer(); 
                //拼接StringBuffer，这个地方一定要用StringBuffer，不要直接使用String 的+ 
        for (int i = 0; i < array.length; i++) 
            if (bs.get(i)) { 
                sb.append(array[i]).append(','); 
            } 
        sb.setLength(sb.length() - 1); 
        combList.add(sb.toString()); 
    } 
  
    public ArrayList<String> getCombList() { 
        return combList; 
    } 
      /** 
     * 主方法测试 
     * 
     */
    public static void main(String[] args) throws Exception { 
        Combination comb = new Combination(); 
        comb.mn(new String[]{"1","2","3","4","5","6"}, 2); 
        for (int i = 0; i < comb.getCombList().size(); i++) { 
            System.out.println(comb.getCombList().get(i)); 
        } 
    } 
  
}
