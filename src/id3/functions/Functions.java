package id3.functions;

import id3.gui.dialogs.ProgressDialog;
import id3.gui.functionpanel.panels.AppendPrependPanel.TextFunction;
import id3.gui.functionpanel.panels.FormattingPanel.TextRemovalType;
import id3.main.GUI;
import id3.main.Program;
import id3.main.Settings;
import id3.objects.Album;
import id3.objects.Artist;
import id3.objects.Library;
import id3.tables.TableEntry;
import id3.utils.Distancing;
import id3.utils.Utils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.StandardArtwork;

import xmlwise.Plist;

/** Static methods used to manipulate each track's
 * ID3 tags. Each {@link FunctionPanel} subclass will point to it's
 * corresponding function method found in this class.
 */
public final class Functions
{
	private static final Logger LOG = Program.LOG;
	
	protected Functions() 
	{ }
	
	/** Appends / Prepends text to a tag field.
	 * @param entry  Library file's equivalent to a track in iTunes
	 * @param function  Append or Prepend ?
	 * @param editField  The field to append / prepend to.
	 * @param text  The text to append / prepend to editField
	 */
	public static void appendOrPrependText(Entry<String, Map> entry, TextFunction function, FieldKey editField, String text)
	{
		Tag tag = Utils.getTagFromTrackEntry(entry);
		String field = tag.getFirst(editField);
		
		switch (function)
		{
			case Append :
			{
				field = field + text;
				break;
			}
			case Prepend :
			{
				field = text + field;
				break;
			}
		}
		
		try
		{
			tag.setField(editField, field);
			
			Utils.saveTagToFile(entry, tag);
			LOG.log(Level.FINER, "Sucessfully app/prepended text: " + tag.toString());
		}
		catch (KeyNotFoundException | FieldDataInvalidException e)
		{
			LOG.log(Level.WARNING, "Failed to append/prepend text: " + tag.toString());
		}
		catch (StringIndexOutOfBoundsException ex)
		{
			// do nothing : bug w/ JAudioTagger, sometimes throws this ex
		}
	}
	
	/** Appends / Prepends one tag's field to another
	 * field on the same tag.
	 * @param entry  Library file's equivalent to a track in iTunes
	 * @param function  Append or Prepend ?
	 * @param editField  The field to append / prepend to.
	 * @param copyField  This field's text will be appended / prepended to editField.
	 */
	public static void appendOrPrependField(Entry<String, Map> entry, TextFunction function, FieldKey editField, FieldKey copyField)
	{
		Tag tag = Utils.getTagFromTrackEntry(entry);
		String field = tag.getFirst(editField);
		String text = tag.getFirst(copyField);
		
		switch (function)
		{
			case Append :
			{
				field = field + text;
				break;
			}
			case Prepend :
			{
				field = text + field;
				break;
			}
		}
		
		try 
		{
			tag.setField(editField, field);
			
			Utils.saveTagToFile(entry, tag);
			LOG.log(Level.FINER, "Sucessfully app/prepended field: " + tag.toString());
		} 
		catch (KeyNotFoundException | FieldDataInvalidException e)
		{
			LOG.log(Level.WARNING, "Failed to append/prepend field: " + tag.toString());
		} 
		catch (StringIndexOutOfBoundsException ex)
		{
			// do nothing : bug w/ JAudioTagger, sometimes throws this ex
		}
	}
	
	/** Checks every track entry's song title to see if the title
	 * also contain's the artist's name. Returns a new table entry if true.
	 * @param entry  Library file's equivalent to a track in iTunes
	 * @return  {@link TableEntry}, which is the object backing 
	 * every {@link ID3Table}'s model.
	 * @see AbstractID3Model
	 */
	public static TableEntry getTitlesWithArtistNameIncluded(Entry<String, Map> entry)
	{
		String fileLocation = Utils.getFilePathFromTrackEntry(entry);
		
		TableEntry tableEntry = new TableEntry();
		tableEntry.SongTitle = (String) entry.getValue().getOrDefault("Name", "");
		tableEntry.Artist = (String) entry.getValue().getOrDefault("Artist", "");
		tableEntry.FilePath = fileLocation;
			
		if(tableEntry.SongTitle.toLowerCase().matches(tableEntry.Artist.toLowerCase() + "\\s\\-\\s.*"))
		{
			tableEntry.NewTitle = tableEntry.SongTitle.substring(tableEntry.Artist.length() + 3, tableEntry.SongTitle.length());
			return tableEntry;
		}
		
		return null;
	}
	
	/** Calculates each track's BPM and save's it to the tag.
	 * @param entry  Library file's equivalent to a track in iTunes
	 */
	public static void detectBPM(Entry<String, Map> entry)
	{
		File file = new File(Utils.getFilePathFromTrackEntry(entry));
			
		try 
		{
			Process process = Runtime.getRuntime().exec(Settings.BPM_CMD + file + "\" -w -o bpm.txt");
			while(process.isAlive()) 
			{
				Thread.sleep(300);
			}
			
			LOG.log(Level.FINER, "Calculated BPM for: " + file.getAbsolutePath());
		}
		catch (IOException | InterruptedException e)
		{
			LOG.log(Level.WARNING, "Failed to calculate BPM");
		}
	}
	
	/** Takes a directory, searches for all songs in sub directories
	 * and builds a new iTunes Library.xml
	 * using the discovered songs and their ID3 tags. The new
	 * xml file will need to be imported into iTunes to work
	 * (file->library->import playlist)
	 * @param directory  directory to scan
	 */
	public static void buildLibraryFile(File directory)
	{
		HashMap<String, Map> tracks = new HashMap<String, Map>();
		ArrayList<String> files = new ArrayList<String>();
		int keyCounter = 1;
		
		LinkedHashMap<String, Object> library = new LinkedHashMap<String, Object>();
		library.put("Major Version", 1);	//These entries are needed for proper xml parsing, otherwise arbitrary
		library.put("Minor Version", 1);
		library.put("Date", new Date());
		library.put("Application Version", "11.1.1");
		library.put("Features", 5);
		library.put("Show Content Ratings", true);
		library.put("Music Folder", "file://localhost" + directory.toURI().getPath());
		library.put("Library Persistent ID", "0000000000000001");
		
		ProgressDialog progress = new ProgressDialog();
		
		files = Utils.getAllFilesInSubDirs(directory);
		
		progress.setDeterminate(files.size());
		
		int progressCounter = 0;
		for(String filepath : files)
		{
			File file = new File(filepath);
			LOG.log(Level.FINER, "PROCESSING: " + file.getAbsolutePath());
			
			HashMap<String, Map> entry = Utils.createNewTrackEntry(keyCounter, file);
			if(!entry.isEmpty())
			{
				tracks.put(String.valueOf(keyCounter), entry);
				keyCounter++;
			}
			progress.update(progressCounter++);
		}
		
		library.put("Tracks", tracks);
		library.put("Playlists", new ArrayList<String>());
		
		progress.finish();
		
		writeXml(library);
	}
	
	/** Writes a library map to a new .xml file
	 * @param library  properly nested Library map
	 * @see Library
	 */
	private static void writeXml(Map library)
	{
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		
		try 
		{
			fos = new FileOutputStream("REBUILT_iTunes Library.xml");
			osw = new OutputStreamWriter(fos, "UTF-8");
			bw = new BufferedWriter(osw);
			
			bw.write(Plist.toPlist(library));
			bw.flush();
			LOG.log(Level.FINE, "XML rebuilt successfully");
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(GUI.frame, "Error creating new library file", "Error", JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			try
			{
				bw.close();
			}
			catch (IOException e)
			{
				LOG.log(Level.WARNING, "Output stream failed to close after writing new library file");
			}
		}
	}
	
	/** Copys all audio files from an iPod to the
	 * chosen directory. This will only work if
	 * "Disk Use" is enabled in iTunes for the iPod!
	 * <p>
	 * This is currently only tested on iPod Classic.
	 * @param driveLetter  Drive letter pointing to the iPod.
	 * @param copyDir  Directory iPod audio files will be copied to.
	 * @param buildXml  If true, also create a new iTunes Library.xml
	 * containing all files copied from the iPod.
	 */
	public static void copyFromIpod(char driveLetter, String copyDir, boolean buildXml)
	{
		File musicDir = new File(driveLetter + ":" + "\\iPod_Control\\Music");
		if(!musicDir.exists())
		{
			JOptionPane.showMessageDialog(GUI.frame, "Failed to discover iPod music directory", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		ProgressDialog progress = new ProgressDialog();
		
		ArrayList<String> files = Utils.getAllFilesInSubDirs(musicDir);
		
		progress.setDeterminate(files.size());
		
		int progressCounter = 0;
		for(String file : files)
		{
			LOG.log(Level.FINER, "PROCESSING: " + file);
			
			File copyFromFile = new File(file);
			String newFileName = "";
			String newDir = copyDir;
			String ext = file.substring(file.lastIndexOf('.'), file.length());
			
			try
			{
				Tag tag = Utils.getTagFromAudioFile(copyFromFile.getAbsolutePath());
				
				String trackNum = tag.getFirst(FieldKey.TRACK);
				if(trackNum.length() == 1)
				{
					trackNum = "0" + trackNum;
				}
				if(!trackNum.isEmpty())
				{
					newFileName += trackNum + " ";
				}
				
				String title = tag.getFirst(FieldKey.TITLE);
				title = Utils.removeIllegalWindowsFilePathChars(title);
				if(!title.isEmpty())
				{
					newFileName += title;
				}
				if(newFileName.isEmpty())
				{
					newFileName = UUID.randomUUID().toString().substring(0, 5);
				}
				
				String artist = tag.getFirst(FieldKey.ARTIST);
				artist = Utils.removeIllegalWindowsFilePathChars(artist);
				if(artist.isEmpty())
				{
					artist = "Unknown Artist";
				}
				String album = tag.getFirst(FieldKey.ALBUM);
				album = Utils.removeIllegalWindowsFilePathChars(album);
				if(album.isEmpty())
				{
					album = "Unknown Album";
				}
				
				newDir += "\\" + artist + "\\" + album + "\\";
				new File(newDir).mkdirs();
				
				File copyToFile = new File(newDir + newFileName + ext);
				if(copyToFile.exists())
				{
					copyToFile = new File(newDir + newFileName + "_copy" + ext);
				}
				
				LOG.log(Level.FINE, "Copying to: " + copyToFile);
				Files.copy(copyFromFile.toPath(), copyToFile.toPath());
				LOG.log(Level.FINE, "Copy Successful");
			}
			catch (IOException e)
			{
				LOG.log(Level.SEVERE, "Error reading tag from iPod file: " + copyFromFile.getAbsolutePath());
			}
			catch (StringIndexOutOfBoundsException ex)
			{
				// do nothing : bug w/ JAudioTagger, sometimes throws this ex
			}
			finally
			{
				progress.update(progressCounter++);
			}
		}
		progress.finish();
		
		if(buildXml)
		{
			buildLibraryFile(new File(copyDir));
		}
	}
	
	/** Calculates artist rating based on their tracks' ratings.
	 * @param lib  The {@link Library} object used to retrieve artists.
	 * @param field  The tag field used to store the calculated rating.
	 * @param isUseCustomRatings  If true, uses a custom rating system to
	 * calculate ratings. Default behavior is to simply average all ratings.
	 * @see Library#createArtistObjects()
	 * @see ArtistRatingDialog
	 */
	public static void calculateArtistRating(Library lib, FieldKey field, boolean isUseCustomRatings)
	{
		if(isUseCustomRatings)
		{
			ProgressDialog progress = new ProgressDialog(lib.getLibraryArtists().size());
			
			int progressCounter = 0;
			for(Artist artist : lib.getLibraryArtists())
			{
				LOG.log(Level.FINER, "Getting rating for artist: " + artist.getName());
				int ratingIsFourPlus = 0;
				int rated = 0;
				int unrated = 0;
				
				for(Album album : artist.getAlbums())
				{
					for(String trackKey : album.getTracks().keySet())
					{
						int rating = (int) lib.getOrDefaultTrackValue(trackKey, "Rating", 0);
						if(rating > 0)
						{
							rated++;
							if(rating >= 80)
							{
								ratingIsFourPlus++;
							}
							else if(rating == 20)
							{
								rated--;
							}
						}
						else
						{
							unrated++;
						}
					}
				}
				
				double total = (double) rated + unrated;
				if(rated / total >= Settings.ratedSongsMin && total >= Settings.totalSongsMin)
				{
					LOG.log(Level.FINEST, ratingIsFourPlus + " / " + rated);
					
					double artistRating;
					if(ratingIsFourPlus == 0)
					{
						artistRating = 0;
					}
					else
					{
						artistRating = (double) ratingIsFourPlus / rated;
					}
					
					//User defined brackets are set in Settings and stored in settings.ini
					if(artistRating == 0 || artistRating < Settings.oneStarMax)
					{
						artist.setRating(1);
					}
					else if (artistRating >= Settings.twoStarMin && artistRating < Settings.threeStarMin)
					{
						artist.setRating(2);
					}
					else if (artistRating >= Settings.threeStarMin && artistRating < Settings.fourStarMin)
					{
						artist.setRating(3);
					}
					else if (artistRating >= Settings.fourStarMin && artistRating < Settings.fiveStarMin)
					{
						artist.setRating(4);
					}
					else if (artistRating >= Settings.fiveStarMin)
					{
						artist.setRating(5);
					}
				}
				else 
				{
					artist.setRating(0);
				}
				
				LOG.log(Level.FINER, "Final rating: " + artist.getRating());
				progress.update(progressCounter++);
			}
			progress.finish();
		}
		else if(!isUseCustomRatings)
		{
			ProgressDialog progress = new ProgressDialog(lib.getLibraryArtists().size());
			
			int progressCounter = 0;
			for(Artist artist : lib.getLibraryArtists())
			{
				int artistRating = 0;
				int ratedCount = 0;
				
				for(Album album : artist.getAlbums())
				{
					for(String track : album.getTracks().keySet())
					{
						int songRating = (int) lib.getOrDefaultTrackValue(track, "Rating", 0) / 20;
						if(songRating != 0)
						{
							ratedCount++;
						}
						artistRating += songRating;
					}
				}
				
				artist.setRating(((int) ((double) artistRating / ratedCount + .5)));
				LOG.log(Level.FINE, "Artist Rating: " + artist.getRating());
				progress.update(progressCounter++);
			}
			progress.finish();
		}
		
		ProgressDialog progress = new ProgressDialog(lib.getLibraryArtists().size());
		
		int progressCounter = 0;
		for(Artist artist : lib.getLibraryArtists())
		{
			String ratingString = "Artist Rating: ";
			int rating = artist.getRating();
			switch(rating)
			{
				case(0) :
					ratingString += "Unrated";
					break;
				default :
					ratingString += rating;
					break;
			}
			
			for(Album album : artist.getAlbums())
			{
				for(String trackPath : album.getTracks().values())
				{
					try
					{
						Tag tag = Utils.getTagFromAudioFile(trackPath);
						tag.setField(field, ratingString);
						
						Utils.saveTagToFile(trackPath, tag);
						LOG.log(Level.FINER, "Artist rating sucessfully saved: " + tag.toString());
					}
					catch (KeyNotFoundException | TagException e)
					{
						LOG.log(Level.WARNING, "Failed to write artist rating to tag: " + trackPath);
					}
				}
			}
			progress.update(progressCounter++);
		}
		progress.finish();
	}
	
	/** Calculates every albums rating from the average of its'
	 * tracks' ratings.
	 * @param lib  The {@link Library} object used to retrieve albums.
	 * @param field  The tag field to save calculated album rating to.
	 * @param minPercentSongsRated  Album calculation will only be done
	 * if at least this percentage of its' tracks are rated.
	 */
	public static void calculateAlbumRating(Library lib, FieldKey field, int minPercentSongsRated)
	{
		ProgressDialog progress = new ProgressDialog(lib.getLibraryArtists().size());
		
		int progressCounter = 0;
		for(Artist artist : lib.getLibraryArtists())
		{
			for(Album album : artist.getAlbums())
			{
				double percent = (double) minPercentSongsRated / 100;
				double minRated = album.getTrackCount() * percent;
				int albumRating = 0;
				int unrated = 0;
				
				for(String trackKey : album.getTracks().keySet())
				{
					int songRating = (int) lib.getOrDefaultTrackValue(trackKey, "Rating", 0);
					if(songRating > 0)
					{
						albumRating = albumRating + (songRating / 20);
					}
					else
					{
						unrated++;
					}
				}
				
				if((album.getTrackCount() - unrated) > minRated)
				{
					album.setRating(((int) ((double) albumRating / (album.getTrackCount() - unrated) + .5)));
					LOG.log(Level.FINEST, albumRating + " / " + (album.getTrackCount() - unrated));
				}
				else 
				{
					album.setRating(0);
				}
				
				LOG.log(Level.FINE, artist.getName() + " - " + album.getName() + "; Rating = " + album.getRating());
			}
			progress.update(progressCounter++);
		}
		progress.finish();
		
		progress = new ProgressDialog(lib.getLibraryArtists().size());
		
		progressCounter = 0;
		for(Artist artist : lib.getLibraryArtists())
		{
			for(Album album : artist.getAlbums())
			{
				String ratingString = "Album Rating: ";
				int rating = album.getRating();
				switch(rating)
				{
					case(0) :
						ratingString += "Unrated";
						break;
					default :
						ratingString += rating;
						break;
				}
				
				for(String trackPath : album.getTracks().values())
				{
					try
					{
						Tag tag = Utils.getTagFromAudioFile(trackPath);
						tag.setField(field, ratingString);
						
						Utils.saveTagToFile(trackPath, tag);
						LOG.log(Level.FINER, "Album rating successfully added: " + tag.toString());
					}
					catch (KeyNotFoundException | TagException e)
					{
						LOG.log(Level.WARNING, "Failed to write album rating to tag: " + trackPath);
					}
				}
			}
			progress.update(progressCounter++);
		}
		progress.finish();
	}
	
	/** Deletes a tag's field.
	 * @param entry  Library file's equivalent to a track in iTunes.
	 * @param field  The tag field to delete.
	 * <p>
	 * Note that the artwork field is deleted by passing
	 * {@code FieldKey.MEDIA}
	 */
	public static void deleteField(Entry<String, Map> entry, FieldKey field)
	{		
		try
		{
			Tag tag = Utils.getTagFromTrackEntry(entry);
			
			if(field == FieldKey.MEDIA)
			{
				tag.deleteArtworkField();
			}
			else
			{
				tag.deleteField(field);
			}
				
			Utils.saveTagToFile(entry, tag);
			LOG.log(Level.FINE, "Deleted " + field + ": " + tag.toString());
		}
		catch (StringIndexOutOfBoundsException ex)
		{
			// do nothing : bug w/ JAudioTagger, sometimes throws this ex
		}
	}
	
	/** If artwork is not saved directly on the tag in a file
	 * and instead contains a url pointing to the image, attempt to
	 * get the image and save it on the tag.
	 * @param entry  Library file's equivalent to a track in iTunes. 
	 */
	public static void embedArtwork(Entry<String, Map> entry)
	{	
		Tag tag = null;
		try
		{
			tag = Utils.getTagFromTrackEntry(entry);
			
			Artwork artwork = tag.getFirstArtwork();
			if(!artwork.isLinked())
			{
				return;
			}
				
			File file = new File(artwork.getImageUrl());
			tag.deleteArtworkField();
				
			StandardArtwork art = StandardArtwork.createArtworkFromFile(file);
			tag.addField(art);
			
			Utils.saveTagToFile(entry, tag);
			LOG.log(Level.FINE, "Sucessfully embedded artwork: " + tag.toString());
		}
		catch (IOException | TagException e)
		{
			LOG.log(Level.WARNING, "Failed to read/write artwork to file: " + tag.toString());
		}
		catch (StringIndexOutOfBoundsException ex)
		{
			// do nothing : bug w/ JAudioTagger, sometimes throws this ex
		}
	}
	
	/** Saves a jpg of all artwork found in a library by checking
	 * all tracks. Duplicate art is ignored.
	 * @param entry  Library file's equivalent to a track in iTunes.
	 * @param exportDir  The directory to save the artwork to.
	 * @param isSplitByArtist  Splits artwork files by artist. The
	 * Folder structure will be exportDir/artist/all artwork. Default
	 * behavior puts all artwork files in exportDir named by
	 * "artist - album.jpg".
	 */
	public static void exportArtwork(Entry<String, Map> entry, String exportDir, boolean isSplitByArtist)
	{
		OutputStream out = null; Tag tag = null;
		try
		{
			tag = Utils.getTagFromTrackEntry(entry);
				
			String artist = tag.getFirst(FieldKey.ARTIST);
			if(artist.isEmpty())
			{
				artist = UUID.randomUUID().toString().substring(0, 5);
			}
			String album = tag.getFirst(FieldKey.ALBUM);
			if(album.isEmpty())
			{
				album = UUID.randomUUID().toString().substring(0, 5);
			}
			artist = Utils.removeIllegalWindowsFilePathChars(artist);
			album = Utils.removeIllegalWindowsFilePathChars(album);
			
			String artworkTitle = artist + " - " + album;
			
			String artworkPath = exportDir + "\\";
			if(isSplitByArtist)
			{
				artworkPath += artist + "\\";
			}
			
			new File(artworkPath).mkdirs();
			artworkPath += artworkTitle + ".jpg";
			
			if(!new File(artworkPath).exists())
			{
				Artwork artwork = tag.getFirstArtwork();
				if(artwork == null)
				{
					return;
				}
				
				out = new BufferedOutputStream(new FileOutputStream(artworkPath));
				out.write(artwork.getBinaryData());
				LOG.log(Level.FINE, "Artwork written sucessfully: " + artworkTitle);
			}
		} 
		catch(IOException e)
		{
			LOG.log(Level.WARNING, "Failed to read file while fetching artwork for export: " + tag.toString());
		}
		catch (StringIndexOutOfBoundsException ex)
		{
			// do nothing : bug w/ JAudioTagger, sometimes throws this ex
		}
		finally
		{
			if(out != null)
			{
				try
				{
					out.close();
				}
				catch (IOException e)
				{
					LOG.log(Level.WARNING, "IO stream failed to close after exporting artwork");
				}
			}
		}
	}
	
	/** Creates a List of all tracks that are listed in
	 * a library, but don't exist on their expected file path.
	 * @param lib  The {@link Library} object used to search
	 * for missing tracks.
	 * @param isAttemptDiscovery  If true, use string distancing to
	 * look for tracks that might be equal to the missing track.
	 * This is helpful for tracks that are renamed to be slightly
	 * different.
	 */
	public static void findMissingSongs(Library lib, boolean isAttemptDiscovery)
	{
		ArrayList<TableEntry> missingSongs = new ArrayList<TableEntry>();
		ProgressDialog progress = new ProgressDialog(lib.getTrackMapEntries().size());
		
		int progressCounter = 0;
		for(Entry<String, Map> entry : lib.getTrackMapEntries())
		{
			progress.update(progressCounter++);
			
			String fileLocation = Utils.getFilePathFromTrackEntry(entry);
			
			TableEntry tableEntry = null; // Not used for table in this method, but for storing values
			String artist = (String) entry.getValue().getOrDefault("Artist", "");
			String album = (String) entry.getValue().getOrDefault("Album", "");
			
			File file = new File(fileLocation);
			String fileName = file.getName();
			if(!file.exists())
			{
				tableEntry = new TableEntry();
				tableEntry.FileName = fileName;
				tableEntry.Artist = artist;
				tableEntry.Album = album;
				tableEntry.FilePath = fileLocation;
				tableEntry.Status = "Missing";
				tableEntry.NewFilePath = "";
				
				if(isAttemptDiscovery)
				{
					artist = Utils.removeIllegalWindowsFilePathChars(artist);
					album = Utils.removeIllegalWindowsFilePathChars(album);
																								
					String builtLocation = lib.getMusicFolder() + "\\" + artist;
					if(album != null)
					{
						builtLocation = builtLocation + "/" + album;
					}
					LOG.log(Level.FINEST, "IN: " + builtLocation);
					
					File dir = new File(builtLocation);	System.out.println(dir.getAbsolutePath());//Look in lib/artist/album directory
					if(!dir.exists())	//Look in lib/artist directory since album dir doesnt exist
					{
						dir = dir.getParentFile();
						if(!dir.exists())	//If artist directory doesn't exist, assume missing
						{
							missingSongs.add(tableEntry);
							LOG.log(Level.FINE, "MISSING: "+ fileLocation);
							continue;
						}
					}
					
					for(String name : dir.list())	// Dir structure looks good, check all files in folder for close matches
					{
						float similarity = Distancing.compare(name, fileName);
						LOG.log(Level.FINER, similarity + " - " + name);
						
						if(similarity > .85)
						{
							File newFile = new File(dir.getAbsolutePath() +"\\" + name);
							LOG.log(Level.FINE, "FOUND: " + newFile.getAbsolutePath() + " | % = " + similarity * 100);

							tableEntry.Status = "Mapped Incorrectly";
							tableEntry.NewFilePath = newFile.toURI().getPath();
							break;
						}
					}
				}
				
				missingSongs.add(tableEntry);
			}
			else
			{
				LOG.log(Level.FINER, "File exists: " + fileLocation);
			}
		}
		
		progress.finish();
		writeMissingTracks(missingSongs);
	}
	
	/** Creates a csv file containing missing tracks.
	 * @param missingTracks  List of missing tracks.
	 * @see findMissingSongs
	 */
	private static void writeMissingTracks(ArrayList<TableEntry> missingTracks)
	{
		BufferedWriter bw = null;
		try
		{
			FileOutputStream fos = new FileOutputStream(new File("Missing Tracks.csv"));
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			bw = new BufferedWriter(osw);
			bw.write("TITLE,ARTIST,ALBUM,LOCATION,STATUS,ACTUAL LOCATION\r\n");
			for(TableEntry track : missingTracks)
			{
				bw.write(track.FileName + "," + track.Artist + "," + track.Album + ","
						+ track.FilePath + "," + track.Status + "," + track.NewFilePath + "\r\n");
			}
			bw.flush();
		} 
		catch (IOException e)
		{
			LOG.log(Level.SEVERE, "Error writing Missing Tracks.csv");
		}
		finally
		{
			try
			{
				bw.close();
			}
			catch (IOException e)
			{
				LOG.log(Level.SEVERE, "Failed to close I/O stream after writing missing tracks");
			}
		}
	}

	/** Looks specifically for files that are duplicates of files already
	 * listed in the library file, but have a number added to the end of
	 * the name. Older versions of iTunes sometimes added copies of the
	 * same song for whatever reason. (i.e 01 name.mp3, 01 name 1.mp3)
	 * @param entry  Library file's equivalent to a track in iTunes.
	 * @return  {@link TableEntry} object, used to back {@link ID3Table}'s
	 * model.
	 * @see AbstractID3Model
	 */
	public static TableEntry deleteDuplicateFiles(Entry<String, Map> entry)
	{
		String fileLocation = Utils.getFilePathFromTrackEntry(entry);
		
		TableEntry tableEntry = null;
			
		String fileName = Utils.getFileNameNoExt(new File(fileLocation));
		Pattern regex = Pattern.compile(Utils.escapeMetaCharacters(fileName) + "\\s[0-9]\\..*", Pattern.CASE_INSENSITIVE);
			
		File[] files = new File(fileLocation).getParentFile().listFiles();
		if(files == null)
		{
			LOG.log(Level.WARNING, "No other files found in directory");
			return null;
		}
			
		for(File file : files)
		{
			if(regex.matcher(file.getName()).matches())
			{
				LOG.log(Level.FINE, "File may be duplicate: " + file.getAbsolutePath());
				tableEntry = new TableEntry();
				tableEntry.FilePath = file.getAbsolutePath();
				tableEntry.Status = "Delete Dup";
			}
		}
		
		return tableEntry;
	}
		
	/** Looks through ALL sub folders in library folder/music for
	 * ALL FILES not listed in the iTunes library file.
	 * For best results, libraryfolder/artist/album folder structure is expected.
	 * @param lib  The {@link Library} object used to
	 * check files against.
	 * @param isIgnoreImages  image files will not be listed. This is
	 * useful if you know some ID3 tags use linked images (thus deleting
	 * the image file would remove the artwork from any linked ID3 tags)
	 * @return  {@link TableEntry} object, used to back {@link ID3Table}'s
	 * model.
	 * @see AbstractID3Model
	 */
	public static ArrayList<TableEntry> findUnlistedSongs(Library lib, boolean isIgnoreImages)
	{
		ProgressDialog progress = new ProgressDialog();
		
		File musicDir = new File(lib.getMusicFolder());
		if(!musicDir.exists())
		{
			JOptionPane.showMessageDialog(GUI.frame, "Music directory doesn't exist!", "Error", JOptionPane.ERROR_MESSAGE);
			progress.finish();
			return new ArrayList<TableEntry>();
		}
			
		ArrayList<String> allFilesInMusicDir = Utils.getAllFilesInSubDirs(musicDir);
			
		if(isIgnoreImages)
		{
			ArrayList<String> imageFiles = new ArrayList<String>();
			for(String file : allFilesInMusicDir)
			{
				String ext = file.substring(file.lastIndexOf("."), file.length());
				if(ext.matches("\\.(jpg|png|gif|bmp|jpeg|jpe)"))
				{
					imageFiles.add(file);
				}
			}
			allFilesInMusicDir.removeAll(imageFiles);
		}
		
		progress.setDeterminate(lib.getTrackMapEntries().size());
		int progressCounter = 0;
		for(Entry<String, Map> entry : lib.getTrackMapEntries())
		{
			String fileLocation = Utils.getFilePathFromTrackEntry(entry);
			fileLocation = fileLocation.substring(1, fileLocation.length()).replace("/", "\\");
			LOG.log(Level.FINER, "SEARCHING: " + fileLocation);
			
			if(allFilesInMusicDir.contains(fileLocation))
			{
				LOG.log(Level.FINER, "ITUNES ENTRY FOUND: " + fileLocation);
				allFilesInMusicDir.remove(fileLocation);
			}
			progress.update(progressCounter++);
		}
		progress.finish();
		
		ArrayList<TableEntry> tableEntries = new ArrayList<TableEntry>();
		
		for(String file : allFilesInMusicDir)
		{
			TableEntry entry = new TableEntry();
			entry.FilePath = file;
			entry.Status = "Find + Move";
			
			if(Utils.isContainsNonAscii(file))
			{
				entry.Status ="Check Manually";
				LOG.log(Level.FINE, "Contains Non-ASCII: " + file);
			}
			
			tableEntries.add(entry);
			LOG.log(Level.FINE, "MISSING: " + file);
		}
		
		return tableEntries;
	}
	
	/** Capitalizes the first letter of every word for every field,
	 * excluding comments, composer, and grouping by default.
	 * @param entry  Library file's equivalent to a track in iTunes
	 * @param isUseProperCaps  Words that shouldn't be capitalzied will
	 * not be (i.e. articles - a, of, the, etc.).
	 * @param isIncludeComments  Include comments field.
	 * @param isIncludeComposer  Include composer field.
	 * @param isIncludeGrouping  Include grouping field.
	 * @see Utils#capitalizeTitle(String, boolean)
	 */
	public static void formatFields(Entry<String, Map> entry, boolean isUseProperCaps, boolean isIncludeComments,
			boolean isIncludeComposer, boolean isIncludeGrouping)
	{
		Tag tag = Utils.getTagFromTrackEntry(entry);
		try 
		{
			String title = tag.getFirst(FieldKey.TITLE);
			String formatTitle = Utils.capitalizeTitle(title, isUseProperCaps);
			
			String artist = tag.getFirst(FieldKey.ARTIST);
			String formatArtist = Utils.capitalizeTitle(artist, isUseProperCaps);
			
			String albumArtist = tag.getFirst(FieldKey.ALBUM_ARTIST);
			String formatAlbumArtist = Utils.capitalizeTitle(albumArtist, isUseProperCaps);
			
			String album = tag.getFirst(FieldKey.ALBUM);
			String formatAlbum = Utils.capitalizeTitle(album, isUseProperCaps);
			
			String genre = tag.getFirst(FieldKey.GENRE);
			String formatGenre = Utils.capitalizeTitle(genre, isUseProperCaps);
			
			tag.setField(FieldKey.TITLE, formatTitle);
			tag.setField(FieldKey.ARTIST, formatArtist);
			tag.setField(FieldKey.ALBUM_ARTIST, formatAlbumArtist);
			tag.setField(FieldKey.ALBUM, formatAlbum);
			tag.setField(FieldKey.GENRE, formatGenre);
			
			if(isIncludeComments)
			{
				String comments = tag.getFirst(FieldKey.COMMENT);
				String formatComments = Utils.capitalizeTitle(comments, isUseProperCaps);
				tag.setField(FieldKey.COMMENT, formatComments);
			}
			
			if(isIncludeComposer)
			{
				String composer = tag.getFirst(FieldKey.COMPOSER);
				String formatComposer = Utils.capitalizeTitle(composer, isUseProperCaps);
				tag.setField(FieldKey.COMPOSER, formatComposer);
			}
			
			if(isIncludeGrouping)
			{
				String grouping = tag.getFirst(FieldKey.GROUPING);
				String formatGrouping = Utils.capitalizeTitle(grouping, isUseProperCaps);
				tag.setField(FieldKey.GROUPING, formatGrouping);
			}
			
			Utils.saveTagToFile(entry, tag);
		}
		catch (KeyNotFoundException | FieldDataInvalidException e)
		{
			LOG.log(Level.WARNING, "Failed to format tag: " + tag.toString());
		}
		catch (StringIndexOutOfBoundsException ex)
		{
			// do nothing : bug w/ JAudioTagger, sometimes throws this ex
		}
	}
	
	/** All leading and trailing whitespace (spaces) will be
	 * removed from all fields on every tag.
	 * @param entry  Library file's equivalent to a track in iTunes
	 */
	public static void removeLeadingTrailingSpaces(Entry<String, Map> entry)
	{
		Tag tag = Utils.getTagFromTrackEntry(entry);
		try 
		{
			tag.setField(FieldKey.TITLE, tag.getFirst(FieldKey.TITLE).trim());
			tag.setField(FieldKey.ARTIST, tag.getFirst(FieldKey.ARTIST).trim());
			tag.setField(FieldKey.ALBUM_ARTIST, tag.getFirst(FieldKey.ALBUM_ARTIST).trim());
			tag.setField(FieldKey.COMPOSER, tag.getFirst(FieldKey.COMPOSER).trim());
			tag.setField(FieldKey.ALBUM, tag.getFirst(FieldKey.ALBUM));
			tag.setField(FieldKey.GROUPING, tag.getFirst(FieldKey.GROUPING).trim());
			tag.setField(FieldKey.GENRE, tag.getFirst(FieldKey.GENRE).trim());
			tag.setField(FieldKey.COMMENT, tag.getFirst(FieldKey.COMMENT).trim()); 
			
			LOG.log(Level.FINEST, tag.toString());
			Utils.saveTagToFile(entry, tag);
		}
		catch (KeyNotFoundException | FieldDataInvalidException e)
		{
			LOG.log(Level.WARNING, "leading/trailing removal failed" + tag.toString());
		}
		catch (StringIndexOutOfBoundsException ex)
		{
			// do nothing : bug w/ JAudioTagger, sometimes throws this ex
		}
	}
	
	/** Remove a number of characters from either the start
	 * or end of a tag field.
	 * @param entry  Library file's equivalent to a track in iTunes
	 * @param field  Field to remove characters from.
	 * @param type  Remove leading or remove tailing chars
	 * @param nChars  Number of characters to remove
	 */
	public static void removeLeadingTrailingChars(Entry<String, Map> entry, FieldKey field, TextRemovalType type, int nChars)
	{
		Tag tag = Utils.getTagFromTrackEntry(entry);
		
		String editField = tag.getFirst(field);
		if(editField.length() < nChars)
		{
			nChars = editField.length();
		}
		
		if(type == TextRemovalType.Leading)
		{
			editField = editField.substring(nChars, editField.length());
		}
		else if(type == TextRemovalType.Trailing)
		{
			editField = editField.substring(0, editField.length() - nChars);
		}
		
		try 
		{
			tag.setField(field, editField);
			
			LOG.log(Level.FINEST, tag.toString());
			Utils.saveTagToFile(entry, tag);
		}
		catch (KeyNotFoundException | FieldDataInvalidException e)
		{
			JOptionPane.showMessageDialog(GUI.frame, "Failed to remove \'n\' chars: " + tag.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			LOG.log(Level.WARNING, "Couldn't remove n chars: " + tag.toString());
		}
		catch (StringIndexOutOfBoundsException ex)
		{
			// do nothing : bug w/ JAudioTagger, sometimes throws this ex
		}
	}
	
	/** Not yet implemented.
	 * @param entry  Library file's equivalent to a track in iTunes
	 */
	public static void fetchLyrics(TableEntry entry)
	{
		LOG.log(Level.FINE, "Get lyrics for: " + entry.Artist + " - " + entry.SongTitle);
		
		//Call Lyrics API
		
		LOG.log(Level.FINE, "Lyrics added: " + entry.SongTitle);
	}
	
	/** Creates TableEntrys for all tracks that have a rating >= the provided rating.
	 * @param entry  Library file's equivalent to a track in iTunes
	 * @param minRating  Minimum rating required. 
	 * @return  {@link TableEntry} object that backs {@link ID3Table}'s
	 * table model.
	 * @see AbstractID3Model
	 */
	public static TableEntry getSongsByMinimumRating(Entry<String, Map> entry, int minRating)
	{
		String fileLocation = Utils.getFilePathFromTrackEntry(entry);
		
		TableEntry tableEntry = null;
			
		int rating = (int) entry.getValue().getOrDefault("Rating", 0) / 20;
		if(rating >= minRating)
		{
			tableEntry = new TableEntry();
			tableEntry.SongTitle = (String) entry.getValue().getOrDefault("Name", "");
			tableEntry.Artist = (String) entry.getValue().getOrDefault("Artist", "");
			tableEntry.Album = (String) entry.getValue().getOrDefault("Album", "");
			tableEntry.Rating = String.valueOf(rating);
			tableEntry.FilePath = fileLocation;
		}
		return tableEntry;
	}
	
	/** Attempts to fill the provided missing fields by looking
	 * at other tracks on the same album and seeing if the field
	 * in question is filled out for the other track.
	 * @param album  {@link Album} object containing the tracks
	 * @param keys  Tag fields to attempt to fill missing
	 * @see Library#createArtistObjects()
	 */
	public static void fixMissing(Album album, FieldKey[] keys)
	{
		for(String trackPath : album.getTracks().values())
		{
			Tag tag = null;
			try
			{
				tag = Utils.getTagFromAudioFile(trackPath);
				
				for(FieldKey key : keys)
				{
					String albumField = null;
					switch(key)
					{
						default:
							break;
						case ALBUM_ARTIST :
						{
							albumField = album.getArtist().getName();
							break;
						}
						case YEAR :
						{
							albumField = String.valueOf(album.getYear());
							break;
						}
						case DISC_TOTAL :
						{
							albumField = String.valueOf(album.getDiscCount());
							break;
						}
						case COMPOSER :
						{
							albumField = album.getComposer();
							break;
						}
						case GROUPING :
						{
							albumField = album.getGrouping();
							break;
						}
						case GENRE :
						{
							albumField = album.getGenre();
							break;
						}
						case COMMENT :
						{
							albumField = album.getComments();
							break;
						}
						case MEDIA :
						{
							albumField = album.getArtwork().getMimeType(); // Non-null means artwork exists
							break;
						}
					}
					
					if(albumField == "-1" || albumField.isEmpty()	//Make sure album obj has meaningful field data
							|| albumField.matches(Library.UUID_REGEX))
					{
						continue;
					}
					
					if(key == FieldKey.MEDIA)
					{
						if(tag.getArtworkList().isEmpty())
						{
							tag.createField(album.getArtwork());
						}
					}
					else
					{
						String tagField = tag.getFirst(key);
						if(tagField.isEmpty())
						{
							tag.setField(key, albumField);
							LOG.log(Level.FINE, "Wrote missing field: " + key + " to file: " + trackPath);	
						}
					}
				}
			}
			catch (TagException e)
			{
				LOG.log(Level.WARNING, "Failed to write missing field to file: " + trackPath);
			}
			catch (StringIndexOutOfBoundsException ex)
			{
				// do nothing : bug w/ JAudioTagger, sometimes throws this ex
			}
			
			Utils.saveTagToFile(trackPath, tag);
		}
	}
	
	/** Calculates an album's "proper" track count
	 * by checking to see how many tracks exist on that album.
	 * Default behavior is to only write the track count tracks
	 * that have empty {@code FieldKey.TRACK_TOTAL} fields.
	 * @param album  {@link Album} object to get track count from.
	 * @param isIncludeNonEmpty  {@code FieldKey.TRACK_TOTAL} will
	 * be overwritten with the newly calculated count.
	 */
	public static void calculateTrackCount(Album album, boolean isIncludeNonEmpty)
	{
		String calculatedCount = String.valueOf(album.getCalculatedTrackCount());
		for(String trackPath : album.getTracks().values())
		{
			try
			{
				Tag tag = Utils.getTagFromAudioFile(trackPath);
				String count = tag.getFirst(FieldKey.TRACK_TOTAL);
				if(!count.isEmpty() && !isIncludeNonEmpty)
				{
					continue;
				}
				
				if(count != calculatedCount)
				{
					tag.setField(FieldKey.TRACK_TOTAL, calculatedCount);
					
					LOG.log(Level.FINE, "Calculated Track Count: " + count +  ", added to: " + trackPath);
					Utils.saveTagToFile(trackPath, tag);
				}
			}
			catch (TagException e)
			{
				LOG.log(Level.WARNING, "Failed to write calculated track count to:  " + trackPath);
			}
			catch (StringIndexOutOfBoundsException ex)
			{
				// do nothing : bug w/ JAudioTagger, sometimes throws this ex
			}
			
		}
	}
	
	/** Creates a TableEntry for each track that has the track number
	 * within the song's title.
	 * @param entry  Library file's equivalent to a track in iTunes
	 * @return  {@link TableEntry} object that backs {@link ID3Table}'s
	 * table model.
	 * @see AbstractID3Model
	 */
	public static TableEntry removeTrackNumberFromTitles(Entry<String, Map> entry)
	{	
		String fileLocation = Utils.getFilePathFromTrackEntry(entry);
		
		TableEntry tableEntry = null;
			
		String title = (String) entry.getValue().getOrDefault("Name", "");
		String newTitle = null;
		String tracknum = null;
			
		if(title.substring(0, 1).matches("[0-9]"))
		{
			if(title.matches("[0-9]{2}\\s\\-\\s.*"))	//Ordering: most specific regexes --> most broad
			{
				tracknum = title.substring(0, 2);
				newTitle = title.substring(5, title.length());
			}
			else if(title.matches("[0-9]{2}\\.\\s.*"))
			{
				tracknum = title.substring(0, 2);
				newTitle = title.substring(4, title.length());
			}
			else if(title.matches("[0-9]{2}(\\s|\\-|\\.).*"))
			{
				tracknum = title.substring(0, 2);
				newTitle = title.substring(3, title.length());
			}
			else if(title.matches("[0-9]\\s\\-\\s.*"))
			{
				tracknum = title.substring(0, 1);
				newTitle = title.substring(4, title.length());
			}
			else if(title.matches("[0-9]\\.\\s.*"))
			{
				tracknum = title.substring(0, 1);
				newTitle = title.substring(3, title.length());
			}
			else if(title.matches("[0-9](\\s|\\-|\\.).*"))
			{
				tracknum = title.substring(0, 1);
				newTitle = title.substring(2, title.length());
			}
		}
			
		if(newTitle != null)
		{
			tableEntry = new TableEntry();
			tableEntry.SongTitle = title;
			tableEntry.NewTitle = newTitle;
			tableEntry.TrackNumber = tracknum;
			tableEntry.FilePath = fileLocation;
		}
		
		return tableEntry;
	}
	
	/** Saves the track's song rating as listed in iTunes
	 * as an equivalent rating on its ID3 tag.
	 * @param entry  Library file's equivalent to a track in iTunes.
	 */
	public static void saveRatings(Entry<String, Map> entry)
	{	
		String rating = String.valueOf(entry.getValue().getOrDefault("Rating", "0"));
		rating = Utils.convertITunesRatingToID3(rating);
			
		try
		{
			Tag tag = Utils.getTagFromTrackEntry(entry);
			tag.setField(FieldKey.RATING, rating);
				
			Utils.saveTagToFile(entry, tag);
			LOG.log(Level.FINE, "Rating saved to tag: " + tag.toString());
		}
		catch (TagException e)
		{
			LOG.log(Level.WARNING, "Failed to add Rating to tag");
		}
	}
	
	/** Swaps two like-fields on the supplied track.
	 * <p>
	 * A like-field are fields that accept the same types
	 * (string, int, etc.)
	 * @param entry  Library file's equivalent to a track in iTunes
	 * @param fieldOne  First field to use in swap.
	 * @param fieldTwo  Second field to use in swap.
	 */
	public static void swapTags(Entry<String, Map> entry, FieldKey fieldOne, FieldKey fieldTwo)
	{
		Tag tag = Utils.getTagFromTrackEntry(entry);
		try
		{
			tag.setField(fieldOne, tag.getFirst(fieldTwo));
			tag.setField(fieldTwo, tag.getFirst(fieldOne));
			
			Utils.saveTagToFile(entry, tag);
			LOG.log(Level.FINER, "Swap successful: " + tag.toString());
		}
		catch (KeyNotFoundException | FieldDataInvalidException e)
		{
			LOG.log(Level.WARNING, "Failed to swap " + fieldOne + " for " + fieldTwo + ":" + tag.toString());
		}
		catch (StringIndexOutOfBoundsException ex)
		{
			// do nothing : bug w/ JAudioTagger, sometimes throws this ex
		}
	}
	
	/** Copies one like-field to another. The copy from field
	 * will remain unchanged.
	 * @param entry  Library file's equivalent to a track in iTunes
	 * @param fieldCopy  Field to copy from.
	 * @param fieldReplace  Field to copy to.
	 */
	public static void copyTags(Entry<String, Map> entry, FieldKey fieldCopy, FieldKey fieldReplace)
	{
		Tag tag = Utils.getTagFromTrackEntry(entry);
		try
		{
			tag.setField(fieldReplace, tag.getFirst(fieldCopy));
			
			Utils.saveTagToFile(entry, tag);
			LOG.log(Level.FINER, "Copy successful: " + tag.toString());
		}
		catch (KeyNotFoundException | FieldDataInvalidException e)
		{
			LOG.log(Level.WARNING, "Failed to copy " + fieldCopy + " to " + fieldReplace + ":" + tag.toString());
		}
		catch (StringIndexOutOfBoundsException ex)
		{
			// do nothing : bug w/ JAudioTagger, sometimes throws this ex
		}
	}
	
	/** Searches for matching text across all fields in all tracks
	 * contained in the Library object.
	 * @param lib
	 * @param textFind  Text to search for.
	 * @param textReplace  Text to replace textFind with.
	 * @return  List of {@link TableEntry}s containing 
	 * fields with the matching text and what the fields will become
	 * if textFind text is replaced with textReplace.
	 */
	public static ArrayList<TableEntry> findMatchingTextInTagFields(Library lib, String textFind, String textReplace)
	{
		ArrayList<TableEntry> tableEntries = new ArrayList<TableEntry>();
		ProgressDialog progress = new ProgressDialog(lib.getTrackMapEntries().size());
		
		int progressCounter = 0;
		for(Entry<String, Map> trackEntry : lib.getTrackMapEntries())
		{
			for(Object obj: trackEntry.getValue().entrySet())
			{
				Entry<String, Object> fieldEntry = (Entry<String, Object>) obj;
				String value = String.valueOf(fieldEntry.getValue());
				
				if(fieldEntry.getKey().equalsIgnoreCase("Location"))
				{
					continue;
				}
				
				if(value.contains(textFind))
				{
					LOG.log(Level.FINER, "Found match: " + value);
					
					TableEntry entry = new TableEntry();
					entry.SongTitle = value;
					entry.NewTitle = value.replace(textFind, textReplace);
					entry.Status = fieldEntry.getKey();
					entry.FilePath = Utils.getFilePathFromTrackEntry(trackEntry);
					tableEntries.add(entry);
				}
			}
			progress.update(progressCounter++);
		}
		progress.finish();
		
		return tableEntries;
	}
}
