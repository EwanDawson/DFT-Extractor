package function.DTExtraction;

import java.util.HashMap;
import java.util.Iterator;

import function.base.ExcelBase;
import function.util.Arrange;
import function.util.Combination;

public class FeatureCombination extends ExcelBase{

	String fileName="F:\\FacetedTaxonomy\\Data_mining\\process\\layer1-select-history.xls";
	String featureName="editor";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FeatureCombination fc=new FeatureCombination();
		fc.run(fc);
	}

	@Override
	public void process() throws Exception {
		// TODO Auto-generated method stub
		combineFeature(featureName,2);
	}
	
	public void combineFeature(String featureName,int combineNumber){
		HashMap<String,Integer> hm=new HashMap<String,Integer>();
		for(int i=1;i<getRows(1);i++){
			String feature=getStringValue(1,featureName,i);
			String term=getStringValue(1,"term",i);
			System.out.println("term:"+term+"--------------");
			if(feature.length()!=0){
				String temp[]=feature.split(",");
				if(temp.length>=combineNumber){
					Combination comb = new Combination(); 
					comb.mn(temp,combineNumber);
					for (int j = 0; j < comb.getCombList().size(); j++) {
						String combineFeature=comb.getCombList().get(j);
			            System.out.println(combineFeature);
			            String[] list = comb.getCombList().get(j).split(","); 
			            Arrange ts = new Arrange(); 
			            ts.perm(list, 0, list.length-1); 
			            int k=0;
			            for (k = 0; k < ts.getArrangeList().size(); k++) {
			            	String arrangeFeature=ts.getArrangeList().get(k);
			            	if(hm.containsKey(arrangeFeature)){
			            		int number=hm.get(arrangeFeature)+1;
			            		hm.put(arrangeFeature, number);
			            		break;
			            	}
			            } 
			            if(k>=ts.getArrangeList().size())
			            	hm.put(combineFeature, 1);
			        }//end for
				}
			}
		}//end for
		Iterator<String> it=hm.keySet().iterator();
		int record=1;
		while(it.hasNext()){
			String combineFeature=it.next();
			int number=hm.get(combineFeature);
			if(number>=2){
				setStringValue(3,featureName,record,combineFeature);
				setIntegerValue(3,"frequency",record++,number);
			}
		}
	}

	@Override
	public void run(ExcelBase eb) {
		// TODO Auto-generated method stub
		eb.go(fileName);
	}

}
