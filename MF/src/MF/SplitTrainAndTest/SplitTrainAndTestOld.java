/**
 * 
 */
package MF.SplitTrainAndTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;


/**   
*    
* @progject_name：BPR   
* @class_name：SplitTrainAndTestOld   
* @class_describe：   
* @creator：zhouge   
* @create_time：2014年7月22日 下午8:47:53   
* @modifier：zhouge   
* @modified_time：2014年7月22日 下午8:47:53   
* @modified_note：   
* @version    
*    
*/
public class SplitTrainAndTestOld {
	/**
	 * 
	 */
	int threadhold=0;
	double[][] trainSetRating;
	double[][] testSetRating;
	ArrayList<String> dataSource;
	HashSet<Integer> userIdhashSet;
	HashSet<Integer> itermIdHashSet;

	public SplitTrainAndTestOld(ArrayList<String> dataSource,double ratio){
		this.threadhold=(int) (dataSource.size()*ratio);
		System.out.println("split Train and Test from"+dataSource.size()+": the ratio is:"+ratio);
		this.dataSource=dataSource;
	}
	
	public double[][] getTrainSetRating(){
		if (trainSetRating==null) {
			userIdhashSet=new HashSet<>();
			itermIdHashSet=new HashSet<>();
			if (this.dataSource.size()<100001) {
				for(String oneLine:this.dataSource){
				String[] split=oneLine.split("\t");
				userIdhashSet.add(Integer.parseInt(split[0]));
				itermIdHashSet.add(Integer.parseInt(split[1]));
				}
			}else {
				for(String oneLine:this.dataSource){
					String[] split=oneLine.split("::");
					userIdhashSet.add(Integer.parseInt(split[0]));
					itermIdHashSet.add(Integer.parseInt(split[1]));
				}
			}
			
			Random random=new Random();
			random.setSeed(System.currentTimeMillis());
			HashSet<Integer> selectSet=new HashSet<>();
			int count=0;
			if (this.dataSource.size()<100001) {
				trainSetRating=new double[this.userIdhashSet.size()][this.itermIdHashSet.size()];
				testSetRating=new double[this.userIdhashSet.size()][this.itermIdHashSet.size()];
					while(count<=threadhold){
						int select=random.nextInt(this.dataSource.size());
						if (!selectSet.contains(select)) {
							String oneLine=this.dataSource.get(select);
							String[] split=oneLine.split("\t");
							int userid=Integer.parseInt(split[0])-1;
							int itermid=Integer.parseInt(split[1])-1;
							if (Double.isNaN(Double.parseDouble(split[2]))||(Double.parseDouble(split[2])<0)||Double.parseDouble(split[2])>5) {
								System.out.println();
							}
							this.trainSetRating[userid][itermid]=Double.parseDouble(split[2]);
							count++;
							selectSet.add(select);
						}
					}
					for (int i = 0; i < this.dataSource.size(); i++) {
						if (!selectSet.contains(i)) {
							String oneLine=this.dataSource.get(i);
							String[] split=oneLine.split("\t");
							int userid=Integer.parseInt(split[0])-1;
							int itermid=Integer.parseInt(split[1])-1;
							if (Double.isNaN(Double.parseDouble(split[2]))||(Double.parseDouble(split[2])<0)||Double.parseDouble(split[2])>5) {
								System.out.println();
							}
							this.testSetRating[userid][itermid]=Double.parseDouble(split[2]);
						}
					}
					
			}else {
				
				trainSetRating=new double[this.userIdhashSet.size()][3952];
				testSetRating=new double[this.userIdhashSet.size()][3952];
				while(count<=threadhold){
					int select=random.nextInt(this.dataSource.size());
					if (!selectSet.contains(select)) {
						String oneLine=this.dataSource.get(select);
						String[] split=oneLine.split("::");
						int userid=Integer.parseInt(split[0])-1;
						int itermid=Integer.parseInt(split[1])-1;
						if (Double.isNaN(Double.parseDouble(split[2]))||(Double.parseDouble(split[2])<0)||Double.parseDouble(split[2])>5) {
							System.out.println();
						}
						this.trainSetRating[userid][itermid]=Double.parseDouble(split[2]);
						count++;
						selectSet.add(select);
					}
				}
				for (int i = 0; i < this.dataSource.size(); i++) {
					if (!selectSet.contains(i)) {
						String oneLine=this.dataSource.get(i);
						String[] split=oneLine.split("::");
						int userid=Integer.parseInt(split[0])-1;
						int itermid=Integer.parseInt(split[1])-1;
						if (Double.isNaN(Double.parseDouble(split[2]))||(Double.parseDouble(split[2])<0)||Double.parseDouble(split[2])>5) {
							System.out.println();
						}
						this.testSetRating[userid][itermid]=Double.parseDouble(split[2]);
					}
				}
					
			}
			return trainSetRating;
		}else {
			return trainSetRating;
		}
		
	}
	public double[][] getTestSetRating(){
		if (testSetRating==null) {
			this.getTrainSetRating();
			return testSetRating;
		}else {
			return testSetRating;
		}
		
	}
}
