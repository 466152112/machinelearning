package util;
/**
 * �������ڴ��?hashmap�и��valueֵ��KEY��������
 */
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class TwoValueComparator implements Comparator<String> {

    Map<String, Double> base_map;
   //标记 0,降序 1升序
    int flag=0;

    /**
     * @param base_map
     * @param flag 标记 0,降序 1升序。默认是降序
     */
    public TwoValueComparator(Map<String, Double> base_map,int flag) {
        this.base_map = base_map;
        this.flag=flag;
    }
    public TwoValueComparator(Map<String, Double> base_map) {
        this.base_map = base_map;
    }
	public int compare(String arg0, String arg1) {

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
