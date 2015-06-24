package id3.gui.customui;

import javax.swing.*;
import java.awt.*;

/** A generic {@code JTextArea} used by
 * {@link id3.gui.functionpanel.FunctionPanel} to briefly describe what
 * each function accomplishes.
 */
public class InfoTextArea extends JTextArea
{
	private static final Rectangle DEFAULT_BOUNDS = new Rectangle(0, 0, 110, 230);	//Default bounds for TableFunctionPanel
	
	/** Creates basic {@code JTextArea}
	 * with no text.
	 */
	public InfoTextArea()
	{
		this.setLayout(null);
		this.setBounds(DEFAULT_BOUNDS);
		this.setWrapStyleWord(true);
		this.setEditable(false);
		this.setBackground(UIManager.getColor("Button.background"));
		this.setLineWrap(true);
		this.setFont(new Font("Verdana", Font.PLAIN, 11));
		this.setBounds(DEFAULT_BOUNDS);
	}
	
	/**
	 * @param infoText  Text to place in text area.
	 * @see InfoTextArea#InfoTextArea()
	 */
	public InfoTextArea(String infoText)
	{
		this();
		setInfoText(infoText);
	}
	
	/**
	 * @param infoText  Text to place in text area.
	 * @param bounds  {@code Rectangle} defining text area bounds.
	 * @see InfoTextArea#InfoTextArea()
	 */
	public InfoTextArea(String infoText, Rectangle bounds)
	{
		this(infoText);
		setBounds(bounds);
	}
	
	public void setInfoText(String infoText)
	{
		this.setText(infoText);
	}

}
