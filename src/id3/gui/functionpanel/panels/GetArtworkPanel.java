package id3.gui.functionpanel.panels;

import id3.gui.functionpanel.SimpleFunctionPanel;
import id3.objects.Library;

import java.util.Map;
import java.util.Map.Entry;

public class GetArtworkPanel extends SimpleFunctionPanel // TODO
{
	private static final String INFO_TEXT = "Opens FindArt - a program that performs a\r\n"
			+ "Google Image search for missing artwork in your library.";
	
	/** Creates a new {@code GetArtworkPanel}
	 * @see SimpleFunctionPanel
	 * @see id3.gui.functionpanel.FunctionPanel
	 */
	public GetArtworkPanel()
	{
		super(INFO_TEXT);
	}
	
	@Override
	public void initFunction(Library lib)
	{
		// Open FindArt
	}

	@Override
	public void runFunction(Entry<String, Map> entry)
	{
		// Do Nothing
	}
}
