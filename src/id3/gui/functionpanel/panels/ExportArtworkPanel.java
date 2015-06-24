package id3.gui.functionpanel.panels;

import id3.functions.Functions;
import id3.gui.customui.FileBrowserPanel;
import id3.gui.customui.InfoTextArea;
import id3.gui.functionpanel.FunctionPanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

public class ExportArtworkPanel extends FunctionPanel
{
	private static final String INFO_TEXT = "Every album that has artwork in the selected\r\n"
			+ "library file will have a copy of its artwork saved\r\nin the selected export directory.\r\n\r\n"
			+ "See HELP for more info.";
	
	private FileBrowserPanel fileBrowser = new FileBrowserPanel(JFileChooser.DIRECTORIES_ONLY);
	
	private JCheckBox chckSplit;
	
	/** Creates a new {@code ExportArtworkPanel}
	 * @see FunctionPanel
	 */
	public ExportArtworkPanel()
	{
		fileBrowser.setBounds(10, 206, 500, 25);
		this.add(fileBrowser);
		
		JLabel lblDir = new JLabel("Select Export Directory:");
		lblDir.setFont(new Font("Verdana", Font.BOLD, 12));
		lblDir.setBounds(10, 180, 193, 14);
		this.add(lblDir);
		
		chckSplit = new JCheckBox("Split files by artist");
		chckSplit.setBounds(10, 233, 193, 23);
		this.add(chckSplit);
		
		InfoTextArea infoTextArea = new InfoTextArea(INFO_TEXT, new Rectangle(6, 11, 388, 138));
		this.add(infoTextArea);
	}
	
	public String getExportDirectory()
	{
		File file = new File(fileBrowser.getSelectedFilePath());
		if(file.exists())
		{
			if(file.isDirectory())
			{
				return file.getAbsolutePath();
			}
		}
		return null;
	}
	
	public boolean isSplitByArtist()
	{
		return chckSplit.isSelected();
	}

	@Override
	public void runFunction(Entry<String, Map> entry)
	{
		Functions.exportArtwork(entry, getExportDirectory(), isSplitByArtist());
	}

	@Override
	public boolean checkForErrors()
	{
		return getExportDirectory() == null;
	}
}
