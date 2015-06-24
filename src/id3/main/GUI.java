package id3.main;

import id3.gui.dialogs.ArtistRatingDialog;
import id3.gui.dialogs.HalfStarsDialog;
import id3.gui.dialogs.HelpDialog;
import id3.gui.dialogs.LicenseDialog;
import id3.gui.functionpanel.FunctionPanel;
import id3.gui.functionpanel.panels.*;
import id3.objects.Library;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.logging.Logger;

/** Handles all non-panel specific UI logic,
 * as well as providing the initial run logic
 * for the selected {@link FunctionPanel}.
 */
public class GUI
{
	public static final Logger LOG = Program.LOG;
	
	/** Primary application {@code JFrame}. All UI objects are placed on this. */
	public static JFrame frame;
	
	private JTextField txtFile;
	private JLabel lblFile;
	private JButton btnBrowse;
	
	/** {@link FunctionPanel} on currently selected Tab */
	private FunctionPanel selectedPanel;
	
	private Library lib;
	
	public GUI()
	{
		init();
	}
	
	private void init()
	{
		frame = new JFrame("iD3 - iTunes ID3 Tagging for Windows");
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBounds(100, 100, 640, 500);
		frame.getContentPane().setLayout(null);
		
		lblFile = new JLabel("Library File:");
		lblFile.setFont(new Font("Verdana", Font.BOLD | Font.ITALIC, 12));
		lblFile.setBounds(8, 30, 89, 14);
		frame.getContentPane().add(lblFile);
		
		JButton btnBegin = new JButton("Begin Operation");
		btnBegin.setToolTipText("See 'About->Help' for more information on the current operation");
		btnBegin.setFont(new Font("Verdana", Font.BOLD, 11));
		btnBegin.addActionListener(e ->
		{
            String fileLibrary = getLibraryFile();
            if(fileLibrary == null)
            {
                JOptionPane.showMessageDialog(GUI.frame, "Library file doesn't exist", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                if (selectedPanel != null)
                {
                    Thread thread = new Thread(() ->
					{
                        if(selectedPanel.isRequiresItunesLibraryFile())
                        {
                            if(isNewLibraryFile())
                            {
                                lib = new Library(fileLibrary);
                            }

                            if(selectedPanel.isRequiresArtistAlbumObjects())
                            {
                                lib.createArtistObjects();
                            }
                        }
                        selectedPanel.initFunction(lib);
                    });
                    thread.setName("Function Process");
                    thread.start();
                }
            }
        });
		btnBegin.setBounds(250, 438, 140, 30);
		frame.getContentPane().add(btnBegin);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 634, 21);
		frame.getContentPane().add(menuBar);
		
		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);
		
		JMenuItem menuitemExit = new JMenuItem("Exit");
		menuitemExit.addActionListener(e -> System.exit(0));
		menuFile.add(menuitemExit);
		
		JMenu menuSettings = new JMenu("Settings");
		menuBar.add(menuSettings);
		
		JMenuItem mnitmArtistRating = new JMenuItem("Edit Artist Rating Criteria");
		mnitmArtistRating.addActionListener(e -> new ArtistRatingDialog());
		menuSettings.add(mnitmArtistRating);
		
		JMenuItem mnitmHalfStars = new JMenuItem("Enable Half-Stars");
		mnitmHalfStars.addActionListener(e -> new HalfStarsDialog());
		menuSettings.add(mnitmHalfStars);
		
		JMenu menuAbout = new JMenu("About");
		menuBar.add(menuAbout);
		
		JMenuItem mnitmHelp = new JMenuItem("Help");
		mnitmHelp.addActionListener(e -> new HelpDialog());
		menuAbout.add(mnitmHelp);
		
		JMenuItem mnitmAboutId3 = new JMenuItem("About iD3");
		mnitmAboutId3.addActionListener(e -> new LicenseDialog());
		menuAbout.add(mnitmAboutId3);
		
		txtFile = new JTextField();
		txtFile.setBounds(104, 27, 421, 20);
		frame.getContentPane().add(txtFile);
		txtFile.setColumns(10);
		
		btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(e ->
		{
            JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
            jfc.setFileFilter(new FileNameExtensionFilter("iTunes Music Library.xml", "xml"));
            int val = jfc.showOpenDialog(frame);

            if(val == JFileChooser.APPROVE_OPTION)
            {
                txtFile.setText(jfc.getSelectedFile().getAbsolutePath());
            }
        });
		btnBrowse.setBounds(535, 26, 89, 23);
		frame.getContentPane().add(btnBrowse);
		
		JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
		tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabs.setBounds(0, 55, 625, 380);
		tabs.addTab("Formatting", new FormattingPanel());
		tabs.addTab("Missing Fields", new MissingFieldsPanel());
		tabs.addTab("Get Lyrics", new GetLyricsPanel());
		tabs.addTab("BPM Detection", new BpmDetectionPanel());
		tabs.addTab("Swap Fields", new SwapFieldsPanel());
		tabs.addTab("Append / Prepend", new AppendPrependPanel());
		tabs.addTab("Find and Replace", new FindAndReplacePanel());
		tabs.addTab("Delete Fields", new DeleteFieldsPanel());
		tabs.addTab("Custom Fields", new CustomFieldsPanel());
		tabs.addTab("Track# In Name", new NumberInNamePanel());
		tabs.addTab("Artist In Name", new ArtistInNamePanel());
		tabs.addTab("Save Ratings", new SaveRatingsPanel());
		tabs.addTab("Build Library Xml", new BuildXmlPanel());
		tabs.addTab("Copy From iPod", new CopyFromIpodPanel());
		tabs.addTab("Export Artwork", new ExportArtworkPanel());
		tabs.addTab("Embed Artwork", new EmbedArtworkPanel());
		tabs.addTab("Remove Duplicates", new RemoveDuplicatePanel());
		tabs.addTab("Missing Songs", new MissingSongsPanel());
		tabs.addTab("Unlisted Files", new UnlistedSongsPanel());
		
		tabs.addChangeListener(e ->
		{
            Component selectedTab = tabs.getSelectedComponent();
            if(selectedTab instanceof FunctionPanel)
            {
                selectedPanel = (FunctionPanel) selectedTab;

                if(!selectedPanel.isRequiresItunesLibraryFile())
                {
                    setLibraryFileInputEnabled(false);
                }
                else
                {
                    setLibraryFileInputEnabled(true);
                }

                if(selectedTab instanceof GetLyricsPanel) // TODO
                {
                    btnBegin.setEnabled(false);
                }
                else
                {
                    btnBegin.setEnabled(true);
                }
            }

        });
		
		selectedPanel = (FunctionPanel) tabs.getSelectedComponent();
		
		frame.getContentPane().add(tabs);
	}
	
	/** Locks the {@literal GUI}, preventing any additional input. */
	public static void lock()
	{
		frame.setEnabled(false);
	}
	
	/** Unlocks the {@literal GUI}, allowing new input. */
	public static void unlock()
	{
		frame.setEnabled(true);
	}
	
	public String getLibraryFile()
	{
		String fileLibrary = txtFile.getText();
		if(!new File(fileLibrary).exists())
		{
			return null;
		}
		return fileLibrary;
	}
	
	/** Shows / Hides file browser. Some Panels don't need
	 * an iTunes library file to work.
	 * @param isEnabled  If true, allowed to browse for iTunes
	 * xml file.
	 */
	private void setLibraryFileInputEnabled(boolean isEnabled)
	{
		lblFile.setVisible(isEnabled);
		btnBrowse.setVisible(isEnabled);
		txtFile.setVisible(isEnabled);
	}
	
	/** Checks to see if the current supplied library file
	 * is the same as one used previously.
	 * @return  True if the file is new, false if not.
	 */
	private boolean isNewLibraryFile()
	{
		if(lib != null)
		{
			if(txtFile.getText().equalsIgnoreCase(lib.getLibraryFile().getAbsolutePath()))
			{
				return false;
			}
		}
		return true;
	}
}
