package id3.gui.functionpanel;

import id3.gui.customui.InfoTextArea;

import java.awt.Rectangle;

/** A simple subclass of {@link FunctionPanel}
 * that outlines a panel containing only a
 * {@link InfoTextArea} containing info
 * on how the panel's function operates.
 */
public abstract class SimpleFunctionPanel extends FunctionPanel
{
	/** Passes the text to the text area
	 * @param infoText  Text to pass
	 */
	public SimpleFunctionPanel(String infoText)
	{
		InfoTextArea infoTextArea = new InfoTextArea(infoText, new Rectangle(98, 60, 345, 140));
		this.add(infoTextArea);
	}
	
	@Override
	public boolean checkForErrors()
	{ 
		return false; 
	}

}
