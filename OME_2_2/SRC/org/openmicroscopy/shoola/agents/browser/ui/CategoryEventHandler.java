/*
 * org.openmicroscopy.shoola.agents.browser.ui.CategoryEventHandler
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

/*------------------------------------------------------------------------------
 *
 * Written by:    Jeff Mellen <jeffm@alum.mit.edu>
 *
 *------------------------------------------------------------------------------
 */
 
package org.openmicroscopy.shoola.agents.browser.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openmicroscopy.ds.st.Category;
import org.openmicroscopy.ds.st.CategoryGroup;
import org.openmicroscopy.ds.st.Classification;
import org.openmicroscopy.shoola.agents.browser.BrowserAgent;
import org.openmicroscopy.shoola.agents.browser.BrowserEnvironment;
import org.openmicroscopy.shoola.agents.browser.datamodel.AttributeMap;
import org.openmicroscopy.shoola.agents.browser.datamodel.CategoryTree;
import org.openmicroscopy.shoola.agents.browser.images.Thumbnail;
import org.openmicroscopy.shoola.agents.browser.images.ThumbnailDataModel;

/**
 * Handles the classification of single or multiple images.  The response
 * behavior to category selection is dependent on whether or not the
 * thumbnail already has been assigned a phenotype and whether or not single
 * or multiple thumbnails are selected at a time.  This handler has methods
 * for single/multiple thumbnails, and then is responsible for making calls
 * into the classifier, based on whether or not an image has already been
 * assigned a phenotype.
 *
 * @author Jeff Mellen, <a href="mailto:jeffm@alum.mit.edu">jeffm@alum.mit.edu</a><br>
 * <b>Internal version:</b> $Revision$ $Date$
 * @version 2.2.1
 * @since OME2.2
 */
public class CategoryEventHandler
{
    private static String CLASSIFICATION_ST_NAME = "Classification";
    
    private static BrowserEnvironment env = BrowserEnvironment.getInstance();
    private static BrowserAgent agent = env.getBrowserAgent();
    
    /**
     * Handle a single thumbnail update/create classification ST.
     * @param t
     * @param group
     * @param c
     */
    public static void handle(Thumbnail t, CategoryGroup group,
                              Category c, CategoryTree ct)
    {
        if(t == null || group == null || c == null || ct == null)
        {
            return;
        }
        ThumbnailDataModel tdm = t.getModel();
        AttributeMap map = tdm.getAttributeMap();
        List classificationList = map.getAttributes(CLASSIFICATION_ST_NAME);
        
        boolean foundOld = false;
        Classification newClassification = null;
        
        if(classificationList != null)
        {
            for(Iterator iter = classificationList.iterator();
                (iter.hasNext() && !foundOld);)
            {
                Classification cl = (Classification)iter.next();
                CategoryGroup theGroup = ct.getGroupForCategory(cl.getCategory());
                if(theGroup.equals(group))
                {
                    // BUG 117 FIX
                    if(!cl.getCategory().equals(c) ||
                       (cl.getCategory().equals(c) && cl.isValid() != null &&
                        !cl.isValid().booleanValue()))
                    {
                        foundOld = true;
                        newClassification = cl;
                        newClassification.setCategory(c);
                    }
                }
            }
        }
        
        if(!foundOld)
        {
            agent.classifyImage(tdm.getID(),c);
        }
        else
        {
            agent.reclassifyImage(newClassification);
        }
    }
    
    /**
     * Handles events for multiple thumbnails.
     * @param ts The list of thumbnails to handle.
     * @param group The category group affected.
     * @param c The category to specify.
     */
    public static void handle(Thumbnail[] ts, CategoryGroup group,
                              Category c, CategoryTree ct)
    {
        if(ts == null || group == null || c == null || ct == null)
        {
            return;
        }
        List newImageList = new ArrayList();
        List oldClassificationList = new ArrayList();
        
        for(int i=0;i<ts.length;i++)
        {
            Thumbnail t = ts[i];
            ThumbnailDataModel tdm = t.getModel();
            AttributeMap map = tdm.getAttributeMap();
            List classificationList = map.getAttributes(CLASSIFICATION_ST_NAME);
        
            boolean foundOld = false;
        
            if(classificationList != null)
            {
                for(Iterator iter = classificationList.iterator();
                    (iter.hasNext() && !foundOld);)
                {
                    Classification cl = (Classification)iter.next();
                    CategoryGroup theGroup = ct.getGroupForCategory(cl.getCategory());
                    if(theGroup.equals(group))
                    {
                        // BUG 117 FIX
                        if(!cl.getCategory().equals(c) ||
                           (cl.getCategory().equals(c) && cl.isValid() != null &&
                            !cl.isValid().booleanValue()))
                        {
                            foundOld = true;
                            cl.setCategory(c);
                            oldClassificationList.add(cl);
                        }
                    }
                }
            }
            
            if(!foundOld)
            {
                newImageList.add(new Integer(tdm.getID()));
            }
        }
        
        int[] imageIDs = new int[newImageList.size()];
        for(int i=0;i<newImageList.size();i++)
        {
            imageIDs[i] = ((Integer)newImageList.get(i)).intValue();
        }
        agent.classifyImages(imageIDs,c);
        Classification[] classifications =
            new Classification[oldClassificationList.size()];
        oldClassificationList.toArray(classifications);
        agent.reclassifyImages(classifications);
    }
    
    /**
     * Handle the declassification of an image.
     * BUG 117 FIX.
     * @param t The thumbnail to declassify.
     * @param cg The category group to declassify from.
     * @param ct The category tree which contains the category hierarchy.
     */
    public static void handleDeclassify(Thumbnail t, CategoryGroup cg,
                                        CategoryTree ct)
    {
        if(t == null || cg == null || ct == null) return;
        ThumbnailDataModel tdm = t.getModel();
        AttributeMap attrMap = tdm.getAttributeMap();
        List classificationList = attrMap.getAttributes(CLASSIFICATION_ST_NAME);
        
        if(classificationList == null) return;
        Classification invalidClassification = null;
        boolean found = false;
        for(Iterator iter = classificationList.iterator();
            (iter.hasNext() && !found);)
        {
            Classification cl = (Classification)iter.next();
            CategoryGroup theGroup = ct.getGroupForCategory(cl.getCategory());
            if(theGroup.equals(cg) &&
               (cl.isValid() == null || cl.isValid().booleanValue()))
            {
                invalidClassification = cl;
                found = true;
            }
        }
        
        if(found)
        {
            agent.declassifyImage(invalidClassification);
        }
    }
    
    public static void handleDeclassify(Thumbnail[] ts, CategoryGroup cg,
                                        CategoryTree ct)
    {
        if(ts == null || cg == null || ct == null || ts.length == 0) return;
        List invalidList = new ArrayList();
        
        for(int i=0;i<ts.length;i++)
        {
            Thumbnail t = ts[i];
            ThumbnailDataModel tdm = t.getModel();
            AttributeMap map = tdm.getAttributeMap();
            List classificationList = map.getAttributes(CLASSIFICATION_ST_NAME);
        
            boolean found = false;
        
            if(classificationList != null)
            {
                for(Iterator iter = classificationList.iterator();
                    (iter.hasNext() && !found);)
                {
                    Classification cl = (Classification)iter.next();
                    CategoryGroup theGroup = ct.getGroupForCategory(cl.getCategory());
                    if(theGroup.equals(cg) &&
                       (cl.isValid() == null || cl.isValid().booleanValue()))
                    {
                        found = true;
                        invalidList.add(cl);
                    }
                }
            }
        }
        
        Classification[] classifications =
            new Classification[invalidList.size()];
        invalidList.toArray(classifications);
        agent.declassifyImages(classifications);
    }
}
