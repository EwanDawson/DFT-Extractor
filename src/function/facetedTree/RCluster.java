package function.facetedTree;

import java.util.Vector;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REngine;
import org.rosuda.REngine.Rserve.RConnection;

import function.util.SetUtil;

/*
 * weibifan: 计算聚类结果及中心度
 * 输入：边列表，可以是字符串。
 * 输出：1）每个节点所属簇的标记，从1开始。
 *            2）每个节点的中心度。
 * * 
 */
public class RCluster {
	
	private String[] mat=null;
	private  REngine eng=null;
	
	String[] names=null;
	int [] cluster_index=null;
	double [] node_bet=null;
	private String relationFile="";//csv文件
	private String clusterFile="";//csv文件
	
	
	public RCluster(String relationFile,String clusterFile){
		this.relationFile=relationFile;
		this.clusterFile=clusterFile;
		init();
	}
	
	public RCluster(String relationFile){
		this.relationFile=relationFile;
		this.clusterFile=relationFile.substring(0, relationFile.lastIndexOf("."))+"-cluster.csv";//默认聚类文件
		init();
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Data_mining Data_structure Computer_network
//	    del();
		String relationFile="F:\\DFT-Extractor\\facetedTree\\Data_structure\\relation.csv";
		RCluster RC=new RCluster(relationFile);
		RC.cluster();
	}
	
	public void testcluster() {
		Vector<String> col1=new Vector<String>();
		Vector<String> col2=new Vector<String>();
		Vector<String> node = new Vector<String>();
		Vector<String> vRelationRecord=SetUtil.readSetFromFile(relationFile);
		for(int i=0;i<vRelationRecord.size();i++){
			String record[]=vRelationRecord.get(i).split(",");
			node.add(record[0]);
			node.add(record[1]);
		}
		Vector<String> nrnode = SetUtil.getNoRepeatVector(node);
		System.out.println(nrnode.size());
		for(int i=0;i<vRelationRecord.size();i++){
			String record[]=vRelationRecord.get(i).split(",");
			col1.add(nrnode.indexOf(record[0])+"1");
			col2.add(nrnode.indexOf(record[1])+"1");
		}
		loadGraph(col1, col2);
		System.out.println("computing");
		computing();
		System.out.println("success!");
		int [] cluster_index=getC_r();
		System.out.println(cluster_index[2]);
	}
	
	public void cluster(){
		String title[]=new String[3];
		title[0]="term";
		title[1]="centrality";
		title[2]="clusterId";
		Vector<String> vRelationRecord=SetUtil.readSetFromFile(relationFile);
		//Vector<Vector<Serializable>> vResult=new Vector<Vector<Serializable>>();
		Vector<String> col1=new Vector<String>();
		Vector<String> col2=new Vector<String>();
		for(int i=1;i<vRelationRecord.size();i++){
			String record[]=vRelationRecord.get(i).split(",");
			col1.add(record[0]);
			col2.add(record[1]);
		}
		//Vector<String> col1=ExcelUtil.readSetFromExcel(relationFile, 0, "sourceURLName");
		//Vector<String> col2=ExcelUtil.readSetFromExcel(relationFile, 0, "toURLName");
		loadGraph(col1, col2);
		System.out.println(col1);
		System.out.println(col1.size());
		computing();
		String[] names=getNames();
		int [] cluster_index=getC_r();
		double [] node_bet=getNode_bet();
		Vector<String> vResult=new Vector<String>();
		vResult.add("term,centrality,clusterId");
        for(int row=0;row<node_bet.length;row++){
        	//Vector<Serializable> record = new Vector<Serializable>();
        	String record=names[row]+","+node_bet[row]+","+cluster_index[row];
        	vResult.add(record);
        	System.out.println(record);
        }
       // ExcelUtil.writeSetToExcel(vResult, clusterFile, 0, title);
        SetUtil.writeSetToFile(vResult, clusterFile);
	}
	
	private void init(){
	    try {
	    	RConnection c = new RConnection();
		    eng = (REngine) c;
		    
	        int flag=eng.parseAndEval("suppressWarnings(require('igraph'))").asInteger();
	        if(flag<1){
	        	System.err.println("init error!");
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  

	}
	
	private void loadGraph(Vector<String> col1,Vector<String> col2){
       int num=col1.size();
        mat=new String[num*2];
        int i=0;
        for(String str:col1){
        	mat[i]=str;
        	i++;
        }
        for(String str:col2){
        	mat[i]=str;
        	i++;
        }
	}
	
	/**
	 * 聚类算法
	 * multilevel.community(ug, weights=NA)
	 * leading.eigenvector.community(ug)
	 * fastgreedy.community(ug)
	 * edge.betweenness.community(ug)
	 * label.propagation.community(ug)
	 */
	private void computing(){
		int num=mat.length/2;
		try {
			eng.assign("m", mat);
			eng.parseAndEval("m<-matrix(m,"+num+","+2+")");
	        @SuppressWarnings("unused")
			REXP r= null;
	        eng.parseAndEval("ug<-graph.data.frame(m, directed=FALSE)");
	        eng.parseAndEval("dg<-graph.data.frame(m)");
	        r= eng.parseAndEval("mc <- multilevel.community(ug, weights=NA)");
	        System.out.println("start");
	        //r= eng.parseAndEval("mc <- edge.betweenness.community(ug)");
	        System.out.println("over");
	        r = eng.parseAndEval("modularity(mc)");
	        System.out.println(r.asString());
	        r = eng.parseAndEval("c_alg<-algorithm(mc)");
//	        System.out.println(r.asString());
	        r = eng.parseAndEval("c_len<-length(mc)");
//	        System.out.println(r.asString());	        
			cluster_index = eng.parseAndEval("c_mem<-membership(mc)").asIntegers();
			names= eng.parseAndEval("names(c_mem)").asStrings();
//			System.out.println(c_r.length);
//			System.out.println(names.length);
			

	        
	        node_bet = eng.parseAndEval("node_bet<-betweenness(dg)").asDoubles();
			names= eng.parseAndEval("names(node_bet)").asStrings();
//			System.out.println(c_r.length);
//			System.out.println(names.length);	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	public String[] getNames() {
		return names;
	}
	public int[] getC_r() {
		return cluster_index;
	}
	public double[] getNode_bet() {
		return node_bet;
	}
}
