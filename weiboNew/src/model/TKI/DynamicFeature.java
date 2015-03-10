/**
 * 
 */
package model.TKI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.MatrixUtil;
import util.ReadWeiboUtil;
import util.WriteUtil;
import bean.AvlTree;
import bean.OnePairTweet;
import bean.User;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：DynamicFeature   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年9月2日 下午12:22:25   
 * @modifier：zhouge   
 * @modified_time：2014年9月2日 下午12:22:25   
 * @modified_note：   
 * @version    
 *    
 */
public class DynamicFeature<T> {
	//test edge
	final Map<T, List<T>> testSet ;
	Map<T, User<T>> userSet;
	final HashMap<String, Integer> wordClass;
	final  String FeatureFileName;
	final List<T> userIdList;
	public DynamicFeature(Map<T, List<T>> testSet,Map<T, User<T>> userSet,String FeatureFileName,List<T> userIdList,HashMap<String, Integer> wordClass) {
		this.testSet=testSet;
		this.userSet=userSet;
		this.wordClass=wordClass;
		this.FeatureFileName=FeatureFileName;
		this.userIdList=userIdList;
		calFeature();
	}
	
	/**
	 *  cal feature
	 *@create_time：2014年9月2日下午12:32:48
	 *@modifie_time：2014年9月2日 下午12:32:48
	  
	 */
	public  void calFeature(){
		
		ReadWeiboUtil readWeiboUtil=new ReadWeiboUtil(FeatureFileName);
		AvlTree<OnePairTweet> origalWeibotree=new AvlTree<>();
		OnePairTweet temp=new OnePairTweet();
		
		System.out.println("the first step :save the weiboId that be retweeted");
		AvlTree<Long> retweetId=new AvlTree<>();
		String oneLine;
		while((oneLine=readWeiboUtil.readerOneLine())!=null){
			long retweedId=OnePairTweet.covertRetweetId(oneLine);
			if (retweedId==0L) {
				continue;
			}
			temp=OnePairTweet.covert(oneLine);
			//如果是转发则保存
			if(temp.getRetweetId()!=0L){
				T userId=(T) String.valueOf(temp.getUserId());
				T retweetUserId=(T) String.valueOf(temp.getRetweetUser_id());
				//如果在测试集里面就不保存
				if (testSet.get(userId)!=null&&testSet.get(userId).contains(retweetUserId)) {
					continue;
				}
				retweetId.insert(temp.getRetweetId());
			}
		}
		
		System.out.println("the second step :deal with origan weibo");
		readWeiboUtil.resetReader();
		
		while((temp=readWeiboUtil.readOnePairTweetInMen())!=null){
			if (retweetId.contains(temp.getWeiboId())) {
				origalWeibotree.insert(temp);
			}
			
			//如果不是转发则直接处理
			if(temp.getRetweetId()==0L){
				
				long userId=temp.getUserId();
				String content=temp.getContent();
				
				if (!content.trim().equals("")&&userSet.containsKey(String.valueOf(userId))) {
					User oneUser=userSet.get(String.valueOf(userId));
					String[] split=content.trim().split(" ");
					double[] newFeature=new double[100];
					for (String word : split) {
						if (!wordClass.containsKey(word)) {
							continue;
						}
						int label=wordClass.get(word);
						newFeature[label]+=1;
					}
					double[] oldFeature=oneUser.getFeature();
					if (oldFeature==null) {
						oneUser.setFeature(newFeature);
					}else {
						newFeature=MatrixUtil.vectorAdd(newFeature, oldFeature);
						oneUser.setFeature(newFeature);
					}
					userSet.put((T) String.valueOf(userId), oneUser);
				}
				
			}
			
		}
		
//		WriteUtil<String> writeUtil=new WriteUtil<>();
//		for (T userId : userIdList) {
//			writeUtil.writeVector(userSet.get(userId).getFeature(), "1kUserOldFeature.txt");
//		}
		System.out.println("the third step :deal with retweet weibo");
		
		readWeiboUtil.resetReader();
		
		while((oneLine=readWeiboUtil.readerOneLine())!=null){
			long retweedId=OnePairTweet.covertRetweetId(oneLine);
			if (retweedId!=0L) {
				continue;
			}
			temp=OnePairTweet.covert(oneLine);
			//如果是转发则获取原始内容
			if (temp==null) {
				continue;
			}
				long userId=temp.getUserId();
				OnePairTweet retweetWeibo=new OnePairTweet();
				retweetWeibo.setWeiboId(temp.getRetweetId());
				retweetWeibo=origalWeibotree.getElement(retweetWeibo, origalWeibotree.root);
				String content="";
				if (retweetWeibo!=null&&!testSet.get(String.valueOf(userId)).contains(String.valueOf(retweetWeibo.getUserId()))) {
					content=temp.getContent().trim()+" "+retweetWeibo.getContent().trim();
				}else
					content=temp.getContent().trim();
				
				if (!content.trim().equals("")&&userSet.containsKey(String.valueOf(userId))) {
					User oneUser=userSet.get(String.valueOf(userId));
					String[] split=content.trim().split(" ");
					double[] newFeature=new double[100];
					for (String word : split) {
						if (!wordClass.containsKey(word)) {
							continue;
						}
						int label=wordClass.get(word);
						newFeature[label]+=1;
					}
					double[] oldFeature=oneUser.getFeature();
					if (oldFeature==null) {
						oneUser.setFeature(newFeature);
					}else {
						newFeature=MatrixUtil.vectorAdd(newFeature, oldFeature);
						oneUser.setFeature(newFeature);
					}
					userSet.put((T) String.valueOf(userId), oneUser);
				}
		}
	}
	
	public Map<T, User<T>> getUserSet(){
		return this.userSet;
	}
}
