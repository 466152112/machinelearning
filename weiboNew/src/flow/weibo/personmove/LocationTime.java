/**
 * 
 */
package flow.weibo.personmove;

import java.util.Calendar;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：dfds   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年10月29日 下午4:28:06   
 * @modifier：zhouge   
 * @modified_time：2014年10月29日 下午4:28:06   
 * @modified_note：   
 * @version    
 *    
 */
public class LocationTime implements Comparable<LocationTime>{
	//1000301920	0020	21	飞机场	2012-10-19 22:52:31	17702
	String city,typeLocation;
	Calendar time;
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return the typeLocation
	 */
	public String getTypeLocation() {
		return typeLocation;
	}
	/**
	 * @param typeLocation the typeLocation to set
	 */
	public void setTypeLocation(String typeLocation) {
		this.typeLocation = typeLocation;
	}
	/**
	 * @return the time
	 */
	public Calendar getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(Calendar time) {
		this.time = time;
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(LocationTime o) {
		// TODO Auto-generated method stub
		if(o.getTime().compareTo(this.time)>0){
			return 1;
		}else if(o.getTime().compareTo(this.time)<0){
			return -1;
		}
		return 0;
	}
	
	
}
