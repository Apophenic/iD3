package id3.gui.functionpanel.panels;

import id3.functions.Functions;
import id3.gui.functionpanel.SimpleFunctionPanel;

import java.util.Map;
import java.util.Map.Entry;

public class EmbedArtworkPanel extends SimpleFunctionPanel
{
	private static final String INFO_TEXT = "Embeds corresponding artwork file\r\ninto every song file in the library.\r\n\r\n"
			+ "This can also be done in iTunes by\r\nperforming \"Consolidate ID3 Tags,\"\r\nbut doing so isn't recommended.";
	
	/** Creates a new {@code EmbedArtworkPanel}
	 * @see SimpleFunctionPanel
	 * @see FunctionPanel
	 */
	public EmbedArtworkPanel()
	{
		super(INFO_TEXT);
	}

	@Override
	public void runFunction(Entry<String, Map> entry)
	{
		Functions.embedArtwork(entry);
	}

}
