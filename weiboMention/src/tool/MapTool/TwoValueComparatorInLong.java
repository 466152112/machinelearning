package tool.MapTool;
/**
 * �������ڴ��?hashmap�и��valueֵ��KEY��������
 */
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class TwoValueComparatorInLong implements Comparator<Long> {

	HashMap<Long, Double> base_map;
   //标记 0,降序 1升序
    int flag=0;

    /**
     * @param value
     * @param flag 标记 0,降序 1升序。默认是降序
     */
    public TwoValueComparatorInLong(HashMap<Long, Double> value,int flag) {
        this.base_map = value;
        this.flag=flag;
    }
    public TwoValueComparatorInLong(Map<Long, Double> value,int flag) {
        this.base_map = (HashMap<Long, Double>) value;
        this.flag=flag;
    }
    public TwoValueComparatorInLong(HashMap<Long, Double> base_map) {
        this.base_map = base_map;
    }
	public int compare(Long arg0, Long arg1) {

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
