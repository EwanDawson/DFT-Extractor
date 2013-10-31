package facetGUI;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.util.Properties;

import javax.swing.JApplet;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;







public class MainApplet_old extends JApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param args
	 */
	
	public void init() {
		// 初始化一个容器
		Container c = this.getContentPane();
		// frame设置
		setVisible(true);
		this.setLocation(300, 200);
		this.setSize(1020, 650);
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
		CrawlerPanel jpCrawler = new CrawlerPanel();// 网络爬虫面板
		Html2TxtPanel jpHtml2Txt = new Html2TxtPanel();// HTML正文提取面板
		LinkFeaturePanel jpLinkFeature = new LinkFeaturePanel();// 链接特征分析面板
		TextFeaturePanel jpTextFeature = new TextFeaturePanel();// 文本特征分析面板
		NetFeaturePanel jpNetFeature = new NetFeaturePanel();// 网络特征分析面板
		DTExtractionPanel jpDTExtraction = new DTExtractionPanel();// 领域术语抽取面板
		HypRelationPanel_old jpHyponymy = new HypRelationPanel_old();// 上下位关系识别面板
		JPanel jpFacetedTerm = new JPanel();// 分面术语抽取面板
		FacetedTreePanel jpTaxonomy = new FacetedTreePanel();// 分类体系构建面板
		funPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		// 功能面板布局
		c.add(funPane, BorderLayout.CENTER);
		funPane.addTab("分类体系构建", jpTaxonomy);
		funPane.addTab("网络爬虫", jpCrawler);
		funPane.addTab("HTML正文提取", jpHtml2Txt);
		funPane.addTab("链接特征分析", jpLinkFeature);
		funPane.addTab("文本特征分析", jpTextFeature);
		funPane.addTab("网络特征分析", jpNetFeature);
		funPane.addTab("领域术语抽取", jpDTExtraction);
		funPane.addTab("上下位关系识别", jpHyponymy);
		funPane.addTab("分面术语抽取", jpFacetedTerm);
	}
}
