/**
 * 
 */
package weibo.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import tool.FileTool.ReadUtil;
import tool.MapTool.HashMapUtil;
import bean.User;

/**   
*    
* @progject_nameÔºöweiboNew   
* @class_nameÔºöSplitTrainAndTest   
* @class_describeÔø?  
* @creatorÔºözhouge   
* @create_timeÔø?014Ôø?Ôø?8Ôø?‰∏ãÂçà3:44:56   
* @modifierÔºözhouge   
* @modified_timeÔø?014Ôø?Ôø?8Ôø?‰∏ãÂçà3:44:56   
* @modified_noteÔø?  
* @version    
*    
*/
public class SplitTrainAndTest {
	
	private  Set<Long> userList=new HashSet();
	/**
	 * 
	 */
	private final String subsampleFileName;
	private final int limit;
		//get clearRatingByLimit
	private Map<Long, List<Long>> followMap=null;
		
		//get testRating
	private Map<Long,List<Long>> testFollow=null;
		//get trainRating
	private Map<Long,List<Long>> trainFollow=null;
	private final double testRatio;
	private Map<Long, User> userSet;
	
	/**
	 * @param subsampleFileName
	 * @param userSet
	 * @param limit
	 * @param testRatio
	 */
	public SplitTrainAndTest(String subsampleFileName,Map<Long, User> userSet,int limit,double testRatio){
		System.out.println("begin SplitTrainAndTest");
		this.subsampleFileName=subsampleFileName;
		this.limit=limit;
		this.testRatio=testRatio;
		this.userSet=userSet;
		this.followMap=clearUserByFollowLimit();
		this.testFollow=getTestByRandomRatingFromEveryOne(followMap);
		this.trainFollow=getTrainByMinusTest(followMap, testFollow);
		
	}
	


	/**
	 * @return
	 * clear the user that no  Meet the limit
	 *@create_timeÔøΩÔøΩ2014ÔøΩÔøΩ8ÔøΩÔøΩ25ÔøΩÔøΩÔøΩÔøΩÔøΩÔøΩ9:20:22
	 *@modifie_timeÔøΩÔøΩ2014ÔøΩÔøΩ8ÔøΩÔøΩ25ÔøΩÔøΩ ÔøΩÔøΩÔøΩÔøΩ9:20:22
	  
	 */
	private  Map<Long, List<Long>> clearUserByFollowLimit(){
		System.out.println("begin the  clearUserByFollowLimit");
		//get followgraph in list
		List<String> FollowList=readRatingFromFile(this.subsampleFileName);
		
		//follow and folloee count
		Map<Long, List<Long>> followerAndNumber=new HashMap<>();
		Set<Long> allUser= userSet.keySet();
		//convert the arraylist followGraph into  the  hashmap 
		for (String OneLine : FollowList) {
			String[] split=OneLine.split(",");
			
			long followerId=Long.valueOf(split[0].trim());
			long followeeId=Long.valueOf(split[1].trim()) ;
			
			if (allUser.contains(followerId)&&allUser.contains(followeeId)) {
				if (followerId==followeeId) {
					continue;
				}
				//add to the hashmap
				if (!followerAndNumber.containsKey(followerId)) {
					List<Long> followeeList=new ArrayList<>();
					followeeList.add(followeeId);
					followerAndNumber.put(followerId, followeeList);
					
					followeeList.add(followeeId);
					userSet.get(followerId).setfollowee(new HashSet<>(followeeList));
				}else {
					followerAndNumber.get(followerId).add(followeeId);
					userSet.get(followerId).getfollowee().add(followeeId);
				}
			}else {
				continue;
			}
			

		}
		
		//new the hashmap util
		HashMapUtil<Long> hashMapUtil=new HashMapUtil<>();
		
		//the follow step is clean user and followee by cycle
		//when flag is false,end the cycle and break;
//		while(true){
//			List<T> removeListOfUser=hashMapUtil.GetRemoveElementFromKeyByNumberLimit(followerAndNumber, limit);
//			System.out.println(removeListOfUser.size());
//			if (removeListOfUser.size()==0) {
//				break;
//			}else {
//				followerAndNumber=hashMapUtil.removeElementFromValueByList(followerAndNumber, removeListOfUser);
//				followerAndNumber=hashMapUtil.removeElementFromKey(followerAndNumber, removeListOfUser);
//				
//				// remove from the userset hashmap
//				for (T userId : removeListOfUser) {
//					userSet.remove(userId);
//					//System.out.println(userId);
//				}
//			}
//		}

		
		
		//cal all the user that remain
//		Set<T> userSettemp=new HashSet<>();
//		Iterator<T> userKeyList=followerAndNumber.keySet().iterator();
//		while(userKeyList.hasNext()){
//			T followerId=userKeyList.next();
//			if (!userSettemp.contains(followerId)) {
//				userSettemp.add(followerId);
//			}
//		}
//		
//	
//		userKeyList=followerAndNumber.keySet().iterator();
//		while(userKeyList.hasNext()){
//			T keyT=userKeyList.next();
//			List<T> followeeList=followerAndNumber.get(keyT);
//			for (T followee : followeeList) {
//				if (!userSettemp.contains(followee)) {
//					userSettemp.add(followee);
//				}
//			}
//			
//		}
//		this.userList=userSettemp;
		System.out.println("finish the  clearUserByFollowLimit");
//		WriteUtil<T> writeUtil=new WriteUtil<>();
//		writeUtil.writeMap(followerAndNumber, "followGraph2.txt");
		return followerAndNumber;
	}
	
	
	private  Map<Long, List<Long>> clearUserByRatingLimit(List<Long> rating){
		
		System.out.println("begin the  clearUserByFollowLimit");
		//get followgraph in list
		List<String> FollowList=readRatingFromFile(this.subsampleFileName);
		
		//follow and folloee count
		Map<Long, List<Long>> followerAndNumber=new HashMap<>();
		
		//convert the arraylist followGraph into  the  hashmap 
		for (String OneLine : FollowList) {
			String[] split=OneLine.split(",");
			
			Long followerId=Long.valueOf(split[0].trim()) ;
			Long followeeId=Long.valueOf(split[1].trim()) ;
			
//			if (!userSet.get(followeeId).isFlag()||!userSet.get(followerId).isFlag()) {
//				continue;
//			}
			
			//add to the hashmap
			if (!followerAndNumber.containsKey(followerId)) {
				List<Long> followeeList=new ArrayList<>();
				followeeList.add(followeeId);
				followerAndNumber.put(followerId, followeeList);
				
				followeeList.add(followeeId);
				userSet.get(followerId).setfollowee(new HashSet<>(followeeList));
			}else {
				followerAndNumber.get(followerId).add(followeeId);
				userSet.get(followerId).getfollowee().add(followeeId);
			}

		}
		
		//new the hashmap util
		HashMapUtil<Long> hashMapUtil=new HashMapUtil<>();
		
		//the follow step is clean user and followee by cycle
		//when flag is false,end the cycle and break;
//		while(true){
//			List<T> removeListOfUser=hashMapUtil.GetRemoveElementFromKeyByNumberLimit(followerAndNumber, limit);
//			System.out.println(removeListOfUser.size());
//			if (removeListOfUser.size()==0) {
//				break;
//			}else {
//				followerAndNumber=hashMapUtil.removeElementFromValueByList(followerAndNumber, removeListOfUser);
//				followerAndNumber=hashMapUtil.removeElementFromKey(followerAndNumber, removeListOfUser);
//				
//				// remove from the userset hashmap
//				for (T userId : removeListOfUser) {
//					userSet.remove(userId);
//					//System.out.println(userId);
//				}
//			}
//		}

		
		
		//cal all the user that remain
//		Set<T> userSettemp=new HashSet<>();
//		Iterator<T> userKeyList=followerAndNumber.keySet().iterator();
//		while(userKeyList.hasNext()){
//			T followerId=userKeyList.next();
//			if (!userSettemp.contains(followerId)) {
//				userSettemp.add(followerId);
//			}
//		}
//		
//	
//		userKeyList=followerAndNumber.keySet().iterator();
//		while(userKeyList.hasNext()){
//			T keyT=userKeyList.next();
//			List<T> followeeList=followerAndNumber.get(keyT);
//			for (T followee : followeeList) {
//				if (!userSettemp.contains(followee)) {
//					userSettemp.add(followee);
//				}
//			}
//			
//		}
//		this.userList=userSettemp;
		System.out.println("finish the  clearUserByFollowLimit");
//		WriteUtil<T> writeUtil=new WriteUtil<>();
//		writeUtil.writeMap(followerAndNumber, "followGraph2.txt");
		return followerAndNumber;
	}
	/**
	 * @param fileName
	 * @return
	 *@create_timeÔø?014Ôø?Ôø?8Êó•‰∏ãÔø?:44:48
	 *@modifie_timeÔø?014Ôø?Ôø?8Ôø?‰∏ãÂçà3:44:48
	  
	 */
	private List<String> readRatingFromFile(String fileName){
		ReadUtil readUtil=new ReadUtil();
		return readUtil.readFileByLine(fileName);
	}
	

	/**
	 * @param Rating
	 * @return
	 *@create_timeÔø?014Ôø?Ôø?8Êó•‰∏ãÔø?:44:58
	 *@modifie_timeÔø?014Ôø?Ôø?8Ôø?‰∏ãÂçà3:44:58
	  
	 */
	private Map<Long, List<Long>> getTestByRandomRatingFromEveryOne(Map<Long, List<Long>> Rating){
		System.out.println("begin the  getTestByRandomRatingFromEveryOne");
		Map<Long, List<Long>> resultMap=new HashMap<>();
		
		Random random=new Random();
		random.setSeed(System.currentTimeMillis());
		
		Iterator<Long> KeyList=Rating.keySet().iterator();
		while(KeyList.hasNext()){
			Long keyT=KeyList.next();
			List<Long> RatingItem=Rating.get(keyT);
			//int testSize=(int)( RatingItem.size()*testRatio);
			int testSize= doubleUp(RatingItem.size()*testRatio);
			//the followeelist must bigger than 10 so the testSize >=1
			if (testSize<1) {
				continue;
			}
			List<Long> randomList=new ArrayList();
		//	System.out.println("the size of userfollowee "+keyT+ " is :"+RatingItem.size());
			while(randomList.size()<testSize){
				int randomIndex=random.nextInt(RatingItem.size());
				Long randomItem=RatingItem.get(randomIndex);
				if(randomList.contains(randomItem))
					continue;
				randomList.add(randomItem);
				boolean flag=userSet.get(keyT).getfollowee().remove(randomItem);
				if (flag==false) {
					System.out.println(flag);
					System.exit(0);
				}
			}
			//System.out.println("the size of userfollowee "+keyT+ " is in test :"+randomList.size());
			resultMap.put(keyT, randomList);
		}
		System.out.println("finish the  getTestByRandomRatingFromEveryOne");
		System.out.println(resultMap.size());
		return resultMap;
	}
	
	private int doubleUp(double kk){
		BigDecimal kBigDecimal=new BigDecimal(kk).setScale(0, BigDecimal.ROUND_HALF_UP); 
		//System.out.println(kk+"\t"+ kBigDecimal.intValue());
		return kBigDecimal.intValue();
	}
	/**
	 * @param Rating
	 * @param TestRating
	 * @return the TraingRating
	 *@create_timeÔø?014Ôø?Ôø?2Êó•‰∏ãÔø?:33:48
	 *@modifie_timeÔø?014Ôø?Ôø?2Ôø?‰∏ãÂçà5:33:48
	 */
	private Map<Long, List<Long>> getTrainByMinusTest(Map<Long, List<Long>> followMap,Map<Long, List<Long>> testMap){
		HashMapUtil<Long> hashMapUtil=new HashMapUtil();
		
		//return the remain element
		Map<Long, List<Long>>  trainMap=hashMapUtil.removeElementFromAnotherMapByKeyAndValue(this.followMap, testMap);
		
		return trainMap;
	}

	/**
	 * @return the userList
	 */
	public Map<Long, User> getUserSet() {
		
		return userSet;
	}


	/**
	 * @return the testFollow
	 */
	public Map<Long, List<Long>> getTestFollow() {
		return testFollow;
	}

	/**
	 * @return the trainFollow
	 */
	public Map<Long, List<Long>> getTrainFollow() {
		return trainFollow;
	}

	 
	
}

