/*
 * org.openmicroscopy.shoola.agents.hiviewer.ImageToCategoriesLoader 
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
package org.openmicroscopy.shoola.agents.hiviewer;


//Java imports
import java.util.Set;


//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.hiviewer.view.HiViewer;
import org.openmicroscopy.shoola.env.data.events.DSCallFeedbackEvent;
import org.openmicroscopy.shoola.env.data.views.CallHandle;

/** 
 * Loads asynchronously a collection of categories containing the specified
 * images.
 * This class calls the <code>loadImages</code> method in the
 * <code>HierarchyBrowsingView</code>.
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
public class ImageToCategoriesLoader
	extends DataLoader
{

    /** Collection of images' id to retrieve. */
    private Set         imagesID;
    
    /** 
     * Set to <code>false</code> if we retrieve the data for the first time,
     * set to <code>true</code> otherwise.
     */
    private boolean		refresh;
    
    /** Handle to the async call so that we can cancel it. */
    private CallHandle  handle;
    
    /**
     * Creates a new instance.
     * 
     * @param viewer    The viewer this data loader is for.
     *                  Mustn't be <code>null</code>.
     * @param imagesID  The collection of images' id.
     * @param refresh	Pass <code>false</code> if we retrieve the data for
     * 					the first time, <code>true</code> otherwise.
     */
    public ImageToCategoriesLoader(HiViewer viewer, Set imagesID, 
    								boolean refresh)
    {
        super(viewer);
        this.imagesID = imagesID;
        this.refresh = refresh;
    }
    
    /**
     * Retrieves the categories.
     * @see DataLoader#load()
     */
    public void load()
    {
    	handle = dhView.findCategoryPaths(imagesID, true, 
        							viewer.getExperimenterID(), this);
    }
    
    /** 
     * Cancels the data loading.
     * @see DataLoader#cancel()
     */
    public void cancel() { handle.cancel(); }
    
    /**
     * Notifies the viewer of progress. 
     * @see DataLoader#update(DSCallFeedbackEvent)
     */
    public void update(DSCallFeedbackEvent fe) 
    {
        String status = fe.getStatus();
        int percDone = fe.getPercentDone();
        if (status == null) 
            status = (percDone == 100) ? HiViewer.PAINTING_TEXT :  //Else
                                       ""; //Description wasn't available.   
        if (percDone != 100) //We've only got one call and don't know how long
            percDone = -1;   //it'll take.  Set to indeterminate.
        viewer.setStatus(status, percDone);
    }
    
    /**
     * Feeds the result back to the viewer.
     * @see DataLoader#handleResult(Object)
     */
    public void handleResult(Object result)
    {
        if (viewer.getState() == HiViewer.DISCARDED) return;  //Async cancel.
        viewer.setHierarchyRoots((Set) result, false, refresh);
    }
    
}
