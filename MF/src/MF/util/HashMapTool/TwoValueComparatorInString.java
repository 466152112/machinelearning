package MF.util.HashMapTool;
/**
 * �������ڴ��?hashmap�и��valueֵ��KEY��������
 */
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class TwoValueComparatorInString implements Comparator<String> {

	HashMap<String, Double> base_map;
   //标记 0,降序 1升序
    int flag=0;

    /**
     * @param value
     * @param flag 标记 0,降序 1升序。默认是降序
     */
    public TwoValueComparatorInString(HashMap<String, Double> value,int flag) {
        this.base_map = value;
        this.flag=flag;
    }
    public TwoValueComparatorInString(Map<String, Double> value,int flag) {
        this.base_map = (HashMap<String, Double>) value;
        this.flag=flag;
    }
    public TwoValueComparatorInString(HashMap<String, Double> base_map) {
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
