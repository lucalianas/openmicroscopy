/*
 * org.openmicroscopy.shoola.agents.hiviewer.cmd.SaveThumbnailsCmd
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

package org.openmicroscopy.shoola.agents.hiviewer.cmd;

//Java imports
import java.util.Set;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.hiviewer.browser.Browser;
import org.openmicroscopy.shoola.agents.hiviewer.browser.ImageDisplay;
import org.openmicroscopy.shoola.agents.hiviewer.browser.ImageDisplayVisitor;
import org.openmicroscopy.shoola.agents.hiviewer.browser.ImageFinder;
import org.openmicroscopy.shoola.agents.hiviewer.layout.LayoutFactory;
import org.openmicroscopy.shoola.agents.hiviewer.view.HiViewer;

/** 
 * Command to saves the thumbnails contained in a dataset or category.
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
public class SaveThumbnailsCmd
    implements ActionCmd
{

    /** Reference to the model. */
    private HiViewer                model;
    
    /**
     * Creates a new command to save the thumbnails contained in a 
     * Dataset/Category.
     * If the node is not a node containing thumbnails, no action is taken.
     * 
     * @param model Reference to the model. Mustn't be <code>null</code>.
     */
    public SaveThumbnailsCmd(HiViewer model) 
    { 
        if (model == null) throw new IllegalArgumentException("No model.");
        this.model = model;
    }
    
    /** Implemented as specified by {@link ActionCmd}. */
    public void execute()
    {
        Browser browser = model.getBrowser();
        if (browser == null) return;
        //if layout is flat
        Set thumbnails = null;
        if (browser.getSelectedLayout() == LayoutFactory.FLAT_LAYOUT) {
        	thumbnails = browser.getImageNodes();
        } else {
        	ImageDisplay selectedDisplay = browser.getLastSelectedDisplay();
        	if (selectedDisplay.containsImages()) {
        		ImageFinder finder = new ImageFinder();
        		selectedDisplay.accept(finder, 
        								ImageDisplayVisitor.IMAGE_NODE_ONLY);
        		thumbnails = finder.getImageNodes();
            }
        }
        if (thumbnails ==  null) return;
        model.saveThumbnails(thumbnails);
    }

}
