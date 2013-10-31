package function.crawler;



public class TestWebCrawler_CN {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Data_mining Data_structure Computer_network
		String listFile="F:\\FacetedTaxonomy\\Computer_network\\process\\layer2.txt";
		String savePath="F:\\FacetedTaxonomy\\Computer_network\\html\\layer2";
		WebCrawler wc=new WebCrawler();
		wc.crawlPageByList(listFile,savePath, 10);
	}
}
