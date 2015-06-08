package id3.gui.functionpanel.panels;

import id3.functions.Functions;
import id3.gui.functionpanel.TableFunctionPanel;
import id3.tables.*;
import id3.tables.abstractid3model.models.FileOpModel;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

public class RemoveDuplicatePanel extends TableFunctionPanel
{
	private static final String INFO_TEXT = "Deletes files that are in iTunes"
			+ " artist/album folders that are duplicates of files already in the library. "
			+ "During importing, iTunes sometimes makes duplicate files (i.e. 01 Title.mp3, 01 Title 2.mp3). The latter will be deleted.";

	/** Creates a new {@code RemoveDuplicatePanel}
	 * @see TableFunctionPanel
	 * @see FunctionPanel
	 */
	public RemoveDuplicatePanel()
	{
		super(new ID3Table(new FileOpModel()), INFO_TEXT);
	}

	@Override
	public void runFunction(Entry<String, Map> entry)
	{
		TableEntry tableEntry = null;
		tableEntry = Functions.deleteDuplicateFiles(entry);
		if(tableEntry != null)
		{
			tableEntries.add(tableEntry);
			table.getModel().setTableEntries(tableEntries);
		}
	}

	@Override
	protected void runCommit(TableEntry entry)
	{
		File file = new File(entry.FilePath);
		if(entry.Status == "Delete Dup")
		{
			file.delete();
		}
	}

	@Override
	protected void resizeColumns()
	{
		table.getColumnModel().getColumn(0).setPreferredWidth(250);
		table.getColumnModel().getColumn(1).setPreferredWidth(40);
	}
}
