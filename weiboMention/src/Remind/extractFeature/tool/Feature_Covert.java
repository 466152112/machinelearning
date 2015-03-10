/**
 * 
 */
package Remind.extractFeature.tool;

import java.util.List;

import tool.data.DenseVector;

/**   
 *    
 * @progject_name：weiboMention   
 * @class_name：Feature_Builder   
 * @class_describe：   
 * @creator：鸽   
 * @create_time：2014年12月29日 下午2:54:18   
 * @modifier：鸽   
 * @modified_time：2014年12月29日 下午2:54:18   
 * @modified_note：   
 * 
The file format for the training data (also testing/validation data) is the same as for SVM-Rank. This is also the format used in LETOR datasets. Each of the following lines represents one training example and is of the following format:
<line> .=. <target> qid:<qid> <feature>:<value> <feature>:<value> ... <feature>:<value> # <info>
<target> .=. <positive integer>
<qid> .=. <positive integer>
<feature> .=. <positive integer>
<value> .=. <float>
<info> .=. <string>
Here's an example: (taken from the SVM-Rank website). Note that everything after "#" are ignored.
3 qid:1 1:1 2:1 3:0 4:0.2 5:0 # 1A
2 qid:1 1:0 2:0 3:1 4:0.1 5:1 # 1B 
1 qid:1 1:0 2:1 3:0 4:0.4 5:0 # 1C
1 qid:1 1:0 2:0 3:1 4:0.3 5:0 # 1D  
1 qid:2 1:0 2:0 3:1 4:0.2 5:0 # 2A  
2 qid:2 1:1 2:0 3:1 4:0.4 5:0 # 2B 
1 qid:2 1:0 2:0 3:1 4:0.1 5:0 # 2C 
1 qid:2 1:0 2:0 3:1 4:0.2 5:0 # 2D  
2 qid:3 1:0 2:0 3:1 4:0.1 5:1 # 3A 
3 qid:3 1:1 2:1 3:0 4:0.3 5:0 # 3B 
4 qid:3 1:1 2:0 3:0 4:0.4 5:1 # 3C 
1 qid:3 1:0 2:1 3:1 4:0.5 5:0 # 3D
 * @version    
 *    
 */
public class Feature_Covert {
	int qid;
	DenseVector feature;
	int target;
	int featureSize;
	String ignoreddata;
	/**
	 * @param qid
	 * @param feature
	 * @param target
	 * @param ignoreddata
	 */
	public Feature_Covert(int qid,	DenseVector feature,int target,String ignoreddata){
		this.qid=qid;
		this.feature=feature;
		this.target=target;
		this.ignoreddata=ignoreddata;
		this.featureSize=feature.getSize();
	}
	
	@Override
	public String toString(){
		StringBuffer Buffer=new StringBuffer();
		Buffer.append(target+" ");
		Buffer.append("qid:"+qid+" ");
		for (int i = 0; i <featureSize; i++) {
			Buffer.append(i+1+":"+feature.get(i)+" ");
		}
		Buffer.append("# "+ignoreddata);
		return Buffer.toString().trim();
	}
	
}
