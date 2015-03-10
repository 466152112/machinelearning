/**
 * 
 */
package bean;

/**   
 *    
 * @progject_name：MF   
 * @class_name：UserItemPair   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年8月22日 下午8:54:35   
 * @modifier：zhouge   
 * @modified_time：2014年8月22日 下午8:54:35   
 * @modified_note：   
 * @version    
 *    
 */
public class UserItemPair<T extends Object> {
	private T userId;
	private T ItemId;
	private int rating;
	
	public UserItemPair(T userId,T ItemId,int rating){
		this.userId=userId;
		this.ItemId=ItemId;
		this.rating=rating;
	}
	public UserItemPair(T userId,T ItemId){
		this.userId=userId;
		this.ItemId=ItemId;
	}
	/**
	 * @return the userId
	 */
	public T getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(T userId) {
		this.userId = userId;
	}
	/**
	 * @return the itemId
	 */
	public T getItemId() {
		return ItemId;
	}
	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(T itemId) {
		ItemId = itemId;
	}
	/**
	 * @return the rating
	 */
	public int getRating() {
		return rating;
	}
	/**
	 * @param rating the rating to set
	 */
	public void setRating(int rating) {
		this.rating = rating;
	}
	
}
