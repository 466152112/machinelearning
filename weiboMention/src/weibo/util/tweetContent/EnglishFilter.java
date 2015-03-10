/**
 * 
 */
package weibo.util.tweetContent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EnglishFilter implements Filter{
		private String filterResultString;
		
		private final static Pattern AT_PATTERN = Pattern
				.compile("@([\u4e00-\u9fa5A-Z0-9a-z(é|ë|ê|è|à|â|ä|á|ù|ü|û|ú|ì|ï|î|í)._-]+){2,30}");
		
		private final static Pattern Retweet_PATTERN = Pattern
				.compile("RT @[\u4e00-\u9fa5a-zA-Z0-9_-]{2,30}");
		
		private final static Pattern TAG_PATTERN = Pattern.compile("#([^\\#|.]+)");
		
		private final static Pattern SHORTLINK_PATTERN = Pattern
				.compile("[http(s)?://([A-Z0-9a-z._-]{8,20})]");

		/**
		 * @param sentence
		 *            闇�杩囨护鐨勮鍙�?
		 */
		public EnglishFilter(String sentence) {
			// System.out.println(sentence);
			filterResultString = RemoveAtName(sentence);
			filterResultString = RemoveRouteName(filterResultString);
			filterResultString = RemoveShortLink(filterResultString);
			// System.out.println(filterResultString);
		}

		public EnglishFilter() {
			
		}

		public Set getAtName(String oneLine) {
			ArrayList<String> nameList = null;
			Matcher retweetMatcher = Retweet_PATTERN.matcher(oneLine);
			if (retweetMatcher.find()) {
				int index = oneLine.indexOf("RT @");
				oneLine=oneLine.substring(0,index);
			}
			Matcher m = AT_PATTERN.matcher(oneLine);
			while (m.find()) {
				if (nameList == null) {
					nameList = new ArrayList<>();
				}
				String atName = m.group();
				nameList.add(atName);
			}
			if (nameList != null) {
				for (int i = 0; i < nameList.size(); i++) {
					nameList.set(i, nameList.get(i).substring(1).trim());
				}
			}
//			if (nameList != null) {
//				for (String name : nameList) {
//					System.out.println(name);
//				}
//			}
			if (nameList!=null) {
				return new HashSet(nameList);
			}else {
				return null;
			}
		}
		public Set<String> getShortLink(String oneLine){
			Matcher mat = SHORTLINK_PATTERN.matcher(oneLine);
			Set<String> resultSet=new HashSet<>();
			if(mat.find()){
				String atName = mat.group();
				resultSet.add(atName);
			}
			return resultSet;
		}
		
		public ArrayList<String> getRouteName(String oneLine) {
			ArrayList<String> nameList = null;
			Matcher m = Retweet_PATTERN.matcher(oneLine);
			while (m.find()) {
				if (nameList == null) {
					nameList = new ArrayList<>();
				}
				String atName = m.group();
				nameList.add(atName);
			}
			if (nameList != null) {
				for (int i = 0; i < nameList.size(); i++) {
					nameList.set(i, nameList.get(i).substring(3).trim());
				}
			}	
//			if (nameList != null) {
//				for (String name : nameList) {
//					System.out.println(name);
//				}
//			}
			return nameList;
		}

		public String RemoveAtName(String oneLine) {
			if (oneLine == null) {
				return null;
			}
			Matcher m = AT_PATTERN.matcher(oneLine);
			oneLine = m.replaceAll(" ");
			return oneLine.trim();
		}

		public String RemoveRouteName(String oneLine) {
			if (oneLine == null) {
				return null;
			}
			Matcher m = Retweet_PATTERN.matcher(oneLine);
			oneLine = m.replaceAll(" ");
			return oneLine.trim();
		}

//		/**
//		 * @param oneLine
//		 * @return
//		 * @create_time�?014�?0�?6日下�?2:22:07
//		 * @modifie_time�?014�?0�?6�?下午12:22:07
//		 */
//		public static ArrayList<String> getNameFromString(String oneLine) {
//			ArrayList<String> nameList = null;
//			String temp = oneLine.trim();
	//
//			// first filter the retweet tag
//			int Flag = temp.indexOf("//@");
//			if (Flag == 0) {
//				return null;
//			}
//			if (Flag != -1) {
//				// System.out.println(temp + "\t" + Flag);
	//
//				temp = temp.substring(0, Flag).trim();
//			}
//			while (true) {
//				// the second is to find @name tag
//				int beginFlag = temp.indexOf("@");
//				int endFlag = temp.indexOf(' ', beginFlag);
//				int endFlag1 = temp.indexOf(':', beginFlag);
//				int endFlag2 = temp.indexOf('@', beginFlag + 1);
//				int endFlag3 = temp.indexOf('�?, beginFlag + 1);
//				int endFlag4 = temp.indexOf(',', beginFlag + 3);
//				if (endFlag2 != -1 && endFlag1 != -1) {
//					endFlag1 = endFlag1 > endFlag2 ? endFlag2 : endFlag1;
//				} else if (endFlag1 == -1 && endFlag2 != -1) {
//					endFlag1 = endFlag2;
//				}
//				if (endFlag3 != -1 && endFlag1 != -1) {
//					endFlag1 = endFlag1 > endFlag3 ? endFlag3 : endFlag1;
//				} else if (endFlag1 == -1 && endFlag3 != -1) {
//					endFlag1 = endFlag3;
//				}
//				if (endFlag4 != -1 && endFlag1 != -1) {
//					endFlag1 = endFlag1 > endFlag4 ? endFlag4 : endFlag1;
//				} else if (endFlag1 == -1 && endFlag4 != -1) {
//					endFlag1 = endFlag4;
//				}
//				if (endFlag1 != -1 && endFlag1 < endFlag) {
//					endFlag = endFlag1;
//				} else if (endFlag == -1 && endFlag1 != -1) {
//					endFlag = endFlag1;
//				}
	//
//				if (beginFlag != -1 && endFlag != -1) {
//					if (nameList == null)
//						nameList = new ArrayList<>();
//					nameList.add(temp.substring(beginFlag + 1, endFlag).trim());
//					temp = temp.substring(endFlag);
//					temp = temp.trim();
	//
//				} else if (beginFlag != -1 && endFlag == -1) {
//					if (nameList == null)
//						nameList = new ArrayList<>();
//					nameList.add(temp.substring(beginFlag + 1).trim());
//					break;
//				} else {
//					break;
//				}
//			}
//			// if (nameList!=null) {
//			// for (String name : nameList) {
//			// System.out.println(name);
//			// }
//			// }
//			return nameList;
//		}

//		public static ArrayList<String> getRouteNameFromString(String oneLine) {
//			ArrayList<String> nameList = null;
//			String temp = oneLine.trim();
//			if (temp.indexOf("//@") == -1) {
//				return nameList;
//			}
//			while (true) {
//				// the second is to find @name tag
//				int beginFlag = temp.indexOf("//@");
//				int endFlag = temp.indexOf(' ', beginFlag + 3);
//				int endFlag1 = temp.indexOf(':', beginFlag + 3);
//				int endFlag2 = temp.indexOf('@', beginFlag + 3);
//				int endFlag3 = temp.indexOf('�?, beginFlag + 3);
	//
//				if (endFlag2 != -1 && endFlag1 != -1) {
//					endFlag1 = endFlag1 > endFlag2 ? endFlag2 : endFlag1;
//				} else if (endFlag1 == -1 && endFlag2 != -1) {
//					endFlag1 = endFlag2;
//				}
//				if (endFlag3 != -1 && endFlag1 != -1) {
//					endFlag1 = endFlag1 > endFlag3 ? endFlag3 : endFlag1;
//				} else if (endFlag1 == -1 && endFlag3 != -1) {
//					endFlag1 = endFlag3;
//				}
	//
//				if (endFlag1 != -1 && endFlag1 < endFlag) {
//					endFlag = endFlag1;
//				} else if (endFlag == -1 && endFlag1 != -1) {
//					endFlag = endFlag1;
//				}
	//
//				if (beginFlag != -1 && endFlag != -1) {
//					if (nameList == null)
//						nameList = new ArrayList<>();
//					nameList.add(temp.substring(beginFlag + 3, endFlag).trim());
//					temp = temp.substring(endFlag);
//					temp = temp.trim();
	//
//				} else if (beginFlag != -1 && endFlag == -1) {
//					if (nameList == null)
//						nameList = new ArrayList<>();
//					nameList.add(temp.substring(beginFlag + 3).trim());
//					break;
//				} else {
//					break;
//				}
//			}
//			return nameList;
//		}

		/**
		 * @param sentence
		 * @return 杩囨护鐭摼鎺� 鍒涘缓鏃堕棿锛�014骞�鏈�4鏃ヤ笂鍗�?:44:36
		 *         淇敼鏃堕棿锛�014骞�鏈�4鏃�涓婂�?0:44:36
		 */
		public String RemoveShortLink(String sentence) {
			if (sentence == null) {
				return null;
			}
			// 妯�?紡鍖归厤銆傛妸鐭摼鎺ュ垹闄�?
			Matcher mat = SHORTLINK_PATTERN.matcher(sentence);
			sentence = mat.replaceAll(" ").trim();
			return sentence;
		}

//		/**
//		 * @param sentence
//		 * @return 杩囨护鐢ㄦ埛鍚� 鍒涘缓鏃堕棿锛�014骞�鏈�4鏃ヤ笅鍗�?:02:05
//		 *         淇敼鏃堕棿锛�014骞�鏈�4鏃�涓嬪�?2:02:05
//		 */
//		public static String filterUserName(String sentence) {
//			sentence = sentence.trim();
//			System.out.println(sentence);
//			// 澶勭悊濡�?/@涓婃捣骞垮憡濯掍綋鍦堬細 鐨勬儏褰�?
//			while (true) {
//				// 鏌ユ壘锛氬惈鏈�/@鏍囪瘑绗︾殑
//				int beginFlag = sentence.indexOf("//@");
//				int endFlag = sentence.indexOf(' ', beginFlag + 3);
//				int endFlag1 = sentence.indexOf(':', beginFlag + 3);
//				int endFlag2 = sentence.indexOf('@', beginFlag + 3);
//				int endFlag3 = sentence.indexOf('�?, beginFlag + 3);
	//
//				if (endFlag2 != -1 && endFlag1 != -1) {
//					endFlag1 = endFlag1 > endFlag2 ? endFlag2 : endFlag1;
//				} else if (endFlag1 == -1 && endFlag2 != -1) {
//					endFlag1 = endFlag2;
//				}
//				if (endFlag3 != -1 && endFlag1 != -1) {
//					endFlag1 = endFlag1 > endFlag3 ? endFlag3 : endFlag1;
//				} else if (endFlag1 == -1 && endFlag3 != -1) {
//					endFlag1 = endFlag3;
//				}
	//
//				if (endFlag1 != -1 && endFlag1 < endFlag) {
//					endFlag = endFlag1;
//				} else if (endFlag == -1 && endFlag1 != -1) {
//					endFlag = endFlag1;
//				}
	//
//				if (beginFlag != -1 && endFlag != -1) {
//					sentence = sentence.substring(0, beginFlag)
//							+ sentence.substring(endFlag);
//					sentence = sentence.trim();
//				} else if (beginFlag != -1 && endFlag == -1) {
//					sentence = sentence.substring(0, beginFlag);
//					sentence = sentence.trim();
//				} else {
//					break;
//				}
//			}
	//
//			// 澶勭悊濡�@reyalee 鐨勬儏褰�?
//			while (true) {
//				// 鏌ユ壘锛氬惈鏈�/@鏍囪瘑绗︾殑
//				int beginFlag = sentence.indexOf("@");
//				int endFlag = sentence.indexOf(' ', beginFlag);
//				int endFlag1 = sentence.indexOf(':', beginFlag);
//				int endFlag2 = sentence.indexOf('@', beginFlag + 1);
//				int endFlag3 = sentence.indexOf('�?, beginFlag + 1);
//				int endFlag4 = sentence.indexOf(',', beginFlag + 3);
//				if (endFlag2 != -1 && endFlag1 != -1) {
//					endFlag1 = endFlag1 > endFlag2 ? endFlag2 : endFlag1;
//				} else if (endFlag1 == -1 && endFlag2 != -1) {
//					endFlag1 = endFlag2;
//				}
//				if (endFlag3 != -1 && endFlag1 != -1) {
//					endFlag1 = endFlag1 > endFlag3 ? endFlag3 : endFlag1;
//				} else if (endFlag1 == -1 && endFlag3 != -1) {
//					endFlag1 = endFlag3;
//				}
//				if (endFlag4 != -1 && endFlag1 != -1) {
//					endFlag1 = endFlag1 > endFlag4 ? endFlag4 : endFlag1;
//				} else if (endFlag1 == -1 && endFlag4 != -1) {
//					endFlag1 = endFlag4;
//				}
//				if (endFlag1 != -1 && endFlag1 < endFlag) {
//					endFlag = endFlag1;
//				} else if (endFlag == -1 && endFlag1 != -1) {
//					endFlag = endFlag1;
//				}
	//
//				if (beginFlag != -1 && endFlag != -1) {
//					sentence = sentence.substring(0, beginFlag)
//							+ sentence.substring(endFlag);
//					sentence = sentence.trim();
//				} else if (beginFlag != -1 && endFlag == -1) {
//					sentence = sentence.substring(0, beginFlag);
//					sentence = sentence.trim();
//				} else {
//					break;
//				}
//			}
//			System.out.println(sentence);
//			return sentence;
//		}

		/**
		 * @return the filterResultString
		 */
		public String getFilterResultString() {
			return filterResultString;
		}

		/* (non-Javadoc)
		 * @see weibo.util.tweetContent.Filter#getTag(java.lang.String)
		 */
		@Override
		public String getTag(String oneLine) {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * @return the filterResultString
		 */
		public String getFilterResultString(String oneline) {
			String temp = RemoveAtName(oneline);
			temp = RemoveRouteName(temp);
			temp = RemoveShortLink(temp);
			return temp;
		}

		/* (non-Javadoc)
		 * @see weibo.util.tweetContent.Filter#getAtNameInList(java.lang.String)
		 */
		@Override
		public List<String> getAtNameInList(String oneLine) {
			ArrayList<String> nameList = null;
			Matcher retweetMatcher = Retweet_PATTERN.matcher(oneLine);
			if (retweetMatcher.find()) {
				int index = oneLine.indexOf("RT @");
				oneLine=oneLine.substring(0,index);
			}
			Matcher m = AT_PATTERN.matcher(oneLine);
			while (m.find()) {
				if (nameList == null) {
					nameList = new ArrayList<>();
				}
				String atName = m.group();
				nameList.add(atName);
			}
			if (nameList != null) {
				for (int i = 0; i < nameList.size(); i++) {
					nameList.set(i, nameList.get(i).substring(1).trim());
				}
			}
//			if (nameList != null) {
//				for (String name : nameList) {
//					System.out.println(name);
//				}
//			}
			if (nameList!=null) {
				Set<String> tempSet=new HashSet<>(nameList);
				return new ArrayList<>(tempSet);
			}else {
				return null;
			}
		}
}
