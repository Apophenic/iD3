package id3.gui.customui;

import id3.main.GUI;
import id3.main.Settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

/** A basic UI component, containing a button to open
 * a {@code JFileChooser} and a {@code JTextField} to save
 * a file or directory path to.
 */
public class FileBrowserPanel extends JPanel
{
	private JTextField txtFile;
	
	/** Creates a UI component used to graphically get
	 * a file or directory path.
	 * @param pathType  Use {@code JFileChooser}'s integer enums
	 */
	public FileBrowserPanel(int pathType)
	{
		this.setLayout(null);
		this.setBounds(0, 0, 500, 25);
		
		txtFile = new JTextField();
		txtFile.setBounds(0, 0, 365, 20);
		txtFile.setColumns(10);
		this.add(txtFile);
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser jfc = new JFileChooser(Settings.PROGRAM_DIR);
				UIManager.put("FileChooser.openDialogTitleText", "Select");
				jfc.setFileSelectionMode(pathType);
				jfc.setMultiSelectionEnabled(false);
				
				int val = jfc.showOpenDialog(GUI.frame);
				if(val == JFileChooser.APPROVE_OPTION)
				{
					String filename = jfc.getSelectedFile().getAbsolutePath();
					txtFile.setText(filename);
				}
			}
		});
		btnBrowse.setBounds(375, 0, 89, 23);
		this.add(btnBrowse);
	}
	
	public String getSelectedFilePath()
	{
		return txtFile.getText();
	}
	
	public void setSelectedFilePath(String path)
	{
		txtFile.setText(path);
	}

}
