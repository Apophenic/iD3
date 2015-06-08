package id3.gui.dialogs;

import id3.main.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Font;
import java.util.Set;

import javax.swing.SwingConstants;

/** Creates a dialog with a progress bar */
public class ProgressDialog extends JDialog
{
	private JProgressBar progress;
	private JLabel lblMsg;
	private JButton btnCancel;

	/** Creates a new {@code ProgressDialog} instance.
	 * By default, the progressbar is indeterminate.
	 * Note that any time a progress bar is active,
	 * {@link GUI#frame} will be disabled, disallowing
	 * any additonal input.
	 */
	public ProgressDialog()
	{
		setTitle("Processing...");
		this.getContentPane().setLayout(null);
		this.setAlwaysOnTop(true);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		lblMsg = new JLabel("Processing...");
		lblMsg.setHorizontalAlignment(SwingConstants.CENTER);
		lblMsg.setFont(new Font("Verdana", Font.PLAIN, 12));
		lblMsg.setBounds(0, 41, 224, 14);
		getContentPane().add(lblMsg);
		
		progress = new JProgressBar();
		progress.setBounds(0, 0, 224, 23);
		progress.setIndeterminate(true);
		getContentPane().add(progress);
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(btnCancel.getText().equals("Cancel"))
				{
					cancel();
					ProgressDialog.this.dispose();;
				}
				else
				{
					ProgressDialog.this.dispose();;
				}
			}
		});
		btnCancel.setBounds(65, 78, 90, 23);
		getContentPane().add(btnCancel);
		
		GUI.lock();
		
		this.addWindowListener(new WindowListener()
		{
			@Override
			public void windowOpened(WindowEvent e)
			{ }
			@Override
			public void windowIconified(WindowEvent e)
			{ }
			@Override
			public void windowDeiconified(WindowEvent e)
			{ }
			@Override
			public void windowDeactivated(WindowEvent e)
			{ }
			@Override
			public void windowClosing(WindowEvent e)
			{ }
			@Override
			public void windowClosed(WindowEvent e)
			{
				cancel();
			}
			@Override
			public void windowActivated(WindowEvent e)
			{ }
		});
		
		this.setBounds(150, 150, 240, 160);
		this.setVisible(true);
	}
	
	/** Creates a determine {@code ProgressDialog}
	 * @param max  Value representing 100% completion
	 * @see ProgressDialog#ProgressDialog()
	 */
	public ProgressDialog(int max)
	{
		this();
		setDeterminate(max);
	}
	
	/** Creates an indeterminate progress bar
	 * with the supplied text.
	 * @param message  Text to place on the dialog.
	 * @see ProgressDialog#ProgressDialog()
	 */
	public ProgressDialog(String message)
	{
		this();
		lblMsg.setText(message);
	}
	
	/** Creates a determinate progress bar
	 * with the supplied text.
	 * @param message  Text to place on the dialog.
	 * @param max  Value representing 100% completion.
	 * @see ProgressDialog#ProgressDialog()
	 */
	public ProgressDialog(String message, int max)
	{
		this(message);
		setDeterminate(max);
	}
	
	/** Changes the current {@code ProgressDialog} instance
	 * to determinate.
	 * @param max  Value representing 100% completion.
	 */
	public void setDeterminate(int max)
	{
		progress.setIndeterminate(false);
		progress.setMinimum(0);
		progress.setMaximum(max);
	}
	
	/** Updates the progress' value and repaints
	 * the progress bar.
	 * @param value  New value of progress bar.
	 */
	public void update(int value)
	{
		progress.setValue(value);
	}
	
	/** Sets progress bar to finished.
	 * The bar is filled and {@link GUI#frame}
	 * is unlocked. 
	 */
	public void finish()
	{
		lblMsg.setText("Finished!");
		btnCancel.setText("Close");
		progress.setIndeterminate(false);
		progress.setMaximum(100);
		progress.setValue(100);
		
		GUI.unlock();
	}
	
	/** Calls {@link #finish()} and immediately
	 * closes the {@code ProgressDialog}
	 */
	public void finishAndClose()
	{
		finish();
		this.setVisible(false); // TODO ?
	}
	
	/** Force stops the currently running function
	 * that is utilizing this instance of
	 * {@code ProgressDialog} and unlocks
	 * the GUI.
	 * @deprecated
	 */
	public static void cancel()
	{
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
		for(Thread thread : threadArray)
		{
			if(thread.getName().equals("Function Process"))
			{
				thread.stop();	// TODO
			}
		}
		
		GUI.unlock();
	}
}
