package model.link.pageRank.algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;



import model.link.pageRank.tools.*;

public class HITS {
	//���ü���ֵ
	final double MIN=0.000000000001;
	int MAXTIME=10000;
	//��ȡ��end�㵽start�����?
	HashMap<String, HashSet<String>> endToStartSource;
	//��ȡ��start�㵽end�����?
	HashMap<String, HashSet<String>> startTOEndSource;
	//�ܽڵ���
	 static int  nodeNumber;
	 //���ڵ����?
	static HashSet<String> nodeName;
	
	//�洢��һ�θ��ڵ��AHֵ
	HashMap<String, AH> lashAH=new HashMap<String, AH>();
	
	//�洢���θ��ڵ��AHֵ
	HashMap<String, AH> currentAH=new HashMap<String, AH>();
	/*  �Խڵ��������?
	 * @param filename ���Դ��?
	 * @return ���ؽڵ�������  
	 */
	
	public ArrayList<String> getAH(String fileName){
		try {
			endToStartSource=new DataReader(fileName).getBackEdge();
			startTOEndSource=new DataReader(fileName).getForwardEdge();
			//��ȡ���ڵ���ƣ��ڵ���û���ظ��?
			nodeName=gettotalNode(endToStartSource);
			nodeNumber=nodeName.size();
		
			//��ʼ������ A=1 H=1
			Iterator<String> iterator=nodeName.iterator();
			while(iterator.hasNext()){
				String key=iterator.next();
				lashAH.put(key,new AH(1.0, 1.0) );
			}
			
			//�����ʼ��?
			getAH();
			int count=0;
			while((MAXTIME-->0)&&!checkAH()){
				System.out.println("HITS:"+count+++":time");
				//����lashAH
				Iterator<String> iterator1=lashAH.keySet().iterator();
				while (iterator1.hasNext()) {
					String key = (String) iterator1.next();
					lashAH.put(key, currentAH.get(key));
					}
				getAH();
			}
			TwoValueComparator twc=new TwoValueComparator(lashAH);
			List<String> keys = new ArrayList<String>(
					lashAH.keySet());
			Collections.sort(keys, twc);
			
			
			// �õ��ź�����б�?result
			ArrayList<String> result = new ArrayList<String>();
			for (String key : keys) {
				result.add(key+"("+lashAH.get(key).getA()+":"+lashAH.get(key).getH()+")");
			}
			return result;
		} catch (IOException e) {
			System.out.println("failure to get the source "+fileName+ " in the PageRank class");
			return null;
		}
		
		
	}
	/*
	 * ����AH
	 * @result ����currentAH �˴μ�����ڵ��AHֵ
	 */
	
	private void getAH(){
		
		Iterator<String> nodeNameIterator=nodeName.iterator();
		while(nodeNameIterator.hasNext()){
			String presentNode=nodeNameIterator.next();
			Object[] neighbour;
			int size;
			//���㵱ǰ�ڵ��Aֵ
			double tempA=0;
			neighbour=endToStartSource.get(presentNode).toArray();
			size=neighbour.length;
			for (int i = 0; i < size; i++) {
				tempA+=lashAH.get((String)neighbour[i]).getH();
			}
			//���㵱ǰ�ڵ��Hֵ
			double tempH=0;
			neighbour=endToStartSource.get(presentNode).toArray();
			size=neighbour.length;
			for (int i = 0; i < size; i++) {
				tempH+=lashAH.get((String)neighbour[i]).getA();
			}
			currentAH.put(presentNode, new AH(tempA, tempH));
			
		}
		// ��һ��(ʹ������?
		AH maxAHValue=getMaxAH(currentAH);
		double maxA=maxAHValue.getA();
		double maxH=maxAHValue.getH();
		double tempa,temph;
		Iterator<String> currentAHIterator=currentAH.keySet().iterator();
		while(currentAHIterator.hasNext()){
			String presentNode=currentAHIterator.next();
			tempa=currentAH.get(presentNode).getA();
			temph=currentAH.get(presentNode).getH();
			currentAH.put(presentNode, new AH(tempa/maxA,temph/maxH));
			
		}
		
	}
	/*
	 * ��ȡ���A Hֵ���ڱ�׼��
	 * @return ����A Hֵ ������ AH����
	 */
	private AH getMaxAH(HashMap<String, AH > temp){
		AH tempAh=new AH();
		double A=0;
		double H=0;
		Iterator<String> iterator=temp.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if (temp.get(key).getA()>A) {
				A=temp.get(key).getA();
			}
			if (temp.get(key).getH()>H) {
				H=temp.get(key).getH();
			}
		}
		tempAh.setA(A);
		tempAh.setH(H);
		return tempAh;
	}
	/*
	 *��ȡ�ܽڵ�
	 *@param source  �ڵ�hashmap���?
	 */
	private HashSet<String> gettotalNode(HashMap<String, HashSet<String>> source){
		
		HashSet<String> temp=new HashSet<String>();
		Iterator<String> iterator=source.keySet().iterator();
		while (iterator.hasNext()) {
			String key= (String) iterator.next();
			temp.add(key);
			Object[] tempArray=source.get(key).toArray();
			for(int i=0;i<tempArray.length;i++)
				temp.add((String)tempArray[i]);
		}
		return temp;
	}
	
	/*
	 * ���AHƽ�����ֵ�Ƿ���Ҫ��?
	 * @result ���?MIN ����false ����true
	 */
	private  boolean checkAH(){
		boolean result=true;
		double temp=0;
		Iterator<String> iterator=lashAH.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			temp+=(currentAH.get(key).getA()-lashAH.get(key).getA())*(currentAH.get(key).getA()-lashAH.get(key).getA())+
			(currentAH.get(key).getH()-lashAH.get(key).getH())*(currentAH.get(key).getH()-lashAH.get(key).getH());
		}
		if(Math.sqrt(temp)>MIN)
		 return	result=false;
		return result;
	}
}
