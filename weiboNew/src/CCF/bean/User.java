/**
 * 
 */
package CCF.bean;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：User   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年9月29日 下午2:26:58   
 * @modifier：zhouge   
 * @modified_time：2014年9月29日 下午2:26:58   
 * @modified_note：   
 * @version    
 *    
 */
public class User implements Comparable<User>{
	private long userId;
	private int recordtime=1;
	
	/**
	 * @return the recordtime
	 */
	public int getRecordtime() {
		return recordtime;
	}

	/**
	 * @param recordtime the recordtime to set
	 */
	public void setRecordtime(int recordtime) {
		this.recordtime = recordtime;
	}
	public void addRecordtime() {
		this.recordtime =this.recordtime+1;
	}
	/**
	 * @return the userId
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(User o) {
		if (this.userId>o.userId) {
			return 1;
		}else if(this.userId<o.userId){
			return -1;
		}else {
			return 0;
		}
	}	
	
}
