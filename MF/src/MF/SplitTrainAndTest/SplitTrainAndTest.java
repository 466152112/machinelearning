/**
 * 
 */
package MF.SplitTrainAndTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import MF.util.HashMapUtil;
import MF.util.ReadUtil;
import MF.util.WriteUtil;


/**   
*    
* @progject_name：BPR   
* @class_name：SplitTrainAndTest   
* @class_describe：   
* @creator：zhouge   
* @create_time：2014年7月22日 下午8:47:57   
* @modifier：zhouge   
* @modified_time：2014年7月22日 下午8:47:57   
* @modified_note：   
* @version    
*    
*/
public class SplitTrainAndTest<T extends Object> {
	private  List<T> userList=new ArrayList<T>();
	private  List<T> ItemList=new ArrayList<T>();
	private final String subsampleFileName;
	private final int limit;
		//get clearRatingByLimit
	private Map<T, List<T>> ratingMap=null;
		
		//get testRating
	private Map<T,List<T>> testRating=null;
		//get trainRating
	private Map<T,List<T>> trainRating=null;
	
	public SplitTrainAndTest(String subsampleFileName,int limit){
		this.subsampleFileName=subsampleFileName;
		this.limit=limit;
		this.ratingMap=clearRatingByLimit();
		this.testRating=getTestByRandomOneRatingFromEveryOne(ratingMap);
		this.trainRating=getTrainByMinusTestRaing(ratingMap, testRating);
	}
	
	/**
	 * @param limit to item and User
	 * @param FileName RatingFile
	 * @return clearRatingByMap
	 *@create_time：2014年7月22日下午3:28:04
	 *@modifie_time：2014年7月22日 下午3:28:04
	  
	 */
	private  Map<T, List<T>> clearRatingByLimit(){
		//get Rating from subsample file
		List<String> ratingList=readRatingFromFile(this.subsampleFileName);
		
		Map<T, List<T>> UserSetAndRatingNumber=new HashMap<>();
		Map<T, List<T>> ItemSetAndRatingNumber=new HashMap<>();
		
		//convert the arraylist Rating the two hashmap 
		for (String OneLine : ratingList) {
			String[] split=OneLine.split(",");
			
			T userId=(T) split[0];
			T itemId=(T) split[1];
			if (!UserSetAndRatingNumber.containsKey(userId)) {
				List<T> itemList=new ArrayList<>();
				itemList.add(itemId);
				UserSetAndRatingNumber.put(userId, itemList);
			}else {
				UserSetAndRatingNumber.get(userId).add(itemId);
			}
			if (!ItemSetAndRatingNumber.containsKey(itemId)) {
				List<T> userList=new ArrayList<>();
				userList.add(userId);
				ItemSetAndRatingNumber.put(itemId, userList);
			}else {
				 ItemSetAndRatingNumber.get(itemId).add(userId);
			}
		}
		
		
		HashMapUtil<T,T> hashMapUtil=new HashMapUtil<>();
		//the follow is clean user and Item by cycle
		//when flag is false,end the cycle and break;
		while(true){
			List<T> removeListOfItem=hashMapUtil.GetRemoveElementFromKeyByNumberLimit(ItemSetAndRatingNumber, limit);
			List<T> removeListOfUser=hashMapUtil.GetRemoveElementFromKeyByNumberLimit(UserSetAndRatingNumber, limit);
			System.out.println(ItemSetAndRatingNumber.size()+"\t"+UserSetAndRatingNumber.size());
			if (removeListOfItem.size()==0&&removeListOfUser.size()==0) {
				break;
			}else {
				UserSetAndRatingNumber=hashMapUtil.removeElementFromValueByList(UserSetAndRatingNumber, removeListOfItem);
				ItemSetAndRatingNumber=hashMapUtil.removeElementFromValueByList(ItemSetAndRatingNumber, removeListOfUser);
				UserSetAndRatingNumber=hashMapUtil.removeElementFromKey(UserSetAndRatingNumber, removeListOfUser);
				ItemSetAndRatingNumber=hashMapUtil.removeElementFromKey(ItemSetAndRatingNumber, removeListOfItem);
			
			}
		}

		Set<T> userSet=new HashSet<>();
		Iterator<T> userKeyList=UserSetAndRatingNumber.keySet().iterator();
		while(userKeyList.hasNext()){
			T keyT=userKeyList.next();
			if (!userSet.contains(keyT)) {
				userSet.add(keyT);
				this.userList.add(keyT);
			}
		}
		
		Set<T> itemSet=new HashSet<>();
		userKeyList=UserSetAndRatingNumber.keySet().iterator();
		while(userKeyList.hasNext()){
			T keyT=userKeyList.next();
			List<T> itemList=UserSetAndRatingNumber.get(keyT);
			for (T itemId : itemList) {
				if (!itemSet.contains(itemId)) {
					itemSet.add(itemId);
					this.ItemList.add(itemId);
				}
			}
			
		}
//		WriteUtil<T> writeUtil=new WriteUtil<>();
//		writeUtil.writeMap(UserSetAndRatingNumber, "weiborating2.txt");
		return UserSetAndRatingNumber;
	}
	/**
	 * @param fileName 
	 * @return rating ArrayList
	 *@create_time：2014年7月22日下午3:21:36
	 *@modifie_time：2014年7月22日 下午3:21:36
	 */
	private List<String> readRatingFromFile(String fileName){
		ReadUtil readUtil=new ReadUtil();
		return readUtil.readFileByLine(fileName);
	}
	
	/**
	 * @param Rating
	 * @return TestRating
	 *@create_time：2014年7月22日下午5:21:19
	 *@modifie_time：2014年7月22日 下午5:21:19
	 */
	private Map<T, List<T>> getTestByRandomOneRatingFromEveryOne(Map<T, List<T>> Rating){
		
		Map<T, List<T>> resultMap=new HashMap<>();
		
		Random random=new Random();
		random.setSeed(System.currentTimeMillis());
		
		Iterator<T> KeyList=Rating.keySet().iterator();
		while(KeyList.hasNext()){
			T keyT=KeyList.next();
			List<T> RatingItem=Rating.get(keyT);
			int randomIndex=random.nextInt(RatingItem.size());
			T randomItem=RatingItem.get(randomIndex);
			LinkedList<T> randomList=new LinkedList<>();
			randomList.add(randomItem);
			resultMap.put(keyT, randomList);
		}
		return resultMap;
	}
	
	/**
	 * @param Rating
	 * @param TestRating
	 * @return the TraingRating
	 *@create_time：2014年7月22日下午5:33:48
	 *@modifie_time：2014年7月22日 下午5:33:48
	 */
	private Map<T, List<T>> getTrainByMinusTestRaing(Map<T, List<T>> Rating,Map<T, List<T>> TestRating){
		HashMapUtil<T,T> hashMapUtil=new HashMapUtil();
		Rating=hashMapUtil.removeElementFromAnotherMapByKeyAndValue(Rating, TestRating);
		return Rating;
	}
	
	/**
	 * @return the userSet
	 */
	public List<?> getUserSet() {
		return this.userList;
	}

	/**
	 * @return the itemSet
	 */
	public List<?> getItemSet() {
		return this.ItemList;
	}

	/**
	 * @return the subsampleFileName
	 */
	public String getSubsampleFileName() {
		return subsampleFileName;
	}

	/**
	 * @return the limit
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * @return the ratingMap
	 */
	public Map<T, List<T>> getRatingMap() {
		return ratingMap;
	}

	/**
	 * @return the testRating
	 */
	public Map<T, List<T>> getTestRating() {
		return testRating;
	}

	/**
	 * @return the trainRating
	 */
	public Map<T, List<T>> getTrainRating() {
		return trainRating;
	}
	
}

