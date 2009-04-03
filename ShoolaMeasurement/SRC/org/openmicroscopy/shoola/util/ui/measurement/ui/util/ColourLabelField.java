/*
 * measurement.component.shapeinspector.ColourLabelField 
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
package org.openmicroscopy.shoola.util.ui.measurement.ui.util;

//Java imports
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

//Third-party libraries

//Application-internal dependencies

/** 
 * 
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 	<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author	Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * 	<a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME3.0
 */
public class ColourLabelField
	extends JLabel  
{

	private String 			colourString;
	private	Color			colour;
	
	/** Create the colouricon which will hold the colours. */
	private static ColourIcon icon = new ColourIcon(32, 24);

	/** Border colour of the cell when the icon is selected. */
	private Border lineBorder = BorderFactory.createLineBorder(Color.gray, 1);

	/** Border colour of the cell when the icon is not selected. */
	private Border emptyBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
	
	/**
	 * 	Creates a new instance.
	 * 	Sets the background to opaque.
	 */
	public ColourLabelField()
	{
		setOpaque(true);
	}

	public void setColour(Color c)
	{
		this.colour = c;
		this.colourString = "[ " + 	"r: "+c.getRed()+
									" g: "+c.getGreen()+
									" b: "+c.getBlue()+ 
									" alpha: " + c.getAlpha()+" ]";
		icon.setColour(colour);
		setIcon(icon);
		this.setVerticalAlignment(SwingConstants.CENTER);
		this.setIconTextGap(40);
		setText((String) colourString);
	}

}


