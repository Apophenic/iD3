package id3.objects;

import id3.gui.dialogs.ProgressDialog;
import id3.main.GUI;
import id3.main.Program;
import id3.utils.Utils;
import org.jaudiotagger.tag.images.Artwork;
import xmlwise.Plist;
import xmlwise.XmlParseException;

import javax.swing.*;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/** Stores relevant info from an iTunes
 * Music Library.xml file.
 * <p>
 * The Library xml file is a typical xml schema
 * but with heavy emphasis on element nesting.
 * xmlwise is used to parse the xml file into
 * a nested Map<String, Object>, where the value
 * is often another Map.
 * <p>
 * The actual track entries for a given library file
 * are paired with the key "Tracks." Tracks are
 * stored as another nested Map with an arbitrary
 * track ID (int) as the key. The actual fields of
 * the track are also stored as a map, with the field
 * (i.e. song title, rating) being the key and the
 * value being the Map's value.
 * <p>
 * It's worth noting that each field's value also
 * has its type defined in the xml file.
 * Unfortunately, the type is lost when the xml
 * is parsed, so explicit casting is almost always
 * needed to retrieve a field's value.
 */
public class Library
{
	private static final Logger 	LOG 					= Program.LOG;
	
	/** Temp file used during xml parsing.
	 *  @see #prepLibraryFile() 
	 *  */
	private static final File 		FILE_TRACKS 			= new File("tracks.txt");
	
	/** Naming format used to create unique Artist
	 * when the artist name is missing from a track.
	 * @see #createArtistObjects()
	 */
	public static final String		UUID_REGEX				= "aa[a-zA-Z0-9]{5}";
	
	/** iTunes Music Library.xml file to be used for operations */
	private File 			fileLibrary;
	
	/** Music folder as referenced in the library file */
	private String 			musicFolder;
	
	/** Contains all song entries in the library file. This is a nested map,
	 * where the first key references the track's ID in iTunes and the first
	 * value returns a map that contains all of that track's information
	 * (name, artist, comments, etc.)
	 */
	private Map<String, Map> trackEntries;
	
	/** Artist objects, created from trackEntries if necessary.
	 * @see #createArtistObjects()
	 */
	private ArrayList<Artist> artists = new ArrayList<>();
	
	/** Create a new library from the supplied
	 * file path. All track entries will be read
	 * from the file and saved to {@link #trackEntries}.
	 * @param libraryPath  iTunes Music Library.xml file path
	 */
	public Library(String libraryPath)
	{
		ProgressDialog progress = new ProgressDialog("Parsing Library");
		
		this.fileLibrary = new File(libraryPath);
		this.readXml();
		
		progress.finishAndClose();
	}
	
	/** Custom version of java.util.Map's getOrDefault that accommodates
	 * iTunes Library xml's heavily nested schema.
	 * @param id  The iTunes "key" int value at the start of each entry in the library file
	 * @param key  The field being called (i.e. "Artist", "Album", "Genre", etc.)
	 * @param defaultValue  Value returned if the field referenced by key is
	 * missing from the library file
	 * @return  Value {@code Object} if it exists, otherwise defaultValue
	 */
	public Object getOrDefaultTrackValue(String id, Object key, Object defaultValue)
	{
		return trackEntries.get(id).getOrDefault(key, defaultValue);
	}
	
	/** Parses the Library xml file, storing all track entries
	 * on {@link #trackEntries} and saving the music directory
	 * (if applicable - library must be consolidated)
	 * to {@link #musicFolder}.
	 */
	public void readXml()
	{
		try
		{
			prepLibraryFile();
			
			Map<String, Object> plist = Plist.load(FILE_TRACKS);
			
			musicFolder = (String) plist.get("Music Folder");

			// "Music folder" key can be missing in the XML.
			if (musicFolder == null) {
				musicFolder = this.getRelativeMusicFolder();
			}
                        
			musicFolder = new File(musicFolder).toString();
			
			trackEntries = (Map<String, Map>) plist.get("Tracks");

			// Only keep locally accessible files
			trackEntries = trackEntries
							.entrySet()
							.stream()
							.filter(entry -> entry.getValue().get("Track Type") != "File")
							.collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
			
			LOG.log(Level.FINE, "Library xml parsed successfully");
		}
		catch (XmlParseException | IOException e)
		{
			JOptionPane.showMessageDialog(GUI.frame, "Error parsing library file", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/** Reads {@link #trackEntries} and creates unique 
	 * {@link Artist} and {@link Album} objects based on the
	 * available tracks. Creation is done by checking literal
	 * artist and album names to determine if a matching object
	 * already exists, or a new one is needed. Album objects are
	 * stored exclusively on their corresponding Artist.
	 * <p>
	 * When either the artist or album name is missing,
	 * an arbitrary one is generated using {@link #UUID_REGEX}'s
	 * format.
	 * <p>
	 * Artist and Album variables are filled from the track entry
	 * at the time of their creation. If a field is empty, each
	 * following track that is added to that object will be checked
	 * to see if the missing variable can be filled from that track's
	 * field.
	 * <p>
	 * This is a very expensive process. Very few functions
	 * actually need Artist/Album objects.
	 * {@link id3.gui.functionpanel.FunctionPanel#isRequiresArtistAlbumObjects} must
	 * be set to true for this method to be called.
	 * @see id3.gui.functionpanel.panels.CustomFieldsPanel
	 * @see id3.gui.functionpanel.panels.MissingFieldsPanel
	 * @see {@literal The functions referenced in above panels}
	 */
	public void createArtistObjects()
	{
		if(!artists.isEmpty())
		{
			return;
		}
		
		ProgressDialog progress = new ProgressDialog("Scanning Artists...", getTrackMapEntries().size());
		
		int progressCounter = 0;
		for(Entry<String, Map> entry : getTrackMapEntries())
		{
			String filelocation = Utils.getFilePathFromTrackEntry(entry);
			
			// Artist/Album lookup by name. If either of these are unavailable, a unique string is assigned to them for
			// identification purposes
			String artistname = (String) entry.getValue().getOrDefault
					("Artist", "aa" + UUID.randomUUID().toString().substring(0, 5));
			String albumname = (String) entry.getValue().getOrDefault
					("Album", "aa" + UUID.randomUUID().toString().substring(0, 5));
			
			if(artistname.matches(UUID_REGEX))
			{	//If a unique string was used, update track entry
				entry.getValue().put("Artist", artistname);
			}
			if(albumname.matches(UUID_REGEX))
			{
				entry.getValue().put("Album", albumname);
			}

			//If album exists, find missing artist field
			if(artistname.matches(UUID_REGEX) && !albumname.matches(UUID_REGEX))
			{
				artistLoop :
				for(Artist artist : artists)
				{
					for(Album album : artist.getAlbums())
					{
						if(album.getName().equalsIgnoreCase(albumname))
						{
							artistname = album.getArtist().getName();
							entry.getValue().replace("Artist", artistname);
							break artistLoop;
						}
					}
				}
			}
			
			Artist artist = null;
			for(Artist art : artists)
			{
				if(art.getName().equalsIgnoreCase(artistname))
				{
					artist = art;
					break;
				}
			}
			if(artist == null)	//Create New Artist from current track entry because none exists
			{
				artist = new Artist(artistname);
				artists.add(artist);
			}
			
			Album album = artist.getAlbum(albumname);
			if(album == null)	//Create New Album from current track entry
			{
				String genre = (String) entry.getValue().getOrDefault("Genre", null);
				int disccount = (int) entry.getValue().getOrDefault("Disc Count", -1);
				int trackcount = (int) entry.getValue().getOrDefault("Track Count", -1);
				int year = (int) entry.getValue().getOrDefault("Year", -1);
				String composer = (String) entry.getValue().getOrDefault("Composer", null);
				String grouping = (String) entry.getValue().getOrDefault("Grouping", null);
				String comments = (String) entry.getValue().getOrDefault("Comments", null);
				
				Artwork artwork = Utils.getArtworkFromID3(filelocation);
				album = new Album(albumname, artist,
						genre, disccount, trackcount, year, 
						composer, grouping, comments, artwork);
				
				artist.addAlbum(album);
			}
			album.addTrack(entry.getKey(), filelocation);
			
			//Attempt to fill empty fields from new entry
			if(album.getComposer() == null)
			{
				album.setComposer((String) entry.getValue().getOrDefault("Composer", null));
			}
			if(album.getGrouping() == null)
			{
				album.setGrouping((String) entry.getValue().getOrDefault("Grouping", null));
			}
			if(album.getComments() == null) 
			{
				album.setComments((String) entry.getValue().getOrDefault("Comments", null));
			}
			if(album.getGenre() == null)
			{
				album.setGenre((String) entry.getValue().getOrDefault("Genre", null));
			}
			if(album.getDiscCount() == -1)
			{
				album.setDiscCount((int) entry.getValue().getOrDefault("Disc Count", -1));
			}
			if(album.getTrackCount() == -1)
			{
				album.setTrackCount((int) entry.getValue().getOrDefault("Track Count", -1));
			}
			if(album.getYear() == -1)
			{
				album.setYear((int) entry.getValue().getOrDefault("Year", -1));
			}
			if(album.getRating() == -1)
			{
				album.setRating((int) entry.getValue().getOrDefault("Rating", -1));
			}
			if(album.getArtwork() == null)
			{
				Artwork artwork = Utils.getArtworkFromID3(filelocation);
				album.setArtwork(artwork);
			}
			
			progress.update(progressCounter++);
		}
		
		progress.finishAndClose();
		LOG.log(Level.FINE, "Artist objects were created successfully");
	}
	
	/** Splits an iTunes library file into a seperate file that only contains the track data.
	 * Not only is this for performance (faster xml parsing), but large library files
	 * (especially those with many playlists) will often fail to read properly otherwise.
	 */
	private void prepLibraryFile()
	{
		String playlistKey = "	<key>Playlists</key>";
		StringBuilder sb = new StringBuilder();
		String line;
		FileInputStream fis = null;
		InputStreamReader isr = null;
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		BufferedReader br = null;
		
		try
		{
			fis = new FileInputStream(fileLibrary);
			isr = new InputStreamReader(fis, "UTF-8");
			fos = new FileOutputStream(FILE_TRACKS);
			osw = new OutputStreamWriter(fos, "UTF-8");
			br = new BufferedReader(isr);
			bw = new BufferedWriter(osw);
			
			while((line = br.readLine()) != null)
			{
				sb.append(line + "\n");
			}
			
			String tracks = sb.substring(0, sb.indexOf(playlistKey));
			tracks = tracks.concat("</dict>	</plist>");	//Needed for proper format, otherwise xml parsing fails
			
			bw.write(tracks);
			bw.flush();
			
			LOG.log(Level.FINE, "Tracks file created successfully");
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(GUI.frame, "Error while creating Tracks file", "Error", JOptionPane.ERROR_MESSAGE);
			LOG.log(Level.SEVERE, "Error while creating Tracks file");
			
		}
		finally
		{
			try
			{
				br.close();
				bw.close();
			}
			catch (IOException e)
			{
				LOG.log(Level.WARNING, "I/O Stream failed to close after constructing tracks file");
			}
		}
	}
        
	/**
	 * Get Music Folder path relative to the Library file
	 * @return String
	 */
	private String getRelativeMusicFolder()
	{
                String path;
		File libraryPath = this.fileLibrary.getParentFile();
                String[] dirNames = {"iTunes Media", "iTunes Music"};
                for (String dirName : dirNames) {
                    path = libraryPath.toURI().getPath() + dirName;
                    if (new File(path).isDirectory()) {
                        return "file://localhost" + path;
                    }
                }
		return null;
	}
	
	public Set<Entry<String, Map>> getTrackMapEntries()
	{
		return trackEntries.entrySet();
	}
	
	public ArrayList<Artist> getLibraryArtists()
	{
		return artists;
	}
	
	public String getMusicFolder()
	{
		return musicFolder;
	}
	
	public File getLibraryFile()
	{
		return fileLibrary;
	}

}
