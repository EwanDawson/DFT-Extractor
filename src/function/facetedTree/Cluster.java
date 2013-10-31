package function.facetedTree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import function.util.MapUtil;
import function.util.SetUtil;

public class Cluster {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * 获取某个簇中除去最大树后要删除的边
	 * 
	 * @param edgeHs
	 * @param maxTreeNode
	 * @param vClusterNode
	 * @return
	 */
	public Vector<String[]> getClusterDelEdge(HashSet<String> edgeHs,
			Vector<String> maxTreeNode, Vector<String> vClusterNode) {
		Vector<String[]> vDelEdge = new Vector<String[]>();
		Iterator<String> it = edgeHs.iterator();
		while (it.hasNext()) {
			String edge = it.next();
			String nodeName[] = edge.split("->");
			if ((maxTreeNode.contains(nodeName[0]) && !maxTreeNode
					.contains(nodeName[1]))
					|| (maxTreeNode.contains(nodeName[1]) && !maxTreeNode
							.contains(nodeName[0])))
				vDelEdge.add(nodeName);
			else if ((vClusterNode.contains(nodeName[0]) && !maxTreeNode
					.contains(nodeName[0]))
					|| (vClusterNode.contains(nodeName[1]) && !maxTreeNode
							.contains(nodeName[1])))
				vDelEdge.add(nodeName);
		}
		return vDelEdge;
	}

	/**
	 * 获取某个簇中除去最大树后要删除的节点
	 * 
	 * @param vClusterNode
	 * @param maxTreeNode
	 * @return
	 */
	public Vector<String> getClusterDelNode(Vector<String> vClusterNode,
			Vector<String> maxTreeNode) {
		return SetUtil.getSubSet(vClusterNode, maxTreeNode);
	}

	/**
	 * 找出最大的树
	 * 
	 * @param vRoot
	 * @return
	 */
	public MyNode getMaxTree(Vector<MyNode> vRoot) {
		int maxNodeNumber = 0;
		MyNode maxRoot = null;
		for (int i = 0; i < vRoot.size(); i++) {
			MyNode root = vRoot.get(i);
			if (root.getNodeNumber() > maxNodeNumber) {
				maxNodeNumber = root.getNodeNumber();
				maxRoot = root;
			}
		}
		return maxRoot;
	}

	/**
	 * 从包含term和中心度的簇中提取出节点名
	 * 
	 * @param vClusters
	 * @return
	 */
	public Vector<String> getClusterNode(Vector<String[]> vCluster) {
		Vector<String> vNode = new Vector<String>();
		for (String[] node : vCluster) {
			vNode.add(node[0]);
		}
		return vNode;
	}

	/**
	 * 获取指定簇vClusterNode中的上下位边
	 * 
	 * @param edgeHs
	 * @param vClusterNode
	 * @return
	 */
	public HashSet<String> getClusterHypeEdge(HashSet<String> edgeHs,
			Vector<String> vClusterNode) {
		HashSet<String> clusterEdgeHs = new HashSet<String>();
		Iterator<String> it = edgeHs.iterator();
		while (it.hasNext()) {
			String edge = it.next();
			String nodeName[] = edge.split("->");
			if (vClusterNode.contains(nodeName[0])
					&& vClusterNode.contains(nodeName[1]))
				clusterEdgeHs.add(edge);
		}
		return clusterEdgeHs;
	}

	/**
	 * 根据簇节点的大小对簇进行合适的位置排列
	 * 
	 * @param vClusters
	 * @return
	 */
	public Vector<Vector<String[]>> getSuitableCluster(
			Vector<Vector<String[]>> vClusters) {
		Vector<Vector<String[]>> vSuitableClusters = new Vector<Vector<String[]>>();
		int clusterNumber = vClusters.size();
		HashMap<String, Integer> hmClusterSize = new HashMap<String, Integer>();// 存放每个簇节点的个数
		HashMap<String, Double> hmAngle = new HashMap<String, Double>();// 存放角度
		for (int i = 0; i < clusterNumber; i++) {
			hmClusterSize.put(String.valueOf(i), vClusters.get(i).size());
			double angle = 360 * 1.0 * i / clusterNumber;
			if (angle >= 90 && angle < 180)
				angle = 180 - angle;
			else if (angle >= 180 && angle < 270)
				angle = angle - 180;
			else if (angle >= 270)
				angle = 360 - angle;
			hmAngle.put(String.valueOf(i), angle);
		}
		Vector<String[]> vAngle = MapUtil.sortMapDoubleValueAsc(hmAngle);// 按照距离水平线的角度升序排列
		Vector<String[]> vClusterSize = MapUtil.sortMapValueDes(hmClusterSize);// 按照节点个数降序排列
		HashMap<Integer, Integer> hmAngleCluster = new HashMap<Integer, Integer>();// 角度和cluster的id映射
		for (int i = 0; i < clusterNumber; i++) {
			hmAngleCluster.put(Integer.valueOf(vAngle.get(i)[0]),
					Integer.valueOf(vClusterSize.get(i)[0]));
		}
		for (int i = 0; i < clusterNumber; i++) {
			vSuitableClusters.add(vClusters.get(hmAngleCluster.get(i)));
		}
		return vSuitableClusters;
	}

	/**
	 * 从csv文件读取簇结果，包含term centrality clusterId
	 * 
	 * @param fileName
	 * @return
	 */
	public Vector<Vector<String[]>> readCluster(String fileName) {
		Vector<Vector<String[]>> vClusters = new Vector<Vector<String[]>>();
		HashMap<Integer, HashMap<String, Double>> hm = new HashMap<Integer, HashMap<String, Double>>();
		Vector<String> vRecord = SetUtil.readSetFromFile(fileName);
		for (int i = 1; i < vRecord.size(); i++) {
			String record[] = vRecord.get(i).split(",");
			String term = record[0];
			Double centrality = Double.valueOf(record[1]);
			int clusterId = Integer.valueOf(record[2]);
			if (!hm.containsKey(clusterId)) {
				HashMap<String, Double> hmCluster = new HashMap<String, Double>();
				hmCluster.put(term, centrality);
				hm.put(clusterId, hmCluster);
			} else {
				HashMap<String, Double> hmCluster = hm.get(clusterId);
				hmCluster.put(term, centrality);
				hm.put(clusterId, hmCluster);
			}
		}// end for
		Iterator<Integer> it = hm.keySet().iterator();
		while (it.hasNext()) {
			int id = it.next();
			HashMap<String, Double> hmCluster = hm.get(id);
			Vector<String[]> vCluster = MapUtil
					.sortMapDoubleValueDes(hmCluster);
			vClusters.add(vCluster);
		}
		return vClusters;
	}

	/**
	 * 计算由上下位边组成的拓扑图的度
	 * 
	 * @param edgeHs
	 * @return
	 */
	public HashMap<String, Integer> getDegree(HashSet<String> edgeHs) {
		HashMap<String, Integer> hmDegree = new HashMap<String, Integer>();
		Iterator<String> it = edgeHs.iterator();
		while (it.hasNext()) {
			String record[] = it.next().split("->");
			if (!hmDegree.containsKey(record[0]))
				hmDegree.put(record[0], 1);
			else {
				int degree = hmDegree.get(record[0]) + 1;
				hmDegree.put(record[0], degree);
			}
			if (!hmDegree.containsKey(record[1]))
				hmDegree.put(record[1], 1);
			else {
				int degree = hmDegree.get(record[1]) + 1;
				hmDegree.put(record[1], degree);
			}
		}
		return hmDegree;
	}
}
