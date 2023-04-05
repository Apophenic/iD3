package id3.gui.functionpanel;

import id3.gui.dialogs.ProgressDialog;
import id3.main.GUI;
import id3.main.Program;
import id3.objects.Library;
import id3.utils.Utils;

import javax.swing.*;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Superclass to all panels used in iD3.
 * Stores relevant panel state to be used in 
 * the appropriate function. This class is largely a template
 * to outline how a panel in iD3 should work.
 * <p>
 * The idea is that beginning an operation from the GUI
 * will be forced to call {@link #initFunction(Library)},
 * which lays down some basic run logic to be passed into
 * the corresponding method in {@link id3.functions.Functions}.
 * In most cases, the corresponding method will be pointed
 * to in {@link #runFunction(Entry)}.
 * <p>
 * Ideally, each method in function will iterate over
 * an {@code Entry<String, Map>} object representing
 * each track in an iTunes Library, but some functions
 * don't follow this procedure and should override
 * {@link #initFunction(Library)} and provide custom
 * logic.
 * <p>
 * In a nutshell: maintains panel state and contains
 * a pointer to the corresponding method in {@link id3.functions.Functions}
 * @see id3.functions.Functions
 * @see Library
 */
public abstract class FunctionPanel extends JPanel
{
	protected static final Logger LOG = Program.LOG;
	
	/** Flag determines if an iTunes file is needed for the function. */
	protected boolean isRequiresItunesLibraryFile = true;
	
	/** Flag determines if Artist objects are needed to complete
	 * the panel's function. This should be avoided unless absolutely
	 * necessary.
	 * @see Library#createArtistObjects()
	 */
	protected boolean isRequiresArtistAlbumObjects = false;
	
	public FunctionPanel()
	{
		this.setLayout(null);
		this.setBounds(0, 55, 624, 387);
	}
	
	/** Creates run logic to be used in tandem
	 * with the panel's function. Default behavior is supply
	 * a progress dialog and iterate over each entry
	 * in the library object.
	 * <p>
	 * This will need to be overriden if the function
	 * needs library specific objects or the entire
	 * library object itself.
	 * @param lib  {@link Library} object to use
	 */
	public void initFunction(Library lib)
	{
		if(!checkForErrors())
		{
			LOG.log(Level.FINE, "Initializing function");
			ProgressDialog progress = new ProgressDialog(lib.getTrackMapEntries().size());
			
			int progressCounter = 0;		
			for(Entry<String, Map> entry : lib.getTrackMapEntries())
			{
				String location = (String) entry.getValue().get("Location");

				// TODO: find reason why some file locations equal null. Skip for now.
				if(location != null) {
						String fileLocation = Utils.getFilePathFromTrackEntry(entry);
						LOG.log(Level.FINER, "PROCESSING: " + fileLocation);

						runFunction(entry);
				}
				
				progress.update(progressCounter++);
			}
					
			progress.finish();
		}
		else
		{
			JOptionPane.showMessageDialog(GUI.frame, "Panel has errors", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public boolean isRequiresItunesLibraryFile()
	{
		return isRequiresItunesLibraryFile;
	}
	
	public boolean isRequiresArtistAlbumObjects()
	{
		return isRequiresArtistAlbumObjects;
	}
	
	/** Panel specific, makes sure the panel state
	 * will pass valid values to the panel's
	 * function method in {@link id3.functions.Functions}.
	 * @return  true if errors, false if no errors
	 */
	public abstract boolean checkForErrors();
	
	/** Called by {@link #initFunction(Library)} for every track
	 * entry in a {@link Library}. If the panel's function
	 * doesn't require iteration over every track, it's
	 * recommended to implement logic in {@link #initFunction(Library)}
	 * and override this method to do nothing.
	 * @param entry  Library file's equivalent to a track in iTunes
	 */
	public abstract void runFunction(Entry<String, Map> entry);
}
