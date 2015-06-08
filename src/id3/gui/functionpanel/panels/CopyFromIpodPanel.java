package id3.gui.functionpanel.panels;

import id3.functions.Functions;
import id3.gui.customui.FileBrowserPanel;
import id3.gui.customui.InfoTextArea;
import id3.gui.functionpanel.FunctionPanel;
import id3.objects.Library;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import javax.swing.filechooser.FileSystemView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

public class CopyFromIpodPanel extends FunctionPanel
{
	private static final String INFO_TEXT = "Copy all songs from the selected iPod\r\n"
			+ "to the folder of your choice. ID3 tags will be\r\nsaved and songs will be renamed to \r\n"
			+ "\"track# songName.mp3\" where applicable\r\n\r\nNOTE: This operation requires you have\r\n"
			+ "selected \"Enable Disk Use\" for your iPod\r\nin iTunes";
	
	private FileBrowserPanel fileBrowser = new FileBrowserPanel(JFileChooser.DIRECTORIES_ONLY);
	
	private JList driveList;
	private JCheckBox chckBuild;
	
	/** Creates a new {@code CopyFromIpodPanel}
	 * @see FunctionPanel
	 */
	public CopyFromIpodPanel()
	{
		init();
		super.isRequiresItunesLibraryFile = false;
	}
	
	private void init()
	{
		JLabel lblDrive = new JLabel("Select iPod drive letter:");
		lblDrive.setFont(new Font("Verdana", Font.BOLD, 12));
		lblDrive.setBounds(10, 11, 181, 14);
		this.add(lblDrive);
		
		driveList = new JList(new DefaultListModel());
		driveList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrollPane = new JScrollPane(driveList);
		scrollPane.setBounds(10, 39, 150, 82);
		this.add(scrollPane);
		
		fillDriveList();
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				fillDriveList();
			}
		});
		btnRefresh.setBounds(10, 132, 89, 23);
		this.add(btnRefresh);
		
		JLabel lblSave = new JLabel("Save songs to directory:");
		lblSave.setFont(new Font("Verdana", Font.BOLD, 12));
		lblSave.setBounds(10, 180, 226, 14);
		this.add(lblSave);
		
		fileBrowser.setBounds(10, 206, 500, 25);
		this.add(fileBrowser);
		
		chckBuild = new JCheckBox("Also build library.xml with this directory");
		chckBuild.setBounds(10, 233, 259, 23);
		this.add(chckBuild);
		
		InfoTextArea infoTextArea = new InfoTextArea(INFO_TEXT, new Rectangle(201, 8, 297, 159));
		this.add(infoTextArea);
	}
		
	/** Gets all available drives
	 * as seen by the Windows OS 
	 */
	private void fillDriveList()
	{
		DefaultListModel model = new DefaultListModel();
		File[] roots = File.listRoots();
		for(int i = 0; i < roots.length; i++)
		{
			String drive = FileSystemView.getFileSystemView().getSystemDisplayName(roots[i]);
			model.add(i, drive);
		}
		driveList.setModel(model);
	}
	
	public char getIpodDriveLetter()
	{
		String selectedDrive = (String) driveList.getSelectedValue(); 
		return selectedDrive.charAt(selectedDrive.length() - 3);
	}
	
	public String getSelectedDirectory()
	{
		return fileBrowser.getSelectedFilePath();
	}
	
	public boolean isAlsoBuildLibraryXml()
	{
		return chckBuild.isSelected();
	}
	
	@Override
	public void initFunction(Library lib)
	{
		Functions.copyFromIpod(getIpodDriveLetter(), getSelectedDirectory(), isAlsoBuildLibraryXml());
	}

	@Override
	public void runFunction(Entry<String, Map> entry)
	{
		// Do Notinh
	}

	@Override
	public boolean checkForErrors()
	{
		File file = new File(getSelectedDirectory());
		if(!file.exists() || !file.isDirectory()
				|| getIpodDriveLetter() == '\u0000')
		{
			return true;
		}
		return false;
	}
}
