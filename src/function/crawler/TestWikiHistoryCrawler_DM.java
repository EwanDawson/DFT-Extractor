package function.crawler;


public class TestWikiHistoryCrawler_DM {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Data_mining Data_structure Computer_network
		WikiHistoryCrawler_new whc=new WikiHistoryCrawler_new();
		String listFile="F:\\FacetedTaxonomy\\Data_mining\\process\\layer2.txt";
		String savePath="F:\\FacetedTaxonomy\\Data_mining\\history\\layer2";
        whc.crawlPageByList(listFile, savePath, 10);
	}
}
