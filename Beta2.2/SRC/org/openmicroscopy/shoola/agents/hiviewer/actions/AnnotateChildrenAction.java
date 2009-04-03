/*
 * org.openmicroscopy.shoola.agents.hiviewer.actions.AnnotateChildrenAction 
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
package org.openmicroscopy.shoola.agents.hiviewer.actions;


//Java imports
import java.awt.event.ActionEvent;
import javax.swing.Action;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.hiviewer.IconManager;
import org.openmicroscopy.shoola.agents.hiviewer.browser.Browser;
import org.openmicroscopy.shoola.agents.hiviewer.browser.ImageDisplay;
import org.openmicroscopy.shoola.agents.hiviewer.cmd.AnnotateCmd;
import org.openmicroscopy.shoola.agents.hiviewer.view.HiViewer;
import org.openmicroscopy.shoola.util.ui.UIUtilities;
import pojos.CategoryData;
import pojos.DatasetData;

/** 
 * Brings the window to annotate the images contained in 
 * the selected container.
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
public class AnnotateChildrenAction 
	extends HiViewerAction
{

	/** The name of the action. */
    private static final String NAME = "Annotate Images";
    
    /** The description of the action. */
    private static final String DESCRIPTION= "Annotate the images " +
    		"contained in the selected folder.";
    
    /**
     * Sets the action enabled depending on the currently selected display
     * @see HiViewerAction#onDisplayChange(ImageDisplay)
     */
    protected void onDisplayChange(ImageDisplay selectedDisplay)
    {
        if (selectedDisplay == null) {
            setEnabled(false);
            return;
        }
        if (selectedDisplay.getParentDisplay() == null) setEnabled(false);
        else {
        	Object ho = selectedDisplay.getHierarchyObject();
        	setEnabled(((ho instanceof CategoryData) || 
                (ho instanceof DatasetData)));
        }
    }
    
	/**
     * Creates a new instance.
     * 
     * @param model Reference to the Model. Mustn't be <code>null</code>.
     */
    public AnnotateChildrenAction(HiViewer model)
    {
        super(model);
        putValue(Action.NAME, NAME);
        putValue(Action.SHORT_DESCRIPTION, 
                UIUtilities.formatToolTipText(DESCRIPTION));
        IconManager im = IconManager.getInstance();
        putValue(Action.SMALL_ICON, im.getIcon(IconManager.ANNOTATE));
    }
    
    /** 
     * Creates a {@link AnnotateCmd} command to execute the action. 
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
    	Browser browser = model.getBrowser();
    	if (browser == null) return;
    	model.annotateChildren(browser.getLastSelectedDisplay());
    }
    
}
