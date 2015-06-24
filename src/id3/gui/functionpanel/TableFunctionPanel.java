package id3.gui.functionpanel;

import id3.gui.customui.InfoTextArea;
import id3.gui.dialogs.ProgressDialog;
import id3.gui.dialogs.TableDialog;
import id3.objects.Library;
import id3.tables.ID3Table;
import id3.tables.TableEntry;
import id3.tables.abstractid3model.AbstractID3Model;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

/** A subclass of {@link FunctionPanel} that
 * graphically defines an {@link ID3Table}
 * on the panel.
 */
public abstract class TableFunctionPanel extends FunctionPanel
{
	protected InfoTextArea infoTextPanel = new InfoTextArea();
	
	/** List that backs this table's model */
	protected ArrayList<TableEntry> tableEntries = new ArrayList<>();
	
	protected ID3Table table;
	private JScrollPane scrollPane;
	
	/** A subclass of {@link FunctionPanel} that
	 * graphically defines an {@link ID3Table}
	 * on the panel.
	 * @param table  Table to place on the panel.
	 * @param infoText  Text to be displayed on the panel
	 * explaining what the panel's function does.
	 * @see AbstractID3Model
	 */
	public TableFunctionPanel(ID3Table table, String infoText)
	{
		this.table = table;
		registerTable();
		initScrollPane();
		setPanelInfoText(infoText);
	}

	@Override
	public boolean checkForErrors()
	{
		return false;
	}
	
	private void registerTable()
	{
		JButton btnCommit = new JButton("Commit Changes");
		btnCommit.setFont(new Font("Verdana", Font.PLAIN, 11));
		btnCommit.addActionListener(e -> initCommit());
		btnCommit.setBounds(197, 320, 163, 35);
		add(btnCommit);
		
		JButton btnOpenTable = new JButton("Open Table");
		btnOpenTable.setFont(new Font("Verdana", Font.PLAIN, 11));
		btnOpenTable.addActionListener(e ->
		{
            TableDialog dialog = new TableDialog(table); //blocking
            table = dialog.getTable();

            TableFunctionPanel.this.remove(scrollPane); // TODO ?
            initScrollPane();
            resizeColumns();
        });
		btnOpenTable.setBounds(10, 320, 115, 35);
		add(btnOpenTable);
		
		infoTextPanel.setBounds(370, 11, 110, 230);
		this.add(infoTextPanel);
	}
	
	private void initScrollPane()
	{
		scrollPane = new JScrollPane(table);
		scrollPane.setBounds(10, 11, 350, 292);
		add(scrollPane);
	}
	
	public void setPanelInfoText(String infoText)
	{
		infoTextPanel.setInfoText(infoText);
	}
	
	@Override
	public void initFunction(Library lib)
	{
		tableEntries.clear();
		super.initFunction(lib);
	}
	
	/** Lays down basic logic as to how
	 * all {@link TableEntry}s should be handled
	 * in an {@link ID3Table} when changes are committed.
	 * <p>
	 * This work's very similarly to
	 *  {@link FunctionPanel#initFunction(Library)},
	 *  where the logic is laid out that iterates over
	 *  a list and another method is called to manipulate
	 *  said list. In this case, {@link #runCommit(TableEntry)}.
	 *  @see FunctionPanel
	 * 
	 */
	public void initCommit()
	{ 	
		ArrayList<TableEntry> entries = table.getModel().getTableEntries();
		if(entries.isEmpty())
		{
			JOptionPane.showMessageDialog(new JFrame(), "Table is empty", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		Thread thread = new Thread(() ->
		{
            ProgressDialog progress = new ProgressDialog();
            LOG.log(Level.FINE, "Commiting changes");

            for(TableEntry entry : entries)
            {
                runCommit(entry);
            }
            table.getModel().getTableEntries().clear();
            table.getModel().fireTableRowsDeleted(0, entries.size());
            progress.finish();
        });
		thread.setName("Commmit Process");
		thread.start();
	}
	
	/** Contains logic for how to manipulate each
	 * {@link TableEntry} in an {@link ID3Table}.
	 * <p>
	 * The design is similar to
	 * {@link FunctionPanel#runFunction(Map.Entry)},
	 * but will always need to be overriden.
	 * @param entry
	 */
	protected abstract void runCommit(TableEntry entry);
	
	/** Determines how columns should be sized.
	 * This is called every time the model updates.
	 */
	protected abstract void resizeColumns();

}
