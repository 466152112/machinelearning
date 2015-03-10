package Remind.statics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tool.FileTool.FileUtil;
import tool.FileTool.ReadUtil;
import tool.FileTool.WriteUtil;
import tool.MapTool.TwoValueComparatorInString;
import tool.TimeTool.CalanderUtil;
import tool.dataStucture.AvlTree;
import weibo.util.ReadUser;
import weibo.util.ReadWeibo;
import weibo.util.tweetContent.ChineseSpliter;
import weibo.util.tweetContent.ChineseFilter;
import Remind.util.BaseUitl;
import bean.OnePairTweet;
import bean.Retweetlist;
import bean.User;
import bean.Remind.RemType;
import bean.Remind.Reminder;

public class CovertToKeyword {
	
	public static void main(String[] srg) {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");  
		 String path = "/home/zhouge/database/weibo/new/";
		String profileFile = path + "userid/profile.txt";
		String reminderFile = path + "rem.txt";
		String sourcePath="/home/zhouge/database/weibo/2012/content/";
		String keywordPath="/home/zhouge/database/weibo/2012/keyword1/";
		String tagFile=path+"userid/usertagfinal.txt";
		HashMap<String, Long> usernameAndIdMap = null;
		usernameAndIdMap = ReadUser.getuserNameAndIdMapFromprofileFile(profileFile);
		AvlTree<User> useravl = ReadUser.getuserFromprofileFile(profileFile);
		useravl=BaseUitl.addTagToUser(useravl, tagFile);
		CovertToKeyword main=new CovertToKeyword();
		//main.countDifferentTagLevelGetRemNumber(useravl, reminderFile, path);
		
	}
	
	

	
}
