/**
 * 
 */
package util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import model.TKI.MainTKI;
import model.TKI.TKI;
import bean.OnePairTweet;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：CovertoWeiboInParallel
 * @class_describe： Parallele run to conver to weibo
 * @creator：zhouge
 * @create_time：2014年9月4日 下午12:31:09
 * @modifier：zhouge
 * @modified_time：2014年9月4日 下午12:31:09
 * @modified_note：
 * @version
 * 
 */
public class CovertoWeiboInParallel {
	private  int parallelNumber = 10;
	private  ForkJoinPool forkJoinPool;
	private  List<String> weiboList;
	private List<OnePairTweet> result;

	public CovertoWeiboInParallel(List<String> weiboList) {
		this.weiboList = weiboList;
		forkJoinPool = new ForkJoinPool(parallelNumber);
		result = getBPRInParallel();
	}

	/**
	 * @return
	 * @return
	 * @create_time锛?014骞?鏈?3鏃ヤ笅鍗?:59:26
	 * @modifie_time锛?014骞?鏈?3鏃?涓嬪崍6:59:26
	 */
	public ArrayList<OnePairTweet> getBPRInParallel() {
		return forkJoinPool.invoke(new BPRTask());

	}

	class BPRTask extends RecursiveTask<ArrayList<OnePairTweet>> {
		WriteUtil<String> writeUtil = new WriteUtil<String>();

		@Override
		protected ArrayList<OnePairTweet> compute() {
			List<RecursiveTask<OnePairTweet>> forks = new LinkedList<>();
			for (int index = 0, size = weiboList.size(); index < size; index++) {
				String oneLine=weiboList.get(index);
				CoverToOnePairWeibo coverToOnePairWeibo = new CoverToOnePairWeibo(
						oneLine);
				forks.add(coverToOnePairWeibo);
				coverToOnePairWeibo.fork();
			}

			ArrayList<OnePairTweet> result = new ArrayList<>();
			for (RecursiveTask<OnePairTweet> Task : forks) {
				OnePairTweet temp = Task.join();
				if (temp != null) {
					result.add(temp);
				}

			}
			// writeUtil.writeList(result, "feature-0-50.txt");
			return result;
		}

	}

	/**
	 * @return the result
	 */
	public List<OnePairTweet> getResult() {
		return result;
	}

}
