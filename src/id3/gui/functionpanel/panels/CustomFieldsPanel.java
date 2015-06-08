package id3.gui.functionpanel.panels;

import id3.functions.Functions;
import id3.gui.customui.InfoTextArea;
import id3.gui.dialogs.ArtistRatingDialog;
import id3.gui.functionpanel.FunctionPanel;
import id3.main.Settings;
import id3.objects.Library;
import id3.utils.Utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JComboBox;
import javax.swing.JButton;

import org.jaudiotagger.tag.FieldKey;

import java.awt.Rectangle;

public class CustomFieldsPanel extends FunctionPanel
{
	private static final String INFO_TEXT = "Looks at tracks per artist\r\nand/or per album to\r\n"
			+ "calculate custom ratings.\r\nSee HELP for details.\r\n\r\n"
			+ "Warning: This is a time intensive\r\nprocess.";
	
	private JCheckBox chckArtistRating;
	private JCheckBox chckAlbumRating;
	private JComboBox comboArtistRatingField;
	private JComboBox comboAlbumRatingField;
	private JButton btnArtistRating;
	private JSlider slideMinSongsRated;
	
	/** Creates a new {@code CustomFieldsPanel}
	 * @see FunctionPanel
	 */
	public CustomFieldsPanel()
	{
		init();
		super.isRequiresArtistAlbumObjects = true;
	}
	
	private void init()
	{
		chckArtistRating = new JCheckBox("Calculate Artist Rating");
		chckArtistRating.setToolTipText("Calculates artist rating based on the percentage of songs an artist has that is four stars or greater. These values and others can be adjusted by clicking \"Edit Rating Criteria.\" See HELP for more info");
		chckArtistRating.setBounds(6, 7, 189, 23);
		this.add(chckArtistRating);
		
		chckAlbumRating = new JCheckBox("Calculate Album Rating");
		chckAlbumRating.setToolTipText("Calculates based on average of song ratings. If the albums percentage of songs rated is less than the below slider, it will be skipped.");
		chckAlbumRating.setBounds(6, 140, 189, 23);
		this.add(chckAlbumRating);
		
		slideMinSongsRated = new JSlider();
		slideMinSongsRated.setEnabled(false);
		slideMinSongsRated.setSnapToTicks(true);
		slideMinSongsRated.setPaintTicks(true);
		slideMinSongsRated.setPaintLabels(true);
		slideMinSongsRated.setMinimum(20);
		slideMinSongsRated.setMaximum(100);
		slideMinSongsRated.setMajorTickSpacing(20);
		slideMinSongsRated.setBounds(29, 192, 173, 38);
		this.add(slideMinSongsRated);
		
		JLabel lblRatedMin = new JLabel("Minimum % of Songs Rated:");
		lblRatedMin.setEnabled(false);
		lblRatedMin.setBounds(38, 170, 163, 14);
		this.add(lblRatedMin);
		
		comboArtistRatingField = new JComboBox(Utils.FIELDS_STRINGS);
		comboArtistRatingField.setEnabled(false);
		comboArtistRatingField.setBounds(29, 62, 119, 20);
		comboArtistRatingField.setSelectedIndex(4);
		this.add(comboArtistRatingField);
		
		comboAlbumRatingField = new JComboBox(Utils.FIELDS_STRINGS);
		comboAlbumRatingField.setEnabled(false);
		comboAlbumRatingField.setBounds(32, 266, 119, 20);
		comboAlbumRatingField.setSelectedIndex(3);
		this.add(comboAlbumRatingField);
		
		JLabel lblArtistField = new JLabel("ID3 Field to use:");
		lblArtistField.setEnabled(false);
		lblArtistField.setBounds(29, 37, 170, 14);
		this.add(lblArtistField);
		
		JLabel lblAlbumField = new JLabel("ID3 Field to use:");
		lblAlbumField.setEnabled(false);
		lblAlbumField.setBounds(32, 241, 170, 14);
		this.add(lblAlbumField);
		
		btnArtistRating = new JButton("Edit Rating Criteria");
		btnArtistRating.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				new ArtistRatingDialog();
			}
		});
		btnArtistRating.setEnabled(false);
		btnArtistRating.setBounds(158, 61, 150, 23);
		this.add(btnArtistRating);
		
		InfoTextArea infoTextArea = new InfoTextArea(INFO_TEXT, new Rectangle(212, 141, 202, 114));
		this.add(infoTextArea);
		
		chckArtistRating.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				lblArtistField.setEnabled(chckArtistRating.isSelected());
				comboArtistRatingField.setEnabled(chckArtistRating.isSelected());
				btnArtistRating.setEnabled(chckArtistRating.isSelected());
			}
		});
		
		chckAlbumRating.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e) {
				lblRatedMin.setEnabled(chckAlbumRating.isSelected());
				slideMinSongsRated.setEnabled(chckAlbumRating.isSelected());
				lblAlbumField.setEnabled(chckAlbumRating.isSelected());
				comboAlbumRatingField.setEnabled(chckAlbumRating.isSelected());
			}
		});
	}
	
	public boolean isCalculateArtistRating()
	{
		return chckArtistRating.isSelected();
	}
	
	public boolean isCalculateAlbumRating()
	{
		return chckAlbumRating.isSelected();
	}
	
	public FieldKey getArtistRatingField()
	{
		return Utils.getFieldKeyFromString((String) comboArtistRatingField.getSelectedItem());
	}
	
	public FieldKey getAlbumRatingField()
	{
		return Utils.getFieldKeyFromString((String) comboAlbumRatingField.getSelectedItem());
	}
	
	public int getMinimumPercentSongsRated()
	{
		return slideMinSongsRated.getValue();
	}

	@Override
	public void initFunction(Library lib)
	{
		if(!checkForErrors())
		{
			if(isCalculateArtistRating())
			{
				Functions.calculateArtistRating(lib, getArtistRatingField(), Settings.useCustomRatings);
			}
			if(isCalculateAlbumRating())
			{
				Functions.calculateAlbumRating(lib, getAlbumRatingField(), getMinimumPercentSongsRated());
			}
		}
	}
	
	@Override
	public void runFunction(Entry<String, Map> entry)
	{
		//Do Nothing
	}

	@Override
	public boolean checkForErrors()
	{
		if(!isCalculateArtistRating() && !isCalculateAlbumRating())
		{
			return true;
		}
		return false;
	}

}
