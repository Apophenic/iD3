package id3.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Stores / saves / loads application
 * specific settings, including settings
 * for custom Artist rating brackets.
 */
public final class Settings
{
	private static final Logger LOG = Program.LOG;
	
	public static final File UNLISTED_DIR =  new File("(UNLISTED MEDIA)");
	public static final String PROGRAM_DIR = System.getProperty("user.dir");
	public static final String BPM_CMD = "java -jar \"" + PROGRAM_DIR + "\\bin\\lib\\trackanalyzer\\TrackAnalyzer.jar\" \"";
	
	public static double ratedSongsMin;
	public static int totalSongsMin;
	public static boolean useCustomRatings;
	public static double oneStarMax;
	public static double twoStarMin;
	public static double threeStarMin;
	public static double fourStarMin;
	public static double fiveStarMin;
	
	public static String itunesDir;
	public static boolean isHalfStarsEnabled;
	
	public static boolean isDebugMode = false;	//Prevents writing ID3 tags
	
	protected Settings()
	{ }
	
	public static void load()
	{
		BufferedReader br = null;
		try 
		{
			if(!UNLISTED_DIR.exists())
			{
				UNLISTED_DIR.mkdir();
			}
			
			br = new BufferedReader(new FileReader(new File("Settings.ini")));
			String line;
			while((line = br.readLine()) != null)
			{
				String[] temp = line.split(" = ");
				String key = temp[0];
				String value = temp[1];
				
				switch(key)
				{
					case("UseCustomRatings"):
						useCustomRatings = Boolean.parseBoolean(value);
						break;
					case("1Max/2Min"):
						oneStarMax = Double.parseDouble(value);
						twoStarMin = Double.parseDouble(value);
						break;
					case("3Min"):
						threeStarMin = Double.parseDouble(value);
						break;
					case("4Min"):
						fourStarMin = Double.parseDouble(value);
						break;
					case("5Min"):
						fiveStarMin = Double.parseDouble(value);
						break;
					case("%RatedMin"):
						ratedSongsMin = Double.parseDouble(value);
						break;
					case("TotalSongsMin"):
						totalSongsMin = Integer.parseInt(value);
						break;
					case("iTunesDir"):
						itunesDir = value;
						break;
					case("isHalfStarsEnabled") :
						isHalfStarsEnabled = Boolean.parseBoolean(value);
						break;
				}
			}
		}
		catch (IOException e)
		{
			LOG.log(Level.SEVERE, "Failed to read settings file");
		}
		finally
		{
			try
			{
				br.close();
			}
			catch (IOException e)
			{
				LOG.log(Level.WARNING, "Failed to close IO stream after reading Settings");
			}
		}
	}
	
	public static void save()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("UseCustomRatings = " + useCustomRatings);
		sb.append("\r\n1Max/2Min = " + oneStarMax);
		sb.append("\r\n3Min = " + threeStarMin);
		sb.append("\r\n4Min = " + fourStarMin);
		sb.append("\r\n5Min = " + fiveStarMin);
		sb.append("\r\n%RatedMin = " + ratedSongsMin);
		sb.append("\r\nTotalSongsMin = " + totalSongsMin);
		sb.append("\r\niTunesDir = " + itunesDir);
		sb.append("\r\nisHalfStarsEnabled = " + isHalfStarsEnabled);
		
		BufferedWriter bw = null;
		try
		{
			bw = new BufferedWriter(new FileWriter(new File("Settings.ini")));
			bw.write(sb.toString());
		}
		catch (IOException e)
		{
			LOG.log(Level.SEVERE, "Failed to save settings");
		}
		finally
		{
			try
			{
				bw.close();
			}
			catch (IOException e)
			{
				LOG.log(Level.WARNING, "Failed to close IO stream after saving Settings");
			}
		}
	}
}
