/**
 * 
 */
package MF.bean;

/**
 * 
 * @progject_name：MF
 * @class_name：Rating
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年10月9日 下午4:07:38
 * @modifier：zhouge
 * @modified_time：2014年10月9日 下午4:07:38
 * @modified_note：
 * @version
 * 
 */
public class Rating {
	private User user;
	private Item item;
	private double rating;
	
	public Rating(User user,Item item,double rating){
		this.user=user;
		this.item=item;
		this.rating=rating;
	}
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	/**
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}
	/**
	 * @param item the item to set
	 */
	public void setItem(Item item) {
		this.item = item;
	}
	/**
	 * @return the rating
	 */
	public double getRating() {
		return rating;
	}
	/**
	 * @param rating the rating to set
	 */
	public void setRating(double rating) {
		this.rating = rating;
	}

}
