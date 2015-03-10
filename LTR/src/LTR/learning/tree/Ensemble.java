/*===============================================================================
 * Copyright (c) 2010-2012 University of Massachusetts.  All Rights Reserved.
 *
 * Use of the RankLib package is subject to the terms of the software license set 
 * forth in the LICENSE file included with this software, and also available at
 * http://people.cs.umass.edu/~vdang/ranklib_license.html
 *===============================================================================
 */

package LTR.learning.tree;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import LTR.db.DataPoint;

/**
 * @author vdang
 */
public class Ensemble {
	protected List<RegressionTree> trees = null;
	protected List<Float> weights = null;
	protected int[] features = null;
	
	public Ensemble()
	{
		trees = new ArrayList<RegressionTree>();
		weights = new ArrayList<Float>();
	}
	public Ensemble(Ensemble e)
	{
		trees = new ArrayList<RegressionTree>();
		weights = new ArrayList<Float>();
		trees.addAll(e.trees);
		weights.addAll(e.weights);
	}
	/**
	 * 用于从文件中读取模型
	 * @param xmlRep
	 */
	public Ensemble(String xmlRep)
	{
		try {
			trees = new ArrayList<RegressionTree>();
			weights = new ArrayList<Float>();
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			byte[] xmlDATA = xmlRep.getBytes();
			ByteArrayInputStream in = new ByteArrayInputStream(xmlDATA);
			Document doc = dBuilder.parse(in);
			NodeList nl = doc.getElementsByTagName("tree");
			HashMap<Integer, Integer> fids = new HashMap<Integer, Integer>();
			for(int i=0;i<nl.getLength();i++)
			{
				Node n = nl.item(i);//each node corresponds to a "tree" (tag)
				//create a regression tree from this node
				Split root = create(n.getFirstChild(), fids);
				//get the weight for this tree
				float weight = Float.parseFloat(n.getAttributes().getNamedItem("weight").getNodeValue().toString());
				//add it to the ensemble
				trees.add(new RegressionTree(root));
				weights.add(weight);
			}
			features = new int[fids.keySet().size()];
			int i = 0;
			for(Integer fid : fids.keySet())
				features[i++] = fid;
		}
		catch(Exception ex)
		{
			System.out.println("Error in Emsemble(xmlRepresentation): " + ex.toString());
			System.exit(1);
		}
	}
	
	/**
	 * @param tree
	 * @param weight
	 *@create_time：2015年1月13日下午9:41:00
	 *@modifie_time：2015年1月13日 下午9:41:00
	  
	 */
	public void add(RegressionTree tree, float weight)
	{
		trees.add(tree);
		weights.add(weight);
	}
	/**
	 * @param k
	 * @return 获取第k课回归树
	 *@create_time：2015年1月13日下午9:41:08
	 *@modifie_time：2015年1月13日 下午9:41:08
	  
	 */
	public RegressionTree getTree(int k)
	{
		return trees.get(k);
	}
	public float getWeight(int k)
	{
		return weights.get(k);
	}
	/**
	 * @return 所有树的方差
	 *@create_time：2015年1月13日下午9:41:44
	 *@modifie_time：2015年1月13日 下午9:41:44
	  
	 */
	public double variance()
	{
		double var = 0;
		for(int i=0;i<trees.size();i++)
			var += trees.get(i).variance();
		return var;
	}
	/**
	 * @param k
	 * 删除第k课树
	 *@create_time：2015年1月13日下午9:42:15
	 *@modifie_time：2015年1月13日 下午9:42:15
	  
	 */
	public void remove(int k)
	{
		trees.remove(k);
		weights.remove(k);
	}
	public int treeCount()
	{
		return trees.size();
	}
	/**
	 * @return
	 * 统计所有叶子节点数
	 *@create_time：2015年1月13日下午9:43:02
	 *@modifie_time：2015年1月13日 下午9:43:02
	  
	 */
	public int leafCount()
	{
		int count = 0;
		for(int i=0;i<trees.size();i++)
			count += trees.get(i).leaves().size();
		return count;
	}
	/**
	 * @param dp
	 * @return 对数据进行打分，为每棵树的打分值*树的权重值 的总和
	 *@create_time：2015年1月13日下午9:43:23
	 *@modifie_time：2015年1月13日 下午9:43:23
	  
	 */
	public float eval(DataPoint dp)
	{
		float s = 0;
		for(int i=0;i<trees.size();i++)
			s += trees.get(i).eval(dp) * weights.get(i);
		return s;
	}
	public String toString()
	{
		String strRep = "<ensemble>" + "\n";
		for(int i=0;i<trees.size();i++)
		{
			strRep += "\t<tree id=\"" + (i+1) + "\" weight=\"" + weights.get(i) + "\">" + "\n";
			strRep += trees.get(i).toString("\t\t");
			strRep += "\t</tree>" + "\n";
		}
		strRep += "</ensemble>" + "\n";
		return strRep;
	}
	public int[] getFeatures()
	{
		return features;
	}
	
	/**
	 * 用于从文件中读取模型
	 * Each input node @n corersponds to a <split> tag in the model file.
	 * @param n
	 * @return
	 */
	private Split create(Node n, HashMap<Integer, Integer> fids)
	{
		Split s = null;
		if(n.getFirstChild().getNodeName().compareToIgnoreCase("feature") == 0)//this is a split
		{
			NodeList nl = n.getChildNodes();
			int fid = Integer.parseInt(nl.item(0).getFirstChild().getNodeValue().toString().trim());//<feature>
			fids.put(fid, 0);
			float threshold = Float.parseFloat(nl.item(1).getFirstChild().getNodeValue().toString().trim());//<threshold>
			s = new Split(fid, threshold, 0);
			s.setLeft(create(nl.item(2), fids));
			s.setRight(create(nl.item(3), fids));
		}
		else//this is a stump
		{
			float output = Float.parseFloat(n.getFirstChild().getFirstChild().getNodeValue().toString().trim());
			s = new Split();
			s.setOutput(output);
		}
		return s;
	}
}
