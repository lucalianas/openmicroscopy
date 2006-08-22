/*
 * org.openmicroscopy.shoola.env.rnd.events.RenderImages3D
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

package org.openmicroscopy.shoola.env.rnd.events;



//Java imports

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.env.event.RequestEvent;
import org.openmicroscopy.shoola.env.rnd.defs.PlaneDef;

/** 
 * 
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
public class RenderImage3D
	extends RequestEvent
{

	/** The ID of the pixels set. */
	private int			pixelsID;
		
	private PlaneDef	XYPlaneDef, XZPlaneDef, ZYPlaneDef;
	
	public RenderImage3D(int pixelsID, PlaneDef XYPlaneDef, PlaneDef XZPlaneDef,
						PlaneDef ZYPlaneDef)
	{
		if (XZPlaneDef == null || ZYPlaneDef == null)
			throw new NullPointerException("No plane definition.");
		this.XYPlaneDef = XYPlaneDef;
		this.XZPlaneDef = XZPlaneDef;
		this.ZYPlaneDef = ZYPlaneDef;
		this.pixelsID = pixelsID;
	}

	/** Return the ID of the pixels set. */
	public int getPixelsID() { return pixelsID; }


	public PlaneDef getXYPlaneDef() { return XYPlaneDef; }
	
	public PlaneDef getXZPlaneDef() { return XZPlaneDef; }
	
	public PlaneDef getZYPlaneDef() { return ZYPlaneDef; }
	
}
