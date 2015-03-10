/**
 * 
 */
package model.IBPR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.RecursiveTask;

import MF.Interface.IterativeRanking;
import MF.Interface.Recommendation;
import MF.bean.Feature;
import util.MathCal;
import util.MatrixUtil;
import util.WriteUtil;

/**
 * 
 * @progject_name锟斤拷weiboNew
 * @class_name锟斤拷MF
 * @class_describe锟斤拷 the simple MF
 * @creator锟斤拷zhouge
 * @create_time锟斤拷2014锟斤拷8锟斤拷27锟斤拷 锟斤拷锟斤拷10:17:15
 * @modifier锟斤拷zhouge
 * @modified_time锟斤拷2014锟斤拷8锟斤拷27锟斤拷 锟斤拷锟斤拷10:17:15
 * @modified_note锟斤拷
 * @version
 * 
 */
public class IBPR extends IterativeRanking{
	
	double basis_reg=0.01;
	/**
	 * @param LatentDimension
	 * @param ratingMatrix
	 * @param maxIter
	 * @param learnRatio
	 * @param theta
	 */

	public IBPR(int LatentDimension,
			 Map<Long, Set<Long>> ratingMatrix,
			 Map<Long, Set<Long>> testMatrix,int maxIter,
			double learnRatio, double theta) {
		super( LatentDimension,ratingMatrix, testMatrix, maxIter, learnRatio, theta);
		System.out.println("enter the IBPR Class--------------");
		ranking=true;
		
		userBasis=new HashMap<>();
		itemBasis=new HashMap<>();
		for (long userid : userList) {
			userBasis.put(userid, random.nextDouble());
		}
		for (long itemid : itemList) {
			itemBasis.put(itemid, random.nextDouble());
		}
		
	}
	
	@Override
	public void IterLearningBySGD() {
		System.out.println("IterBySGD----------------");
		
		while (iterCount++ < this.maxIter) {
			
			userGradient();
			
			itemGradient();
			if (iterCount%100==0) {
				System.out.println("enter the BPR Class--------------");
				calculateMeasure();
			}
		}
		calculateMeasure();
	}
	
	/**
	 * 
	 *@create_time：2014年12月17日下午1:27:46
	 *@modifie_time：2014年12月17日 下午1:27:46
	  
	 */
	private void userGradient() {
		//复制用户列表，并且打乱顺序
		List<Long> PuserList=new ArrayList<>(userList);
		//Collections.shuffle(PuserList);
		for (Long puserid : PuserList) {
	
			for (Long pitemid : ratingMatrix.get(puserid)) {
				double[] puserFeature = userFeaturemap.get(puserid).copyFeature();	
				double[] pitemFeature = ItemFeaturemap.get(pitemid)
						.copyFeature();
				//随机抽取一个负例
				long compareItemid=0;
				do{
					int itemIndex=random.nextInt(itemSize);
					compareItemid=itemList.get(itemIndex);
				}while(ratingMatrix.get(puserid).contains(compareItemid));
				
				double[] compareitemFeature = ItemFeaturemap.get(compareItemid)
						.copyFeature();
				//计算评分
				double Ratingui1 = MatrixUtil.vectorMul(pitemFeature,
						puserFeature);
				double Ratingui2 = MatrixUtil.vectorMul(compareitemFeature,
						puserFeature);
			
				double pitem_basis=itemBasis.get(pitemid);
				double compareitem_basis=itemBasis.get(compareItemid);
				
				double Ratingui12=pitem_basis+Ratingui1-Ratingui2-compareitem_basis;
				
				double cmg = MathCal.sigmoid(-Ratingui12);
				
				double[] updataU=MatrixUtil.vectorMul(MatrixUtil.vectormin(pitemFeature, compareitemFeature), cmg);
				updataU=MatrixUtil.vectormin(updataU, MatrixUtil.vectorMul(puserFeature, reg_user));
				updataU=MatrixUtil.vectorMul(updataU, learnRatio);
				userFeaturemap.get(puserid).addFeature(updataU);
				
				double[] updateI1=MatrixUtil.vectorMul(puserFeature, cmg);
				updateI1=MatrixUtil.vectormin(updateI1, MatrixUtil.vectorMul(pitemFeature, reg_Item));
				updateI1=MatrixUtil.vectorMul(updateI1, learnRatio);
				ItemFeaturemap.get(pitemid).addFeature(updateI1);
				
				double[] updateI2=MatrixUtil.vectorMul(puserFeature, -cmg);
				updateI2=MatrixUtil.vectormin(updateI2, MatrixUtil.vectorMul(compareitemFeature, reg_Item));
				updateI2=MatrixUtil.vectorMul(updateI2, learnRatio);
				ItemFeaturemap.get(compareItemid).addFeature(updateI2);

				//basis
				itemBasis.put(pitemid, pitem_basis+learnRatio*(cmg-basis_reg*pitem_basis));
				itemBasis.put(compareItemid, compareitem_basis+learnRatio*(-cmg-basis_reg*compareitem_basis));
				
				}
			}
	}

	/**
	 * 
	 *@create_time：2014年12月17日下午1:27:49
	 *@modifie_time：2014年12月17日 下午1:27:49
	  
	 */
	private void itemGradient() {
		//复制用户列表，并且打乱顺序
		List<Long> PitemList=new ArrayList<>(itemList);
		//Collections.shuffle(PitemList);
		for (Long pitemid : PitemList) {
			
			for (Long puserId : item_user_Map.get(pitemid)) {
				double[] pitemFeature = ItemFeaturemap.get(pitemid).copyFeature();
				double[] puserFeature = userFeaturemap.get(puserId)
						.copyFeature();
				//随机抽取一个负例
				long compareUserid=0;
				do{
					int userIndex=random.nextInt(userSize);
					compareUserid=userList.get(userIndex);
				}while(item_user_Map.get(pitemid).contains(compareUserid));
				
				double[] compareUserFeature = userFeaturemap.get(compareUserid)
						.copyFeature();
				//计算评分
				double Ratingui1 = MatrixUtil.vectorMul(puserFeature,pitemFeature);
				double Ratingui2 = MatrixUtil.vectorMul(compareUserFeature,
						pitemFeature);
				double puser_basis=userBasis.get(puserId);
				double compareuser_basis=userBasis.get(compareUserid);
				
				double Ratingiu12=puser_basis+Ratingui1-Ratingui2-compareuser_basis;
				
				double cmg = MathCal.sigmoid(-Ratingiu12);
				
				double[] updataI=MatrixUtil.vectorMul(MatrixUtil.vectormin(puserFeature, compareUserFeature), cmg);
				updataI=MatrixUtil.vectormin(updataI, MatrixUtil.vectorMul(pitemFeature, reg_Item));
				updataI=MatrixUtil.vectorMul(updataI, learnRatio);
				ItemFeaturemap.get(pitemid).addFeature(updataI);
				
				double[] updateU1=MatrixUtil.vectorMul(pitemFeature, cmg);
				updateU1=MatrixUtil.vectormin(updateU1, MatrixUtil.vectorMul(puserFeature, reg_user));
				updateU1=MatrixUtil.vectorMul(updateU1, learnRatio);
				userFeaturemap.get(puserId).addFeature(updateU1);
				
				double[] updateU2=MatrixUtil.vectorMul(pitemFeature, -cmg);
				updateU2=MatrixUtil.vectormin(updateU2, MatrixUtil.vectorMul(compareUserFeature, reg_user));
				updateU2=MatrixUtil.vectorMul(updateU2, learnRatio);
				userFeaturemap.get(compareUserid).addFeature(updateU2);

				//basis update
				userBasis.put(puserId, puser_basis+learnRatio*(cmg-basis_reg*puser_basis));
				userBasis.put(compareUserid, compareuser_basis+learnRatio*(-cmg-basis_reg*compareuser_basis));
				
				}
			}
	}


}
