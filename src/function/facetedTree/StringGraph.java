package function.facetedTree;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.BreadthFirstIterator;
/*
 * 有向有权图：
 * 
 */
public class StringGraph {
	DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph=
			new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StringGraph sg=new StringGraph();
		HashSet<String> hs=new HashSet<String>();
		hs.add("v1->v2");
		hs.add("v1->v3");
		hs.add("v2->v4");
		hs.add("v2->v5");
		hs.add("v3->v6");
		hs.add("v3->v7");
		hs.add("v4->v8");
		hs.add("v4->v9");
		hs.add("v4->v1");
		hs.add("v5->v2");
		hs.add("v5->v1");
		sg.getTree(hs, "v2");
	}
	
	/**
	 * 根据给定的上下位关系集合和指定的根返回相应的树
	 * @param hsEdge
	 * @param root
	 * @return
	 */
	public HashSet<String> getTree(HashSet<String> hsEdge,String root){
		HashSet<String> hsTree=new HashSet<String>();
		//广度遍历
		Iterator<String> it=hsEdge.iterator();
		while(it.hasNext()){
			String edge[]=it.next().split("->");
			if(!edge[1].equals(root))//去除指向根的边
				addEdge(edge[0], edge[1]);
		}
		Vector<String> srcs =new  Vector<String> ();  //新图的源节点
		Vector<String> dess =new  Vector<String> (); //新图的目标节点
		appCreateBreadthFirstGraph(root, srcs, dess);
		//打印结果
		System.out.println("根："+root);
		for(int i=0;i<srcs.size();i++){
			String edge=srcs.get(i)+"->"+dess.get(i);
			System.out.println(edge);
			hsTree.add(edge);
		}
		return hsTree;
	}

	public static void test_example1() {
		StringGraph rw=new StringGraph();
		rw.addEdge("v1", "v2");
		rw.addEdge("v1", "v2");
		rw.addEdge("v1", "v2");
		rw.graph.edgeSet().size();
		System.out.println(rw.graph.edgeSet().size());
	}
	
	
	public void testPath(){
		String vs="v1";
		Set<String> traveledVertise=new HashSet<String>();
		Queue<String> q = new LinkedList<String>();
		q.add(vs);
		while(!q.isEmpty()){
			String src=q.poll();
			traveledVertise.add(src);
			Vector<String> targets=getTargetVertexs(src);
			for(String des:targets){
				if(traveledVertise.contains(des)) continue;
				traveledVertise.add(des);
				System.out.println(src+"->"+des);
				q.add(des);
			}
		}		
	}
	
	public void appCreateBreadthFirstGraph(String startVertex, Vector<String> srcs, Vector<String> dess){
		Set<String> traveledVertise=new HashSet<String>();
		Queue<String> q = new LinkedList<String>();
		q.add(startVertex);
		while(!q.isEmpty()){
			String src=q.poll();
			traveledVertise.add(src);
			Vector<String> targets=getTargetVertexs(src);
			for(String des:targets){
				if(traveledVertise.contains(des)) continue;
				traveledVertise.add(des);
//				System.out.println(src+"->"+des);
				srcs.add(src);
				dess.add(des);
				q.add(des);
			}
		}		
	}
	
	//测试广度遍历算法
	public void appShortestPath(){
		String vs="v1";
		BreadthFirstIterator<String,DefaultWeightedEdge> bfi=new BreadthFirstIterator<String,DefaultWeightedEdge>(graph,vs);
		while(bfi.hasNext()){
			String v=bfi.next();
			System.out.print(v+" ");
		}
		System.out.println("");
	}
	
	private String normalization(String v1) {

		return v1;
	}
	
	public void addEdge(String src,String des){
		src=normalization(src);
		des=normalization(des);
		
		graph.addVertex(src);
		graph.addVertex(des);
		DefaultWeightedEdge edge=graph.addEdge(src, des);
		if(edge==null){
			edge=graph.getEdge(src, des);
			double weight=graph.getEdgeWeight(edge);
			weight=weight+1;
			graph.setEdgeWeight(edge, weight);
			
		}

	}
	
	public double getEdgeWeight(String src,String des){
		double weight=0;
		DefaultWeightedEdge edge=graph.getEdge(src, des);
		if(edge==null){
			return weight;
		}
		return graph.getEdgeWeight(edge);
	}
	
	public int addEdges(Vector<String> src, Vector<String>target){
		if(src.size() !=target.size()){
			return -1;
		}
		for(int i=0;i<src.size();i++){
			addEdge(src.get(i),target.get(i));
		}
		
		return src.size();
	}
	
	public Vector<String> getAllEdges(){
		Vector<String> strEdges=new Vector<String>();
		Set<DefaultWeightedEdge> edges=graph.edgeSet();
		for(DefaultWeightedEdge edge: edges){
			String src=graph.getEdgeSource(edge);
			String des=graph.getEdgeTarget(edge);
			double weight=graph.getEdgeWeight(edge);
			
			String line=src+","+des+","+weight;
			strEdges.add(line);
			System.out.println(line);
		}
		return strEdges;
	}
	
	public Vector<String> getAllVertexs(){
		Vector<String> strVertexs=new Vector<String>();
		Set<String> vertexs=graph.vertexSet();
		strVertexs.addAll(vertexs);
		return strVertexs;
	}
	
	public Vector<String> getTargetVertexs(String src){
		Vector<String> targets=new Vector<String>();
		Set<DefaultWeightedEdge> edges=graph.outgoingEdgesOf(src);
		for(DefaultWeightedEdge edge: edges){
			String des=graph.getEdgeTarget(edge);
			if(des!=null){
				targets.add(des);
			}
		}
		return targets;
	}
	
	public int getEdgeSize(){
		return graph.edgeSet().size();
	}
	public int getVertexSize(){
		return graph.vertexSet().size();
	}
	
	public void printDirectGraph(String start){
		
		 Vector<String> target=getTargetVertexs(start);
		 while(!target.isEmpty()){
			 for(String uper:target){
				 System.out.println(uper);
			 }
		 }
	}
}
