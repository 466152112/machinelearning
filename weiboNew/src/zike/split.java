/**
 * 
 */
package zike;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import util.ChineseSpliter;
import util.ReadUtil;
import util.WriteUtil;

/**
 * 
 * @progject_name��weiboNew
 * @class_name��split
 * @class_describe��
 * @creator��zhouge
 * @create_time��2014��8��14�� ����1:38:12
 * @modifier��zhouge
 * @modified_time��2014��8��14�� ����1:38:12
 * @modified_note��
 * @version
 * 
 */
public class split {
	public static void main(String[] ge) {
		String pathString="C:/Users/zhouge/Desktop/��/";
		String caiFileName=pathString+"���״�ȫ.txt";
		String wordSet=pathString+"wordset.txt";
		String caiMatrixString=pathString+"caiMatrix.txt";
		ArrayList<String> text=(ArrayList<String>) new ReadUtil().readFileByLine(caiFileName);
		ArrayList<Cai> cailist=new ArrayList<>();
		HashMap<String, Integer> wordMap=new HashMap<>();
		for (String oneLine : text) {
			
			String[] splits=oneLine.split("\t");
			String matrix=splits[3];
			ChineseSpliter spliter=new ChineseSpliter();
			ArrayList<String> temp;
			try {
				temp = spliter.spliterChinese(matrix);
				for (String word : temp) {
					if (wordMap.containsKey(word)) {
						wordMap.put(word, wordMap.get(word)+1);
					}else {
						wordMap.put(word, 1);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		//	matrix=spliter.spliter(matrix);
			//System.out.println( newCai.getId()+"\t" +matrix);
			
		}
		WriteUtil writeUtil=new WriteUtil<>();
		
		writeUtil.writemapkeyAndValue(wordMap, wordSet);
		
		for (String oneLine : text) {
			Cai newCai =new Cai();
			String[] splits=oneLine.split("\t");
			newCai.setId(Integer.valueOf(splits[0]));
			newCai.setName(splits[1]);
			newCai.setType(splits[2]);
			String matrix=splits[3];
			ChineseSpliter spliter=new ChineseSpliter();
			ArrayList<String> temp;
			try {
				temp = spliter.spliterChinese(matrix);
				ArrayList<String> remove=new ArrayList<>();
				for (String word : temp) {
					if (wordMap.get(word)>4) {
						remove.add(word);
					}
				}
				temp.retainAll(remove);
				newCai.setMaterial(temp);
				cailist.add(newCai);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		ArrayList<String> temp=new ArrayList<>();
		for (Cai cai : cailist) {
			temp.add(cai.tostring());
		}
		writeUtil.writeList(temp, caiMatrixString);
	}

	
}
 class Cai {
	
	public int id;
	public String name;
	public String type;
	public String material;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the material
	 */
	public String getMaterial() {
		return material;
	}
	/**
	 * @param material the material to set
	 */
	public void setMaterial(ArrayList<String> temp) {
		StringBuffer resultBuffer=new StringBuffer();
		for (String string : temp) {
			resultBuffer.append(string+",");
		}
		this.material = resultBuffer.toString();
	}
	public String tostring(){
		StringBuffer result=new StringBuffer();
		result.append(name+"\t"+material);
		return result.toString();
	}
}
