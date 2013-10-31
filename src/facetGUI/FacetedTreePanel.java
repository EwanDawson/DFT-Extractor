package facetGUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import twaver.Layer;
import twaver.Link;
import twaver.ResizableNode;
import twaver.TDataBox;
import twaver.TWaverConst;
import twaver.network.TNetwork;
import function.facetedTree.Cluster;
import function.facetedTree.FacetedTree;
import function.facetedTree.MyNode;
import function.facetedTree.RCluster;
import function.facetedTree.TreeView;
import function.util.FileUtil;
import function.util.SetUtil;

public class FacetedTreePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4034136806675760042L;
	// relation file select
	private JPanel jpTop = new JPanel();
	private JLabel relationFileLabel = new JLabel("Domain Relation File：");
	private JTextField relationFileTf = new JTextField();
	private JButton relationFileChooseBtn = new JButton("File...");
	// cluster
	private TDataBox clusterBox = new TDataBox();
	private TNetwork clusterNetwork = new TNetwork(clusterBox);
	private JScrollPane clusterJsp = new JScrollPane(clusterNetwork);
	private JButton clusterBtn = new JButton(
			"<html>&nbsp;&nbsp;&nbsp;&nbsp;Facet<br>Detection</html>");
	// treeView
	private TreeView tw = new TreeView();
	private JPanel jp = new JPanel();
	private JScrollPane jsp = new JScrollPane(jp);
	private JTree tree = new JTree();
	JPanel treeListJp = new JPanel();
	private JScrollPane treeListJsp = new JScrollPane(treeListJp);
	private Border border = BorderFactory.createEtchedBorder(Color.white,
			Color.gray);
	private Border jspTitle = BorderFactory.createTitledBorder(border,
			"Faceted Tree View", TitledBorder.LEFT, TitledBorder.TOP);
	private Border treeListJspTitle = BorderFactory.createTitledBorder(border,
			"Faceted Tree List", TitledBorder.LEFT, TitledBorder.TOP);
	private JButton treeBtn = new JButton("<html>Taxonomy<br>Generation</html>");
	/*********** 其他属性定义 ************/
	private HashMap<String, MyNode> hmNode = new HashMap<String, MyNode>();
	// cluster布局
	private int centerX, centerY, r;
	private Vector<ResizableNode> vNode = new Vector<ResizableNode>();// 检测重叠之用
	private Layer edgeLayer = new Layer("edgeLayer");
	private Layer nodeLayer = new Layer("nodeLayer");
	// cluster集合
	private Vector<Vector<String[]>> vSuitableClusters = new Vector<Vector<String[]>>();
	private Cluster cluster = new Cluster();
	// 流程控制
	private int processId = 0;
	private String processIdFile = "";

	/**
	 * @param args
	 */

	public int getProcessId() {
		int id = Integer.valueOf(FileUtil.readFile(processIdFile));
		processId = id;
		return processId;
	}

	public void setProcessId(int processId) {
		this.processId = processId;
		String id = String.valueOf(processId);
		FileUtil.writeStringFile(id, processIdFile);
	}

	public FacetedTreePanel() {
		setLayout(null);
		this.setBackground(Color.white);

		// relation file select
		this.add(jpTop);
		jpTop.setLayout(null);
		jpTop.setBackground(new Color(230, 239, 248));
		jpTop.setBounds(10, 10, 990, 50);
		jpTop.add(relationFileLabel);
		jpTop.add(relationFileTf);
		jpTop.add(relationFileChooseBtn);
		relationFileLabel.setBounds(45, 15, 170, 25);
		relationFileTf.setBounds(180, 15, 510, 25);
		relationFileChooseBtn.setBounds(700, 15, 60, 25);
		relationFileTf
				.setText("F:\\DOFT-data\\facetedTree\\Data_structure\\relation-normal.csv");

		relationFileChooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser file = new JFileChooser("f:/");
				int result = file.showOpenDialog(null);
				// JFileChooser.APPROVE_OPTION是0，代表已经选择了文件夹
				if (result == JFileChooser.APPROVE_OPTION) {
					// 获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
					String path = file.getSelectedFile().getAbsolutePath();
					relationFileTf.setText(path);
				}
			}
		});

		// cluster
		this.add(clusterJsp);
		this.add(clusterBtn);
		clusterJsp.setBounds(10, 70, 900, 250);
		clusterBtn.setBounds(920, 150, 90, 60);
		clusterBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final String relationFile = relationFileTf.getText(); // 术语关系文件所在路径
				if (relationFile.length() == 0) {
					JOptionPane.showMessageDialog(FacetedTreePanel.this,
							"Please select the domain relation file！");
				} else {
					final Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							clusterNetwork.showFullScreen();
							clusterBox.getLayerModel().addLayer(edgeLayer);
							clusterBox.getLayerModel().addLayer(nodeLayer);
							Dimension screensize = Toolkit.getDefaultToolkit()
									.getScreenSize();
							int width = (int) screensize.getWidth();// 得到宽
							int height = (int) screensize.getHeight();// 得到高
							centerX = width / 2;
							centerY = height / 2 - 50;
							r = centerY * 4 / 5;
							// 聚类
							RCluster RC=new RCluster(relationFile);
							RC.cluster();
							String clusterFile = relationFile.substring(0,
									relationFile.lastIndexOf("."))
									+ "-cluster.csv";
							// 加点
							Vector<Vector<String[]>> vClusters = cluster
									.readCluster(clusterFile);
							vSuitableClusters = cluster
									.getSuitableCluster(vClusters);
							addClusters(vSuitableClusters);
							Vector<String> vEdge = SetUtil
									.readSetFromFile(relationFile);
							HashSet<String> edgeHs = new FacetedTree()
									.generateHypEdge(vEdge);
							// 加边
							addEdge(edgeHs);
						}
					});
					t.start();
					processIdFile = relationFile.substring(0,
							relationFile.lastIndexOf("\\"))
							+ "\\processId.txt";
					setProcessId(1);
				}
			}
		});
		// treeView

		this.add(treeListJsp);
		this.add(jsp);
		this.add(treeBtn);
		treeListJsp.setBounds(10, 330, 190, 410);
		jsp.setBounds(200, 330, 712, 410);
		treeBtn.setBounds(920, 500, 90, 60);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jsp.setBorder(jspTitle);
		treeListJsp
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		treeListJsp.setBorder(treeListJspTitle);

		treeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final String relationFile = relationFileTf.getText(); // 术语关系文件所在路径
				if (relationFile.length() == 0) {
					JOptionPane.showMessageDialog(FacetedTreePanel.this,
							"Please select the domain relation file！");
				} else {
					final Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							processIdFile = relationFile.substring(0,
									relationFile.lastIndexOf("\\"))
									+ "\\processId.txt";
							File f = new File(processIdFile);
							FacetedTree ft = new FacetedTree();
							DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode();
							TreeModel treeModel = new DefaultTreeModel(treeRoot);
							tree.removeAll();
							tree.setModel(treeModel);
							tree.setRootVisible(false);
							tree.putClientProperty("JTree.lineStyle", "None");// 清除线
							tree.setShowsRootHandles(true);// 设置图标
							if (!f.exists() || getProcessId() != 1) {// 直接添加上下位生成树
								Vector<MyNode> vMyNode = ft
										.constructTrees(relationFile);
								for (int i = 0; i < vMyNode.size(); i++) {
									MyNode root = vMyNode.get(i);
									treeRoot = generateTree(root, treeRoot);
								}
								treeListJp.add(tree);
								tree.updateUI();
							} else {// 根据聚类的结果删边后再添加生成树
								//改变窗口大小
								jpTop.setVisible(false);
								clusterJsp.setBounds(10, 10, 900, 560);
								treeListJsp.setBounds(10, 580, 190, 160);
								jsp.setBounds(200, 580, 712, 160);
								try {
									Thread.sleep(3000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Vector<String> vEdge = SetUtil
										.readSetFromFile(relationFile);
								HashSet<String> edgeHs = ft
										.generateHypEdge(vEdge);
								for (int i = 0; i < vSuitableClusters.size(); i++) {
									Vector<String[]> vCluster = vSuitableClusters
											.get(i);
									Vector<String> vClusterNode = cluster
											.getClusterNode(vCluster);
									System.out.println("-----------cluster<"
											+ i + ">-----------");
									System.out.println("节点个数"
											+ vClusterNode.size() + ":"
											+ vClusterNode);
									HashSet<String> clusterEdgeHs = cluster
											.getClusterHypeEdge(edgeHs,
													vClusterNode);
									System.out
											.println("----------clusterEdge--------");
									Iterator<String> it = clusterEdgeHs
											.iterator();
									while (it.hasNext()) {
										System.out.println(it.next());
									}
									Vector<MyNode> vClusterRoot = ft
											.constructTrees(clusterEdgeHs);
									MyNode maxTreeNode = cluster
											.getMaxTree(vClusterRoot);
									if (maxTreeNode != null) {
										Vector<String> vMaxTreeNodeName = maxTreeNode
												.getAllNodeName();
										Vector<String[]> vDelEdge = cluster
												.getClusterDelEdge(edgeHs,
														vMaxTreeNodeName,
														vClusterNode);
										System.out
												.println("--------delEdge--------");
										System.out.println("删除边数："
												+ vDelEdge.size());
										for (String edgeTemp[] : vDelEdge)
											System.out.println(edgeTemp[0]
													+ "->" + edgeTemp[1]);
										Vector<String> vDelNode = cluster
												.getClusterDelNode(
														vClusterNode,
														vMaxTreeNodeName);
										System.out
												.println("--------delNode--------");
										System.out.println("删除节点数："
												+ vDelNode.size());
										for (String nodeTemp : vDelNode)
											System.out.println(nodeTemp);
										delEdge(vDelEdge);
										delNode(vDelNode);
										treeRoot = generateTree(maxTreeNode,
												treeRoot);// 添加树

									} else {
										Vector<String[]> vDelEdge = cluster
												.getClusterDelEdge(edgeHs,
														vClusterNode,
														vClusterNode);
										System.out
												.println("--------delEdge--------");
										System.out.println("删除边数："
												+ vDelEdge.size());
										for (String edgeTemp[] : vDelEdge)
											System.out.println(edgeTemp[0]
													+ "->" + edgeTemp[1]);
										Vector<String> vDelNode = vClusterNode;
										System.out
												.println("--------delNode--------");
										System.out.println("删除节点数："
												+ vDelNode.size());
										for (String nodeTemp : vDelNode)
											System.out.println(nodeTemp);
										delEdge(vDelEdge);
										delNode(vDelNode);
									}
									System.out
											.println("--------------------------cluster<"
													+ i
													+ "> end-----------------------------");
									// 添加到树
									treeListJp.add(tree);
									tree.updateUI();
									try {
										Thread.sleep(3000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}// end for each cluster
								//恢复窗口大小
								jpTop.setVisible(true);
								clusterJsp.setBounds(10, 70, 900, 250);
								treeListJsp.setBounds(10, 330, 190, 410);
								jsp.setBounds(200, 330, 712, 410);
							}
							setProcessId(2);
						}
					});
					t.start();
				}
			}
		});

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree
						.getLastSelectedPathComponent();// 返回最后选定的节点
				String selectedNodeName = selectedNode.toString();
				if (hmNode.containsKey(selectedNodeName)) {
					MyNode node = hmNode.get(selectedNodeName);
					FacetedTree ft = new FacetedTree();
					String desXmlFile = "C:/Program Files/tree.xml";
					ft.generateTreeXml(node, desXmlFile);
					jp.removeAll();
					jp.add(tw.showTree(desXmlFile, "name"));
					jp.updateUI();
					File f = new File(desXmlFile);
					f.delete();// 加载完就删除
				}
			}
		});
	}

	/**
	 * 向parent中添加根为root的子树
	 * 
	 * @param root
	 * @param parent
	 * @return
	 */
	public DefaultMutableTreeNode generateTree(MyNode root,
			DefaultMutableTreeNode parent) {
		if (root.child.size() != 0 && !root.nodeName.equals("noUse")) {
			DefaultMutableTreeNode rootNode=null;
			if(root.nodeName.equals("Data_structure")){
				rootNode = new DefaultMutableTreeNode(
						"Data_type");
			}
			else
				rootNode = new DefaultMutableTreeNode(
						root.nodeName);
			hmNode.put(root.nodeName, root);// 保存结点名和结点的映射
			parent.add(rootNode);
			for (int i = 0; i < root.child.size(); i++) {
				generateTree(root.child.elementAt(i), rootNode);
			}
		} else if (root.child.size() == 0 && !root.nodeName.equals("noUse")) {
			DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
					root.nodeName);
			parent.add(rootNode);
			hmNode.put(root.nodeName, root);// 保存结点名和结点的映射
		}
		return parent;
	}

	/**
	 * 删除指定的边
	 * 
	 * @param vEdge
	 */
	public void delEdge(Vector<String[]> vEdge) {
		for (String nodeName[] : vEdge) {
			String linkName = nodeName[0] + "->" + nodeName[1];
			Link l = (Link) clusterBox.getElementByName(linkName);
			if (l != null) {
				clusterBox.removeElement(l);
				this.updateUI();
			}
		}
	}

	/**
	 * 删除指定的节点
	 * 
	 * @param vNode
	 */
	public void delNode(Vector<String> vNode) {
		for (String nodeName : vNode) {
			ResizableNode node = (ResizableNode) clusterBox
					.getElementByName(nodeName);
			if (node != null) {
				clusterBox.removeElement(node);
				this.updateUI();
			}
		}
	}

	/**
	 * 添加边,只添加父到子的边
	 * 
	 * @param edgeHs
	 *            B->A格式的
	 */
	public void addEdge(HashSet<String> edgeHs) {
		Iterator<String> it = edgeHs.iterator();
		while (it.hasNext()) {
			String edge = it.next();
			String nodeName[] = edge.split("->");
			ResizableNode from = (ResizableNode) clusterBox
					.getElementByName(nodeName[0]);
			ResizableNode to = (ResizableNode) clusterBox
					.getElementByName(nodeName[1]);
			Link l = new Link(from, to);
			l.setName(edge);
			l.setDisplayName("");
			if (!clusterBox.contains(l)) {
				l.putLinkColor(new Color(191, 188, 188)); // 设置线的颜色为部分透明的浅蓝
				l.putLinkWidth(1);
				l.putLinkOutlineWidth(0);
				l.putLinkAntialias(true);
				l.putLinkBundleExpand(true);
				l.putLinkBundleSize(0);
				l.setLayerID("edgeLayer");
				clusterBox.addElement(l);
				this.updateUI();
			}
		}
	}

	/**
	 * 把簇添加到network里面
	 * 
	 * @param vSuitableClusters
	 */
	public void addClusters(Vector<Vector<String[]>> vSuitableClusters) {
		int clusterNumber = vSuitableClusters.size();
		double angle = 360 * 1.0 / clusterNumber;
		for (int i = 0; i < clusterNumber; i++) {
			Vector<String[]> vCluster = vSuitableClusters.get(i);
			double max = Double.valueOf(vCluster.get(0)[1]);
			Color clusterColor = new Color((int) (255 * Math.random()),
					(int) (255 * Math.random()), (int) (255 * Math.random()));
			for (int j = 0; j < vCluster.size(); j++) {
				double size = Double.valueOf(vCluster.get(j)[1]) / max;
				double clusterR = 30 + 1.2 * vCluster.size();
				double r1, r2;// 外径和内径
				if (size == 1.0) {
					r1 = 0;
					r2 = 0;
				} else if (size > 0.7) {
					r1 = Math.max(clusterR / 10, 10);
					r2 = r1 + Math.max(clusterR / 10, 10);
				} else if (size > 0.4) {
					r1 = Math.max(2 * clusterR / 10, 20);
					r2 = r1 + Math.max(clusterR / 10, 10);
				} else {
					r1 = Math.max(3 * clusterR / 10, 30);
					r2 = clusterR;
				}
				int rTemp = (int) (r1 + (r2 - r1) * Math.random());
				double angleTemp = 360 * Math.random();
				int xTemp = (int) (centerX + r
						* Math.cos(Math.PI * angle * i / 180));
				int yTemp = (int) (centerY - r
						* Math.sin(Math.PI * angle * i / 180));
				while (!addNode(xTemp, yTemp, rTemp, angleTemp,
						vCluster.get(j)[0], size, clusterColor)) {
					rTemp = (int) (r1 + (r2 - r1) * Math.random());
					angleTemp = 360 * Math.random();
					xTemp = (int) (centerX + r
							* Math.cos(Math.PI * angle * i / 180));
					yTemp = (int) (centerY - r
							* Math.sin(Math.PI * angle * i / 180));
				}
			}
		}
	}

	/**
	 * 
	 * @param centerX
	 * @param centerY
	 * @param r
	 * @param angle
	 * @param name
	 * @param size
	 */
	public boolean addNode(int centerX, int centerY, int r, double angle,
			String name, double size, Color c) {
		int x = (int) (centerX + r * Math.cos(Math.PI * angle / 180));
		int y = (int) (centerY - r * Math.sin(Math.PI * angle / 180));
		return addNode(x, y, name, size, c);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param name
	 * @param size
	 *            0-1的一个,设置大小和颜色
	 * @param Color
	 */
	public boolean addNode(int x, int y, String name, double size, Color c) {
		final int nodeSize = (int) (8 + 10 * size);
		ResizableNode node = new ResizableNode() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public int getHeight() {
				return nodeSize;
			}

			public int getWidth() {
				return nodeSize;
			}
		};
		node.setName(name);
		if (size < 0.7) {
			node.setToolTipText(name);
			node.setDisplayName("");
		}
		if (y < 5)
			y = (int) (5 * Math.random()) + 5;
		node.setLocation(x, y);
		node.putCustomDraw(true);
		// node.putCustomDrawFill3D(true);
		node.putCustomDrawShapeFactory(TWaverConst.SHAPE_CIRCLE);
		node.putLabelColor(new Color(0, 0, 0, 120)); // 设置名字的颜色有一定透明度
		node.putCustomDrawFillColor(c);
		for (ResizableNode nodeTemp : vNode) {
			Rectangle r1 = node.getBounds();
			Rectangle r2 = nodeTemp.getBounds();
			if (r1.intersects(r2))// 检测重叠
				return false;
		}
		node.setLayerID("nodeLayer");
		clusterBox.addElement(node);
		this.updateUI();
		vNode.add(node);
		return true;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
