package id3.gui.functionpanel.panels;

import id3.functions.Functions;
import id3.gui.customui.FileBrowserPanel;
import id3.gui.customui.InfoTextArea;
import id3.gui.functionpanel.FunctionPanel;
import id3.objects.Library;

import javax.swing.JFileChooser;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

public class BuildXmlPanel extends FunctionPanel
{
	private static final String INFO_TEXT = "All files in this directory and its subdirectories will be\r\n"
			+ "used to create a new iTunes Music Library.xml file, \r\n"
			+ "containing these files. iTunes fields will be filled based\r\n"
			+ "on available ID3 tag information.\r\n\r\nThis xml file will need to be \"imported\" into iTunes.\r\n"
			+ "In iTunes: file->library->import playlist\r\n\r\nSee HELP for more information";
	
	private FileBrowserPanel fileBrowser = new FileBrowserPanel(JFileChooser.DIRECTORIES_ONLY);
	
	/** Creates a new {@code BuildXmlPanel}
	 * @see FunctionPanel
	 */
	public BuildXmlPanel()
	{
		init();
		super.isRequiresItunesLibraryFile = false;
	}
	
	private void init()
	{
		JLabel lblDir = new JLabel("Select Your Media Directory:");
		lblDir.setFont(new Font("Verdana", Font.BOLD, 12));
		lblDir.setBounds(10, 11, 224, 14);
		this.add(lblDir);
		
		fileBrowser.setBounds(10, 37, 500, 25);
		this.add(fileBrowser);
		
		InfoTextArea infoTextArea = new InfoTextArea(INFO_TEXT, new Rectangle(10, 68, 353, 159));
		this.add(infoTextArea);
	}
	
	public String getSelectedDirectory()
	{
		return fileBrowser.getSelectedFilePath();
	}
	
	@Override
	public void initFunction(Library lib)
	{
		if(!checkForErrors())
		{
			Functions.buildLibraryFile(new File(getSelectedDirectory()));
		}
	}

	@Override
	public void runFunction(Entry<String, Map> entry)
	{
		// Do Nothing
	}

	@Override
	public boolean checkForErrors()
	{
		File file = new File(getSelectedDirectory());
		if(!file.exists() || !file.isDirectory())
		{
			return true;
		}
		return false;
	}
}
