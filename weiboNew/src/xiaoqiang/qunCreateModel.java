/**
 * 
 */
package xiaoqiang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import bean.AvlTree;
import CCF.bean.User;
import preprocess._1014.staticreminderweibo;
import util.ReadUtil;
import util.WriteUtil;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：qun
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年10月28日 下午6:43:35
 * @modifier：zhouge
 * @modified_time：2014年10月28日 下午6:43:35
 * @modified_note：
 * @version
 * 
 */
public class qunCreateModel {

	static String path = "J:/workspacedata/weiboNew/data/xiaoqiang/";
	static String sourceName = path + "Gnet.txt";
	static String resultName = path + "result.txt";
	static double alpal = 0.13;
	static double belta = 0.36;
	static double gama = 0.35;
	static Random random = new Random();

	public static void main(String[] args) {
		random.setSeed(System.currentTimeMillis());
		qunCreateModel qunCreateModel=new qunCreateModel();
		ReadUtil<String> readUtil = new ReadUtil<>();
		List<String> sourceList = readUtil.readFileByLine(sourceName);
		AvlTree<user> usermapMap = new AvlTree<>();
		Set<Long> userList=new HashSet();
		for (String one : sourceList) {
			String[] split=one.split(" ");
			long userid1=Long.valueOf(split[0]);
			long userid2=Long.valueOf(split[1]);
			user temp1=new user(userid1);
			if(!usermapMap.contains(temp1))
			{
				 temp1=new user(userid1,alpal,belta,gama);
				usermapMap.insert(temp1);
			}
			user temp2=new user(userid2);
			if(!usermapMap.contains(temp2))
			{
				 temp2=new user(userid2,alpal,belta,gama);
				usermapMap.insert(temp2);
			}
			userList.add(userid1);
			userList.add(userid2);
			usermapMap.getElement(temp1, usermapMap.root).addFriend(userid2);
			usermapMap.getElement(temp2, usermapMap.root).addFriend(userid1);
		}
		System.out.println();
	}

	public void run(AvlTree<user> usermapMap,Set<Long> userList) {
		WriteUtil<Long> writeUtil=new WriteUtil<>();
		for (Long preuserid : userList) {
			user preUser=new user(preuserid);
			preUser=usermapMap.getElement(preUser, usermapMap.root);
			for (int i = 0; i < 10; i++) {
				if (preUser.getInterests()[i]==1) {
					Set<Long> qunUseridlist=new HashSet<>();
					qunUseridlist.add(preuserid);
					qunUseridlist=createprocess(qunUseridlist,preuserid,usermapMap,i);
					writeUtil.writeList(new ArrayList<>(qunUseridlist), path+"model.txt");
				}
			}
		}
	}
	
	public Set<Long> createprocess(Set<Long> qunUseridlist,long preUserid,AvlTree<user> usermapMap,int indexInterest){
		
		final int bigsize=2000;
		
		user preUser=new user(preUserid);
		preUser=usermapMap.getElement(preUser, usermapMap.root);
		Set<Long> friendlist=preUser.getFriendSet();
		double fvlaue=function(friendlist.size());
		Set<Long> adduser=new HashSet<>();
		for (Long friendid : friendlist) {
			if (qunUseridlist.contains(friendid)) {
				continue;
			}
			user friend=new user(friendid);
			friend=usermapMap.getElement(friend, usermapMap.root);
			if(friend.getInterests()[indexInterest]==1){
				double rand=random.nextDouble();
				if(rand<fvlaue){
					rand=random.nextDouble();
					double arguep=friend.getParam2()+preUser.getParam3();
					if(rand<arguep){
						adduser.add(friendid);
						qunUseridlist.add(friendid);
					}
					if(qunUseridlist.size()>2000){
						return qunUseridlist;
					}
				}
			}
		}
		for (Long adduserid : adduser) {
			qunUseridlist=createprocess(qunUseridlist,adduserid,usermapMap,indexInterest);
		}
		return qunUseridlist;
	}
	public double function(int param){
		double result=0;
		result=(param+35);
		result=Math.pow(result, -1.2);
		result*=100;
		return result;
	}
}



class user implements Comparable<user>{

	long userid;
	private double param1, param2, param3;
	int[] interests = new int[10];
	Set<Long> friendSet;
	public user(long userid){
		this.userid=userid;
	}
	public user(long userid,double alpal, double belta, double gama  ){
		Random random=new Random();
		random.setSeed(System.currentTimeMillis());
		this.userid=userid;
		double temp=Double.MAX_VALUE;
		while(temp>=alpal){
			temp=random.nextDouble();
		}
		param1=temp;
		
		temp=Double.MAX_VALUE;
		while(temp>=belta){
			temp=random.nextDouble();
		}
		param2=temp;
		
		temp=Double.MAX_VALUE;
		while(temp>=gama){
			temp=random.nextDouble();
		}
		param3=temp;
		friendSet=new HashSet<>();
		
		for (int i = 0; i < 10; i++) {
			temp=random.nextDouble();
			if(temp<param1)
				interests[i]=1;
		}
		int sum=0;
		for (int i = 0; i < 10; i++) {
			sum+=interests[i];
		}
		if(sum==0)
		{
			int index=random.nextInt(10);
			interests[index]=1;
		}
	}
	/**
	 * @return the friendSet
	 */
	public Set<Long> getFriendSet() {
		return friendSet;
	}

	/**
	 * @param friendSet the friendSet to set
	 */
	public void addFriend(Long userq ) {
		this.friendSet.add(userq);
	}

	/**
	 * @return the interests
	 */
	public int[] getInterests() {
		return interests;
	}

	/**
	 * @param interests
	 *            the interests to set
	 */
	public void setInterests(int[] interests) {
		this.interests = interests;
	}

	/**
	 * @return the userid
	 */
	public long getUserid() {
		return userid;
	}

	/**
	 * @param userid the userid to set
	 */
	public void setUserid(long userid) {
		this.userid = userid;
	}

	/**
	 * Set<user> friend; /**
	 * 
	 * @return the param1
	 */
	public double getParam1() {
		return param1;
	}

	/**
	 * @param param1
	 *            the param1 to set
	 */
	public void setParam1(double param1) {
		this.param1 = param1;
	}

	/**
	 * @return the param2
	 */
	public double getParam2() {
		return param2;
	}

	/**
	 * @param param2
	 *            the param2 to set
	 */
	public void setParam2(double param2) {
		this.param2 = param2;
	}

	/**
	 * @return the param3
	 */
	public double getParam3() {
		return param3;
	}

	/**
	 * @param param3
	 *            the param3 to set
	 */
	public void setParam3(double param3) {
		this.param3 = param3;
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(user o) {
		// TODO Auto-generated method stub
		if (this.userid>o.userid) {
			return 1;
		}else if(this.userid<o.userid){
			return -1;
		}else {
			return 0;
		}
	}

}
