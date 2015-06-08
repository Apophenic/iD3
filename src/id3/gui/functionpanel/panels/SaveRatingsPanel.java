package id3.gui.functionpanel.panels;

import id3.functions.Functions;
import id3.gui.functionpanel.SimpleFunctionPanel;

import java.util.Map;
import java.util.Map.Entry;

public class SaveRatingsPanel extends SimpleFunctionPanel
{
	private static final String INFO_TEXT = "Saves song ratings from library file onto\r\n"
			+ "individual song ID3 tags\r\n\r\nSee HELP for more information";
	
	/** Creates a new {@code SaveRatingsPanel}
	 * @see SimpleFunctionPanel
	 * @see FunctionPanel
	 */
	public SaveRatingsPanel()
	{
		super(INFO_TEXT);
	}

	@Override
	public void runFunction(Entry<String, Map> entry)
	{
		Functions.saveRatings(entry);
	}

}
