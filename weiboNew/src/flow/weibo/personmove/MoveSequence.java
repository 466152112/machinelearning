/**
 * 
 */
package flow.weibo.personmove;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import bean.User;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：MoveSequence
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年10月29日 下午4:23:22
 * @modifier：zhouge
 * @modified_time：2014年10月29日 下午4:23:22
 * @modified_note：
 * @version
 * 
 */
public class MoveSequence extends User {
	List<LocationTime> locationSequence = new ArrayList<>();
	boolean ifSort=false;
	/**
	 * @return the locationSequence
	 */
	public List<LocationTime> getLocationSequence() {
		return locationSequence;
	}

	/**
	 * @param locationSequence the locationSequence to set
	 */
	public void setLocationSequence(List<LocationTime> locationSequence) {
		this.locationSequence = locationSequence;
	}
	
	public List<LocationTime> getSortLocation(){
		if(!ifSort){
			Collections.sort(locationSequence);
			ifSort=true;
		}
		
		return locationSequence;
	}

}
