/*
 * org.openmicroscopy.shoola.util.ui.TableComponent
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2004 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *------------------------------------------------------------------------------
 */

package org.openmicroscopy.shoola.util.ui.table;


//Java imports
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

//Third-party libraries

//Application-internal dependencies

/** 
 * A JTable which displays any JComponent.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author  <br>Andrea Falconi &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:a.falconi@dundee.ac.uk">
 * 					a.falconi@dundee.ac.uk</a>
 * @version 2.2 
 * <small>
 * (<b>Internal version:</b> $Revision$ $Date$)
 * </small>
 * @since OME2.2
 */
public class TableComponent 
	extends JTable
{

    /**
     * Creates a new instance.
     * 
     * @param row   The number of rows.
     * @param col   The number of columns.
     */
	public TableComponent(int row, int col)
	{
		super(row, col);
	}
    
    /**
     * Overrides the method to return the proper renderer to display.
     * @see JTable#getCellRenderer(int, int)
     */
	public TableCellRenderer getCellRenderer(int row, int column)
	{
		TableColumn tableColumn = getColumnModel().getColumn(column);
		TableCellRenderer renderer = tableColumn.getCellRenderer();
		if (renderer == null) {
			Class c = getColumnClass(column);
			if (c.equals(Object.class)) {
				if (getValueAt(row, column) != null)
					c = getValueAt(row, column).getClass();
			}
			renderer = getDefaultRenderer(c);
		}
		return renderer;
	}
		
    /**
     * Overrides the method to return the proper editor to display.
     * @see JTable#getCellEditor(int, int)
     */
	public TableCellEditor getCellEditor(int row, int column)
	{
		TableColumn tableColumn = getColumnModel().getColumn(column);
		TableCellEditor editor = tableColumn.getCellEditor();
		if (editor == null) {
			Class c = getColumnClass(column);
			if (c.equals(Object.class)) {
				if (getValueAt(row, column) != null)
					c = getValueAt(row, column).getClass();
			}
			editor = getDefaultEditor(c);
		}
		return editor;
	}
	
}
