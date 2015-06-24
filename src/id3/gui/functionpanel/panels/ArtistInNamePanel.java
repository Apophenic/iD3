package id3.gui.functionpanel.panels;

import id3.functions.Functions;
import id3.gui.functionpanel.TableFunctionPanel;
import id3.tables.ID3Table;
import id3.tables.TableEntry;
import id3.tables.abstractid3model.models.ArtistInNameModel;
import id3.utils.Utils;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

public class ArtistInNamePanel extends TableFunctionPanel
{
	private static final String INFO_TEXT = "Looks for track titles with the artist as part of the name" +
			" (i.e. Rush - 2012.mp3). "
			+ "This will be fixed, with the proper title and artist being placed in their respective tag fields.";
	
	/** Creates a new {@code ArtistInNamePanel}
	 * @see TableFunctionPanel
	 * @see id3.gui.functionpanel.FunctionPanel
	 */
	public ArtistInNamePanel()
	{
		super(new ID3Table(new ArtistInNameModel()), INFO_TEXT);
	}

	@Override
	public void runFunction(Entry<String, Map> entry)
	{
		TableEntry tableEntry = Functions.getTitlesWithArtistNameIncluded(entry);
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
			Tag tag = Utils.getTagFromAudioFile(entry.FilePath);
			tag.setField(FieldKey.TITLE, entry.NewTitle);
			
			Utils.saveTagToFile(entry.FilePath, tag);
			LOG.log(Level.FINE, "Successfully removed artist name from: " + tag.toString());
		}
		catch (TagException e) 
		{
			LOG.log(Level.WARNING, "Failed to remove artist name from title: " + entry.FilePath);
		}
	}

	@Override
	protected void resizeColumns()
	{
		table.getColumnModel().getColumn(0).setPreferredWidth(140);
		table.getColumnModel().getColumn(1).setPreferredWidth(125);
		table.getColumnModel().getColumn(2).setPreferredWidth(55);
	}
	
}
