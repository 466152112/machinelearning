package model.link.pageRank.algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import model.link.pageRank.tools.*;
public class PR {
	//���ü���ֵ
	final double MIN=0.00000000000000001;
	int MAXTIME=10000;
	//���ñ�������?
	final double RATIO=0.85; 
	//��ȡ��end�㵽start�����?
	HashMap<String, HashSet<String>> endToStartSource;
	//��ȡ��start�㵽end�����?
	HashMap<String, HashSet<String>> startTOEndSource;
	//�ܽڵ���
	 static int  nodeNumber;
	 //���ڵ����?
	static HashSet<String> nodeName;
	
	//�洢��һ�θ��ڵ��PRֵ
	HashMap<String, Double> lastPR=new HashMap<String, Double>();
	
	//�洢���θ��ڵ��PRֵ
	HashMap<String, Double> currentPR=new HashMap<String, Double>();
	/*  �Խڵ��������?
	 * @param filename ���Դ��?
	 * @return ���ؽڵ�������  
	 */
	
	public ArrayList<String> getPageRank(String fileName){
		try {
			endToStartSource=new DataReader(fileName).getBackEdge();
			startTOEndSource=new DataReader(fileName).getForwardEdge();
			//��ȡ���ڵ���ƣ��ڵ���û���ظ��?
			nodeName=gettotalNode(endToStartSource);
			nodeNumber=nodeName.size();
		
			//��ʼ������
			Iterator<String> iterator=nodeName.iterator();
			while(iterator.hasNext()){
				String key=iterator.next();
				lastPR.put(key, 1.0);
			}
			
			//�����ʼ��?
			getPageRank();
			int count=0;
			while((MAXTIME-->0)&&!checkPR()){
				System.out.println("PR:"+count+++":time");
				//����PR
				Iterator<String> iterator1=lastPR.keySet().iterator();
				while (iterator1.hasNext()) {
					String key = (String) iterator1.next();
					lastPR.put(key, currentPR.get(key));
					}
				getPageRank();
			}
		
			ByValueComparator bvc = new ByValueComparator(lastPR);
			List<String> keys = new ArrayList<String>(
					lastPR.keySet());
			Collections.sort(keys, bvc);
			
			
			// �õ��ź�����Ƽ��б�?result
			ArrayList<String> result = new ArrayList<String>();
			for (String key : keys) {
				result.add(key+"("+lastPR.get(key)+")");
			}
			return result;
		} catch (IOException e) {
			System.out.println("failure to get the source "+fileName+ " in the PageRank class");
			return null;
		}
		
		
	}
	/*
	 * ����PageRank
	 * @result ����currentPR �˴μ�����ڵ��������
	 */
	
	private void getPageRank(){
		
		Iterator<String> iterator=nodeName.iterator();
		while(iterator.hasNext()){
			double temp=0;
			String presentNode=iterator.next();
			
			//���ǰ�ڵ����ָ����Ľڵ㣬����㴫�ݸ�ǰ�ڵ������ֵ��?����Ҫ�����������?
			if (endToStartSource.get(presentNode)!=null) {
				Object[] neighbor= endToStartSource.get(presentNode).toArray();
				int size=neighbor.length;
				for (int i = 0; i < size; i++) {
					String aa=(String) neighbor[i];
					temp+=RATIO*lastPR.get(aa)/(startTOEndSource.get(aa).size()*1.0);
				}
			}
			
			temp+=(1-RATIO)/(nodeNumber*1.0);
			currentPR.put(presentNode, temp);
		}
		
		
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
	 * ���PRƽ�����ֵ�Ƿ���Ҫ��?
	 * @param check �˴θ��ڵ�PR���?
	 * @param ref �ϴθ��ڵ�PR���?
	 * @result ���?MIN ����false ����true
	 */
	private  boolean checkPR(){
		boolean result=true;
		double temp=0;
		int i=0;
		Iterator<String> iterator=lastPR.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			temp+=(currentPR.get(key)-lastPR.get(key))*(currentPR.get(key)-lastPR.get(key));
			i++;
		}
		if(Math.sqrt(temp)>MIN)
		 return	result=false;
		return result;
	}
}
