/*
 * org.openmicroscopy.shoola.env.ui.ExitDialog
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006 University of Dundee. All rights reserved.
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

package org.openmicroscopy.shoola.env.ui;


//Java imports
import javax.swing.Icon;
import javax.swing.JFrame;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.env.Container;
import org.openmicroscopy.shoola.util.ui.OptionsDialog;

/** 
 * Asks the user if he/she really wants to exit the application.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author	Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $ $Date: $)
 * </small>
 * @since OME2.2
 */
class ExitDialog
    extends OptionsDialog
{

    /** The title of this window. */
    private static final String TITLE = "Exit application";
    
    /** The message displayed. */
    private static final String MESSAGE = "Do you really want to exit?";
    
    /** Reference to the container. */
    private Container       container;
    
    /**
     * Closes the application.
     * @see OptionsDialog#onYesSelection()
     */
    protected void onYesSelection() { container.exit(); }
    
    /**
     * Creates a new instance.
     * 
     * @param parent    The parent of this dialog.
     * @param icon      The icon displayed next to the message.
     * @param container Reference to the container.
     */
    ExitDialog(JFrame parent, Icon icon, Container  container)
    {
        super(parent, TITLE, MESSAGE, icon);
        this.container = container;
    }
    
}
