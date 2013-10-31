package function.crawler;


public class TestWikiHistoryCrawler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Data_mining Data_structure Computer_network
		WikiHistoryCrawler_new whc=new WikiHistoryCrawler_new();
		String listFile="F:\\extractTest\\Data_structure\\process\\layer0.txt";
		String savePath="F:\\extractTest\\Data_structure\\history\\layer0";
		whc.removeProxy();
        whc.crawlPageByList(listFile, savePath, 10);
	}
}
