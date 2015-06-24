package id3.gui.functionpanel.panels;

import id3.functions.Functions;
import id3.gui.functionpanel.SimpleFunctionPanel;
import id3.objects.Library;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Map.Entry;

public class MissingSongsPanel extends SimpleFunctionPanel
{
	private static final String INFO_TEXT = "Creates a .csv file listing all missing song's file's locations."
			+ " If song discovery is attempted, missing songs will be looked for"
			+ " based on their title, artist, album, etc.";
	
	private JCheckBox chckDiscovery;
	
	/** Creates a new {@code MissingSongsPanel}
	 * @see SimpleFunctionPanel
	 * @see id3.gui.functionpanel.FunctionPanel
	 */
	public MissingSongsPanel()
	{
		super(INFO_TEXT);
		
		chckDiscovery = new JCheckBox("Attempt Song Discovery");
		chckDiscovery.setFont(new Font("Verdana", Font.PLAIN, 11));
		chckDiscovery.setBounds(94, 200, 199, 23);
		this.add(chckDiscovery);
	}
	
	public boolean isAttemptDiscovery()
	{
		return chckDiscovery.isSelected();
	}
	
	@Override
	public void initFunction(Library lib)
	{
		if(!checkForErrors())
		{
			Functions.findMissingSongs(lib, isAttemptDiscovery());
		}
	}
	

	@Override
	public void runFunction(Entry<String, Map> entry)
	{
		// Do Nothing
	}
}
