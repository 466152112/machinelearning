/**
 * 
 */
package preprocess._1014;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import preprocess.getFollowGraphFromTxt;
import bean.AvlTree;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：statisticsIndegreeandOutdegree   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年10月14日 下午4:19:37   
 * @modifier：zhouge   
 * @modified_time：2014年10月14日 下午4:19:37   
 * @modified_note：   
 * @version    
 *    
 */
public class statisticsInFollowGraph {
	
	public static void main(String[] dk) {
		//String path = "/media/pc/new2/data/new/followGraph/";
		 String path = "J:/workspace/weiboNew/data/topic/";
		//String followGraph=path+"followGraph.txt";
		String userFolloweeNumber=path+"userFolloweeNumber.txt";
		String userFolloweepro=path+"userFolloweepro.txt";
		
		statisticsInFollowGraph sIndegreeandOutdegree=new statisticsInFollowGraph();
		//sIndegreeandOutdegree.statisticsInfollowee(followGraph, userFolloweeNumber);
		sIndegreeandOutdegree.calProbablity(userFolloweeNumber, userFolloweepro, 1);
	}

	/**
	 * @param resourcefile
	 * @param userFolloweeNumber
	 *@create_time：2014年10月14日下午4:26:28
	 *@modifie_time：2014年10月14日 下午4:26:28
	  
	 */
	public void statisticsInfollowee(String resourcefile ,String userFolloweeNumber){
		
		try(BufferedReader bufferedReader = new BufferedReader(new FileReader(
				resourcefile));
				BufferedWriter Writer= new BufferedWriter(new FileWriter(new File(userFolloweeNumber), true));
				) {
			String tempLine;
			long positive=0L;
			StringBuffer result=new StringBuffer();
			while ((tempLine = bufferedReader.readLine()) != null) {
				String[] split = tempLine.split("\t");
				
					long follower=Long.valueOf(split[0]);
					int length=split.length-1;
					Writer.write(follower+"\t"+length);
					Writer.newLine();
			}
			Writer.flush();
			Writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * @param resourcefile
	 * @param userFolloweeNumber
	 *@create_time：2014年10月14日下午4:26:28
	 *@modifie_time：2014年10月14日 下午4:26:28
	  
	 */
	public void calProbablity(String resourcefile ,String resultFile,int interval){
		
		try(BufferedReader bufferedReader = new BufferedReader(new FileReader(
				resourcefile));
				BufferedWriter Writer= new BufferedWriter(new FileWriter(new File(resultFile), true));
				) {
			String tempLine;
			long positive=0L;
			StringBuffer result=new StringBuffer();
			int bigNumber=Integer.MIN_VALUE;
			int smallNumber=Integer.MAX_VALUE;
			List<Integer> resourceList=new ArrayList<>();
			while ((tempLine = bufferedReader.readLine()) != null) {
				String[] split = tempLine.split("\t");
				int number=Integer.valueOf(split[1]);
				if (number>bigNumber) {
					bigNumber=number;
				}
				if (number<smallNumber) {
					smallNumber=number;
				}
				resourceList.add(number);
			}
			
			int numberOfinterval=(int) Math.ceil(1.0*(bigNumber-smallNumber)/interval);
			int[] intervalList=new int[numberOfinterval+2];
			for (int number : resourceList) {
				int index=(number-smallNumber)/interval;
				intervalList[index]++;
			}
			for (int i = 0; i < intervalList.length; i++) {
				double temp=1.0*intervalList[i]/resourceList.size();
				Writer.write((i+1)*interval+"\t"+temp);
				Writer.newLine();
			}
			Writer.flush();
			Writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
