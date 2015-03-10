package model.link;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import util.Matrix.Matrix;


public class xiaoqiangkatz {
	static	String path="J:/workspace/twitter/data/twitter/";
	public static void main(String[]args) throws FileNotFoundException{
		ArrayList<Double> KatZAUC = new ArrayList<Double>();
		
		ArrayList<String> AllPreRatio = new ArrayList<String>();
		ArrayList<String> AllRecallRatio = new ArrayList<String>();
		
		String type = "1w";
		double alpha = 0.01;
		int size = 11016;
		if(type.equals("1w")){
			size = 11016;
		}
		xiaoqiangkatz tksd = new xiaoqiangkatz();
		int start=0;
		for(int km=start;km<1;km++){ 
		//System.out.println("iter:"+km);
		
		String testFile = path+"bprLr.fortest";
		//String idAndFeature = "E:/"+type+"/newdata/idAndFeature.txt";
	//	String idAndFeature = "/home/xiaoqiang/twitter/idAndFeature.txt";
		//String followTrainSet = "E:/"+type+"/finaldata/"+km+"/bprLr.base";
		String followTrainSet = path+"bprLr.base";
		ArrayList<String> testSet = tksd.getFileContent(testFile);
		HashMap<Integer,ArrayList<Integer>> followlist = tksd.getFollowList(followTrainSet);
		
		
		xiaoqiangkatz ka=  new xiaoqiangkatz();
		
		Matrix mat = new Matrix(size, size);
//		//构建矩阵
//		for(int i=0;i<size;i++){
//			for(int j=0;j<size;j++){
//				if(i==j){
//				mat.set(i, j, 1);
//				}
//				else{
//					mat.set(i, j, 0);
//				}
//			}
//		}//initial matrix
//		
//	//	KatzAUCMatrix ka=  new KatzAUCMatrix();
//		ArrayList<int[]> traingraph = ka.getTrainGraph(followTrainSet);
//		for(int i=0,len=traingraph.size();i<len;i++){
//			int[] pos = traingraph.get(i);
//			mat.set(pos[0], pos[1], -alpha*1);
//		}//construct matrix done
//		
//		ArrayList<String> rec = new ArrayList<String>();
//		for(int i=0;i<mat.rowCount();i++){
//			String temp = String.valueOf(mat.get(i, 0));
//			for(int j=1;j<mat.columnCount();j++){
//				temp = temp+"\t"+mat.get(i, j);
//			}
//			rec.add(temp);
//		}
//		
//		WriteDataIntoFile wdif = new WriteDataIntoFile();
//		wdif.writefile("/home/xiaoqiang/twitter/zhouge/"+km+"matrix.txt", rec);
//		mat = null;
//		rec = null;		


		String filepath = path+"katzinv.matrix";
		double[][] inv = ka.getInvMatrix(filepath,size);
		
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				mat.set(i, j, inv[i][j]);
			}
		}
		
		inv = null;
		
		for(int i=0;i<size;i++){
			mat.set(i, i, mat.get(i, i)-1);
		}
		
		ArrayList<Double> aucONES = new ArrayList<Double>();
		
		ArrayList<String> PreRatio = new ArrayList<String>();
		ArrayList<String> RecallRatio = new ArrayList<String>();
		
		//now have S,then catculate precision,recall and AUC
	//	int lenOfTest = testSet.size();
		for(int i=0,len=testSet.size();i<len;i++){
			String[] test = testSet.get(i).split("\t");
		    int curUserFrom = Integer.parseInt(test[0]);
		    HashSet<Integer> edgesOfCurUserFrom = new HashSet<Integer>();
		     for(int k=1;k<test.length;k++){
		    	 edgesOfCurUserFrom.add(Integer.parseInt(test[k]));
		     }
		     
		     ArrayList<Integer> baseset =  followlist.get(curUserFrom);
		     for(int k=0;k<baseset.size();k++){
		    	 edgesOfCurUserFrom.add(baseset.get(k));
		     }
		     
		    ArrayList<Integer> remainAll = new ArrayList<Integer>();
		    for(int m=0;m<size;m++){
		    	if(!edgesOfCurUserFrom.contains(m)&&m!=curUserFrom){
		    		remainAll.add(m);
		    	}
		    }
		    edgesOfCurUserFrom = null;
			
			//now have one person's testSet and RemainSet
		    
		    ArrayList<Double> testKatz = new ArrayList<Double>();
		    ArrayList<Double> remainKatz = new ArrayList<Double>();
		    HashMap<Integer,Double> MapOfTksdValue = new HashMap<Integer,Double>();
		    
		    HashSet<Integer> testCollection = new HashSet<Integer>();
		    
		    for(int j=1;j<test.length;j++){
		    	testKatz.add(mat.get(curUserFrom, Integer.parseInt(test[j])));
		    	testCollection.add(Integer.parseInt(test[j]));
		    	MapOfTksdValue.put(Integer.parseInt(test[j]), mat.get(curUserFrom, Integer.parseInt(test[j])));
		    }
		    
		    for(int j=0,len1=remainAll.size();j<len1;j++){
		    	remainKatz.add(mat.get(curUserFrom, remainAll.get(j)));
		    	MapOfTksdValue.put(remainAll.get(j), mat.get(curUserFrom, remainAll.get(j)));
		    }
		    
			String preStringOfRatio = "";
			String recallStringOfRatio = "";
			
			int[] reclistLength = {1,2,5,10,15,20,30,40,50,60,70,80,90,100};
			for(int t=0;t<reclistLength.length;t++){
				int length = reclistLength[t];
				ArrayList<Integer> reclistRatio = ka.getRecList(MapOfTksdValue);
				double[] preAndRecallOfRatio = ka.getPrecision(reclistRatio, length, testCollection);
				preStringOfRatio = preStringOfRatio+"\t"+preAndRecallOfRatio[0];
				recallStringOfRatio = recallStringOfRatio+"\t"+preAndRecallOfRatio[1];
			}
			
			PreRatio.add(preStringOfRatio);
			RecallRatio.add(recallStringOfRatio);
		    
		    double auc = tksd.computeAuc(remainKatz, testKatz);
		    aucONES.add(auc);
			
		}//end all auc
		
		AllPreRatio.add(tksd.getAverageValueOfDiffLength(PreRatio));
		AllRecallRatio.add(tksd.getAverageValueOfDiffLength(RecallRatio));
		double aveAuc = tksd.getAvgAuc(aucONES);
		
		System.out.println(aveAuc);
		KatZAUC.add(aveAuc);
		 }
		
//		WriteDataIntoFile wdif = new WriteDataIntoFile();
//		wdif.writeDoublefile("/home/xiaoqiang/twitter/zhouge/0/KatzAuc.txt", KatZAUC);
//		
//		WriteDataIntoFile wdif1 = new WriteDataIntoFile();
//		wdif1.writefile("/home/xiaoqiang/twitter/zhouge/0/"+start+"_AllPreRatio.txt", AllPreRatio);
//		
//		WriteDataIntoFile wdif2 = new WriteDataIntoFile();
//		wdif2.writefile("/home/xiaoqiang/twitter/zhouge/0/"+start+"_AllRecallRatio.txt", AllRecallRatio);
	}
	
	public String getAverageValueOfDiffLength(ArrayList<String> PreRatio){
		
		//1,2,5,10,15,20,30,40,50,60,70,80,90,100
		ArrayList<Double> val1 = new ArrayList<Double>();
		ArrayList<Double> val2 = new ArrayList<Double>();
		ArrayList<Double> val5 = new ArrayList<Double>();
		ArrayList<Double> val10 = new ArrayList<Double>();
		ArrayList<Double> val15 = new ArrayList<Double>();
		ArrayList<Double> val20 = new ArrayList<Double>();
		ArrayList<Double> val30 = new ArrayList<Double>();
		ArrayList<Double> val40 = new ArrayList<Double>();
		ArrayList<Double> val50 = new ArrayList<Double>();
		ArrayList<Double> val60 = new ArrayList<Double>();
		ArrayList<Double> val70 = new ArrayList<Double>();
		ArrayList<Double> val80 = new ArrayList<Double>();
		ArrayList<Double> val90 = new ArrayList<Double>();
		ArrayList<Double> val100 = new ArrayList<Double>();
	//	System.out.println("PreRatio.size()---"+PreRatio.size());
		for(int i=0,len=PreRatio.size();i<len;i++){
	//		System.out.println(PreRatio.get(i));
			String[] temp = PreRatio.get(i).split("\t");
			val1.add(Double.parseDouble(temp[1]));
			val2.add(Double.parseDouble(temp[2]));
			val5.add(Double.parseDouble(temp[3]));
			val10.add(Double.parseDouble(temp[4]));
			val15.add(Double.parseDouble(temp[5]));
			val20.add(Double.parseDouble(temp[6]));
			val30.add(Double.parseDouble(temp[7]));
			val40.add(Double.parseDouble(temp[8]));
			val50.add(Double.parseDouble(temp[9]));
			val60.add(Double.parseDouble(temp[10]));
			val70.add(Double.parseDouble(temp[11]));
			val80.add(Double.parseDouble(temp[12]));
			val90.add(Double.parseDouble(temp[13]));
			val100.add(Double.parseDouble(temp[14]));
		}
		
		String mean1 = getMeanValue(val1)+"\t"+getMeanValue(val2)+"\t"+getMeanValue(val5)+"\t"+getMeanValue(val10)+"\t"+getMeanValue(val15)+"\t"+getMeanValue(val20)+"\t"+getMeanValue(val30)+"\t"+getMeanValue(val40)+"\t"+getMeanValue(val50)+"\t"+getMeanValue(val60)+"\t"+getMeanValue(val70)+"\t"+getMeanValue(val80)+"\t"+getMeanValue(val90)+"\t"+getMeanValue(val100);
		
		return mean1;
		
	}
	
	public double getMeanValue(ArrayList<Double> val){
		double sum = 0;
		for(int i=0;i<val.size();i++){
			sum = sum+val.get(i);
		}
		
		double mean = sum/val.size();
		
		return mean;
	}
	
	public double[][] getInvMatrix(String filepath,int size) throws FileNotFoundException{
		double[][] temp = new double[size][size];
		int i = -1;
		FileReader f1 = new FileReader(filepath);
 		BufferedReader bf = new BufferedReader(f1);
 		try {
 			String t1 = bf.readLine();
 			
 			while (t1 != null) {
 				i++;
 				String[] tt = t1.split("\t");
 				for(int j=0;j<tt.length;j++){
 					temp[i][j]=Double.parseDouble(tt[j]);
 				}
 				
 				t1 = bf.readLine();
 			}
 			
 			bf.close();
 			f1.close();
 		}catch(IOException e){
 			e.printStackTrace();
 		}
 		
 		return temp;
	}
	
	public double getAvgAuc(ArrayList<Double> AUC){
		double avgAuc = 0;
		double sumAuc = 0;
		for(int i=0;i<AUC.size();i++){
			
			sumAuc = sumAuc+AUC.get(i);
		}
		
		avgAuc = sumAuc/AUC.size();
		
		return avgAuc;
	}
	
	 public double computeAuc(ArrayList<Double> allTksdValuesOfRemain,ArrayList<Double> allTksdValueOfTest){
			int n1 = 0; //big than
			int n2 = 0; //equal
			int n = allTksdValueOfTest.size()*allTksdValuesOfRemain.size();
			for(int i=0;i<allTksdValueOfTest.size();i++){
				double ratioOfTest = allTksdValueOfTest.get(i); 
				for(int ii=0;ii<allTksdValuesOfRemain.size();ii++){
					if(ratioOfTest>allTksdValuesOfRemain.get(ii)){
						n1++;
					}
					else if(ratioOfTest==allTksdValuesOfRemain.get(ii)){
						n2++;
					}
				}
			}
			
			double auc = (n1+0.5*n2)/n;
			
			return auc;
			
		}
	
	public ArrayList<int[]> getTrainGraph(String filepath) throws FileNotFoundException{
		ArrayList<int[]> edges = new ArrayList<int[]>();
		FileReader f2 = new FileReader(filepath);
 		BufferedReader bf2 = new BufferedReader(f2);
 		try {
 			String t2 = bf2.readLine();
 			
 			while (t2 != null) {
 				String[] temp = t2.split("\t");
 				int[] a = new int[2];
 				a[0] = Integer.parseInt(temp[0]);
 				a[1] = Integer.parseInt(temp[1]);
 				
 				edges.add(a);
 				
 				t2 = bf2.readLine();
 			}
 			
 			bf2.close();
 			f2.close();
 		}catch(IOException e){
 			e.printStackTrace();
 		}
 		
 		return edges;
 		
	}
	
	public ArrayList<String> getFileContent(String filepath) throws FileNotFoundException{
		ArrayList<String> FileContent = new ArrayList<String>();
		
		FileReader f1 = new FileReader(filepath);
 		BufferedReader bf = new BufferedReader(f1);
 		try {
 			String t1 = bf.readLine();
 			
 			while (t1 != null) {
 				FileContent.add(t1);
 				
 				t1 = bf.readLine();
 			}
 			
 			bf.close();
 			f1.close();
 		}catch(IOException e){
 			e.printStackTrace();
 		}
 		
 		return FileContent;
	}
	
	public HashMap<Integer,ArrayList<Integer>> getFollowList(String filepath) throws FileNotFoundException{
		HashMap<Integer,ArrayList<Integer>> followlist = new HashMap<Integer,ArrayList<Integer>>();
		
		FileReader f1 = new FileReader(filepath);
 		BufferedReader bf = new BufferedReader(f1);
 		try {
 			String t1 = bf.readLine();
 			
 			while (t1 != null) {
 				String[] temp = t1.split("\t");
 				int useridFrom = Integer.parseInt(temp[0]);
 			//	System.out.println("useridFrom---"+useridFrom);
 				int useridTo = Integer.parseInt(temp[1]);
 				if(followlist.containsKey(useridFrom)){
 					ArrayList<Integer> userids = followlist.get(useridFrom);
 					userids.add(useridTo);
 					followlist.put(useridFrom, userids);
 				}else{
 					ArrayList<Integer> userids = new ArrayList<Integer>();
 					userids.add(useridTo);
 					followlist.put(useridFrom, userids);
 				}
 				
 				t1 = bf.readLine();
 			}
 			
 			bf.close();
 			f1.close();
 		}catch(IOException e){
 			e.printStackTrace();
 		}
 		
 		return followlist;
	}
	
	 static class ByValueComparator implements Comparator<Integer> {
	        HashMap<Integer, Double> base_map;
	 
	        public ByValueComparator(HashMap<Integer, Double> base_map) {
	            this.base_map = base_map;
	        }
	 
	        public int compare(Integer arg0, Integer arg1) {
	            if (!base_map.containsKey(arg0) || !base_map.containsKey(arg1)) {
	                return 0;
	            }
	 
	            if (base_map.get(arg0) < base_map.get(arg1)) {
	                return 1;
	            } else if (base_map.get(arg0) == base_map.get(arg1)) {
	                return 0;
	            } else {
	                return -1;
	            }
	        }
	    }
	   
	   public ArrayList<Integer> getRecList(HashMap<Integer,Double> MapValue){
			 ArrayList<Integer> reclist = new ArrayList<Integer>();
			
			 ByValueComparatorDouble bvc = new ByValueComparatorDouble(MapValue);
	         
		     //第二种方法
		        List<Integer> keys = new ArrayList<Integer>(MapValue.keySet());
		        Collections.sort(keys, bvc);
		        for(Integer key1 : keys) {
		        	reclist.add(key1);
		           System.out.printf("%d -> %f\n", key1, MapValue.get(key1));
		        }
		        
		        return reclist;
		}
		
		   static class ByValueComparatorDouble implements Comparator<Integer> {
		        HashMap<Integer, Double> base_map;
		 
		        public ByValueComparatorDouble(HashMap<Integer, Double> base_map) {
		            this.base_map = base_map;
		        }
		 
		        public int compare(Integer arg0, Integer arg1) {
		            if (!base_map.containsKey(arg0) || !base_map.containsKey(arg1)) {
		                return 0;
		            }
		 
		            if (base_map.get(arg0) < base_map.get(arg1)) {
		                return 1;
		            } else if (base_map.get(arg0) == base_map.get(arg1)) {
		                return 0;
		            } else {
		                return -1;
		            }
		        }
		    }
		
			public double[] getPrecision(ArrayList<Integer> reclistRatio,int t,HashSet<Integer> testCollection){
				int count = 0;
				for(int i=0;i<t;i++){
					if(testCollection.contains(reclistRatio.get(i))){
						count++;
					}
				}
				
				double precision = count*1.0/t;
				double recall = count*1.0/testCollection.size();
				
				double[] values = new double[2];
				values[0] = precision;
				values[1] = recall;
				return values;
			}
			

	
	
	

}
