/*
 * org.openmicroscopy.shoola.util.ui.MacOSMenuHandler 
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
package util;

// new version of this class that does not use third-party Mac libraries

//Java imports
import javax.swing.JFrame;

//Third-party libraries

//Application-internal dependencies

/** 
* Controls the behaviour of the <code>About</code> and <code>Quit</code>
* menu items.
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
public class MacOSMenuHandler
{

	/** 
	 * Bound property indicating that the <code>About</code>
	 * menu item is selected.
	 */
	public static final String ABOUT_APPLICATION_PROPERTY = "aboutApplication";
	
	/** 
	 * Bound property indicating that the <code>Quit</code>
	 * menu item is selected.
	 */
	public static final String QUIT_APPLICATION_PROPERTY = "quitpplication";
	
	/** Helper reference to the parent. */
	private JFrame parent;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param parent Reference to the parent. Mustn't be <code>null</code>.
	 */
	public MacOSMenuHandler(JFrame parent)
	{
		if (parent == null)
			throw new IllegalArgumentException("No manager specified.");
		this.parent = parent;
		
	}
	
	/**
	 * Sets the method we want to handle.
	 * @throws Throwable If the adapter cannot be created.
	 */
	public void initialize()
		throws Throwable
	{
		OSMenuAdapter.setQuitHandler(this, getClass().getDeclaredMethod("quit",
									(Class[]) null));
		OSMenuAdapter.setAboutHandler(this, getClass().getDeclaredMethod(
									"about", (Class[]) null));
	}
	
	/** 
	 * Handles the action fired by the <code>About</code> menu item provided
	 * by the Mac menu item.
	 */
	public void about()
	{ 
		parent.firePropertyChange(ABOUT_APPLICATION_PROPERTY, 0, 1);
	}
	
	/** 
	 * Handles the action fired by the <code>Quit</code> menu item provided
	 * by the Mac menu item.
	 */
	public void quit() 
	{ 
		parent.firePropertyChange(QUIT_APPLICATION_PROPERTY, 0, 1);
	}
	
}
