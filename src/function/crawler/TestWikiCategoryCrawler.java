package function.crawler;


public class TestWikiCategoryCrawler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String catTermPath="F:\\Data\\testData\\DM-cat.txt";
		WikiCategoryCrawler wcc=new WikiCategoryCrawler();
		wcc.buildCategoryTree(catTermPath,4);//维基目录爬取功能测试
	}
}
