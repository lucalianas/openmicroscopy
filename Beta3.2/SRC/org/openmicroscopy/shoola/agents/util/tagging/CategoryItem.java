/*
 * org.openmicroscopy.shoola.agents.util.tagging.CategoryItem 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2007 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.agents.util.tagging;


//Java imports
import javax.swing.JMenuItem;

//Third-party libraries

//Application-internal dependencies
import pojos.CategoryData;
import pojos.CategoryGroupData;
import pojos.DataObject;

/** 
 * Utility class hosting a category.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME3.0
 */
public class CategoryItem
	extends JMenuItem
{

	/** Indicates that the data object is a category. */
	public static final int		CATEGORY = 0;
	
	/** Indicates that the data object is a category. */
	public static final int		CATEGORY_GROUP = 1;
	
	/** The category hosted by the component. */
	private DataObject data;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param data	The category hosting by this node.
	 */
	public CategoryItem(DataObject data)
	{
		if (data == null)
			throw new IllegalArgumentException("No category specified.");
		
		this.data = data;
		setText(getObjectName());
		setToolTipText(getObjectDescription());
	}
	
	/**
	 * Returns the object hosted by this component.
	 * 
	 * @return See above.
	 */
	public DataObject getDataObject() { return data; }
	
	/**
	 * Sets the description of the data object.
	 * 
	 * @param des The value to set.
	 */
	public void setObjectDescription(String des)
	{ 
		if (data instanceof CategoryData) {
			((CategoryData) data).setDescription(des);  
		} else if (data instanceof CategoryGroupData) {
			((CategoryGroupData) data).setDescription(des);  
		}
	}
	
	/**
	 * Sets the name of the data object.
	 * 
	 * @param name The value to set.
	 */
	public void setObjectName(String name)
	{ 
		if (data instanceof CategoryData) {
			((CategoryData) data).setName(name);  
		} else if (data instanceof CategoryGroupData) {
			((CategoryGroupData) data).setName(name);  
		}
	}
	
	/**
	 * Returns the description of the data object.
	 * 
	 * @return See above.
	 */
	public String getObjectDescription()
	{ 
		if (data instanceof CategoryData) {
			return ((CategoryData) data).getDescription(); 
		} else if (data instanceof CategoryGroupData) {
			return ((CategoryGroupData) data).getDescription(); 
		}
		return null;
	}
	
	/**
	 * Returns the name of the data object.
	 * 
	 * @return See above.
	 */
	public String getObjectName()
	{ 
		if (data instanceof CategoryData) {
			return ((CategoryData) data).getName(); 
		} else if (data instanceof CategoryGroupData) {
			return ((CategoryGroupData) data).getName(); 
		}
		return null;
	}
	
	/**
	 * Returns the id of the category owner.
	 * 
	 * @return See above.
	 */
	public long getOwnerID() { return data.getOwner().getId(); }
	
	/**
	 * Returns the id of the object hosted by this component.
	 * 
	 * @return See above.
	 */
	public long getObjectID() { return data.getId(); }
	
	/**
	 * Overridden to return the name of the data object.
	 * @see Object#toString()
	 */
	public String toString() { return getObjectName(); }

}
