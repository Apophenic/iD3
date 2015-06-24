package id3.gui.functionpanel.panels;

import id3.functions.Functions;
import id3.gui.customui.InfoTextArea;
import id3.gui.dialogs.ProgressDialog;
import id3.gui.functionpanel.FunctionPanel;
import id3.main.GUI;
import id3.objects.Album;
import id3.objects.Artist;
import id3.objects.Library;
import org.jaudiotagger.tag.FieldKey;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class MissingFieldsPanel extends FunctionPanel
{
	private static final String INFO_TEXT = "Fix Missing: Tracks missing fields will have other tracks\r\n"
			+ "belonging to the same field examined\r\nto see if the missing field exists on\r\nthe other track." +
			"\r\n\r\n"
			+ "Calculate Track Count:\r\nDetermines actual track count based \r\non number of tracks on album.\r\n"
			+ "Default behavior is to ignore tracks\r\nthat already have a track count.\r\n-Include Non-Empty:" +
			" Existing \r\n"
			+ "track count fields are ignored.";
	
	private JCheckBox chckFixMissing;
	private JCheckBox chckAlbumArtist;
	private JCheckBox chckComposer;
	private JCheckBox chckGrouping;
	private JCheckBox chckAlbumYear;
	private JCheckBox chckDiscCount;
	private JCheckBox chckGenre;
	private JCheckBox chckComments;
	private JCheckBox chckArtwork;
	
	private JCheckBox chckTrackCount;
	private JCheckBox chckIncNonEmpty;

	/** Creates a new {@code MissingFieldsPanel}
	 * @see FunctionPanel
	 */
	public MissingFieldsPanel()
	{
		init();
		super.isRequiresArtistAlbumObjects = true;
	}
	
	private void init()
	{
		chckArtwork = new JCheckBox("Artwork");
		chckArtwork.setEnabled(false);
		chckArtwork.setToolTipText("");
		chckArtwork.setBounds(17, 235, 127, 23);
		this.add(chckArtwork);
		
		chckGenre = new JCheckBox("Genre");
		chckGenre.setEnabled(false);
		chckGenre.setToolTipText("");
		chckGenre.setBounds(17, 183, 127, 23);
		this.add(chckGenre);
		
		chckAlbumYear = new JCheckBox("Album Year");
		chckAlbumYear.setEnabled(false);
		chckAlbumYear.setToolTipText("");
		chckAlbumYear.setBounds(17, 131, 127, 23);
		this.add(chckAlbumYear);
		
		chckTrackCount = new JCheckBox("Calculate Track Count");
		chckTrackCount.setToolTipText("Calculates track count based on how many tracks are on an album ONLY IF the track count field is empty");
		chckTrackCount.setBounds(150, 27, 156, 23);
		this.add(chckTrackCount);
		
		chckIncNonEmpty = new JCheckBox("Include non-empty track counts");
		chckIncNonEmpty.setEnabled(false);
		chckIncNonEmpty.setToolTipText("Calculates track count for every album and replaces ALL track count fields with the newly calculated count");
		chckIncNonEmpty.setBounds(176, 49, 218, 23);
		this.add(chckIncNonEmpty);
		
		chckFixMissing = new JCheckBox("Fix Missing");
		chckFixMissing.setToolTipText("Attempts to find a song's missing field by looking through fields in other song files that are on the same album");
		chckFixMissing.setBounds(6, 27, 107, 23);
		this.add(chckFixMissing);
		
		chckAlbumArtist = new JCheckBox("Album Artist");
		chckAlbumArtist.setEnabled(false);
		chckAlbumArtist.setBounds(16, 53, 97, 23);
		this.add(chckAlbumArtist);
		
		chckComposer = new JCheckBox("Composer");
		chckComposer.setEnabled(false);
		chckComposer.setBounds(16, 79, 97, 23);
		this.add(chckComposer);
		
		chckGrouping = new JCheckBox("Grouping");
		chckGrouping.setEnabled(false);
		chckGrouping.setBounds(16, 105, 97, 23);
		this.add(chckGrouping);
		
		chckDiscCount = new JCheckBox("Disc Count");
		chckDiscCount.setEnabled(false);
		chckDiscCount.setBounds(17, 157, 97, 23);
		this.add(chckDiscCount);
		
		chckComments = new JCheckBox("Comments");
		chckComments.setEnabled(false);
		chckComments.setBounds(17, 209, 97, 23);
		this.add(chckComments);
		
		InfoTextArea infoTextArea = new InfoTextArea(INFO_TEXT, new Rectangle(171, 106, 223, 212));
		this.add(infoTextArea);
		
		chckFixMissing.addChangeListener(e ->
		{
            chckAlbumArtist.setEnabled(chckFixMissing.isSelected());
            chckComposer.setEnabled(chckFixMissing.isSelected());
            chckGrouping.setEnabled(chckFixMissing.isSelected());
            chckAlbumYear.setEnabled(chckFixMissing.isSelected());
			chckDiscCount.setEnabled(chckFixMissing.isSelected());
			chckGenre.setEnabled(chckFixMissing.isSelected());
			chckComments.setEnabled(chckFixMissing.isSelected());
			chckArtwork.setEnabled(chckFixMissing.isSelected());
        });
		
		chckTrackCount.addChangeListener(e -> chckIncNonEmpty.setEnabled(chckTrackCount.isSelected()));
	}
	
	public boolean isFixMissing()
	{
		return chckFixMissing.isSelected();
	}
	
	public boolean isFixMissingAlbumArtist()
	{
		return chckAlbumArtist.isSelected();
	}
	
	public boolean isFixMissingComposer()
	{
		return chckComposer.isSelected();
	}
	
	public boolean isFixMissingComments()
	{
		return chckComments.isSelected();
	}
	
	public boolean isFixMissingGrouping()
	{
		return chckGrouping.isSelected();
	}
	
	public boolean isFixMissingAlbumYear()
	{
		return chckAlbumYear.isSelected();
	}
	
	public boolean isFixMissingGenre()
	{
		return chckGenre.isSelected();
	}
	
	public boolean isFixMissingDiscCount()
	{
		return chckDiscCount.isSelected();
	}
	
	public boolean isFixMissingArtwork()
	{
		return chckArtwork.isSelected();
	}
	
	public boolean isCalculateTrackCount()
	{
		return chckTrackCount.isSelected();
	}
	
	public boolean isIncludeNonEmptyTrackCounts()
	{
		return chckIncNonEmpty.isSelected();
	}
	
	public FieldKey[] getFixMissingFieldKeys()
	{
		ArrayList<FieldKey> fieldKeys = new ArrayList<>();
		if(isFixMissingAlbumArtist())
		{
			fieldKeys.add(FieldKey.ALBUM_ARTIST);
		}
		if(isFixMissingAlbumYear())
		{
			fieldKeys.add(FieldKey.YEAR);
		}
		if(isFixMissingArtwork())
		{
			fieldKeys.add(FieldKey.MEDIA);
		}
		if(isFixMissingComments())
		{
			fieldKeys.add(FieldKey.COMMENT);
		}
		if(isFixMissingComposer())
		{
			fieldKeys.add(FieldKey.COMPOSER);
		}
		if(isFixMissingDiscCount())
		{
			fieldKeys.add(FieldKey.DISC_TOTAL);
		}
		if(isFixMissingGenre())
		{
			fieldKeys.add(FieldKey.GENRE);
		}
		if(isFixMissingGrouping())
		{
			fieldKeys.add(FieldKey.GROUPING);
		}
		return fieldKeys.toArray(new FieldKey[fieldKeys.size()]);
	}
	
	@Override
	public void initFunction(Library lib)
	{
		if(!checkForErrors())
		{
			ProgressDialog progress = new ProgressDialog(lib.getLibraryArtists().size());
					
			int progressCounter = 0;
			for(Artist artist : lib.getLibraryArtists())
			{
				for(Album album : artist.getAlbums())
				{
					if(isFixMissing())
					{
						Functions.fixMissing(album, getFixMissingFieldKeys());
					}
					if(isCalculateTrackCount())
					{
						Functions.calculateTrackCount(album, isIncludeNonEmptyTrackCounts());
					}
							
				}
				progress.update(progressCounter++);
			}
			progress.finish();
		}
		else
		{
			JOptionPane.showMessageDialog(GUI.frame, "Error on panel", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void runFunction(Entry<String, Map> entry)
	{
		//Do Nothing
	}

	@Override
	public boolean checkForErrors()
	{
		if(!isFixMissing() && !isCalculateTrackCount())
		{
			return true;
		}
		else if(isFixMissing())
		{
			if(!isFixMissingAlbumArtist() && !isFixMissingAlbumYear()
				&& !isFixMissingArtwork() && !isFixMissingComments() && !isFixMissingComposer()
				&& !isFixMissingDiscCount() && !isFixMissingGenre() && !isFixMissingGrouping())
			{
				return true;
			}
		}
		return false;
	}
}
