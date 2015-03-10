package util;
/**
 * �������ڴ��?hashmap�и��valueֵ��KEY��������
 */
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class TwoValueComparatorInInteger implements Comparator<Integer> {

	HashMap<Integer, Double> base_map;
   //标记 0,降序 1升序
    int flag=0;

    /**
     * @param value
     * @param flag 标记 0,降序 1升序。默认是降序
     */
    public TwoValueComparatorInInteger(HashMap<Integer, Double> value,int flag) {
        this.base_map = value;
        this.flag=flag;
    }
    public TwoValueComparatorInInteger(Map<Integer, Double> value,int flag) {
        this.base_map = (HashMap<Integer, Double>) value;
        this.flag=flag;
    }
    public TwoValueComparatorInInteger(HashMap<Integer, Double> base_map) {
        this.base_map = base_map;
    }
	public int compare(Integer arg0, Integer arg1) {

        if (!base_map.containsKey(arg0) || !base_map.containsKey(arg1)) {
            return 0;
        }

        if (base_map.get(arg0)<base_map.get(arg1)) {
        	if(flag==1)
            return -1;
        	else 
				return 1;
        } else if (base_map.get(arg0)==base_map.get(arg1)) {

            return 0;
        } else {
        	if (flag==1) 
        		 return 1;
        	else 
				 return -1;
        }

    }
	
}
