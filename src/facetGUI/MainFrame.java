package facetGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.io.File;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = -4209263941008740114L;
	private String tempPath="";
	private String tempDN="";
	public MainFrame(){
		Container c = this.getContentPane();
		// frame设置
		this.setVisible(true);
		this.setLocation(200, 10);
		this.setSize(1030, 850);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("DFT-Extractor: Extracting Domain-specific Faceted Taxonomies from Wikipedia");
		c.setBackground(Color.WHITE);
		try {
			Properties props = new Properties();
	        props.put("logoString", "my company");
	        
	        com.jtattoo.plaf.mcwin.McWinLookAndFeel.setCurrentTheme(props);
			//com.jtattoo.plaf.mcwin.McWinLookAndFeel.setTheme("Pink", "", "my company");
	        // Select the Look and Feel
	        UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");

	        // Start the application
	    }
	    catch (Exception ex) {
	        ex.printStackTrace();	        
	    }
		
		// 功能面板设置
		JTabbedPane funPane = new JTabbedPane();// 功能面板
		funPane.setBackground(new Color(67,142,228));
		funPane.setForeground(new Color(240,245,247));
		final DTExtractionPanel jpDTExtraction = new DTExtractionPanel();// 领域术语抽取面板
		final HypRelationPanel jpHyponymy = new HypRelationPanel();// 上下位关系识别面板
		final FacetedTreePanel_new jpTaxonomy = new FacetedTreePanel_new();// 分类体系构建面板
		funPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		// 功能面板布局
		c.add(funPane, BorderLayout.CENTER);
		funPane.addTab("Focused Crawling and Filtering", jpDTExtraction);
		funPane.addTab("Hyponym Relation Extraction", jpHyponymy);
		funPane.addTab("Faceted Taxonomy Generation", jpTaxonomy);
		
		//路径设置事件
		funPane.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				String domainName=jpDTExtraction.getDomainName();
				String savePath=jpDTExtraction.getSavePath();
				if(!tempPath.equals(savePath)||!tempDN.equals(domainName)){
					tempPath=savePath;
					tempDN=domainName;
					String htmlPath=savePath+"\\hyperExtraction\\"+domainName+"\\html";
					jpHyponymy.setHtmlDirPath(htmlPath);
					String relationPath=savePath+"\\facetedTree\\"+domainName+"\\relation.csv";
					jpTaxonomy.setRelationFilePath(relationPath);
				}//发生变化才改变后两个面板的输入框内容
				else{
					File fHtml=new File(jpHyponymy.getHtmlDirPath());
					String hypoDomainName=fHtml.getParentFile().getName();//第二个面板的领域名称
					String hypoSavePath=fHtml.getParentFile().getParentFile().getParentFile().getAbsolutePath();//第二个面板的保存路径
					String relationPath=hypoSavePath+"\\facetedTree\\"+hypoDomainName+"\\relation.csv";
					jpTaxonomy.setRelationFilePath(relationPath);
				}//不发生改变只根据第二个面板的html路径改变第三个路径
			}
			
		});
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MainFrame();
	}

}
