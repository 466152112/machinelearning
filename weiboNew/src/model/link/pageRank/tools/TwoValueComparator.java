package model.link.pageRank.tools;
/**
 * �������ڴ��?hashmap�и��valueֵ��KEY��������
 */
import java.util.Comparator;
import java.util.HashMap;

public class TwoValueComparator implements Comparator<String> {

    HashMap<String, AH> base_map;


    public TwoValueComparator(HashMap<String, AH> base_map) {

        this.base_map = base_map;

    }



    public int compare(String arg0, String arg1) {

        if (!base_map.containsKey(arg0) || !base_map.containsKey(arg1)) {

            return 0;

        }



        if ((base_map.get(arg0).getA()+base_map.get(arg0).getH()) <(base_map.get(arg1).getA()+base_map.get(arg1).getH())) {

            return 1;

        } else if ((base_map.get(arg0).getA()+base_map.get(arg0).getH()) ==(base_map.get(arg1).getA()+base_map.get(arg1).getH())) {

            return 0;

        } else {

            return -1;

        }

    }
}
