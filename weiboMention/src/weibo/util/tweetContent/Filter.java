/**
 * 
 */
package weibo.util.tweetContent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface  Filter {


	public Set<String> getAtName(String oneLine);
	public List<String> getAtNameInList(String oneLine);
	public List<String> getRouteName(String oneLine);
	public String getTag(String oneLine);
	public String RemoveAtName(String oneLine);

	public String RemoveRouteName(String oneLine);
	public String RemoveShortLink(String sentence);
	public String getFilterResultString();
	public String getFilterResultString(String oneLine);
	public Set<String> getShortLink(String oneLine);
}
