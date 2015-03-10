/**
 * 
 */
package tool.FileTool;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**   
 *    
 * @progject_name��weiboNew   
 * @class_name��FileUtil   
 * @class_describe��   
 * @creator��zhouge   
 * @create_time��2014��9��27�� ����11:05:11   
 * @modifier��zhouge   
 * @modified_time��2014��9��27�� ����11:05:11   
 * @modified_note��   
 * @version    
 *    
 */
public class FileUtil {

	/**
	 * @param path
	 * @param DateFormat
	 * @return
	 *@create_time��2014��9��27������11:06:37
	 *@modifie_time��2014��9��27�� ����11:06:37
	  
	 */
	public List<String> getFileListSortByTimeASC(String path,String DateFormat) {
		File pathFile = new File(path);
		File[] Filelist = pathFile.listFiles();
		HashMap<String, Calendar> fileMap = new HashMap<>();
		List<String> result = new ArrayList<>();
		for (int j = 0; j < Filelist.length; j++) {
			File file = Filelist[j];
			if (file.isFile()) {
				String dayString = file.getName().substring(0,
						file.getName().indexOf("."));
				Date date = getDay(dayString,DateFormat);
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(date);
				fileMap.put(file.getPath(), calendar);
			}
		}
		List<String> fileList = new ArrayList<>(fileMap.keySet());
		CalendarComparator calendarComparator = new CalendarComparator(fileMap);
		Collections.sort(fileList, calendarComparator);
		return fileList;
	}
	
	public List<String> getFileListSortByTimeASC(String path1,String path2,String DateFormat) {
		HashMap<String, Calendar> fileMap = new HashMap<>();
		for (int i = 0; i < 2; i++) {
			File pathFile;
			if (i==1) {
				pathFile = new File(path1);
			}else {
				 pathFile = new File(path2);
			}
			File[] Filelist = pathFile.listFiles();
		
			for (int j = 0; j < Filelist.length; j++) {
				File file = Filelist[j];
				if (file.isFile()) {
					String dayString = file.getName().substring(0,
							file.getName().indexOf("."));
					Date date = getDay(dayString,DateFormat);
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(date);
					fileMap.put(file.getPath(), calendar);
				}
			}
		}
		
		List<String> fileList = new ArrayList<>(fileMap.keySet());
		CalendarComparator calendarComparator = new CalendarComparator(fileMap);
		Collections.sort(fileList, calendarComparator);
		return fileList;
	}
	/**
	 * @param time
	 * @param DateFormat
	 * @return
	 *@create_time��2014��9��27������11:06:26
	 *@modifie_time��2014��9��27�� ����11:06:26
	  
	 */
	public static Date getDay(String time,String DateFormat ) {

		final SimpleDateFormat sf1 = new SimpleDateFormat(DateFormat,
				Locale.ENGLISH);
		try {
			return sf1.parse(time.trim());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**   
	*    
	* @progject_name��weiboNew   
	* @class_name��CalendarComparator   
	* @class_describe��   
	* @creator��zhouge   
	* @create_time��2014��9��27�� ����11:06:32   
	* @modifier��zhouge   
	* @modified_time��2014��9��27�� ����11:06:32   
	* @modified_note��   
	* @version    
	*    
	*/
	private static class CalendarComparator implements Comparator<String> {
		HashMap<String, Calendar> baseHashMap = null;

		public CalendarComparator(HashMap<String, Calendar> baseHashMap) {
			this.baseHashMap = baseHashMap;
		}

		public int compare(String arg0, String arg1) {
			return this.baseHashMap.get(arg0).compareTo(
					this.baseHashMap.get(arg1));
		}

	}
	
	/**
	 * Get all file (non-recursively) from a directory.
	 * @param directory The directory to read.
	 * @return A list of filenames (without path) in the input directory.
	 */
	public static String[] getAllFiles(String directory)
	{
		File dir = new File(directory);
		String[] fns = dir.list();
		return fns;
	}
	/**
	 * Get all file (non-recursively) from a directory.
	 * @param directory The directory to read.
	 * @return A list of filenames (without path) in the input directory.
	 */
	public static List<String> getAllFiles2(String directory)
	{
		File dir = new File(directory);
		String[] fns = dir.list();
		List<String> files = new ArrayList<String>();
		if(fns != null)
			for(int i=0;i<fns.length;i++)
				files.add(fns[i]);
		return files;
	}
	/**
	 * Test whether a file/directory exists.
	 * @param file the file/directory to test.
	 * @return TRUE if exists; FALSE otherwise.
	 */
	public static boolean exists(String file)
	{
		File f = new File(file);
		return f.exists();
	}
	/**
	 * Copy a file.
	 * @param srcFile The source file.
	 * @param dstFile The copied file.
	 */
	public static void copyFile(String srcFile, String dstFile)
	{
		try {
		    FileInputStream fis  = new FileInputStream(new File(srcFile));
		    FileOutputStream fos = new FileOutputStream(new File(dstFile));
		    try
		    {
		    	byte[] buf = new byte[40960];
		    	int i = 0;
		    	while ((i = fis.read(buf)) != -1) {
		    		fos.write(buf, 0, i);
		    	}
		    } 
		    catch (Exception e)
		    {
		    	System.out.println("Error in FileUtils.copyFile: " + e.toString());
		    }
		    finally
		    {
		    	if (fis != null) fis.close();
		    	if (fos != null) fos.close();
		    }
		}
		catch(Exception ex)
		{
			System.out.println("Error in FileUtils.copyFile: " + ex.toString());
		}
	}
	/**
	 * Copy all files in the source directory to the target directory.
	 * @param srcDir The source directory.
	 * @param dstDir The target directory.
	 * @param files The files to be copied. NOTE THAT this list contains only names (WITHOUT PATH).
	 */
	public static void copyFiles(String srcDir, String dstDir, List<String> files)
	{
		for(int i=0;i<files.size();i++)
			FileUtil.copyFile(srcDir+files.get(i), dstDir+files.get(i));
	}
	public static final int BUF_SIZE = 51200;
    /**
     * Gunzip an input file.
     * @param file_input	Input file to gunzip.
     * @param dir_output	Output directory to contain the ungzipped file (whose name = file_input - ".gz")
     * @return 1 if succeed, 0 otherwise.
     */
	public static int gunzipFile (File file_input, File dir_output) {
        // Create a buffered gzip input stream to the archive file.
    	GZIPInputStream gzip_in_stream;
        try {
        	FileInputStream in = new FileInputStream(file_input);
        	BufferedInputStream source = new BufferedInputStream (in);
        	gzip_in_stream = new GZIPInputStream(source);
        }
		catch (IOException e) {
			System.out.println("Error in gunzipFile(): " + e.toString());
			return 0;
		}

        // Use the name of the archive for the output file name but
        // with ".gz" stripped off.
		String file_input_name = file_input.getName ();
		String file_output_name = file_input_name.substring (0, file_input_name.length () - 3);

        // Create the decompressed output file.
		File output_file = new File (dir_output, file_output_name);

        // Decompress the gzipped file by reading it via
        // the GZIP input stream. Will need a buffer.
		byte[] input_buffer = new byte[BUF_SIZE];
		int len = 0;
		try {
			// Create a buffered output stream to the file.
			FileOutputStream out = new FileOutputStream(output_file);
			BufferedOutputStream destination = new BufferedOutputStream (out, BUF_SIZE);

         	//Now read from the gzip stream, which will decompress the data,
          	//and write to the output stream.
			while ((len = gzip_in_stream.read (input_buffer, 0, BUF_SIZE)) != -1)
				destination.write (input_buffer, 0, len);
			destination.flush (); // Insure that all data is written to the output.
			out.close ();
		}
        catch (IOException e) {
        	System.out.println("Error in gunzipFile(): " + e.toString());
        	return 0;
        }

        try {
        	gzip_in_stream.close ();
        }
        catch (IOException e) {
        	return 0;
        }
        return 1;
    }
	/**
	 * Gzip an input file.
	 * @param inputFile The input file to gzip.
	 * @param gzipFilename The gunzipped file's name.
	 * @return 1 if succeeds, 0 otherwise
	 */
	public static int gzipFile(String inputFile, String gzipFilename)
    {
		try {
	    	// Specify gzip file name
			GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(gzipFilename));
	    	
	    	// Specify the input file to be compressed
	    	FileInputStream in = new FileInputStream(inputFile);
	    	
	    	// Transfer bytes from the input file 
	    	// to the gzip output stream
	    	byte[] buf = new byte[BUF_SIZE];
	    	int len;
	    	while ((len = in.read(buf)) > 0) {
	    		out.write(buf, 0, len);
	    	}
	    	in.close();
	    	
	    	// Finish creation of gzip file
	    	out.finish();
	    	out.close();
		}
		catch (Exception ex)
		{
			return 0;
		}
		return 1;
    }

	public static String getFileName(String pathName)
	{
		int idx1 = pathName.lastIndexOf("/");
		int idx2 = pathName.lastIndexOf("\\");
		int idx = (idx1 > idx2)?idx1:idx2;
		return pathName.substring(idx+1);
	}
	public static String makePathStandard(String directory)
	{
		String dir = directory;
		char c = dir.charAt(dir.length()-1);
		if(c != '/' && c != '\\')
			dir += File.pathSeparator;
		return dir;
	}
}
