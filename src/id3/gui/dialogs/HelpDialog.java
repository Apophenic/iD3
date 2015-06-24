package id3.gui.dialogs;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

/** Opens a tree dialog containing
 * additional information about iD3 and
 * its functions.
 */
public class HelpDialog extends JDialog
{
	public static final String ID3_INFO 		= "Copies info from iTunes library file into song ID3 tags.\r\n"
												+ "Supported file formats: mp3, mp4, m4a, m4p, ogg, flac, wma";
	public static final String FORMATTING_INFO 	= "CAPITALIZE FIELDS\r\n"
												+ "Capitalizes every word in the following fields: name, artist,\r\n"
												+ "album, genre, and album artist.\r\n"
												+ "PROPER CAPITALIZATION: Attempts to follow proper English\r\n"
												+ "capitalization rules by skipping certainw words (of, a, an, etc.)\r\n"
												+ "unless they're the first word in a string.\r\n"
												+ "INCLUDE COMPOSER/GROUPING/COMMENTS: Follows the same\r\n"
												+ "capitalization rules as selected if these fields are included.\r\n"
												+ "\r\nREMOVE LEADING/TRAILING SPACES\r\n"
												+ "Removes spaces that occur before and after the first\r\n"
												+ "and last word, respectively, in all string-type fields.\r\n"
												+ "\r\nREMOVE \'N\' LEADING/TRAILING CHARACTERS\r\n"
												+ "Removes a number of letters either from the beginning or\r\n"
												+ "end of the selected field.";
	public static final String MISSING_INFO 	= "FIX MISSING\r\n"
												+ "Attempts to fill the selected missing field by looking\r\n"
												+ "at the same fields on other songs that share the same\r\n"
												+ "ARTIST and/or ALBUM\r\n"
												+ "DO NOT attempt to fill missing fields that you use for\r\n"
												+ "song specific info (i.e. you use the comments field for lyrics)\r\n"
												+ "\r\nCALCULATE TRACK COUNT\r\n"
												+ "Determines how many songs exist on an album and fills in\r\n"
												+ "the track total field. Default behavior is to only do this for\r\n"
												+ "empty fields. Select \"Include non-empty track counts\" to\r\n"
												+ "force this operation on ALL songs/albums.";
	public static final String SWAP_INFO 		= "SWAP FIELDS\r\n"
												+ "Swaps the values of the two provided fields. Not all fields\r\n"
												+ "can be swapped with one another, as some fields only take numbers\r\n"
												+ "and others only take letters\r\n"
												+ "\r\nCOPY FIELDS\r\n"
												+ "Copies the first field to the second field, leaving the\r\n"
												+ "first field unchanged. The same field restrictions as mentioned\r\n"
												+ "above still apply.";
	public static final String APPEND_INFO		= "Adds either another field or user input text to the beginning\r\n"
												+ "or end of another field. Remember: this is performed on ALL\r\n"
												+ "songs in the music library!";
	public static final String CUSTOM_INFO 		= "These are user created fields that use pre-existing fields\r\n"
												+ "for an entirely different purpose.\r\n"
												+ "\r\nARTIST RATINGS\r\n"
												+ "Calculates an artist's rating by taking the average rating\r\n"
												+ "of all rated songs. Set to 'unrated' if the artist doesn't have\r\n"
												+ "enough songs/enough rated songs to calculate on\r\n"
												+ "(this can be edited in artist rating criteria)\r\n"
												+ "Use Custom Rating Bracks:\r\n"
												+ "This is an alternate way to determine artist rating.\r\n"
												+ "Artist rating is calculated by determining the percentage of\r\n"
												+ "songs that are four stars or greater of an artist's total\r\n"
												+ "number of rated songs (4+ / total rated). This percentage is then\r\n"
												+ "compared to a bracket that can be set by clicking\r\n"
												+ "\"Edit Rating Criteria\".\r\n"
												+ "For example, say an artist has 10 songs rated and 5 of them are\r\n"
												+ "4 or 5 star songs. Then, say your brackets are set to 0, 20, 40, 60.\r\n"
												+ "Since 5 / 10 is 50%, which is between 40 and 60 %, that artist\r\n"
												+ "would receive a rating of 4.\r\n"
												+ "NOTE: 1 star songs ARE NOT COUNTED WHEN USING THIS METHOD\r\n"
												+ "AS RATED SONGS!\r\n"
												+ "Note2: obviously, a higher rating\r\n"
												+ "must require a higher percentage than the lower ratings\r\n"
												+ "\r\nYou can also select the percentage of an artist's songs\r\n"
												+ "that must be rated to receive an artist rating.\r\n"
												+ "Example: You choose 50%, but an artist only has 4/10 songs rated.\r\n"
												+ "This artist's rating will become \"Unrated\"\r\n"
												+ "\r\nALBUM RATINGS\r\n"
												+ "Calculates an album rating based on the average rating of\r\n"
												+ "songs on said album. This is no different from what iTunes does,\r\n"
												+ "other than saving this info to an ID3 field.\r\n"
												+ "Again, you can set the minimum percentage of songs that must\r\n"
												+ "be rated on an album for the album to receive an album rating.\r\n"
												+ "Anything less will be set to \"Unrated\"\r\n";
	public static final String TRACKNUM_INFO 	= "Looks for song's that have a title similar to \"01 name.mp3\".\r\n"
												+ "(NOTE: The SONG TITLE not the FILE NAME, as this is normal when\r\n"
												+ "using organized iTunes folders)\r\n"
												+ "A list will be returned with all songs containing the track\r\n"
												+ "number in their title. Clicking \"Commit Changes\"\r\n"
												+ "will remove the track number, place it in the track number\r\n"
												+ "field, and rename the song title for all songs listed.\r\n"
												+ "Right-click remove all songs you don't want to change\r\n"
												+ "\r\nSUPPORTED FORMATS:\r\n"
												+ "## - Name, ##. Name, ##xName,\r\n"
												+ "# - Name, #. Name, #xName\r\n"
												+ "(where \"x\" is \".\" or \"-\" or space)";
	public static final String ARTISTNAME_INFO 	= "Searches the iTunes library file for song titles that\r\n"
												+ "have the artist name as part of the title, and removes the name.\r\n"
												+ "Specifically looks for song titles formatted as:\r\n"
												+ "\"ArtistName - SongTitle.mp3\"\r\n"
												+ "You'll have the opportunity to review songs that match this\r\n"
												+ "format before making any actual changes.";
	public static final String SAVERATINGS_INFO = "Reads iTunes library xml file and save song ratings to\r\n"
												+ "the song's ID3 tag. iTunes does not have this ability,\r\n"
												+ "nor can it read ratings from ID3 tags.\r\n"
												+ "\r\nNonetheless, this is good practice for at least two reasons:\r\n"
												+ "1) God forbid you lose your library file, a new file can still be\r\n"
												+ "rebuilt using the information stored in song ID3 tags. Obviously though,\r\n"
												+ "if song rating information isn't on the ID3 tag, then it would still be\r\n"
												+ "gone forever at that point. See \"Build Library Xml\" for more info\r\n"
												+ "2) Migration to other media plays should be significantly easier\r\n"
												+ "\r\nThis operation won't require an \"update\" of your iTunes library\r\n"
												+ "since iTunes doesn't use ID3 ratings. Note that most other functions that\r\n"
												+ "write to ID3 tags will automatically save ratings to the tags.\r\n"
												+ "This function should only be needed if you wish not to do anything\r\n"
												+ "else, or want to be particularly certain you've saved ratings to ID3.";
	public static final String DELETE_INFO		= "Deletes the selected field from the ID3 tag for all songs\r\n"
												+ "in the music library file. This is primarily for mass field\r\n"
												+ "deletion that isn't handled in iTunes.\r\n"
												+ "Note: Since song ratings are stored in the xml file,\r\n"
												+ "deleting them from the ID3 tag won't remove them from iTunes.";
	public static final String LYRICS_INFO 		= "Searches for lyrics using ____\r\n"
												+ "Choose the minimum rating a song must have to be considered\r\n"
												+ "for lyric search, then click \"Find Songs\".\r\n"
												+ "You'll then have the chance to review\r\n"
												+ "what songs will be sent for lyric retrieval before clicking\r\n"
												+ "\"Get Lyrics\"";
	public static final String BPM_INFO 		= "Uses the TrackAnalyzer cmd line tool\r\n"
												+ "(a loose port of BeatRoot) to determine a song\'s bpm\r\n"
												+ "and writes it to the song\'s ID3 tag, for every song\r\n"
												+ "in the music library file";
	public static final String BUILDXML_INFO	= "Builds a new iTunes Music Library.xml file from the chosen\r\n"
												+ "directory + all sub directories. Information available in\r\n"
												+ "file's ID3 tags will be used to fill out the iTunes fields.\r\n"
												+ "\r\nThis function is available in iTunes (simply add all songs\r\n"
												+ "to a new library), however, this approach fails to properly fill\r\n"
												+ "some fields, most notably song ratings.\r\n"
												+ "THIS OPERATION WILL CORRECTLY COPY SONG RATINGS IF THEY'RE\r\n"
												+ "ON THE ID3 TAG\r\n"
												+ "\r\nNOTE: Once you have a rebuilt xml file, delete the one in your\r\n"
												+ "iTunes directory, open iTunes, file->library->import playlist\r\n"
												+ "and select the new xml file.";
	public static final String EXPORTART_INFO	= "Saves a copy of every piece of artwork available in the selected\r\n"
												+ "library file. This is done by searching through every song looking for\r\n"
												+ "unique pieces of artwork. All artwork will be saved into the selected\r\n"
												+ "export directory. Default behavior is to name artwork files as something\r\n"
												+ "like \"ARTIST - ALBUM.jpg\". By selecting \"split files by artist\",\r\n"
												+ "artist folders will be created in the export directory that contain all\r\n"
												+ "artwork files from that artist for all albums.";
	public static final String COPYIPOD_INFO	= "NOTE: You MUST check \"Enable disk use\" in iTunes for your device\r\n"
												+ "before you can perform this operation!\r\n"
												+ "\r\nCopies all song files from the selected device. Files are renamed to\r\n"
												+ "\"track# songTitle.mp3\" and placed into artist and album folders, where\r\n"
												+ "applicable (as long as the info is available on the ID3).\r\n"
												+ "All ID3 tags will be maintained.\r\n"
												+ "You may also choose to build a new library xml file with these\r\n"
												+ "exported song files."
												+ "See BUILD LIBRARY XML for more info.";
	public static final String EMBEDART_INFO	= "ID3 tags can either directly store artwork or a file path TO the artwork.\r\n"
												+ "The latter is called \'linked\' artwork. This is poor practice becauses it\'s\r\n"
												+ "far too easy for the artwork to go missing or be unavailable. This function\r\n"
												+ "will search all library files and save the artwork directly to the song file\'s\r\n"
												+ "ID3 tag if it isn\'t already.";
	public static final String DUPLICATE_INFO	= "Searches for files that are named similar to: \"01 name 1.mp3\"\r\n"
												+ "Older versions of iTunes had a bad tendency to create needless\r\n"
												+ "duplicates of songs as they were added.\r\n"
												+ "Clicking commit changes will delete all listed files.\r\n"
												+ "Right-click and remove any songs you don't want to change.\r\n";
	public static final String MISSINGSONGS_INFO = "Creates a .csv file (opens with excel), listing\r\n"
												+ "missing songs and information about said song.\r\n"
												+ "\r\nYou also have the option to\r\n"
												+ "\"Attempt Discovery\" of missing songs, which\r\n"
												+ "will look in the \"expected\" folder for the song\r\n"
												+ "and look for songs with VERY similar names.\r\n"
												+ "Sometimes a song is renamed and iTunes\r\n"
												+ "can no longer see it. Unfortunately, these songs\r\n"
												+ "can't automatically be remapped or readded easily\r\n"
												+ "(in windows at least), but you can still do so\r\n"
												+ "manually!\r\n";
	public static final String UNLISTEDFILES_INFO = "Searches all sub folders in the iTunes Music Library folder\r\n"
												+ "for files that don't exist in the library file.\r\n"
												+ "You have the option to ignore images, which is recommended\r\n"
												+ "if you use linked artwork\r\n"
												+ "Clicking commit changes will move all listed songs\r\n"
												+ "to the folder: iD3directory/(UNLISTED MEDIA)\r\n"
												+ "Right-click remove any songs you don't wish to change\r\n"
												+ "NOTE: For operation \"Check Manually\", make certain said file\r\n"
												+ "isn't actually being used in iTunes. This will often be thrown for\r\n"
												+ "file names that have accented characters. Will still be moved\r\n"
												+ "after you click commit changes.";
	public static final String FINDREPLACE_INFO = "Searches all tag fields, on all files, for text matching the\r\n"
												+ "supplied text. You may also replace the found text with other text,\r\n"
												+ "just be sure to supply the replacement text before searching, otherwise\r\n"
												+ "committing changes will fail.";
	
	public HelpDialog()
	{
		this.getContentPane().setLayout(null);
		this.setTitle("HELP");
		this.setAlwaysOnTop(true);
		
		JEditorPane text = new JEditorPane();
		text.setText(ID3_INFO);
		text.setEditable(false);
		text.setFont(new Font("Verdana", Font.PLAIN, 12));
		text.setBounds(156, 0, 468, 442);
		
		JScrollPane scrollPane = new JScrollPane(text);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(156, 0, 468, 442);
		getContentPane().add(scrollPane);
		
		JTree tree = new JTree();
		tree.setModel(new DefaultTreeModel (
			new DefaultMutableTreeNode("iD3")
			{
				{
					DefaultMutableTreeNode node;
					node = new DefaultMutableTreeNode("Formatting");
					add(node);
					node = new DefaultMutableTreeNode("Missing Fields");
					add(node);
					node = new DefaultMutableTreeNode("Get Lyrics");
					add(node);
					node = new DefaultMutableTreeNode("BPM Detection");
					add(node);
					node = new DefaultMutableTreeNode("Swap Fields");
					add(node);
					node = new DefaultMutableTreeNode("Append / Prepend");
					add(node);
					node = new DefaultMutableTreeNode("Find and Replace");
					add(node);
					node = new DefaultMutableTreeNode("Delete Fields");
					add(node);
					node = new DefaultMutableTreeNode("Custom Fields");
					add(node);
					node = new DefaultMutableTreeNode("Track # In Name");
					add(node);
					node = new DefaultMutableTreeNode("Artist In Name");
					add(node);
					node = new DefaultMutableTreeNode("Save Ratings");
					add(node);
					node = new DefaultMutableTreeNode("Build Library Xml");
					add(node);
					node = new DefaultMutableTreeNode("Copy From iPod");
					add(node);
					node = new DefaultMutableTreeNode("Export Artwork");
					add(node);
					node = new DefaultMutableTreeNode("Remove Duplicates");
					add(node);
					node = new DefaultMutableTreeNode("Missing Songs");
					add(node);
					node = new DefaultMutableTreeNode("Unlisted Files");
					add(node);
				}
			}
		));
		tree.setBounds(0, 0, 146, 442);
		tree.setSelectionRow(0);
		getContentPane().add(tree);
		
		tree.addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				String path = e.getPath().toString();
				switch(path)
				{
					case("[iD3]") :
						text.setText(ID3_INFO);
						break;
					case("[iD3, Formatting]") :
						text.setText(FORMATTING_INFO);
						break;
					case("[iD3, Missing Fields]") :
						text.setText(MISSING_INFO);
						break;
					case("[iD3, Swap Fields]") :
						text.setText(SWAP_INFO);
						break;
					case("[iD3, Append / Prepend]") :
						text.setText(APPEND_INFO);
						break;
					case("[iD3, Custom Fields]") :
						text.setText(CUSTOM_INFO);
						break;
					case("[iD3, Track # In Name]") :
						text.setText(TRACKNUM_INFO);
						break;
					case("[iD3, Artist In Name]") :
						text.setText(ARTISTNAME_INFO);
						break;
					case("[iD3, Get Lyrics]") :
						text.setText(LYRICS_INFO);
						break;
					case("[iD3, BPM Detection]") :
						text.setText(BPM_INFO);
						break;
					case ("[iD3, Save Ratings]") :
						text.setText(SAVERATINGS_INFO);
						break;
					case("[iD3, Build Library Xml]") :
						text.setText(BUILDXML_INFO);
						break;
					case("[iD3, Export Artwork]") :
						text.setText(EXPORTART_INFO);
						break;
					case("[iD3, Copy From iPod]") :
						text.setText(COPYIPOD_INFO);
						break;
					case("[iD3, Find and Replace]") :
						text.setText(FINDREPLACE_INFO);
						break;
					case("[iD3, Delete Fields]") :
						text.setText(DELETE_INFO);
						break;
					case("[iD3, Embed Artwork]") :
						text.setText(EMBEDART_INFO);
						break;
					case("[iD3, Remove Duplicates]") :
						text.setText(DUPLICATE_INFO);
						break;
					case("[iD3, Missing Songs]") :
						text.setText(MISSINGSONGS_INFO);
						break;
					case("[iD3, Unlisted Files]") :
						text.setText(UNLISTEDFILES_INFO);
						break;
				}
			}
		});
		
		this.setBounds(150, 150, 640, 480);
		this.setVisible(true);
	}
}
