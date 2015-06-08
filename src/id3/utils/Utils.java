package id3.utils;

import id3.main.Program;
import id3.main.Settings;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.images.Artwork;

/** A set of utility methods that range from
 * string manipulation to ID3 Tag related
 * operations.
 */
public final class Utils
{
	private static final Logger LOG = Program.LOG;
	
	/** These words will not be capitalized
	 * @see #capitalizeTitle(String, boolean)
	 */
	public static final String[] IGNORE_CAPS = {"a", "of", "the", "in", "an", "and", "to",
												"with", "are", "if", "be", "has"};
	
	/* Available fields in iTunes, split by like-data type */
	public static final String[] FIELDS_STRINGS 	= {"Artist", "Album", "Album Artist", "Composer", "Grouping", "Genre", "Comments"};
	public static final String[] FIELDS_INTS 		= {"Year", "Track Number", "Track Count", "Disc Number", "Disc Count", "BPM"};
	
	protected Utils() {}
	
	/** Capitalizes the first letter of every word,
	 * by default.
	 * @param toProperTitle  String to capitalize words in.
	 * @param isUseProperEnglishCaps  If true, skip capitalizing
	 * certain words, such as articles (a, of, etc.).
	 * @return  The capitalized String.
	 * @see #IGNORE_CAPS
	 */
	public static String capitalizeTitle(String toProperTitle, boolean isUseProperEnglishCaps)
	{
		if(toProperTitle.isEmpty() || toProperTitle==null)
		{
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		String[] temp = toProperTitle.split(" ");
		
		for(String word : temp)
		{
			if(word.matches("[a-zA-Z].*"))
			{
				word = Character.toUpperCase(word.charAt(0)) + word.substring(1);
				
				if(isUseProperEnglishCaps)
				{
					if(!word.equals(temp[0]))	//Is not first word, check if should be de-capitalized
					{
						for(String article : IGNORE_CAPS)
						{
							if(word.toLowerCase().equals(article))
							{
								word = Character.toLowerCase(word.charAt(0)) + word.substring(1);
								break;
							}
						}
					}
				}
			}
			sb.append(word + " ");
		}
		sb.deleteCharAt(sb.length()-1);
		
		LOG.log(Level.FINEST, sb.toString());
		return sb.toString();
	}
	
	/** Replaces metacharacters disallowed in file paths in windows
	 * with an underscore character ("_")
	 * @param text  File path to be altered.
	 * @returns  Windows OS safe file path.
	 */
	public static String removeIllegalWindowsFilePathChars(String text)
	{
		return text.replaceAll("[\\\\:\\/\\*\\?\"\\<\\>\\|\\;\\+\\`\\~\\{\\}\\[\\]\\="
								+ "\\!\\@\\#\\$\\%\\^\\&]", "_");
	}
	
	/** Removes extension from file name
	 * @param file  {@code File} to remove extension from.
	 * @returns  Literal file name without the extension.
	 */
	public static String getFileNameNoExt(File file)
	{
		String filename = file.getName();
		return filename.substring(0, filename.lastIndexOf("."));
	}
	
	/** Changes forward slashes '/' to
	 * backslashes '\'. Useful for
	 * comparing inconsistent file path formats.
	 * @param path  File path string to change slashes
	 * @return  Converted string
	 */
	public static String convertForwardToBackSlash(String path)
	{
		if(path.charAt(0) == '/' || path.charAt(0) == '\\')
		{
			path = path.substring(1);
		}
		
		if(path.charAt(2) == '/')
		{
			path = path.replaceAll("[/]", "\\\\");
		}
		
		path = path.substring(0, path.lastIndexOf("\\"));
		path = "\"" + path + "\"";
		return path;
	}
	
	/** Escapes metacharacters for proper string comparison
	 * @param string  String to be escaped
	 * @returns  Meta-character escaped string
	 */
	public static String escapeMetaCharacters(String string)
	{
		string = string.replaceAll("\\\\", "\\\\\\\\");
		string = string.replaceAll("\\.", "\\\\.");
		string = string.replaceAll("\\?", "\\\\?");
		string = string.replaceAll("\\(", "\\\\(");
		string = string.replaceAll("\\)", "\\\\)");
		string = string.replaceAll("\\[", "\\\\[");
		string = string.replaceAll("\\]", "\\\\]");
		string = string.replaceAll("\\{", "\\\\{");
		string = string.replaceAll("\\}", "\\\\}");
		string = string.replaceAll("\\<", "\\\\<");
		string = string.replaceAll("\\>", "\\\\>");
		string = string.replaceAll("\\!", "\\\\!");
		string = string.replaceAll("\\@", "\\\\@");
		string = string.replaceAll("\\#", "\\\\#");
		string = string.replaceAll("\\$", "\\\\\\$");
		string = string.replaceAll("\\%", "\\\\%");
		string = string.replaceAll("\\^", "\\\\^");
		string = string.replaceAll("\\&", "\\\\&");
		string = string.replaceAll("\\*", "\\\\*");
		string = string.replaceAll("\\+", "\\\\+");
		string = string.replaceAll("\\=", "\\\\+");
		string = string.replaceAll("\\-", "\\\\-");
		string = string.replaceAll("\\_", "\\\\_");
		string = string.replaceAll("\\|", "\\\\|");
		string = string.replaceAll("\\\"", "\\\\\"");
		string = string.replaceAll("\\'", "\\\\'");
		string = string.replaceAll("\\:", "\\\\:");
		string = string.replaceAll("\\;", "\\\\;");
		string = string.replaceAll("\\," , "\\\\,");
		string = string.replaceAll("\\/", "\\\\/"); 
		string = string.replaceAll("\\\\~", "\\\\~");
		return string;
	}
	
	/** Checks each char in a string to see if the string contains
	 * any char values greater than those used in ASCII.
	 * @param string  String to search for Non-ASCII chars.
	 * @returns  True if any char is greater than u007f, false otherwise
	 */
	public static boolean isContainsNonAscii(String string)
	{
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        for (char c : string.toCharArray())
        {
            if (c >= '\u007F')
            {
            	return true;
            }
        }
        return false;
    }
	
	/** Takes a directory and returns all files within
	 * that directory as well as all sub directories
	 * @param directory  Directory to be searched
	 * @return  List of all file paths
	 */
	public static ArrayList<String> getAllFilesInSubDirs(File directory)
	{
		ArrayList<String> files = new ArrayList<String>();
		try
		{
		    Path startPath = Paths.get(directory.getAbsolutePath());
		    Files.walkFileTree(startPath, new SimpleFileVisitor<Path>()
		    {
		        @Override
		        public FileVisitResult preVisitDirectory(Path dir,
		                BasicFileAttributes attrs)
		        {
		            return FileVisitResult.CONTINUE;
		        }

		        @Override
		        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
		        {
		        	files.add(file.toString().toLowerCase());
		            return FileVisitResult.CONTINUE;
		        }

		        @Override
		        public FileVisitResult visitFileFailed(Path file, IOException e)
		        {
		            return FileVisitResult.CONTINUE;
		        }
		    });
		}
		catch (IOException e)
		{
		    LOG.log(Level.SEVERE, "Failed to retrieve subdir files. Check dir exists");
		}
		return files;
	}
	
	/** Creates a document filter that accepts
	 * only numbers.
	 * @param document  Document to apply filter to.
	 */
	public static void setDocFormatNumbersOnly(AbstractDocument document)
	{
        document.setDocumentFilter(new DocumentFilter()
        {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException
            {
                Document doc = fb.getDocument();
                StringBuilder sb = new StringBuilder();
                sb.append(doc.getText(0,  doc.getLength()));
                sb.insert(offset, string);
                
                if(doc.getLength() >= 2)
                {
                	return;
                }
                	
                if(testInput(sb.toString()))
                {
                	super.insertString(fb, offset, string, attr);
                }
                else
                {
                	Toolkit.getDefaultToolkit().beep();
                }

            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException
            {
            	Document doc = fb.getDocument();
                StringBuilder sb = new StringBuilder();
                sb.append(doc.getText(0,  doc.getLength()));
                sb.replace(offset, offset + length, text);
                
                if(doc.getLength() >= 2)
                {
                	return;
                }
                	
                if(testInput(sb.toString()))
                {
                	super.replace(fb, offset, length, text, attrs);
                }
                else
                {
                	Toolkit.getDefaultToolkit().beep();
                }
            }
            
            private boolean testInput(String input)
            {
            	try
            	{
            		Integer.parseInt(input);
            		return true;
            	} 
            	catch (NumberFormatException e)
            	{
            		return false;
            	}
            }
        });
    }
	
	/** Gets the "Location" field from an iTunes Track Entry
	 * (aka: the file path) and formats it to a proper URI.
	 * @param entry  Library file's equivalent to a track in iTunes
	 * @return  File path corresponding to the Track Entry
	 * @see Library
	 */
	public static String getFilePathFromTrackEntry(Entry<String, Map> entry)
	{
		return URI.create((String) entry.getValue().get("Location")).getPath();
	}
	
	/** Attempts to find the relevant {@code FieldKey} from
	 * the supplied string. This FieldKey is a pointer to
	 * a file's ID3 tag's corresponding field.
	 * @param item  Text to find matching {@code FieldKey} for.
	 * @return  {@code FieldKey} representing the literal string item.
	 */
	public static FieldKey getFieldKeyFromString(String item)
	{
		switch(item)
		{
			default :
				LOG.log(Level.SEVERE, "FIELD KEY DOESN'T EXIST FOR: " + item);
				return null;
			case "Artist" :
				return FieldKey.ARTIST;
			case "Album" :
				return FieldKey.ALBUM;
			case "Album Artist" :
				return FieldKey.ALBUM_ARTIST;
			case "Composer" :
				return FieldKey.COMPOSER;
			case "Grouping" :
				return FieldKey.GROUPING;
			case "Genre" :
				return FieldKey.GENRE;
			case "Comments" :
				return FieldKey.COMMENT;
			case "Year" :
				return FieldKey.YEAR;
			case "Track Number" :
				return FieldKey.TRACK;
			case "Track Count" :
				return FieldKey.TRACK_TOTAL;
			case "Disc Number" :
				return FieldKey.DISC_NO;
			case "Disc Count" :
				return FieldKey.DISC_TOTAL;
			case "BPM" :
				return FieldKey.BPM;
		}
	}
	
	/** Reads ID3 tag on file and creates a new {@code Map} with
	 * all available information.
	 * @param id  Arbitrary trackID # to be used (no duplicates)
	 * @param file  File to read ID3 tag from
	 * @returns  Map with info and matching keys. Values' types
	 * cannot be generic objects - otherwise xml parsing
	 * will fail.
	 * @see Library
	 */
	public static HashMap createNewTrackEntry(int id, File file)
	{
		HashMap<String, Object> entry = new HashMap<String, Object>();
		try
		{
			AudioFile af = AudioFileIO.read(file);
			Tag tag = af.getTag();
			if(tag == null)
			{
				throw new TagException();
			}
			
			entry.put("Track ID", id);
			entry.put("Location", "file://localhost" + file.toURI().getPath().replace("#", "%23"));
			entry.put("Name", tag.getFirst(FieldKey.TITLE));
			entry.put("Artist", tag.getFirst(FieldKey.ARTIST));
			entry.put("Album Artist", tag.getFirst(FieldKey.ALBUM_ARTIST));
			entry.put("Composer", tag.getFirst(FieldKey.COMPOSER));
			entry.put("Album", tag.getFirst(FieldKey.ALBUM));
			entry.put("Grouping", tag.getFirst(FieldKey.GROUPING));
			entry.put("Genre", tag.getFirst(FieldKey.GENRE));
			entry.put("Size", (int) file.length());
			entry.put("Total Time", af.getAudioHeader().getTrackLength() * 1000);
			entry.put("Rating", convertID3ToItunesRating(tag.getFirst(FieldKey.RATING)));
			entry.put("Comments", tag.getFirst(FieldKey.COMMENT));
			
			String field = "";
			field = tag.getFirst(FieldKey.DISC_NO);
			if(!field.isEmpty())
			{
				if(field.matches("[0-9]{1,2}"))
				{
					entry.put("Disc Number", Integer.parseInt(field));
				}
			}
			field = tag.getFirst(FieldKey.DISC_TOTAL);
			if(!field.isEmpty())
			{
				if(field.matches("[0-9]{1,2}"))
				{
					entry.put("Disc Count", Integer.parseInt(field));
				}
			}
			field = tag.getFirst(FieldKey.TRACK);
			if(!field.isEmpty()) {
				if(field.matches("[0-9]{1,2}"))
				{
					entry.put("Track Number", Integer.parseInt(field));
				}
			}
			field = tag.getFirst(FieldKey.TRACK_TOTAL);
			if(!field.isEmpty())
			{
				if(field.matches("[0-9]{1,2}"))
				{
					entry.put("Track Count", Integer.parseInt(field));
				}
			}
			field = tag.getFirst(FieldKey.YEAR);
			if(!field.isEmpty())
			{
				if(field.matches("[0-9]{2,4}"))
				{
					entry.put("Year", Integer.parseInt(field));
				}
			}
			field = tag.getFirst(FieldKey.BPM);
			if(!field.isEmpty())
			{
				if(field.matches("[0-9]{1,3}"))
				{
					entry.put("BPM", Integer.parseInt(field));
				}
			}
		} 
		catch (TagException | InvalidAudioFrameException e) 
		{
			LOG.log(Level.WARNING, "Error reading tag during entry creation: " + file.getAbsolutePath());
		}
		catch (IOException | ReadOnlyFileException | CannotReadException e)
		{
			LOG.log(Level.SEVERE, "Failed to read file for entry creation: " + file.getAbsolutePath());
		}
		return entry;
	}
	
	/** Gets ID3 tag info from a file. If the tag is empty or doesn't exist, 
	 * a new one will be created. This new tag will then attempt to be filled
	 * with information from the iTunes Track Entry. Even if a new
	 * tag isn't necessary, empty fields will  still attempt to be filled.
	 * All tag objects are forced into ID3v2.3 format, the
	 * most compatible format.
	 * @param entry  iTunes Track Entry
	 * @returns new tag object
	 * @see Library
	 */
	public static Tag getTagFromTrackEntry(Entry<String, Map> entry)
	{
		String filePath = Utils.getFilePathFromTrackEntry(entry);
		Tag tag = null;

		try
		{
			tag = getTagFromAudioFile(filePath);
			
			if(tag == null)
			{
				tag = new ID3v23Tag();
				LOG.log(Level.WARNING, " : EMPTY TAG, creating new");
			}
			
			tag.setField(FieldKey.TITLE, (String) entry.getValue().getOrDefault("Name", ""));
			tag.setField(FieldKey.ARTIST, (String) entry.getValue().getOrDefault("Artist", ""));
			tag.setField(FieldKey.ALBUM_ARTIST, (String) entry.getValue().getOrDefault("Album Artist", ""));
			tag.setField(FieldKey.COMPOSER, (String) entry.getValue().getOrDefault("Composer", ""));
			tag.setField(FieldKey.ALBUM, (String) entry.getValue().getOrDefault("Album", ""));
			tag.setField(FieldKey.GROUPING, (String) entry.getValue().getOrDefault("Grouping", ""));
			tag.setField(FieldKey.GENRE, (String) entry.getValue().getOrDefault("Genre", ""));
			tag.setField(FieldKey.DISC_NO, String.valueOf(entry.getValue().getOrDefault("Disc Number", "0")));
			tag.setField(FieldKey.DISC_TOTAL, String.valueOf(entry.getValue().getOrDefault("Disc Count", "0")));
			tag.setField(FieldKey.TRACK, String.valueOf(entry.getValue().getOrDefault("Track Number", "0")));
			tag.setField(FieldKey.TRACK_TOTAL, String.valueOf(entry.getValue().getOrDefault("Track Count", "0")));
			tag.setField(FieldKey.YEAR, String.valueOf(entry.getValue().getOrDefault("Year", "0")));
			tag.setField(FieldKey.COMMENT, (String) entry.getValue().getOrDefault("Comments", ""));
			tag.setField(FieldKey.BPM, String.valueOf(entry.getValue().getOrDefault("BPM" , "0")));
			
			String rating = String.valueOf(entry.getValue().getOrDefault("Rating", 0));
			rating = convertITunesRatingToID3(rating);
			tag.setField(FieldKey.RATING, rating);
			
		}
		catch (TagException e)
		{
			LOG.log(Level.SEVERE, "Error reading/creating tag: " + filePath);
		}
		return tag;
	}
	
	/** Searches a file's ID3 tag for artwork information.
	 * @param filePath  File path to search
	 * @returns  {@link Artwork} object containing byte[] of artwork
	 */
	public static Artwork getArtworkFromID3(String filePath)
	{
		Artwork artwork = null;
		if(!new File(filePath).exists())
		{
			return null;
		}
			
		Tag tag = getTagFromAudioFile(filePath);
			
		if(tag != null)
		{
			artwork = tag.getFirstArtwork();
		}
		
		return artwork;
	}
	
	/** Converts song rating string from iTunes library xml (0 - 100) to its 
	 * equivalent value used in an ID3 tag (0 - 255).
	 * <li> 224-255 = 5 stars when READ with Windows Explorer, writes 255
	 * <li> 160-223 = 4 stars when READ with Windows Explorer, writes 196
	 * <li> 096-159 = 3 stars when READ with Windows Explorer, writes 128
	 * <li> 032-095 = 2 stars when READ with Windows Explorer, writes 64
	 * <li> 001-031 = 1 stars when READ with Windows Explorer, writes 1
	 * @param rating  iTunes Track Entry rating.
	 * @return  String representing iTunes equivalent rating.
	 */
	public static String convertITunesRatingToID3(String rating)
	{
		switch(rating)
		{
			default :
				return "0";
			case "20" :
				return "1";
			case "40" :
				return "64";
			case "60" :
				return "128";
			case "80" :
				return "196";
			case "100" :
				return "255";
		}
	}
	
	/** Converts ID3 tag rating (0 - 255) to its equivalent iTunes
	 * rating value (0 - 100).
	 * <li> 224-255 = 5 stars when READ with Windows Explorer, writes 255
	 * <li> 160-223 = 4 stars when READ with Windows Explorer, writes 196
	 * <li> 096-159 = 3 stars when READ with Windows Explorer, writes 128
	 * <li> 032-095 = 2 stars when READ with Windows Explorer, writes 64
	 * <li> 001-031 = 1 stars when READ with Windows Explorer, writes 1
	 * @param rating  ID3 tag rating field text.
	 * @return
	 */
	public static int convertID3ToItunesRating(String rating)
	{
		switch(rating)
		{
			default :
				return 0;
			case "1" :
				return 20;
			case "64" :
				return 40;
			case "128" :
				return 60;
			case "196" :
				return 80;
			case "255" :
				return 100;
		}
	}
	
	/** Combines multiple arrays to generate
	 * one array of all available iTunes fields.
	 * @return  String[] of iTunes fields.
	 */
	public static String[] getFieldDisplay()
	{
		String[] FIELDS;
		String[] a = {"Select a Field"};
		String[] a1 = FIELDS_STRINGS;
		String[] a2 = FIELDS_INTS;
		FIELDS = new String[1 + a1.length + a2.length];
		System.arraycopy(a, 0, FIELDS, 0, 1);
		System.arraycopy(a1,0,FIELDS, 1, a1.length);
		System.arraycopy(a2,0,FIELDS,a1.length + 1, a2.length);
		return FIELDS;
	}
	
	/** Saves a {@code Tag} (ID3 Tag) to the supplied
	 * iTunes Track Entry's file.
	 * @param entry  Library file's equivalent to a track in iTunes
	 * @param tag  {@link Tag} to save to file.
	 */
	public static void saveTagToFile(Entry<String, Map> entry, Tag tag)
	{
		String fileLocation = Utils.getFilePathFromTrackEntry(entry);
		saveTagToFile(fileLocation, tag);
	}
	
	/** Saves a [@code Tag} (ID3 Tag) to the supplied
	 * file path.
	 * @param filePath  File path to save ID3 tag to.
	 * @param tag  {@link Tag} to save to file.
	 */
	public static void saveTagToFile(String filePath, Tag tag)
	{
		if(!Settings.isDebugMode)
		{	
			try
			{
				AudioFile af = AudioFileIO.read(new File(filePath));
				af.setTag(tag);
				if(!af.getFile().canWrite())
				{
					af.getFile().setWritable(true);
				}
				af.commit();
				LOG.log(Level.FINER, "Tag written successfully" + filePath);
			}
			catch (CannotReadException | IOException
					| ReadOnlyFileException e)
			{
				LOG.log(Level.SEVERE, "Tried to save tag to file, file doesn't exist: " + filePath);
			}
			catch (TagException | InvalidAudioFrameException e) 
			{
				LOG.log(Level.WARNING, "Error saving tag: " + filePath);
			}
			catch (CannotWriteException e)
			{
				LOG.log(Level.SEVERE, "ID3 Tag failed to write: " + filePath);
			}
			catch (StringIndexOutOfBoundsException ex)
			{
				// do nothing : bug w/ JAudioTagger, sometimes throws this ex
			}
		}
	}
	
	/** Gets a {@link Tag} object, representing
	 * the supplied file path's ID3 tag info.
	 * @param path  File path to retrieve ID3 tag info from.
	 * @return  {@code Tag} representing ID3 tag info.
	 */
	public static Tag getTagFromAudioFile(String path)
	{
		Tag tag = null;
		try
		{
			tag = AudioFileIO.read(new File(path)).getTag();
		}
		catch (CannotReadException | IOException
				| ReadOnlyFileException e)
		{
			LOG.log(Level.SEVERE, "Failed to read file: " + path);
		}
		catch (TagException | InvalidAudioFrameException e) 
		{
			LOG.log(Level.WARNING, "Error reading tag: " + path);
		}
		catch (StringIndexOutOfBoundsException ex)
		{
			// do nothing : bug w/ JAudioTagger, sometimes throws this ex
		}
		return tag;
	}

}
