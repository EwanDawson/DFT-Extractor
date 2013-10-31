package facetGUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import twaver.Link;
import twaver.ResizableNode;
import twaver.TDataBox;
import twaver.TWaverConst;
import twaver.network.TNetwork;
import function.hypRelation.HypRelationExtractor_old;
import function.util.SetUtil;

public class HypRelationPanel_old extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4034136806675760042L;
	private JLabel htmldirLabel = new JLabel("领域HTML文件夹：");
	private JTextField htmldirTf = new JTextField();
	private JButton htmldirChooseBtn = new JButton("浏览...");
	private JButton htmldirBtn = new JButton("一键构建");
	private JLabel termRelationLabel = new JLabel(" 术语关系文件：");
	private JTextField termRelationTf = new JTextField();
	private JButton termRelationChooseBtn = new JButton("浏览...");
	private JButton termRelationBtn = new JButton("拓扑展示");
	private TDataBox box = new TDataBox();
	private TNetwork network = new TNetwork(box);
	private JScrollPane jsp = new JScrollPane(network);
	
	private JPanel jp = new JPanel();

	/**
	 * @param args
	 */
	
	public HypRelationPanel_old() {
		setLayout(null);
		this.setBackground(Color.white);
		
		this.add(jp);
		jp.setLayout(null);
		jp.setBackground(new Color(230,239,248));
		jp.setBounds(0, 10, 1000, 150);
		jsp.setBounds(0, 180, 1000, 420);
		
		//领域上下位构建布局设置
		jp.add(htmldirLabel);
		jp.add(htmldirTf);
		jp.add(htmldirChooseBtn);
		jp.add(htmldirBtn);
		htmldirLabel.setBounds(45, 25, 110, 25);
		htmldirTf.setBounds(145, 25, 510, 25);
		htmldirChooseBtn.setBounds(665, 25, 60, 25);
		htmldirBtn.setBounds(740, 25, 85, 25);
		
		htmldirChooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser file = new JFileChooser("f:/");
				file.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 设置选择文件夹
				int result = file.showOpenDialog(null);
				// JFileChooser.APPROVE_OPTION是0，代表已经选择了文件夹
				if (result == JFileChooser.APPROVE_OPTION) {
					// 获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
					String path = file.getSelectedFile().getAbsolutePath();
					htmldirTf.setText(path);
				}
			}
		});
		
		htmldirBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final String htmlDir = htmldirTf.getText(); //领域名称
				if (htmlDir.length() == 0) {
					JOptionPane.showMessageDialog(HypRelationPanel_old.this,
							"请选择领域HTML文件夹！");
				} else {
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							HypRelationExtractor_old hre = new HypRelationExtractor_old();
							hre.extractHypRelation(htmlDir);
							//显示结果
							String relationPath=htmlDir+"-hypRelation/relation.csv";
							showGraph(relationPath);
						}
					});
					t.start();
				}
			}
		});
		
		//HTML文件上下位构建布局设置
		jp.add(termRelationLabel);
		jp.add(termRelationTf);
		jp.add(termRelationChooseBtn);
		jp.add(termRelationBtn);
		
		termRelationLabel.setBounds(50, 70, 110, 25);
		termRelationTf.setBounds(145, 70, 510, 25);
		termRelationChooseBtn.setBounds(665, 70, 60, 25);
		termRelationBtn.setBounds(740, 70, 85, 25);
		
		termRelationChooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser file = new JFileChooser("f:/");
				int result = file.showOpenDialog(null);
				// JFileChooser.APPROVE_OPTION是0，代表已经选择了文件夹
				if (result == JFileChooser.APPROVE_OPTION) {
					// 获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
					String path = file.getSelectedFile().getAbsolutePath();
					termRelationTf.setText(path);
				}
			}
		});
		
		termRelationBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final String relationPath = termRelationTf.getText();   //HTML文件所在路径
				if (relationPath.length() == 0) {
					JOptionPane.showMessageDialog(HypRelationPanel_old.this,
							"请选择术语关系文件路径！");
				} else {
					//显示结果
					showGraph(relationPath);
				}
			}
		});
		
		//拓扑结果展示布局设置
		this.add(jsp);
		
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	
	}
	
	/**
	 * 根据关系文件显示拓扑图
	 * @param relationPath
	 */
	public void showGraph(String relationPath){
		box.clear();
		final Vector<String> vRelation = SetUtil.readSetFromFile(relationPath);
		final HashSet<String> hs = new HashSet<String>();
		final HashSet<String> hsRelation = new HashSet<String>();
		final Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// 加入节点和边
				//统计节点个数
				for (int i = 1; i < vRelation.size(); i++) {
					String record[] = vRelation.get(i).split(",");
					hs.add(record[0]);
					hs.add(record[1]);
				}
				final int count=hs.size();
				hs.clear();
				//加入节点和边
				for (int i = 1; i < vRelation.size(); i++) {
					String record[] = vRelation.get(i).split(",");
					String relationStr="";
					if(record[2].equals("A is a B"))
						relationStr=record[1]+"->"+record[0];
					else if(record[2].equals("B is a A"))
						relationStr=record[0]+"->"+record[1];
					if(hsRelation.contains(relationStr)||relationStr.equals(""))
						continue;
					else{
						hsRelation.add(relationStr);
						if (!hs.contains(record[0])) {
							ResizableNode node = new ResizableNode(){
								/**
								 * 
								 */
								private static final long serialVersionUID = 1L;
								public int getHeight() {
							        return 10;
							    }
							    public int getWidth() {
							        return 10;
							    }
							};
							node.setName(record[0]);
							double angle=Math.PI*2/ count*hs.size();
							int x=500+(int)(500*Math.cos(angle));
							int y=500+(int)(500*Math.sin(angle));
							node.setLocation(x,y);
							node.putCustomDraw(true);
							node.putCustomDrawFill3D(true);
							node.putCustomDrawShapeFactory(TWaverConst.SHAPE_CIRCLE);
							node.putLabelColor(new Color(0,0,0,120)); //设置名字的颜色有一定透明度
							box.addElement(node);
							hs.add(record[0]);
						}
						if (!hs.contains(record[1])) {
							ResizableNode node = new ResizableNode(){
								/**
								 * 
								 */
								private static final long serialVersionUID = 1L;
								public int getHeight() {
							        return 10;
							    }
							    public int getWidth() {
							        return 10;
							    }
							};
							node.setName(record[1]);
							double angle=Math.PI*2/ count*hs.size();
							int x=500+(int)(500*Math.cos(angle));
							int y=500+(int)(500*Math.sin(angle));
							node.setLocation(x,y);
							node.putCustomDraw(true);
							node.putCustomDrawFill3D(true);
							node.putCustomDrawShapeFactory(TWaverConst.SHAPE_CIRCLE);
							node.putLabelColor(new Color(0,0,0,120));//设置名字的颜色有一定透明度
							box.addElement(node);
							hs.add(record[1]);
						}
						String nodeName[]=relationStr.split("->");
						ResizableNode from = (ResizableNode) box.getElementByName(nodeName[1]);
						ResizableNode to = (ResizableNode) box.getElementByName(nodeName[0]);
						Link l = new Link(from, to);
						l.putLinkFromArrow(true);
						l.putLinkColor(new Color(0,0,255,100)); //设置线的颜色为部分透明的浅蓝
						l.putLinkWidth(2);
						l.putLinkOutlineWidth(0);
						box.addElement(l);
					}
				}
				 //调整节点大小
				@SuppressWarnings("unchecked")
				List<ResizableNode> nodes = box.getElementsByType(ResizableNode.class); //获得所有节点
				for(ResizableNode node : nodes) {
					int degree = node.getAllLinks().size();
					int size = degree + 5;
					if(size > 30)
						size = 30;
					node.setSize(size, size);  //设置节点大小
					Color c;                   //节点颜色
					if(degree < 3)
						c = new Color(0,240,80);   //绿
					else if(degree < 8)
						c = new Color(180,240,80); //浅绿
					else if(degree < 20)
						c = new Color(250,250,80); //黄
					else if(degree < 30)
						c = new Color(240,180,80); //黄红
					else 
						c = new Color(240,120,80); //浅红
					node.putCustomDrawFill(true);
					node.putCustomDrawFillColor(c);//设置节点颜色
				}
				
				//network.doLayout(TWaverConst.LAYOUT_HIERARCHIC, true);
				network.doLayout(TWaverConst.LAYOUT_SYMMETRIC, true);
			}
		});
		t.start();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
