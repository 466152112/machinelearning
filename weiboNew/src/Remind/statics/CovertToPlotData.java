package Remind.statics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import util.MathCal;
import util.ReadUser;
import Remind.util.BaseUitl;
import bean.AvlTree;
import bean.RemType;
import bean.Reminder;
import bean.User;
import bean.AvlTree.AvlNode;

public class CovertToPlotData {

	public static void main(String[] argsStrings) {
		String path = "/home/zhouge/database/weibo/new/";

		String userReminHistoryFile = path + "userid/userReminHistory.txt";
		String followGraph = path + "userid/FollowGraph.txt";
		String reminderFile = path + "rem.txt";
		CovertToPlotData re = new CovertToPlotData();

		AvlTree<User> useravl = BaseUitl.readUserRemHistory(userReminHistoryFile);
		//useravl=BaseUitl.addFollowee(useravl, followGraph);
		//re.relationship(useravl, reminderFile);
		//re.RetweetSizeInFollowSize(useravl,reminderFile);
		//re.run(useravl);
	}

	
	
	/**
	 * @param useravl
	 * @param reminderFile
	 */
	public void RetweetSizeInFollowSize(AvlTree<User> useravl,String reminderFile){
		List<List<Double>> attachNumberList=new ArrayList<>();
		
		for (int i = 0; i < 80; i++) {
			List<Double> temp=new ArrayList<>();
			attachNumberList.add(temp);
		}
		
		try (BufferedReader reader = new BufferedReader(new FileReader(
				reminderFile))) {
			String oneLine;
			while ((oneLine = reader.readLine()) != null) {
				RemType remType = RemType.covert(oneLine);
				long rootuserid=remType.getRoot().getUserId();
				User rootuser=new User();
				rootuser.setUserId(rootuserid);
				rootuser=useravl.getElement(rootuser, useravl.root);
				int followerC=rootuser.getFollowers_count();
				
				int index=MathCal.getTenBinIndex(followerC);
				
				attachNumberList.get(index).add((double) remType.getRetweetNumber());
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < 80; i++) {
			List<Double> temp=attachNumberList.get(i);
			if(temp.size()==0)
				continue;
			double avg=MathCal.getAverage(temp);
			double bzfc=MathCal.getBZFangCha(temp);
			//double sum=MathCal.getSum(temp);
			int number=MathCal.RecoverTenBinIndex(i);
			System.out.println(number+"\t"+avg+"\t"+bzfc);
			//System.out.println(number+"\t"+sum);
		}
		System.out.println();
		
	}
	public void relationship(AvlTree<User> useravl,
			String reminderFile){
			//final int top1000followee = 326341;
//		int attachFriendCount=0;
//		int attachFriendSucCount=0;
//		int attachBiFriendCount=0;
//		int attachBiFriendSucCount=0;
		int[] count=new int[6];
			try (BufferedReader reader = new BufferedReader(new FileReader(
					reminderFile))) {
				String oneLine;
				while ((oneLine = reader.readLine()) != null) {
					RemType remType = RemType.covert(oneLine);
					List<Reminder> listrem = remType.getReminders();
					long rootuserid=remType.getRoot().getUserId();
					User rootuser=new User();
					rootuser.setUserId(rootuserid);
					rootuser=useravl.getElement(rootuser, useravl.root);
					for (Reminder reminder : listrem) {
						long targetId=reminder.getUserId();
						User targetUser=new User();
						targetUser.setUserId(targetId);
						targetUser=useravl.getElement(targetUser, useravl.root);
						
						if(rootuser.getfollowee().contains(targetId)){
							count[0]++;
							if(reminder.isIfRely()){
								count[1]++;
							}
							if(targetUser.getfollowee().contains(rootuserid)){
								count[2]++;
								if(reminder.isIfRely()){
									count[3]++;
								}
							}
						}else {
								count[4]++;
								if(reminder.isIfRely()){
									count[5]++;
								}
							}
							
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(count[0]);
			System.out.println(count[1]);
			System.out.println(count[2]);
			System.out.println(count[3]);
			System.out.println(count[4]);
			System.out.println(count[5]);
			
	}
	public void run(AvlTree<User> useravl) {

		Stack<AvlNode<User>> stack = new Stack<>();
		AvlNode<User> puser = useravl.root;
		List<List<Double>> attachNumberList=new ArrayList<>();
		for (int i = 0; i < 80; i++) {
			List<Double> temp=new ArrayList<>();
			attachNumberList.add(temp);
		}
		
		// an zhao 2 de mi lai hua fen
		while (puser != null || !stack.isEmpty()) {
			while (puser != null) {
				User tempUser=puser.getElement();
				int followerC=tempUser.getFollowers_count();
				
				int index=MathCal.getTenBinIndex(followerC);
				
//				//System.out.println(followerC+"\t"+index);
//				if(tempUser.getAttackNumber()!=0){
//					double kk=tempUser.getBeRelyNumber()*1.0/tempUser.getAttackNumber();
//					
//					attachNumberList.get(index).add(kk);
//				}
				attachNumberList.get(index).add(1.0);
				
				stack.push(puser);
				puser = puser.left;
			}
			if (!stack.isEmpty()) {
				puser = stack.pop();
				puser = puser.right;
			}
		}
		
		for (int i = 0; i < 80; i++) {
			List<Double> temp=attachNumberList.get(i);
			if(temp.size()==0)
				continue;
//			double avg=MathCal.getAverage(temp);
//			double bzfc=MathCal.getBZFangCha(temp);
			double sum=MathCal.getSum(temp);
			int number=MathCal.RecoverTenBinIndex(i);
			//System.out.println(number+"\t"+avg+"\t"+bzfc);
			System.out.println(number+"\t"+sum);
		}
		System.out.println();
		
	}
	
	
}
