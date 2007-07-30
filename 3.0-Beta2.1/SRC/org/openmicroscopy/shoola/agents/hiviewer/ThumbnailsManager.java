/*
 * org.openmicroscopy.shoola.agents.hiviewer.ThumbnailsManager
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

package org.openmicroscopy.shoola.agents.hiviewer;


//Java imports
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.hiviewer.browser.ImageNode;
import org.openmicroscopy.shoola.agents.hiviewer.browser.Thumbnail;

import pojos.ImageData;

/** 
 * Manages the process of assigning thumbnails to {@link ImageNode}s in a
 * visualization tree.
 * This class encapsulates the strategy used to share thumbnail pixels.
 * A given <i>OME</i> Image can be contained in more than one hierarchy
 * container &#151; like a Dataset or Category, in which case it would
 * end up being contained in more than one {@link ImageNode} in the
 * visualization tree &#151; this is because {@link ImageNode}s can't be
 * shared among <code>ImageSet</code>s.  Even if we can have more than one
 * {@link ImageNode} to represent the same Image in a visualization tree,
 * usually just one thumbnail is required to display all the nodes.  This
 * class enforces the sharing strategy. 
 *
 * @see org.openmicroscopy.shoola.agents.hiviewer.view.HiViewer
 * @see org.openmicroscopy.shoola.agents.hiviewer.browser.Browser
 * @see org.openmicroscopy.shoola.agents.hiviewer.browser.ImageDisplay
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
public class ThumbnailsManager
{
    
    /** How many different Images we have. */
    private int     			totalIDs;
    
    /** Ids of the images whose thumbnails have already been set. */
    private Set<Long>     		processedIDs;
    
    /** 
     * Maps an Image id onto all the {@link ThumbnailProvider}s in the
     * visualization tree that have to provide a thumbnail for that Image.
     * Note that {@link ThumbnailProvider}s are not shared, so there's one
     * for each {@link ImageNode} that represents the given Image. 
     */
    private Map<Long, Set>     	thumbProviders;

    /**
     * Creates a new instance.
     * 
     * @param imageNodes All the {@link ImageNode}s in a given visualization
     *                   tree.  Mustn't be <code>null</code>.
     */
    public ThumbnailsManager(Set imageNodes)
    {
        if (imageNodes == null) 
            throw new NullPointerException("No image nodes.");
        totalIDs = 0;
        processedIDs = new HashSet<Long>();
        thumbProviders = new HashMap<Long, Set>();
        Iterator i = imageNodes.iterator();
        ImageNode node;
        ImageData is;
        Long id;
        Set<Thumbnail> providers;
        while (i.hasNext()) {
            node = (ImageNode) i.next();
            is = (ImageData) node.getHierarchyObject();
            id = new Long(is.getId());
            providers = thumbProviders.get(id);
            if (providers == null) {
                totalIDs++;
                providers = new HashSet<Thumbnail>();
                thumbProviders.put(id, providers);
            }
            providers.add(node.getThumbnail());
        }
    }
    
    /**
     * Sets the specified pixels to be the thumbnail for the specified Image.
     * 
     * @param imageID The id of the Image.
     * @param thumb   The thumbnail pixels. Mustn't be <code>null</code>.
     */
    public void setThumbnail(long imageID, BufferedImage thumb)
    {
        if (thumb == null) throw new NullPointerException("No thumbnail.");
        Long id = new Long(imageID);
        Set providers = thumbProviders.get(id);
        if (providers != null) {
            Iterator p = providers.iterator();
            while (p.hasNext())
                ((ThumbnailProvider) p.next()).setFullScaleThumb(thumb);
            processedIDs.add(id);
        }
    }
    
    /**
     * Tells if every {@link ImageNode} in the visualization tree has been
     * assigned a thumbnail.
     * 
     * @return <code>true</code> for yes, <code>false</code> for no.
     */
    public boolean isDone() { return (processedIDs.size() == totalIDs); }
    
}
