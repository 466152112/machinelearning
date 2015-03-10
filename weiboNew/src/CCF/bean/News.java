/**
 * 
 */
package CCF.bean;

import java.util.Calendar;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：News
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年9月29日 下午2:25:04
 * @modifier：zhouge
 * @modified_time：2014年9月29日 下午2:25:04
 * @modified_note：
 * @version
 * 
 */
public class News implements Comparable<News> {
	protected long newsId;
	protected String title;
	protected String content;
	protected Calendar createTime;
	protected int readTime=1;

	public static News getnew(NewsBrowseRecord newsBrowseRecord) {
		News news = new News();
		news.setNewsId(newsBrowseRecord.getNewsId());
		news.setTitle(newsBrowseRecord.getTitle());
		news.setContent(newsBrowseRecord.getContent());
		news.setCreateTime(newsBrowseRecord.getCreateTime());
		return news;
	}

	/**
	 * @return the readTime
	 */
	public int getReadTime() {
		return readTime;
	}

	/**
	 * @param readTime the readTime to set
	 */
	public void setReadTime(int readTime) {
		this.readTime = readTime;
	}
	/**
	 * @param readTime the readTime to set
	 */
	public void addReadTime() {
		this.readTime = this.readTime+1;
	}
	/**
	 * @return the newsId
	 */
	public long getNewsId() {
		return newsId;
	}

	/**
	 * @param newsId
	 *            the newsId to set
	 */
	public void setNewsId(long newsId) {
		this.newsId = newsId;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the createTime
	 */
	public Calendar getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime
	 *            the createTime to set
	 */
	public void setCreateTime(Calendar createTime) {
		this.createTime = createTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(News o) {
		if (this.newsId > o.newsId) {
			return 1;
		} else if (this.newsId < o.newsId) {
			return -1;
		} else {
			return 0;
		}
	}
	public String toString(){
		StringBuffer result=new StringBuffer();
		result.append(newsId+"\t");
		result.append(readTime+"\t");
		if(createTime==null){
			result.append("NULL"+"\t");
		}else {
			String create=createTime.get(Calendar.YEAR)+"/"+(createTime.get(Calendar.MONTH)+1)+"/"+createTime.get(Calendar.DATE)+"/"+createTime.get(Calendar.HOUR_OF_DAY);
			result.append(create+"\t");
		}
		result.append(title+"\t");
		result.append(content);
	
		return result.toString();
	}

}
