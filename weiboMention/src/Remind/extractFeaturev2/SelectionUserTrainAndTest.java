/**
 * 
 */
package Remind.extractFeaturev2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import Remind.extractFeaturev2.tool.Mention_tool;
import bean.MentionAndRely;

/**
 * 
 * @progject_name：weiboMention
 * @class_name：GetTrainAndTest
 * @class_describe：
 * @creator：zhouge
 * @create_time：2015年1月29日 下午8:28:53
 * @modifier：zhouge
 * @modified_time：2015年1月29日 下午8:28:53
 * @modified_note：
 * @version
 * 
 */
public class SelectionUserTrainAndTest {
	private Set<Long> all_user_id_set = null;

	public SelectionUserTrainAndTest(Set<Long> all_user_id_set) {
		this.all_user_id_set = all_user_id_set;
	}

	public Map<Long, MentionAndRely> getTrainUser(int mentionLimit, int relyLimit) {
		return getResult(mentionLimit, relyLimit, true);
	}
	public Map<Long, MentionAndRely> getTestUser(int mentionLimit, int relyLimit) {
		return getResult(mentionLimit, relyLimit, false);
	}
	
	public Map<Long, MentionAndRely>  getResult(int mentionLimit, int relyLimit,boolean ifTrain){
		Map<Long, MentionAndRely> TrainUserSet = new HashMap<Long, MentionAndRely>();
		// 用与保存用户在五个月里面的被mention次数，和回复次数
		ExecutorService executor = Executors.newFixedThreadPool(23);
		List<Future<MentionAndRely>> results = new ArrayList<>();
		for (Long userid : all_user_id_set) {
			results.add(executor.submit(new getInPal(userid, ifTrain)));
		}
		for (Future<MentionAndRely> oneResult : results) {
			try {
				MentionAndRely mentionAndRely = oneResult.get();
				if (mentionAndRely.getBeMention() >= mentionLimit
						&& mentionAndRely.getRely() > relyLimit) {
					TrainUserSet.put(mentionAndRely.getUserId(), mentionAndRely);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		executor.shutdown();
		return TrainUserSet;
	}

	class getInPal implements Callable<MentionAndRely> {
		final long userid;
		final boolean ifTrain;

		public getInPal(long sourceUser, boolean ifTrain) {
			this.userid = sourceUser;
			this.ifTrain = ifTrain;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public MentionAndRely call() throws Exception {
			Mention_tool mention_tool = new Mention_tool();
			return mention_tool.getUser_BeMention_rely(userid, ifTrain);
		}
	}

	
}
