package id3.tables;

import id3.tables.TableEntryComparator.CompareType;
import id3.tables.abstractid3model.AbstractID3Model;
import id3.utils.Utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Collections;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

/** Custom {@code JTable} backed by
 * an {@link AbstractID3Model}.
 */
public class ID3Table extends JTable
{
	protected AbstractID3Model model;
	
	protected JPopupMenu rightClickMenu = new JPopupMenu();
	
	/** Custom {@code JTable} backed by
	 * an {@link AbstractID3Model}.
	 * @param model  {@link AbstractID3Model} to back
	 */
	public ID3Table(AbstractID3Model model)
	{
		this.setModel(model);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		this.getTableHeader().setReorderingAllowed(false);
		
		registerRightClick();
		registerTableHeader();
	}
	
	/** Adds right click options and logic
	 * to the table.
	 */
	private void registerRightClick()
	{
		JMenuItem itemRemove = new JMenuItem("Remove Selected");
		itemRemove.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] selectedrows = ID3Table.this.getSelectedRows();
				
				for(int i = selectedrows.length - 1; i >= 0; i--)
				{
					ID3Table.this.getModel().getTableEntries().remove(selectedrows[i]);
					ID3Table.this.getModel().fireTableRowsDeleted(i, i);
				}
				
				ID3Table.this.clearSelection();
			}
		});
		rightClickMenu.add(itemRemove);
		
		JMenuItem itemOpen = new JMenuItem("Open File Location");
		itemOpen.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] selectedrows = ID3Table.this.getSelectedRows();
				
				for(int current : selectedrows)
				{
					TableEntry tableEntry = ID3Table.this.getModel().getTableEntries().get(current);
					String path = Utils.convertForwardToBackSlash(tableEntry.FilePath);
					try 
					{
						Runtime.getRuntime().exec("explorer " + path);
					}
					catch (IOException ex)
					{
						ex.printStackTrace();
					}
				}
			}
		});
		rightClickMenu.add(itemOpen);
		
		this.addMouseListener(new MouseListener()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				int r = ID3Table.this.rowAtPoint(e.getPoint());
		        if (r >= 0 && r < ID3Table.this.getRowCount())
		        {
		        	int[] selected = ID3Table.this.getSelectedRows();
		        	for(int current : selected)
		        	{
		        		if(r==current)
		        		{
		        			return;
		        		}
		        	}
		        	ID3Table.this.setRowSelectionInterval(r, r);
		        }
		        else
		        {
		        	ID3Table.this.clearSelection();
		        }
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if(e.isPopupTrigger())
				{
					rightClickMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
		});
	}
	
	/** Provides sorting logic for
	 * clicks on the table header.
	 */
	private void registerTableHeader()
	{
		this.getTableHeader().addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e)
			{
				JTableHeader clickedheader = (JTableHeader) e.getSource();
				String columnName = ID3Table.this.getColumnName(clickedheader.columnAtPoint(e.getPoint()));
				switch(columnName)
				{
					case "Title" :
					case "Song" :
					case "Current Song Title" : //TODO reverse sort
					{
						Collections.sort(model.getTableEntries(), new TableEntryComparator(CompareType.SongTitle));
						break;
					}
					case "New Title" :
					case "New Song Title" :
					{
						Collections.sort(model.getTableEntries(), new TableEntryComparator(CompareType.NewTitle));
						break;
					}
					case "Artist" :
					{
						Collections.sort(model.getTableEntries(), new TableEntryComparator(CompareType.Artist));
						break;
					}
					case "Album" :
					{
						Collections.sort(model.getTableEntries(), new TableEntryComparator(CompareType.Album));
						break;
					}
					case "Track #" :
					{
						Collections.sort(model.getTableEntries(), new TableEntryComparator(CompareType.TrackNumber));
						break;
					}
					case "Rating" :
					{
						Collections.sort(model.getTableEntries(), new TableEntryComparator(CompareType.Rating));
						break;
					}
					case "File Path" :
					{
						Collections.sort(model.getTableEntries(), new TableEntryComparator(CompareType.FilePath));
						break;
					}
					case "New File Path" :
					{
						Collections.sort(model.getTableEntries(), new TableEntryComparator(CompareType.NewFilePath));
						break;
					}
					case "Field" :
					case "Status" :
					{
						Collections.sort(model.getTableEntries(), new TableEntryComparator(CompareType.Status));
						break;
					}
				}
				model.fireTableDataChanged();
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
	}
	
	public AbstractID3Model getModel()
	{
		return model;
	}
	
	/** Sets the model, but also calls
	 * {@link JTable#setModel(TableModel)}
	 * to fire the appropriate update listeners.
	 * @param model  {@link AbstractID3Model} to back
	 * this ID3Table instance.
	 */
	public void setModel(AbstractID3Model model)
	{
		super.setModel((TableModel) model);
		this.model = model;
	}

}
