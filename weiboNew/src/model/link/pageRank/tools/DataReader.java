package model.link.pageRank.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DataReader {
	File file;
	FileReader fileReader;
	BufferedReader bufferedReader;
	
	//���췽��
	public DataReader(String fileName){
		file=new File(fileName);
		try {
			fileReader=new FileReader(file);
			bufferedReader=new BufferedReader(fileReader);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.print("failure to open the file :"+fileName+" ");
		}
	}
	
	/*
	 * ��ȡdata����������?
	 * 
	 */
	public HashMap<String, HashSet<String>> getForwardEdge() throws IOException{
		HashMap<String, HashSet<String>> nodeHashMap=new HashMap<String, HashSet<String>>();
		String temp;
		temp=bufferedReader.readLine();
		
		while(temp!=null){
			//�Կո񻮷�
			String [] aa=temp.split(",");
			String start=aa[0];
			String end=aa[1];
			if (nodeHashMap.containsKey(start)) {
				HashSet<String> tempArrayList=nodeHashMap.get(start);
				tempArrayList.add(end);
				nodeHashMap.put(start, tempArrayList);
			}
			else {
				HashSet<String> tempArrayList=new HashSet<String>();
				tempArrayList.add(end);
				nodeHashMap.put(start, tempArrayList);
			}
			temp=bufferedReader.readLine();
		}
		return nodeHashMap;
	}
	
	/*
	 * ��ȡ�������?
	 */
	public HashMap<String, HashSet<String>> getBackEdge() throws IOException{
		HashMap<String, HashSet<String>> nodeHashMap=new HashMap<String, HashSet<String>>();
		String temp;
		temp=bufferedReader.readLine();
		
		while(temp!=null){
			//�Կո񻮷�
			
			String [] aa=temp.split(",");
			String start=aa[1];
			String end=aa[0];
			if (nodeHashMap.containsKey(start)) {
				HashSet<String> tempArrayList=nodeHashMap.get(start);
				tempArrayList.add(end);
				nodeHashMap.put(start, tempArrayList);
			}
			else {
				HashSet<String> tempArrayList=new HashSet<String>();
				tempArrayList.add(end);
				nodeHashMap.put(start, tempArrayList);
			}
			temp=bufferedReader.readLine();
		}
		return nodeHashMap;
	}
	/*public HashMap<Node, Node> getLine() throws IOException {
		HashMap<Node, ArrayList<Node>> nodeHashMap=new HashMap<Node, ArrayList<Node>>();
		
		String temp;
		temp=bufferedReader.readLine();
		
		while(temp!=null){
			//�Կո񻮷�
			String [] aa=temp.split(" ");
			String start=aa[0];
			String end=aa[1];
			Node startNode=new Node(start);
			Node endNode=new Node(end);
			
			//�����ʼ�ڵ����
			if (nodeHashMap.containsKey(startNode)) {
				startNode.setOutdegree(outdegree)
				ArrayList<Node> arrayList=nodeHashMap.get(startNode);
				if (arrayList==null) {
					
				}
			}
			temp=bufferedReader.readLine();
		}
		return nodeHashMap;
	}*/
	protected void finalize(){
		try {
			bufferedReader.close();
			fileReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.print("�ر��ļ�ʧ��");
		}
		
		
	}
}
