package function.facetedTree;

import java.util.Vector;

public class MyNode {

	public int layer=0;
	MyNode father=null;
	public String nodeName="";
	public Vector<MyNode> child=new Vector<MyNode>();
	public MyNode(String nodeName,Vector<MyNode> child,MyNode father,int layer){
		this.nodeName=nodeName;
		this.child=child;
		this.father=father;
		this.layer=layer;
	}
	
	public void removeChild(String childName){
		for(int i=0;i<this.child.size();i++){
			if(this.child.elementAt(i).nodeName.equals(childName)){
				MyNode n=new MyNode("noUse",new Vector<MyNode>(),null,0);
				this.child.setElementAt(n, i);
				break;
			}
		}
	}
	
	/**
	 * 获取节点数量
	 * @return
	 */
	public int getNodeNumber(){
		int nodeNumber=0;
		if (this.child.size() != 0 && !this.nodeName.equals("noUse")) {
			nodeNumber++;
			for (int i = 0; i < this.child.size(); i++) {
				nodeNumber+=this.child.elementAt(i).getNodeNumber();
			}
		} else if (this.child.size() == 0 && !this.nodeName.equals("noUse")) {
			nodeNumber++;
		}
		return nodeNumber;
	}
	
	/**
	 * 获取节点和其子节点的名称集合
	 * @return
	 */
	public Vector<String> getAllNodeName(){
		Vector<String> vNodeName=new Vector<String>();
		if (this.child.size() != 0 && !this.nodeName.equals("noUse")) {
			vNodeName.add(this.nodeName);
			for (int i = 0; i < this.child.size(); i++) {
				vNodeName.addAll(this.child.elementAt(i).getAllNodeName());
			}
		} else if (this.child.size() == 0 && !this.nodeName.equals("noUse")) {
			vNodeName.add(this.nodeName);
		}
		return vNodeName;
	}
	
}
