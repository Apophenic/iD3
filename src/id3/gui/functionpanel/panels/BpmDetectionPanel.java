package id3.gui.functionpanel.panels;

import id3.functions.Functions;
import id3.gui.functionpanel.SimpleFunctionPanel;

import java.util.Map;
import java.util.Map.Entry;

public class BpmDetectionPanel extends SimpleFunctionPanel
{
	private static final String INFO_TEXT = "Analyzes song files and calculates the BPM for every\r\n"
			+ "song in the library file. The results will be written\r\nto the song's ID3 tag.\r\n\r\nWARNING: This is a very time intensive process";

	/** Creates a new {@code BpmDetectionPanel}
	 * @see SimpleFunctionPanel
	 * @see id3.gui.functionpanel.FunctionPanel
	 */
	public BpmDetectionPanel()
	{
		super(INFO_TEXT);
	}

	@Override
	public void runFunction(Entry<String, Map> entry)
	{
		Functions.detectBPM(entry);
	}
	
}
