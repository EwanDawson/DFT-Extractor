package function.hypRelation;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/*
 * 目标：构建内存图结构
 * 1) 默认对节点字符串没有做任何处理。
 */

public class TermGraph {
	public DirectedGraph<String, DefaultEdge> stringGraph =
        new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);	



	public int addEdges(Vector<String> src, Vector<String>target){
		if(src.size() !=target.size()){
			return -1;
		}
		for(int i=0;i<src.size();i++){
			addEdge(src.get(i),target.get(i));
		}
		
		return src.size();
	}
	
	public void addEdge(String v1,String v2){
		v1 = strNormalization(v1);
		v2=strNormalization(v2);		
		Graphs.addEdgeWithVertices(stringGraph, v1, v2);
	}
	private String strNormalization(String v1) {

		return v1;
	}

	
    //查找两个点是否有上下位关系（有最短路径）
	//2 v1 是 v2父类
	//1 v1 是 v2子类
	//0 v1和v2没有关系
	//-1 v1或v2不在图中
	public int getRelation(String v1, String v2,List<DefaultEdge> outEdge) {
		v1 = strNormalization(v1);
		v2=strNormalization(v2);		
		int flag = 0;
		DijkstraShortestPath<String, DefaultEdge> dsp;
		try {
			dsp = new DijkstraShortestPath<String, DefaultEdge>(
					stringGraph, v1, v2);
			List<DefaultEdge> edge=null;
			if (null != (edge=dsp.getPathEdgeList())) {
				flag = 2;
				System.out.println(edge);
			} else {
				dsp = new DijkstraShortestPath<String, DefaultEdge>(
						stringGraph, v2, v1);
				if (null != (edge=dsp.getPathEdgeList())){
					flag = 1;
					System.out.println(edge);
				}
			}
			if(null != edge)outEdge.addAll(edge);

		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
			System.out.println("TermGraph: start vertice=" + v1 + "  end vertice=" + v2);
			flag = -1;
		}
		return flag;
	}
	public int getRelation(String v1, String v2) {
		List<DefaultEdge> edge=new ArrayList<DefaultEdge>();
		return getRelation(v1,v2,edge);
	}
	
	/*
	 * 简化版： 1) 找到节点到根的边序列。 2)对比该序列，找到共同父节点。
	 * 如果每个节点包含父亲指针，把两个节点到根的路径都记录下来，两条路径的最后面的元素肯定相同，
	 * 从两条路径的最后一个元素向前比较，直到第一次出现分叉为止，就可以找到最近节点。复杂度为O（n）， 路径最长可能是n
	 * 如果不包含父亲节点，那就先前序遍历二叉树，遍历的时候可以像哈夫曼树那样左右01编号，
	 * 记录给定两节点的到达路径，最后比较两个0，1序列的前面位数，直到出现不相等为止，就找到最近父节点， 复杂度也是O（n）
	 */

	public int getCommonParentNode(String root, String node1, String node2) {
//		root = strNormalization(root);
//		node1=strNormalization(node1);		
//		node2 = strNormalization(node2);

		List<DefaultEdge> edge1 = new ArrayList<DefaultEdge>();
		List<DefaultEdge> edge2 = new ArrayList<DefaultEdge>();
		int flag1 = -1;
		int flag2 = -1;
		flag1 = getRelation(root, node1, edge1);
		flag2 = getRelation(root, node2, edge2);
			int size1 = edge1.size();
			int size2 = edge2.size();
			
		int commondepth = 0;
		if (flag1 == 2 && flag2 == 2) {

			int index = 0;
			while (index < size1 && index < size2) {
				//[(data_structure : data_type), (data_type : abstract_data_type)]
				//[(data_structure : data_type), (data_type : record)]
				DefaultEdge e1 = edge1.get(index);
				DefaultEdge e2 = edge2.get(index);
				String child1 = stringGraph.getEdgeTarget(e1);
				String child2 = stringGraph.getEdgeTarget(e2);
				if (!child1.equalsIgnoreCase(child2)) {
					break;
				}
				
				commondepth++;
				index++;
			}
		}else{
			
			return 99;
		}

		// System.out.println(edge1);
		// System.out.println(edge2);
		Vector<String> commonNodes=new Vector<String>();
		
		commonNodes.add(root);
		for (int i = 0; i < commondepth; i++) {
			DefaultEdge e1 = edge1.get(i);			
			String childNode = stringGraph.getEdgeTarget(e1);
			commonNodes.add(childNode);
		}
		System.out.println("common nodes: "+commonNodes);
		int commonDistance=size1+1+size2+1-commonNodes.size()*2;
		System.out.println("commonDistance= "+commonDistance);
		return commonDistance;
	}

	//-------------------------------------------------------------------------------------------------------

//	public void addChildEdges(String v1,Vector<String> childs){
//	for(String child:childs){
//	Graphs.addEdgeWithVertices(stringGraph, v1, child);
//	}
//}
//public void addParentEdges(String v2,Vector<String> parents){
//	for(String parent:parents){
//	Graphs.addEdgeWithVertices(stringGraph, parent, v2);
//	}
//}
}