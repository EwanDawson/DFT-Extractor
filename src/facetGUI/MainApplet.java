package facetGUI;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.util.Properties;

import javax.swing.JApplet;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;


public class MainApplet extends JApplet {

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
		this.setSize(1020, 780);
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
		DTExtractionPanel jpDTExtraction = new DTExtractionPanel();// 领域术语抽取面板
		HypRelationPanel jpHyponymy = new HypRelationPanel();// 上下位关系识别面板
		FacetedTreePanel jpTaxonomy = new FacetedTreePanel();// 分类体系构建面板
		funPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		// 功能面板布局
		c.add(funPane, BorderLayout.CENTER);
		funPane.addTab("Focused Crawling and Filtering", jpDTExtraction);
		funPane.addTab("Hyponym Relation Extraction", jpHyponymy);
		funPane.addTab("Faceted Taxonomy Generation", jpTaxonomy);
	}
}
