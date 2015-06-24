package id3.gui.dialogs;

import id3.gui.customui.FileBrowserPanel;
import id3.gui.customui.InfoTextArea;
import id3.main.Program;
import id3.main.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Opens a dialog that is used to enable
 * or disable half-star ratings in iTunes.
 */
public class HalfStarsDialog extends JDialog
{
	private static final Logger LOG = Program.LOG;
	
	private static final String INFO_TEXT = "Enable/Disable Half-Star ratings in iTunes.\r\n"
			+ "If you \"click-drag\" the rating on a song, \r\nyou'll have the ability to select half stars.\r\n\r\n"
			+ "Note: half stars may not work on certain devices,\r\nand half stars aren't supported on ID3 tags\r\n"
			+ "(they'll be rounded down).";
	
	/** Executed on iTunes.exe to enable/disable half-stars (after appending 0 or 1)*/
	public static final String HALF_STAR_CMD = " /setPrefInt allow-half-stars ";
	
	private FileBrowserPanel fileBrowser = new FileBrowserPanel(JFileChooser.FILES_ONLY);
	
	private JLabel lblStatus;
	
	private boolean isHalfStarsEnabled;

	/** Used to enable/disable half-star
	 * ratings in iTunes.
	 */
	public HalfStarsDialog()
	{
		this.getContentPane().setLayout(null);
		this.setTitle("Enable/Disable Half-Stars");
		
		fileBrowser.setBounds(10, 182, 494, 25);
		this.getContentPane().add(fileBrowser);
		
		InfoTextArea infoTextArea = new InfoTextArea(INFO_TEXT, new Rectangle(10, 11, 344, 128));
		this.getContentPane().add(infoTextArea);
		
		JLabel lblDir = new JLabel("iTunes.exe Location:");
		lblDir.setFont(new Font("Verdana", Font.BOLD, 12));
		lblDir.setBounds(10, 156, 150, 14);
		getContentPane().add(lblDir);
		
		JLabel lblCurrent = new JLabel("Half ratings are currently:");
		lblCurrent.setFont(new Font("Verdana", Font.PLAIN, 12));
		lblCurrent.setBounds(10, 234, 207, 14);
		getContentPane().add(lblCurrent);
		
		lblStatus = new JLabel("Disabled");
		lblStatus.setForeground(Color.RED);
		lblStatus.setFont(new Font("Verdana", Font.BOLD, 12));
		lblStatus.setBounds(177, 234, 109, 14);
		getContentPane().add(lblStatus);
		
		JButton btnEnableDisable = new JButton("Enable / Disable");
		btnEnableDisable.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String cmd = HALF_STAR_CMD;
				if(isHalfStarsEnabled)
				{
					cmd += 0;
				}
				else
				{
					cmd += 1;
				}
				
				try
				{	
					Process process = Runtime.getRuntime().exec("\"" + fileBrowser.getSelectedFilePath() + "\"" + cmd);
					while(process.isAlive())
					{
						Thread.sleep(300);
					}
				}
				catch (IOException | InterruptedException e1)
				{
					LOG.log(Level.WARNING, "Failed to enable/disable half-stars");
				}
				
				setHalfStarsEnabledStatus(!isHalfStarsEnabled);
			}
		});
		btnEnableDisable.setBounds(180, 280, 148, 50);
		getContentPane().add(btnEnableDisable);
		
		this.addWindowListener(new WindowListener()
		{
			public void windowOpened(WindowEvent e) {}
			public void windowClosing(WindowEvent e)
			{
				Settings.isHalfStarsEnabled = isHalfStarsEnabled;
				Settings.itunesDir = fileBrowser.getSelectedFilePath();
				Settings.save();
			}
			public void windowClosed(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
		});
		
		load();
		this.setBounds(150, 150, 520, 375);
		this.setVisible(true);
	}
	
	/** Attempts to determine whether
	 * half-star ratings is currently
	 * enabled or disabled.
	 */
	private void load()
	{
		String dir = Settings.itunesDir;
		if(dir == null)
		{
			dir = "";
		}
		fileBrowser.setSelectedFilePath(dir);
		
		if(!dir.isEmpty())
		{
			String os = System.getProperty("os.name");
			String osDir = "Users";
			if(os.equals("Windows XP"))
			{
				osDir = "Documents and Settings";
			}
			
			String prefLocation = dir.charAt(0) + ":\\" + osDir + "\\" + System.getProperty("user.name") + 
					"\\AppData\\Roaming\\Apple Computer\\iTunes\\iTunesPrefs.xml";
			
			Scanner s = null;
			String line;
			try
			{
				s = new Scanner(new File(prefLocation));
				while((line = s.nextLine()) != null)
				{
					if(line.contains("<key>allow-half-stars</key>"))
					{
						line = s.nextLine();
						if(line.contains("0"))
						{
							Settings.isHalfStarsEnabled = false;
						}
						else
						{
							Settings.isHalfStarsEnabled = true;
						}
						break;
					}
				}
			}
			catch (FileNotFoundException e)
			{
				// do nothing
			}
			finally
			{
				s.close();
				setHalfStarsEnabledStatus(Settings.isHalfStarsEnabled);
			}
		}
	}
	
	private void setHalfStarsEnabledStatus(boolean isEnabled)
	{
		if(isEnabled)
		{
			lblStatus.setText("Enabled");
			lblStatus.setForeground(Color.GREEN);
		}
		else
		{
			lblStatus.setText("Disabled");
			lblStatus.setForeground(Color.RED);
		}
		isHalfStarsEnabled = isEnabled;
	}
}
