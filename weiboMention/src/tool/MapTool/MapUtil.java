/**
 * 
 */
package tool.MapTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：Snippet
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年9月18日 下午7:08:02
 * @modifier：zhouge
 * @modified_time：2014年9月18日 下午7:08:02
 * @modified_note：
 * @version
 * 
 */
public class MapUtil {

	public static String getProvince(String targetLocation)
			throws JSONException {
		// String httpRequest =
		// "http://api.map.baidu.com/geocoder/v2/?ak=8734e696135585e722c32017ffc313b9&callback=renderReverse&address="+targetLocation+"&output=json&pois=0";
		String httpRequest = "http://api.map.baidu.com/geocoder?address="
				+ targetLocation + "&output=json&src=zhouge";
		URLConnection connection = null;
		try {
			connection = new URL(httpRequest).openConnection();
			connection.connect();

			InputStream fin = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(fin,
					"UTF-8"));
			String temp = null;
			String lngString = "";
			String latString = "";
			while ((temp = br.readLine()) != null) {
				if (temp.indexOf("lng") != -1)
					lngString = temp.trim().replace(",", "");
				if (temp.indexOf("lat") != -1) {
					latString = temp.trim();
					break;
				}
			}
			if (lngString.equals("") || latString.equals("")) {
				return null;
			} else {
				double lng = Double.valueOf(lngString.split(":")[1]);
				double lat = Double.valueOf(latString.split(":")[1]);
				// System.out.println(lngString+latString);
				httpRequest = "http://api.map.baidu.com/geocoder/v2/?ak=8734e696135585e722c32017ffc313b9&callback=renderReverse&location="
						+ lat + "," + lng + "&output=json&pois=0";
				connection = new URL(httpRequest).openConnection();
				connection.connect();
				fin = connection.getInputStream();
				br = new BufferedReader(new InputStreamReader(fin, "UTF-8"));
				while ((temp = br.readLine()) != null) {
					if (temp.indexOf("province") != -1) {
						int index=temp.indexOf("province");
						String sub=temp.substring(index);
						index=sub.indexOf(",");
						sub=sub.substring(0, index);
						sub=sub.split(":")[1];
						sub=sub.replace("\"", "");
						return sub;
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
