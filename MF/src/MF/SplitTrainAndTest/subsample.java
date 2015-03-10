/**
 * 
 */
package MF.SplitTrainAndTest;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import MF.bean.Folder;
import MF.bean.RatingDocument;

public class subsample {

	public HashMap<Integer, List<Integer>> getRating(RatingDocument document) {
		HashMap<Integer, List<Integer>> result = new HashMap<>();
		List<Integer> oneItemRating = new LinkedList<>();
		for (String oneLine : document.getLines()) {
			String[] splitTemp = oneLine.split(",");
			oneItemRating.add(Integer.valueOf(splitTemp[0]));
		}
		result.put(document.getItemId(), oneItemRating);
		return result;
	}

	class FolderSearchTask extends
			RecursiveTask<HashMap<Integer, List<Integer>>> {
		private final Folder folder;

		FolderSearchTask(Folder folder) {
			super();
			this.folder = folder;
		}

		@Override
		protected HashMap<Integer, List<Integer>> compute() {

			HashMap<Integer, List<Integer>> result = new HashMap<>();

			List<RecursiveTask<HashMap<Integer, List<Integer>>>> forks = new LinkedList<>();

			for (Folder subFolder : folder.getSubFolders()) {
				FolderSearchTask task = new FolderSearchTask(subFolder);
				forks.add(task);
				task.fork();
			}

			for (RatingDocument document : folder.getDocuments()) {
				DocumentSearchTask task = new DocumentSearchTask(document);
				forks.add(task);
				task.fork();
			}
			// �ϲ����
			for (RecursiveTask<HashMap<Integer, List<Integer>>> task : forks) {
				HashMap<Integer, List<Integer>> oneDocRating = task.join();
				Iterator<Integer> itemIdIterator = oneDocRating.keySet()
						.iterator();
				int itemId = itemIdIterator.next();
				result.put(itemId, oneDocRating.get(itemId));
			}
			return result;
		}
	}

	/*
	 * ...........................................................................
	 * ..............
	 */

	class DocumentSearchTask extends
			RecursiveTask<HashMap<Integer, List<Integer>>> {
		private final RatingDocument document;

		DocumentSearchTask(RatingDocument document) {
			super();
			this.document = document;
		}

		@Override
		protected HashMap<Integer, List<Integer>> compute() {
			return getRating(document);
		}
	}

	/*
	 * ...........................................................................
	 * ..............
	 */
	// ��ֺϲ���
	private final ForkJoinPool forkJoinPool = new ForkJoinPool();

	HashMap<Integer, List<Integer>> getRatingInParallel(Folder folder) {
		return forkJoinPool.invoke(new FolderSearchTask(folder));
	}

	/*
	 * ...........................................................................
	 * ..............
	 */

	public static void main(String[] args) throws IOException {
		subsample wordCounter = new subsample();
		Folder folder = Folder
				.fromDirectory(new File(
						"H:/dataset/recommendation/nf_prize_dataset/download/1/"));
		// Folder folder = Folder.fromDirectory(new File(
		// "/home/zhouge/database/recommendation/netflix/training_set"));
		HashMap<Integer, List<Integer>> rating = new HashMap<>();

		rating = wordCounter.getRatingInParallel(folder);
		wordCounter.getRequireNumberItemAndUser(rating,5000,10000,10);
		System.out.println();
	}

	public void getRequireNumberItemAndUser(
			HashMap<Integer, List<Integer>> rating, int numberOfItem,
			int numberOfUser, int limit) {
		HashMap<Integer, List<Integer>> result = new HashMap<>();
		
		HashMap<Integer,List<Integer>> UserSet=new HashMap<>();
		HashMap<Integer,List<Integer>> ItemSet=new HashMap<>();
		
		//���ҷ��
		while(ItemSet.size()<numberOfItem*1.6){
			Iterator<Integer> ItemIds=rating.keySet().iterator();
			while (ItemIds.hasNext()) {
				int itemId=ItemIds.next();
				if (rating.get(itemId).size()>10) {
					ItemSet.put(itemId, rating.get(itemId));
				}
			}
		}
		
		Iterator<Integer> ItemIds=ItemSet.keySet().iterator();
		while(ItemIds.hasNext()){
			int itemId=ItemIds.next();
			List<Integer> userList=ItemSet.get(itemId);
			for (Integer userId : userList) {
				if (UserSet.containsKey(userId)) {
					UserSet.get(userId).add(itemId);
				}else {
					List<Integer> itemList=new LinkedList<>();
					itemList.add(itemId);
					UserSet.put(userId, itemList);
				}
			}
		}
		System.out.println(ItemSet.size());
		System.out.println(UserSet.size());
	//	return result;
	}
}
