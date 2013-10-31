package function.linkAnalysis.redirectProcess;

public class RedirectDetectMain {

	String htmlDir="";
	boolean dirTag=false;//网页是否在目录htmlDir中获取
	public RedirectDetectMain(String htmlDir,boolean dirTag) {
		// TODO Auto-generated constructor stub
		this.htmlDir=htmlDir;
		this.dirTag=dirTag;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//test_2_4_tree();
	}
	public String getRedirectTerm(String term){
		RedirectDetect rd=new RedirectDetect();
		String[] newTerm=new String[1];
		/*在Terminology章节中。
		*/
		boolean flag;
		if(!dirTag)
			flag=rd.DetectFromWeb(term, newTerm);
		else
			flag=rd.DetectFromDir(htmlDir,term, newTerm);
		newTerm[0].replace("<i>", "");
		newTerm[0].replace("<\\i>", "");
		if(newTerm[0].contains("/")||newTerm[0].contains("\\")||newTerm[0].contains("?")){
			newTerm[0]=term;
			flag=false;
		}
		if(flag){
			System.err.println("error!");
			System.out.println("redirect: "+term+"->"+newTerm[0]);
		}else{
			System.out.println("nodirect: "+term+"="+newTerm[0]);
		}
		return newTerm[0];
	}
	
	public void test_2_4_tree(){
		RedirectDetect rd=new RedirectDetect();
		String term;
		String[] newTerm=new String[1];
		
		/*在Terminology章节中。
		*/
		term="Regularization_(machine_learning)";
		boolean flag;
		if(!dirTag)
			flag=rd.DetectFromWeb(term, newTerm);
		else
			flag=rd.DetectFromDir(htmlDir,term, newTerm);
		if(flag){
			System.err.println("error!");
			System.out.println("redirect: "+term+"->"+newTerm[0]);
		}else{
			System.out.println("nodirect: "+term+"="+newTerm[0]);
		}		
	}
	public static void test(){
		RedirectDetect rd=new RedirectDetect();
		String term;
		String[] newTerm=new String[1];
		
		/*在Terminology章节中。
		*/
		term="Leaf_node";
		boolean flag=rd.DetectFromWeb(term, newTerm);
		if(flag){
			System.err.println("error!");
			System.out.println("redirect: "+term+"->"+newTerm[0]);
		}else{
			System.out.println("nodirect: "+term+"="+newTerm[0]);
		}		
	}
	
	
	public void test2(){
		RedirectDetect rd=new RedirectDetect();
		String term;
		String[] newTerm=new String[1];
		
		/*在Terminology章节中。
		*/
		term="Graph_color";
		boolean flag;
		if(!dirTag)
			flag=rd.DetectFromWeb(term, newTerm);
		else
			flag=rd.DetectFromDir(htmlDir,term, newTerm);
		if(flag){
			System.err.println("error!");
			System.out.println("redirect: "+term+"->"+newTerm[0]);
		}else{
			System.out.println("nodirect: "+term+"="+newTerm[0]);
		}
			
		
	}
public void testReferTo(){
		RedirectDetect rd=new RedirectDetect();
		String term;
		String[] newTerm=new String[1];
		
		/*在Terminology章节中。
		*/
		term="Cage";
		boolean flag;
		if(!dirTag)
			flag=rd.DetectFromWeb(term, newTerm);
		else
			flag=rd.DetectFromDir(htmlDir,term, newTerm);
		if(flag){
			System.err.println("error!");
			System.out.println("redirect: "+term+"->"+newTerm[0]);
		}else{
			System.out.println("nodirect: "+term+"="+newTerm[0]);
		}
			
		
	}
}
