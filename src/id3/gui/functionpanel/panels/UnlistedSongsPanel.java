package id3.gui.functionpanel.panels;

import id3.functions.Functions;
import id3.gui.functionpanel.TableFunctionPanel;
import id3.main.Settings;
import id3.objects.Library;
import id3.tables.ID3Table;
import id3.tables.TableEntry;
import id3.tables.abstractid3model.models.FileOpModel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class UnlistedSongsPanel extends TableFunctionPanel
{
	private static final String INFO_TEXT = "Looks through all folders in the iTunes media folder for songs that aren't"
			+ " listed in the library file and moves them into a new folder called \"(UNLISTED MEDIA)\"";
	
	private JCheckBox chckIgnoreImages;
	
	/** Creates a new {@code UnlistedSongsPanel}
	 * @see TableFunctionPanel
	 * @see id3.gui.functionpanel.FunctionPanel
	 */
	public UnlistedSongsPanel()
	{
		super(new ID3Table(new FileOpModel()), INFO_TEXT);
		init();
	}
	
	private void init()
	{
		chckIgnoreImages = new JCheckBox("Ignore Image Files");
		chckIgnoreImages.setFont(new Font("Verdana", Font.PLAIN, 11));
		chckIgnoreImages.setBounds(360, 248, 153, 23);
		this.add(chckIgnoreImages);
	}
	
	public boolean isIgnoreImageFiles()
	{
		return chckIgnoreImages.isSelected();
	}
	
	@Override
	public void initFunction(Library lib)
	{
		ArrayList<TableEntry> tableEntries = Functions.findUnlistedSongs(lib, isIgnoreImageFiles());
		table.getModel().setTableEntries(tableEntries);
	}

	@Override
	protected void runCommit(TableEntry entry)
	{
		File file = new File(entry.FilePath);
		if(Objects.equals(entry.Status, "Find + Move"))
		{
			file.renameTo(new File(Settings.UNLISTED_DIR + "\\" + file.getName()));
		}
	}

	@Override
	protected void resizeColumns()
	{
		table.getColumnModel().getColumn(0).setPreferredWidth(250);
		table.getColumnModel().getColumn(1).setPreferredWidth(40);
	}

	@Override
	public void runFunction(Entry<String, Map> entry)
	{
		// Do Nothing
	}

}
