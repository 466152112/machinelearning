/**
 * 
 */
package Resource;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bean.MentionAndRely;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import Resource.superClass.superResource;
import tool.TimeTool.CalanderUtil;
import tool.mongo.Mongo_Source;

/**
 * 
 * @progject_name：weiboMention
 * @class_name：MongDB_Source
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年12月23日 下午7:28:05
 * @modifier：zhouge
 * @modified_time：2014年12月23日 下午7:28:05
 * @modified_note：
 * @version
 * 
 */
public class mongoDB_Source extends superResource {
	/* socialgraph database */
//	private final static String socialgraph_IP = "172.17.166.4";
//	private final static int socialgraph_Port = 27017;
//	private final static String socialgraph_DB_Name = "Weibo";
//	private final static String socialgraph_Collection_Name = "SocialGraph";

	/*
	 * intection databse _id,sourceId,targetId,create_time,type
	 */
	private  static String retweet_IP,intection_IP;
	private final static int intection_Port = 27018;
	private final static String intection_DB_Name = "intection";
	private final static String intection_Collection_Name = "record";
	private final static String intectionv2_Collection_Name = "record2";
	private final static String socialgraphv2_Collection_Name = "SocialGraph";
	
	/*
	 * retweet_history databse
	 * _id,retweeter,retweet_time,sourcer,source_id,source_time
	 */
	//private final static String retweet_IP = "172.31.218.51";
	private final static int retweet_Port = 27018;
	private final static String retweet_DB_Name = "intection";
	private	final static String retweet_Collection_Name = "retweet_history";

//	private static Mongo_Source socialGraph = null;
	private static Mongo_Source intection = null;
	private static Mongo_Source retweet = null;
	//private static Mongo_Source intectionv2 = null;
	private static Mongo_Source socialGraph = null;
	
	private CalanderUtil calanderUtil = new CalanderUtil();
	static {
		
			retweet_IP="192.168.100.100";
			intection_IP="192.168.100.100";
		//}else {
//			retweet_IP="172.31.218.51";
//			intection_IP="172.31.218.51";
		//}
		
	}
	public mongoDB_Source() {
		super();
		getSocialGraph();
		getIntection();
		getRetweet();
		
	}

//	/**
//	 * @return the socialGraph
//	 */
//	public Mongo_Source getSocialGraph() {
//		// String ip,int port,String db_name,String collection_name
//		if (socialGraph == null) {
//			socialGraph = new Mongo_Source(socialgraph_IP, socialgraph_Port,
//					socialgraph_DB_Name, socialgraph_Collection_Name);
//		}
//		return socialGraph;
//	}

//	/**
//	 * @return the intection
//	 */
//	public Mongo_Source getIntection() {
//		if (intection == null) {
//			intection = new Mongo_Source(intection_IP, intection_Port,
//					intection_DB_Name, intection_Collection_Name);
//		}
//		return intection;
//	}
	/**
	 * @return the intection
	 */
	public Mongo_Source getIntection() {
		if (intection == null) {
			intection = new Mongo_Source(intection_IP, intection_Port,
					intection_DB_Name, intectionv2_Collection_Name);
		}
		return intection;
	}

	/**
	 * @return the intection
	 */
	public Mongo_Source getSocialGraph() {
		if (socialGraph == null) {
			socialGraph = new Mongo_Source(intection_IP, intection_Port,
					intection_DB_Name, socialgraphv2_Collection_Name);
		}
		return socialGraph;
	}
	/**
	 * @return the intection
	 */
	public Mongo_Source getRetweet() {
		if (retweet == null) {
			retweet = new Mongo_Source(retweet_IP, retweet_Port,
					retweet_DB_Name, retweet_Collection_Name);
		}
		return retweet;
	}

	/**
	 * @param userid
	 * @return the friend count from db
	 * @create_time：2014年12月28日下午8:13:00
	 * @modifie_time：2014年12月28日 下午8:13:00
	 */
	public Set<Long> getFriend_list(long userid) {
		if (socialGraph == null) {
			getSocialGraph();
		}
		BasicDBObject select = new BasicDBObject();
		select.put("_id", userid);
		BasicDBObject result = socialGraph.findOne(select);
		if (result == null) {
			return Collections.EMPTY_SET;
		}
		List<Object> friend = (List<Object>) result.get("ids");
		Set<Long> friendList = new HashSet<>();
		for (Object object : friend) {
			if (object instanceof Integer)
				friendList.add(Long.valueOf(String.valueOf(object)));
			else {
				friendList.add((long) object);
			}
		}
		return friendList;
	}
	
	/**
	 * @param userid1
	 * @param userid2
	 * @return 判断两个用户是否是双向关注
	 * @create_time：2015年1月20日下午1:49:39
	 * @modifie_time：2015年1月20日 下午1:49:39
	 */
	public boolean ifBifriend(long userid1, long userid2) {
		BasicDBObject select=new BasicDBObject();
		select.put("_id", userid1);
		select.put("ids", userid2);
		int	count1=socialGraph.Count(select);
		select.put("_id", userid2);
		select.put("ids", userid1);
		int	count2=socialGraph.Count(select);
		if (count1!=0&&count2!=0) {
			return true;
		}else {
			return false;
		}
//		Set<Long> friendList1 = getFriend_list(userid1);
//		Set<Long> friendList2 = getFriend_list(userid2);
//		if (friendList1.contains(userid2) && friendList2.contains(userid1)) {
//			return true;
//		} else {
//			return false;
//		}
	}

	/**
	 * @param userid1
	 * @param friendList1
	 * @param userid2
	 * @param friendList2
	 * @return
	 * @create_time：2015年1月20日下午1:50:51
	 * @modifie_time：2015年1月20日 下午1:50:51
	 */
	public boolean ifBifriend(long userid1, Set<Long> friendList1,
			long userid2, Set<Long> friendList2) {
		if (friendList1.contains(userid2) && friendList2.contains(userid1)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param userid
	 * @return 从数据库中获取用户被转发的微博次数
	 * @create_time：2014年12月28日下午9:21:04
	 * @modifie_time：2014年12月28日 下午9:21:04
	 */
	public int getUserWeibo_beRetweet_Number(long userid) {
		if (intection == null) {
			getIntection();
		}
		BasicDBObject select = new BasicDBObject();
		select.put("sourceId", userid);
		select.put("type", 1);
		int BeretweetCount = intection.Count(select);
		return BeretweetCount;
	}
	/**
	 * @param userid
	 * @return 从数据库中获取用户被转发的微博次数
	 * @create_time：2014年12月28日下午9:21:04
	 * @modifie_time：2014年12月28日 下午9:21:04
	 */
	public int getUserWeibo_beRetweet_Number(long userid,Date TimeLine) {
		if (retweet == null) {
			getRetweet();
		}
		BasicDBObject select = new BasicDBObject();
		select.put("user_id", userid);
		List<BasicDBObject> list=retweet.select(select);
		// clear by time
		list = clearByTimeLine(TimeLine, list, "created_time");
		int BeretweetCount =0;
		for (BasicDBObject onerecord : list) {
			BasicDBObject retweet=(BasicDBObject) onerecord.get("retweet");
			BeretweetCount+=retweet.size();
		}
		return BeretweetCount;
	}
	/**
	 * @param userid
	 * @return 从数据库中获取用户被转发的微博次数
	 * @create_time：2014年12月28日下午9:21:04
	 * @modifie_time：2014年12月28日 下午9:21:04
	 */
	public int getBeAttackedNumber(long userid,Date begin,Date end) {
		if (intection == null) {
			getIntection();
		}
		BasicDBObject select = new BasicDBObject();
		select.put("mention_id.user_id", userid);
		List<BasicDBObject> list=intection.select(select);
		if (list!=null) {
			// clear by time
			list = clearByTimeLine(begin, end,list, "created_time");
			return list.size();
		}
		return 0;
	}
	
	/**
	 * @param userid
	 * @return 从数据库中获取用户被转发的微博次数
	 * @create_time：2014年12月28日下午9:21:04
	 * @modifie_time：2014年12月28日 下午9:21:04
	 */
	public int getBeRelyNumber(long userid,Date begin,Date end) {
		if (intection == null) {
			getIntection();
		}
		BasicDBObject select = new BasicDBObject();
		select.put("user_id", userid);
		List<BasicDBObject> list=intection.select(select);
		// clear by time
		if (list!=null) {
			int count=0;
			list = clearByTimeLine(begin, end,list, "created_time");
			for (BasicDBObject mentiontemp : list) {
				List<BasicDBObject> mention=(List<BasicDBObject>) mentiontemp.get("mention_id");
				for (BasicDBObject temp : mention) {
						if (temp.getInt("retweet")==1) {
							count++;
						}
					}
				}
			return count;
		}
		return 0;
	}
	
	/**
	 * @param userid
	 * @return 从数据库中获取某条微博在某个时刻被转发的情况
	 * @create_time：2014年12月28日下午9:21:04
	 * @modifie_time：2014年12月28日 下午9:21:04
	 */
	public BasicDBObject getBeRetweenList(long weibo) {
		if (retweet == null) {
			getRetweet();
		}
		BasicDBObject select = new BasicDBObject();
		select.put("_id", weibo);
		BasicDBObject list=retweet.findOne(select);
		return list;
	}
	
	/**
	 * @param userid
	 * @return 从数据库中获取用户被转发的微博次数
	 * @create_time：2014年12月28日下午9:21:04
	 * @modifie_time：2014年12月28日 下午9:21:04
	 */
	public int getUserWeibo_beRetweet_Number(long userid,Date begin,Date end) {
		if (retweet == null) {
			getRetweet();
		}
		BasicDBObject select = new BasicDBObject();
		select.put("user_id", userid);
		List<BasicDBObject> list=retweet.select(select);
		// clear by time
		list = clearByTimeLine(begin, end,list, "created_time");
		int BeretweetCount =0;
		for (BasicDBObject onerecord : list) {
			List<BasicDBObject> retweet= (List<BasicDBObject>) onerecord.get("retweet");
			BeretweetCount+=retweet.size();
		}
		return BeretweetCount;
	}
	/**
	 * @param userid2
	 * @param userid2
	 *            _id,retweeter,retweet_time,sourcer,source_id,source_time
	 * @return 获取用户的共同转发微博的次数
	 * @create_time：2015年1月15日上午11:09:24
	 * @modifie_time：2015年1月15日 上午11:09:24
	 */
	private double getCoRetweet_count_old(long userid1, long userid2, Date begin,Date end) {
		if (retweet == null) {
			getRetweet();
		}
		
		 BasicDBList condList = new BasicDBList(); 
		//年龄大于1小于100
		  BasicDBObject cond1= new BasicDBObject();
		  cond1.put("retweet.user_id", userid1);

		  //性别为女
		  BasicDBObject cond2= new BasicDBObject();

		  cond1.put("retweet.user_id", userid2);


		  //将两个条件加入到条件集合中（多条件）
		  condList.add(cond1);

		  condList.add(cond2);
		  
		  BasicDBObject condition= new BasicDBObject();

		  condition.put("$and", condList);

		// select from db
		BasicDBObject selectu1 = new BasicDBObject();
		selectu1.put("retweet.user_id", userid1);
		List<BasicDBObject> listu1 = retweet.select(selectu1);
		// clear by time
		listu1 = clearByTimeLine(begin,end, listu1, "retweet.created_time");

		BasicDBObject selectu2 = new BasicDBObject();
		selectu2.put("retweet.user_id", userid2);
		List<BasicDBObject> listu2 = retweet.select(selectu2);
		listu2 = clearByTimeLine(begin,end, listu2, "retweet.created_time");

		// 做内积
		if (listu1.size() > 0 && listu2.size() > 0) {
			Set<Long> retweet_weibo_id_u1 = new HashSet<>();
			for (BasicDBObject db : listu1) {
				retweet_weibo_id_u1.add(db.getLong("_id"));
			}

			Set<Long> retweet_weibo_id_u2 = new HashSet<>();
			for (BasicDBObject db : listu2) {
				retweet_weibo_id_u2.add(db.getLong("_id"));
			}

			retweet_weibo_id_u1.retainAll(retweet_weibo_id_u2);
			return retweet_weibo_id_u1.size();
		}
		return 0;
	}

	
	/**
	 * @param userid2
	 * @param userid2
	 *            _id,retweeter,retweet_time,sourcer,source_id,source_time
	 * @return 获取用户的共同转发微博的次数
	 * @create_time：2015年1月15日上午11:09:24
	 * @modifie_time：2015年1月15日 上午11:09:24
	 */
	public double getCoRetweet_count(long userid1, long userid2, Date begin,Date end) {
		if (retweet == null) {
			getRetweet();
		}
		 BasicDBList condList = new BasicDBList(); 
		  BasicDBObject cond1= new BasicDBObject();
		  cond1.put("retweet.user_id", userid1);
		  BasicDBObject cond2= new BasicDBObject();
		  cond2.put("retweet.user_id", userid2);
		  //将两个条件加入到条件集合中（多条件）
		  condList.add(cond1);
		  condList.add(cond2);
		  BasicDBObject condition= new BasicDBObject();
		  condition.put("$and", condList);
		// select from db
		  List<BasicDBObject> list = retweet.select(condition);
		  list = clearByTimeLine(begin,end, list, "created_time");
		  return list.size();
	}
	
	/**
	 * @param userid2
	 * @param userid2
	 *            _id,retweeter,retweet_time,sourcer,source_id,source_time
	 * @return 获取用户的共同被mention的次数
	 * @create_time：2015年1月15日上午11:09:24
	 * @modifie_time：2015年1月15日 上午11:09:24
	 */
	public double getCoBemention_count(long userid1, long userid2, Date begin,Date end) {
		if (intection == null) {
			getIntection();
		}
		 BasicDBList condList = new BasicDBList(); 
		  BasicDBObject cond1= new BasicDBObject();
		  cond1.put("mention_id.user_id", userid1);
		  BasicDBObject cond2= new BasicDBObject();
		  cond2.put("mention_id.user_id", userid2);
		  //将两个条件加入到条件集合中（多条件）
		  condList.add(cond1);
		  condList.add(cond2);
		  BasicDBObject condition= new BasicDBObject();
		  condition.put("$and", condList);
		// select from db
		  List<BasicDBObject> list = intection.select(condition);
		  list = clearByTimeLine(begin,end, list, "created_time");
		  return list.size();
	}
	public List<BasicDBObject> clearByTimeLine(Date TimeLine,
			List<BasicDBObject> list, String timeField) {
		for (int i = 0; i < list.size(); i++) {
			String time = list.get(i).getString(timeField);
			Date pretime = calanderUtil.getDate(time);
			if (pretime.compareTo(TimeLine) > 0) {
				list.remove(i);
				i--;
			}
		}
		return list;
	}

	public List<BasicDBObject> clearByTimeLine(Date begin,Date end,
			List<BasicDBObject> list, String timeField) {
		if (list==null) {
			return Collections.EMPTY_LIST;
		}
		for (int i = 0; i < list.size(); i++) {
			String time = list.get(i).getString(timeField);
			Date pretime = calanderUtil.getDate(time);
			if (pretime.compareTo(begin) < 0) {
				list.remove(i);
				i--;
			}else if (pretime.compareTo(end) > 0) {
				list.remove(i);
				i--;
			}
		}
		return list;
	}
	
	public MentionAndRely getMentionAndRely(long userid,Date beginTime,Date endTime){
		MentionAndRely result=new MentionAndRely(userid);
		BasicDBObject select = new BasicDBObject();
		select.put("mention_id.user_id", userid);
		List<BasicDBObject> list = intection.select(select);
		int Bemention=0;
		int rely=0;
		if (list != null) {
			list=clearByTimeLine(beginTime, endTime, list, "created_time");
			for (BasicDBObject tempmention : list) {
				List<BasicDBObject> mention=(List<BasicDBObject>) tempmention.get("mention_id");
				for (BasicDBObject temp : mention) {
					if (temp.getLong("user_id")==userid) {
						Bemention++;
						if (temp.getInt("retweet")==1) {
							rely++;
						}
					}
				}
			}
			result.setRevlentMention(list);
			result.setBeMention(Bemention);
			result.setRely(rely);
		}
		return result;
	}
	/**
	 * @param sourceId
	 *            信息源
	 * @param targetId
	 *            信息消费者
	 * @return
	 * @create_time：2014年12月28日下午10:10:28
	 * @modifie_time：2014年12月28日 下午10:10:28
	 */
	private int getUser_Retweet_Count(long sourceId, long targetId,
			Date createTime) {
		BasicDBObject select = new BasicDBObject();
		select.put("sourceId", sourceId);
		select.put("targetId", targetId);
		select.put("type", 1);
		List<BasicDBObject> list = intection.select(select);
		int BeretweetCount = 0;
		if (list != null) {
			for (BasicDBObject basicDBObject : list) {
				if (calanderUtil
						.getDate(basicDBObject.getString("create_time"))
						.compareTo(createTime) < 0)
					BeretweetCount++;
			}
		}

		return BeretweetCount;
	}
	/**
	 * @param sourceId
	 * 信息源
	 * @param targetId
	 * 信息消费者
	 * @param beginTime
	 * 
	 * @param endTime
	 * @return
	 *@create_time：2015年1月28日下午1:49:59
	 *@modifie_time：2015年1月28日 下午1:49:59
	  
	 */
	public int getUser_Retweet_Count(long sourceId, long targetId,
			Date beginTime,Date endTime) {
		BasicDBObject select = new BasicDBObject();
		select.put("user_id", sourceId);
		select.put("retweet.user_id", targetId);
		List<BasicDBObject> list = retweet.select(select);
		int BeretweetCount = 0;
		if (list != null) {
			for (BasicDBObject basicDBObject : list) {
				if (calanderUtil.getDate(basicDBObject.getString("created_time")).compareTo(endTime) < 0&&
						calanderUtil.getDate(basicDBObject.getString("created_time")).compareTo(beginTime) > 0	)
					BeretweetCount++;
			}
		}

		return BeretweetCount;
	}
	/**
	 * @param sourceId
	 *            mention 的发起者
	 * @param targetId
	 *            mention 的接收者
	 * @return
	 * @create_time：2014年12月28日下午10:10:32
	 * @modifie_time：2014年12月28日 下午10:10:32
	 */
	public int getUser_Mention_Count(long sourceId, long targetId,
			Date createTime) {
		BasicDBObject select = new BasicDBObject();
		select.put("user_id", sourceId);
		select.put("mention_id.user_id", targetId);
		int BeretweetCount = 0;
		List<BasicDBObject> list = intection.select(select);
		if (list != null) {
			for (BasicDBObject basicDBObject : list) {
				if (calanderUtil
						.getDate(basicDBObject.getString("created_time"))
						.compareTo(createTime) < 0)
					BeretweetCount++;
			}
		}
		return BeretweetCount;
	}
	/**
	 * @param sourceId
	 *            mention 的发起者
	 * @param targetId
	 *            mention 的接收者
	 * @return
	 * @create_time：2014年12月28日下午10:10:32
	 * @modifie_time：2014年12月28日 下午10:10:32
	 */
	public int getUser_Mention_Count(long sourceId, long targetId,
			Date beginTime,Date endTime) {
		BasicDBObject select = new BasicDBObject();
		select.put("user_id", sourceId);
		select.put("mention_id.user_id", targetId);
		int BeretweetCount = 0;
		List<BasicDBObject> list = intection.select(select);
		if (list != null) {
			list=clearByTimeLine(beginTime, endTime, list, "created_time");
			return list.size();
		}
		return BeretweetCount;
	}
	
	public int getUser_BeMention_Count( long targetId,
			Date beginTime,Date endTime) {
		BasicDBObject select = new BasicDBObject();
		select.put("mention_id.user_id", targetId);
		int BeretweetCount = 0;
		List<BasicDBObject> list = intection.select(select);
		if (list != null) {
			list=clearByTimeLine(beginTime, endTime, list, "created_time");
			return list.size();
		}
		return BeretweetCount;
	}
	/**
	 * @param sourceId
	 *            mention 的发起者
	 * @param targetId
	 *            mention 的接收者
	 * @return 发起者在之前成功mention的次数
	 * @create_time：2014年12月28日下午10:10:32
	 * @modifie_time：2014年12月28日 下午10:10:32
	 */
	public int getUser_Mention_Success_Count(long sourceId, long targetId,
			Date beginTime,Date endTime) {
		BasicDBObject select = new BasicDBObject();
		select.put("user_id", sourceId);
		select.put("mention_id.user_id", targetId);
		int BeretweetCount = 0;
		List<BasicDBObject> list = intection.select(select);
		if (list != null) {
			list=clearByTimeLine(beginTime, endTime, list, "created_time");
			for (BasicDBObject temp : list) {
				BeretweetCount+=IfRetweet(temp, targetId);
			}
		}
		return BeretweetCount;
	}
	
	
	/**
	 * @param targetUserid 目标用户
	 * @param weiboid 目标微博id
	 * @param time 某个时间
	 * @return 从数据库中检索某个用户在某个时间段后是否转发指定微博
	 *  1 转发 0 没转发
	 *@create_time：2015年1月28日下午2:50:31
	 *@modifie_time：2015年1月28日 下午2:50:31
	  
	 */
	public int IfRetweet(BasicDBObject oneLine,long targetUserId){
		int targetValue=0;
		List<BasicDBObject> mention_list=(List<BasicDBObject>) oneLine.get("mention_id");
		for (BasicDBObject mention : mention_list) {
			if (mention.getLong("user_id")==targetUserId) {
				if (mention.getInt("retweet")==1) {
					targetValue=1;
				}else {
					break;
				}
			}
		}
		return targetValue;
	}
}
