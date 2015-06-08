package id3.gui.functionpanel.panels;

import id3.functions.Functions;
import id3.gui.functionpanel.SimpleFunctionPanel;

import java.awt.Font;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JCheckBox;

import org.jaudiotagger.tag.FieldKey;

public class DeleteFieldsPanel extends SimpleFunctionPanel 
{
	private static final String TEXT_INFO = "Deletes the selected field from the ID3 tag\r\nfor ALL songs in the music library.\r\n\r\n"
			+ "NOTE: Delete Ratings will not remove the\r\nrating from iTunes, only from the ID3 tag";
	
	private JCheckBox chckDelArt;
	private JCheckBox chckDelLyrics;
	private JCheckBox chckDelRatings;

	/** Creates a new {@code DeleteFieldsPanel}
	 * @see SimpleFunctionPanel
	 * @see FunctionPanel
	 */
	public DeleteFieldsPanel()
	{
		super(TEXT_INFO);
		
		chckDelArt = new JCheckBox("Delete Artwork");
		chckDelArt.setFont(new Font("Verdana", Font.PLAIN, 12));
		chckDelArt.setBounds(95, 202, 119, 23);
		this.add(chckDelArt);
		
		chckDelLyrics = new JCheckBox("Delete Lyrics");
		chckDelLyrics.setFont(new Font("Verdana", Font.PLAIN, 12));
		chckDelLyrics.setBounds(95, 228, 119, 23);
		this.add(chckDelLyrics);
		
		chckDelRatings = new JCheckBox("Delete Ratings");
		chckDelRatings.setFont(new Font("Verdana", Font.PLAIN, 12));
		chckDelRatings.setBounds(95, 254, 119, 23);
		this.add(chckDelRatings);
	}

	@Override
	public void runFunction(Entry<String, Map> entry)
	{
		if(chckDelArt.isSelected())
		{
			Functions.deleteField(entry, FieldKey.MEDIA); // Flag to delete artwork NOT media
		}
		if(chckDelLyrics.isSelected())
		{
			Functions.deleteField(entry, FieldKey.LYRICS);
		}
		if(chckDelRatings.isSelected())
		{
			Functions.deleteField(entry, FieldKey.RATING);
		}
	}
	
	@Override
	public boolean checkForErrors()
	{
		if(!chckDelArt.isSelected() && !chckDelLyrics.isSelected() &&
				!chckDelRatings.isSelected())
		{
			return true;
		}
		return false;
	}
}
