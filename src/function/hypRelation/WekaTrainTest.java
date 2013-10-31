package function.hypRelation;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import function.util.SetUtil;

public class WekaTrainTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String trainFile="D:\\Program Files (x86)\\Weka-3-7\\data\\contact-lenses.arff";
		String testFile="D:\\Program Files (x86)\\Weka-3-7\\data\\contact-lenses.arff";
		String methods="weka.classifiers.trees.RandomForest";
		new WekaTrainTest().trainTest(trainFile,testFile,methods);

	}

	public HashMap<Integer,String> trainTest(String trainFile, String testFile, String method) {
		HashMap<Integer,String> hm=new HashMap<Integer,String>();//id-predictvalue
		Instances TrainIns = null;
		Instances TestIns = null;
		Classifier cfs = null;
		try {
			// (1)读入训练样本，csv格式
			File trainF = new File(trainFile);
			CSVLoader trainLoader = new CSVLoader();
			trainLoader.setFile(trainF);
			TrainIns = trainLoader.getDataSet();
			// 读入测试样本，csv格式
			File testF = new File(testFile);
			CSVLoader testLoader = new CSVLoader();
			testLoader.setFile(testF);
			TestIns = testLoader.getDataSet();

			// 在使用样本之前一定要首先设置instances的classIndex，否则在使用instances对象是会抛出异常
			TrainIns.setClassIndex(TrainIns.numAttributes() - 1);
			TestIns.setClassIndex(TestIns.numAttributes() - 1);

			// 初始化分类器 ，请将特定分类器的class名称放入forName函数
			cfs = (Classifier) Class.forName(method).newInstance();

			// (2)使用训练样本训练分类器
			cfs.buildClassifier(TrainIns);

			// (3)使用测试样本测试分类器的学习效果
			Instance testInst;

			//构建测试集类标签的映射，必须是trainFile
			HashMap<Double,String> hmClass=buildClassMap(trainFile);
			// Evaluation即它是用于检测分类模型的类
			Evaluation testingEvaluation = new Evaluation(TestIns);
			int length = TestIns.numInstances();
			for (int i = 0; i < length; i++) {
				testInst = TestIns.instance(i);
				// 通过这个方法来用每个测试样本测试分类器的效果
				testingEvaluation.evaluateModelOnceAndRecordPrediction(cfs,
						testInst);
				int id=(int)testInst.value(testInst.attribute(0));
				String classLabel=testInst.stringValue(testInst.classIndex());//按照顺序，所以没必要返回
				String predictClassLabel=hmClass.get(cfs.classifyInstance(testInst));
				System.out.println(id+","+classLabel+","+predictClassLabel);
				hm.put(id, predictClassLabel);
			}

			// (4)打印分类结果 在这里我们打印了分类器的正确率 其它的一些信息我们可以通过Evaluation对象的其它方法得到
			System.out
					.println("分类器的正确率：" + (1 - testingEvaluation.errorRate()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hm;
	}
	
	/**
	 * 从已知文件中构建类标签的映射
	 * @param file
	 * @return
	 */
	public HashMap<Double,String> buildClassMap(String file){
		Vector<String> v=SetUtil.readSetFromFile(file);
		HashMap<Double,String> hmResult=new HashMap<Double, String>();
		HashMap<String,Double> hm=new HashMap<String, Double>();
		double classNumber=0.0;
		for(int i=1;i<v.size();i++){
			String s=v.get(i);
			String att[]=s.split(",");
			String classLabel=att[att.length-1];
			if(!hm.containsKey(classLabel)){
				hm.put(classLabel, classNumber);
				hmResult.put(classNumber, classLabel);
				classNumber+=1.0;
			}
		}
		return hmResult;
	}

}
