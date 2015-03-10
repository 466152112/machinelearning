package util;
import java.util.Comparator;

import bean.OnePairTweet;

public  class TweetCalendarComparator implements Comparator {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Object o1, Object o2) {
			// TODO Auto-generated method stub
			OnePairTweet one1=(OnePairTweet)o1;
			OnePairTweet one2=(OnePairTweet)o2;
			int flag=one1.getCalendar().compareTo(one2.getCalendar());
			
			return flag;
		}
		
	}