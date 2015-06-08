package id3.gui.dialogs;

import id3.tables.ID3Table;
import id3.tables.abstractid3model.AbstractID3Model;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

/** Opens an {@link ID3Table} in its own,
 * larger dialog box.
 * @see TableFunctionPanel
 */
public class TableDialog extends JDialog
{
	private ID3Table table;
	
	/** Creates a seperate and larger dialog for
	 * an {@link ID3Table}
	 * @param table  Table to place in dialog.
	 */
	public TableDialog(ID3Table table)
	{ 
		this.table = table;
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setBounds(100, 100, 640, 480);
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(0, 0, 624, 442);

		this.getContentPane().add(scrollPane);
		this.setVisible(true);
	}
	
	public ID3Table getTable()
	{
		return table;
	}
	
	public AbstractID3Model getModel()
	{
		return table.getModel();
	}
}
