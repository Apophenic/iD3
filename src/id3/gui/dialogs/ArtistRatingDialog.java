package id3.gui.dialogs;

import id3.main.GUI;
import id3.main.Settings;

import javax.swing.JDialog;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JCheckBox;

/** Creates a dialog with options for how
 * Artist Rating should be handled if used.
 * <p>
 * There are two options:
 * <li> Every track by an artist that has a rating
 * will be averaged together to determine the artist
 * rating.
 * <li> Custom ratings: Artist rating is determined by
 * the percentage of songs an artist has that are 4 or more stars
 * over their total count of rated songs. "Brackets" are defined
 * to determine what the artist rating should then be. For example,
 * If an artist has 10 rated songs, with 6 of them being 4 or 5 stars,
 * you'll have a 4+ percentage of 60. If you define your bracket to assign
 * a rating of 3 for percentages between 40 and 70, the artist in question will
 * receieve a rating of 3.
 * @see Functions#calculateArtistRating()
 * @see Settings
 */
public class ArtistRatingDialog extends JDialog
{
	private final String[] PERCENTS = {"10", "20", "30", "40", "50", "60", "70", "80", "90", "100"};
	private final String[] SONGMIN = {"1", "2", "3", "5", "8", "10", "12", "15", "20"};
	
	private JSlider slide2;
	private JSlider slide3;
	private JSlider slide4;
	private JSlider slide5;
	
	private JCheckBox chckUseCustom;
	private JComboBox comboPercent;
	private JComboBox comboTotal;

	/** Opens a dialog used to pick artist
	 * rating specific options.
	 */
	public ArtistRatingDialog()
	{
		getContentPane().setLayout(null);
		
		JLabel lblArtistRating = new JLabel("Artist Ratings");
		lblArtistRating.setFont(new Font("Verdana", Font.BOLD, 14));
		lblArtistRating.setBounds(10, 11, 136, 18);
		getContentPane().add(lblArtistRating);
		
		JLabel lblOneStar = new JLabel("1 Star Max / 2 Star Min:");
		lblOneStar.setEnabled(false);
		lblOneStar.setFont(new Font("Verdana", Font.PLAIN, 12));
		lblOneStar.setBounds(68, 128, 185, 14);
		getContentPane().add(lblOneStar);
		
		JLabel lbl3Star = new JLabel("3 Star Min:");
		lbl3Star.setEnabled(false);
		lbl3Star.setFont(new Font("Verdana", Font.PLAIN, 12));
		lbl3Star.setBounds(151, 168, 87, 14);
		getContentPane().add(lbl3Star);
		
		JLabel lbl4Star = new JLabel("4 Star Min:");
		lbl4Star.setEnabled(false);
		lbl4Star.setFont(new Font("Verdana", Font.PLAIN, 12));
		lbl4Star.setBounds(151, 221, 185, 14);
		getContentPane().add(lbl4Star);
		
		JLabel lbl5Star = new JLabel("5 Star Min:");
		lbl5Star.setEnabled(false);
		lbl5Star.setFont(new Font("Verdana", Font.PLAIN, 12));
		lbl5Star.setBounds(151, 271, 185, 14);
		getContentPane().add(lbl5Star);
		
		JLabel lblRatedPercent = new JLabel("Minimum % of songs per artist that must be RATED:");
		lblRatedPercent.setFont(new Font("Verdana", Font.PLAIN, 12));
		lblRatedPercent.setBounds(20, 36, 363, 14);
		getContentPane().add(lblRatedPercent);
		
		JLabel lblTotal = new JLabel("Minimum TOTAL songs artist must have to be rated:");
		lblTotal.setFont(new Font("Verdana", Font.PLAIN, 12));
		lblTotal.setBounds(20, 61, 363, 14);
		getContentPane().add(lblTotal);
		
		JLabel lblPercent = new JLabel("% 4*+");
		lblPercent.setEnabled(false);
		lblPercent.setBounds(458, 153, 42, 14);
		getContentPane().add(lblPercent);
		
		JLabel lblPercent2 = new JLabel("% 4*+");
		lblPercent2.setEnabled(false);
		lblPercent2.setBounds(458, 197, 42, 14);
		getContentPane().add(lblPercent2);
		
		JLabel lblPercent3 = new JLabel("% 4*+");
		lblPercent3.setEnabled(false);
		lblPercent3.setBounds(458, 247, 42, 14);
		getContentPane().add(lblPercent3);
		
		JLabel lblPercent4 = new JLabel("% 4*+");
		lblPercent4.setEnabled(false);
		lblPercent4.setBounds(458, 292, 42, 14);
		getContentPane().add(lblPercent4);
		
		slide2 = new JSlider();
		slide2.setEnabled(false);
		slide2.setValue(0);
		slide2.setSnapToTicks(true);
		slide2.setPaintTicks(true);
		slide2.setPaintLabels(true);
		slide2.setMinimum(0);
		slide2.setMaximum(100);
		slide2.setMajorTickSpacing(10);
		slide2.setBounds(228, 128, 230, 38);
		getContentPane().add(slide2);
		
		slide3 = new JSlider();
		slide3.setEnabled(false);
		slide3.setValue(30);
		slide3.setSnapToTicks(true);
		slide3.setPaintTicks(true);
		slide3.setPaintLabels(true);
		slide3.setMinimum(10);
		slide3.setMaximum(100);
		slide3.setMajorTickSpacing(10);
		slide3.setBounds(228, 172, 230, 38);
		slide3.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				slide2.setMaximum(slide3.getValue() - 10);
			}
		});
		getContentPane().add(slide3);
		
		slide4 = new JSlider();
		slide4.setEnabled(false);
		slide4.setValue(60);
		slide4.setSnapToTicks(true);
		slide4.setPaintTicks(true);
		slide4.setPaintLabels(true);
		slide4.setMinimum(20);
		slide4.setMaximum(100);
		slide4.setMajorTickSpacing(10);
		slide4.setBounds(228, 222, 230, 38);
		slide4.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				slide3.setMaximum(slide4.getValue() - 10);
			}
		});
		getContentPane().add(slide4);
		
		slide5 = new JSlider();
		slide5.setEnabled(false);
		slide5.setValue(80);
		slide5.setSnapToTicks(true);
		slide5.setPaintTicks(true);
		slide5.setPaintLabels(true);
		slide5.setMinimum(30);
		slide5.setMaximum(100);
		slide5.setMajorTickSpacing(10);
		slide5.setBounds(228, 267, 230, 38);
		slide5.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				slide4.setMaximum(slide5.getValue() - 10);
			}
		});
		getContentPane().add(slide5);
		
		comboPercent = new JComboBox(PERCENTS);
		comboPercent.setSelectedIndex(4);
		comboPercent.setBounds(393, 34, 107, 20);
		getContentPane().add(comboPercent);
		
		comboTotal = new JComboBox(SONGMIN);
		comboTotal.setSelectedIndex(3);
		comboTotal.setBounds(393, 61, 107, 20);
		getContentPane().add(comboTotal);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!isHasErrors())
				{
					save();
					ArtistRatingDialog.this.dispose();
				}
				else
				{
					JOptionPane.showMessageDialog(GUI.frame, "Incorrect rating bracket settings!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnSave.setFont(new Font("Verdana", Font.PLAIN, 12));
		btnSave.setBounds(517, 408, 97, 23);
		getContentPane().add(btnSave);
		
		chckUseCustom = new JCheckBox("Use Custom Rating Brackets");
		chckUseCustom.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				lblOneStar.setEnabled(chckUseCustom.isSelected());
				lbl3Star.setEnabled(chckUseCustom.isSelected());
				lbl4Star.setEnabled(chckUseCustom.isSelected());
				lbl5Star.setEnabled(chckUseCustom.isSelected());
				slide2.setEnabled(chckUseCustom.isSelected());
				slide3.setEnabled(chckUseCustom.isSelected());
				slide4.setEnabled(chckUseCustom.isSelected());
				slide5.setEnabled(chckUseCustom.isSelected());
				lblPercent.setEnabled(chckUseCustom.isSelected());
				lblPercent2.setEnabled(chckUseCustom.isSelected());
				lblPercent3.setEnabled(chckUseCustom.isSelected());
				lblPercent4.setEnabled(chckUseCustom.isSelected());
			}
		});
		chckUseCustom.setFont(new Font("Verdana", Font.PLAIN, 12));
		chckUseCustom.setBounds(20, 98, 268, 23);
		getContentPane().add(chckUseCustom);
		
		load();
		this.setBounds(160, 160, 640, 480);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
	}
	
	private void save()
	{
		Settings.useCustomRatings = chckUseCustom.isSelected();
		Settings.oneStarMax = (double) slide2.getValue()/100;
		Settings.twoStarMin = (double) slide2.getValue()/100;
		Settings.threeStarMin = (double) slide3.getValue()/100;
		Settings.fourStarMin = (double) slide4.getValue()/100;
		Settings.fiveStarMin = (double) slide5.getValue()/100;
		Settings.ratedSongsMin = Double.parseDouble((String) comboPercent.getSelectedItem())/100;
		Settings.totalSongsMin = Integer.parseInt((String) comboTotal.getSelectedItem());
		
		Settings.save();
	}
	
	private void load()
	{
		chckUseCustom.setSelected(Settings.useCustomRatings);
		slide2.setValue((int) (Settings.twoStarMin*100));
		slide3.setValue((int) (Settings.threeStarMin*100));
		slide4.setValue((int) (Settings.fourStarMin*100));
		slide5.setValue((int) (Settings.fiveStarMin*100));
		comboPercent.setSelectedItem(String.valueOf((int) (Settings.ratedSongsMin*100)));
		comboTotal.setSelectedItem(String.valueOf(Settings.totalSongsMin));
	}
	
	private boolean isHasErrors()
	{
		if(slide2.getValue() < slide3.getValue())
		{
			if(slide3.getValue() < slide4.getValue())
			{
				if(slide4.getValue() < slide5.getValue())
				{
					return false;
				}
			}
		}
		return true;
	}
}
