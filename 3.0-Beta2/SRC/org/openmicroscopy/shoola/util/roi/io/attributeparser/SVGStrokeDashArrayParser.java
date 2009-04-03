/*
 * org.openmicroscopy.shoola.util.roi.io.attributeparser.SVGStrokeDashArrayParser 
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
package org.openmicroscopy.shoola.util.roi.io.attributeparser;

import static org.jhotdraw.draw.AttributeKeys.STROKE_DASHES;

import java.io.IOException;

import net.n3.nanoxml.IXMLElement;

import org.openmicroscopy.shoola.util.roi.figures.ROIFigure;

//Java imports

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
public 	class SVGStrokeDashArrayParser
		implements SVGAttributeParser
{

	/* (non-Javadoc)
	 * @see org.openmicroscopy.shoola.util.roi.io.attributeparser.SVGAttributeParser#parse(org.openmicroscopy.shoola.util.roi.figures.ROIFigure, net.n3.nanoxml.IXMLElement, java.lang.String)
	 */
	public void parse(ROIFigure figure, IXMLElement element, String value) 
	{
		if (! value.equals("none")) 
		{
			String[] values = toCommaSeparatedArray(value);
            double[] dashes = new double[values.length];
            for (int i=0; i < values.length; i++) {
                dashes[i] = new Double(values[i]);
            }
            STROKE_DASHES.set(figure, dashes);
        }	
	}
	
	public static String[] toCommaSeparatedArray(String str) 
	{
	        return str.split("\\s*,\\s*");
	}

}


