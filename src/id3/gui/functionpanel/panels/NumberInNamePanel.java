package id3.gui.functionpanel.panels;

import id3.functions.Functions;
import id3.gui.functionpanel.TableFunctionPanel;
import id3.tables.ID3Table;
import id3.tables.TableEntry;
import id3.tables.abstractid3model.models.NumberInNameModel;
import id3.utils.Utils;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

public class NumberInNamePanel extends TableFunctionPanel
{
	private static final String INFO_TEXT = "Song titles such as \"05 Name.mp3\" will become \"Name.mp3\", "
			+ "and the number will be placed in the tag's track number field. See HELP for supported formats.";
	
	/** Creates a new {@code NumberInNamePanel}
	 * @see TableFunctionPanel
	 * @see id3.gui.functionpanel.FunctionPanel
	 */
	public NumberInNamePanel()
	{
		super(new ID3Table(new NumberInNameModel()), INFO_TEXT);
	}

	@Override
	public void runFunction(Entry<String, Map> entry)
	{
		TableEntry tableEntry = Functions.removeTrackNumberFromTitles(entry);
		if(tableEntry != null)
		{
			tableEntries.add(tableEntry);
			table.getModel().setTableEntries(tableEntries);
		}
	}

	@Override
	protected void runCommit(TableEntry entry)
	{
		try
		{
			Tag tag = AudioFileIO.read(new File(entry.FilePath)).getTag();
			tag.setField(FieldKey.TITLE, entry.NewTitle);
			tag.setField(FieldKey.TRACK, entry.TrackNumber);
			
			Utils.saveTagToFile(entry.FilePath, tag);
			LOG.log(Level.FINE, "Track number removed from name: " + tag.toString());
		}
		catch (CannotReadException | IOException | TagException
				| ReadOnlyFileException| InvalidAudioFrameException e)
		{
			LOG.log(Level.WARNING, "Failed to write title w/o track num to tag: " + entry.FilePath);
		}
	}

	@Override
	protected void resizeColumns()
	{
		table.getColumnModel().getColumn(0).setPreferredWidth(140);
		table.getColumnModel().getColumn(1).setPreferredWidth(140);
		table.getColumnModel().getColumn(2).setPreferredWidth(40);
	}
}
