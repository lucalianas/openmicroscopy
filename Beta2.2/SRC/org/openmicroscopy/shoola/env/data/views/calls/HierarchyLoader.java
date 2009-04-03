/*
 * org.openmicroscopy.shoola.env.data.views.calls.HierarchyLoader
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

package org.openmicroscopy.shoola.env.data.views.calls;


//Java imports
import java.util.HashSet;
import java.util.Set;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.env.data.OmeroDataService;
import org.openmicroscopy.shoola.env.data.views.BatchCall;
import org.openmicroscopy.shoola.env.data.views.BatchCallTree;
import pojos.CategoryData;
import pojos.CategoryGroupData;
import pojos.DatasetData;
import pojos.ProjectData;

/** 
 * Command to load a data hierarchy rooted by a given node.
 * <p>The root node can be one out of: Project, Dataset, Category Group, or
 * Category.  Image and Dataset items are retrieved with annotations.
 * The final <code>DSCallOutcomeEvent</code> will contain the requested node
 * as root and all of its descendants.</p>
 * <p>A Project tree will be represented by <code>ProjectData, 
 * DatasetData, and ImageData</code> objects. A Dataset tree
 * will only have objects of the latter two types.</p>
 * <p>A Category Group tree will be represented by <code>CategoryGroupData, 
 * CategoryData</code>, and <code>ImageData</code> objects. A Category 
 * tree will only have objects of the latter two types.</p>
 * <p>So the object returned in the <code>DSCallOutcomeEvent</code> will be
 * a <code>ProjectData, DatasetData, CategoryGroupData</code> or
 * <code>CategoryData</code> depending on whether you asked for a Project, 
 * Dataset, Category Group, or Category tree.</p>
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
public class HierarchyLoader
    extends BatchCallTree
{
    
    /** The Id of the user. */
    private long        userID;
    
    /** The root nodes of the found trees. */
    private Set         rootNodes;
    
    /** Loads the specified tree. */
    private BatchCall   loadCall;
    
    /**
     * Creates {@link BatchCall} if the type is supported.
     * 
     * @param rootNodeType  The type of the root node. Can only be one out of:
     *                      {@link ProjectData}, {@link DatasetData},
     *                      {@link CategoryGroupData}, {@link CategoryData}.
     * @param rootNodeIDs   Collection of root node ids.
     */
    private void validate(Class rootNodeType, Set<Long> rootNodeIDs)
    {
        if (rootNodeType == null) 
            throw new IllegalArgumentException("No root node type.");
        if (rootNodeIDs == null && rootNodeIDs.size() == 0)
            throw new IllegalArgumentException("No root node ids.");
        /*
        try {
            rootNodeIDs.toArray(new Long[] {});
        } catch (ArrayStoreException ase) {
            throw new IllegalArgumentException("rootNodeIDs only contain " +
                                                "Long.");
        }  
        */
        if (rootNodeType.equals(ProjectData.class) ||
            rootNodeType.equals(DatasetData.class) ||
            rootNodeType.equals(CategoryGroupData.class) ||
            rootNodeType.equals(CategoryData.class))
            loadCall = makeBatchCall(rootNodeType, rootNodeIDs);
        else
            throw new IllegalArgumentException("Unsupported type: "+
                                                rootNodeType);
    }
    
    /**
     * Creates a {@link BatchCall} to retrieve a Container tree, either
     * Project, Dataset, CategoryGroup or Category.
     * 
     * @param rootNodeType The type of the root node.
     * @param rootNodeIDs Collection of container's id. 
     * @return The {@link BatchCall}.
     */
    private BatchCall makeBatchCall(final Class rootNodeType, 
                                    final Set rootNodeIDs)
    {
        return new BatchCall("Loading container tree: ") {
            public void doCall() throws Exception
            {
                OmeroDataService os = context.getDataService();
                rootNodes = os.loadContainerHierarchy(rootNodeType,
                                                    rootNodeIDs, true, userID);
            }
        };
    }
    
    /**
     * Adds the {@link #loadCall} to the computation tree.
     * 
     * @see BatchCallTree#buildTree()
     */
    protected void buildTree() { add(loadCall); }
    
    /**
     * Returns the root node of the requested tree.
     * 
     * @see BatchCallTree#getResult()
     */
    protected Object getResult() { return rootNodes; }
    
    /**
     * Creates a new instance to load a tree rooted by the object having the
     * specified type and id.
     * If bad arguments are passed, we throw a runtime exception so to fail
     * early and in the caller's thread.
     * 
     * @param rootNodeType  The type of the root node. Can only be one out of:
     *                      {@link ProjectData}, {@link DatasetData},
     *                      {@link CategoryGroupData} or {@link CategoryData}.
     * @param rootNodeID    The id of the root node.
     * @param userID   		The id of the user.
     */
    public HierarchyLoader(Class rootNodeType, long rootNodeID, long userID)
    {
        this.userID = userID;
        HashSet<Long> set = new HashSet<Long>(1);
        set.add(new Long(rootNodeID));
        validate(rootNodeType, set);
    }
    
    /**
     * Creates a new instance to load a tree rooted by the object having the
     * specified type and id.
     * If bad arguments are passed, we throw a runtime exception so to fail
     * early and in the caller's thread.
     * 
     * @param rootNodeType  The type of the root node. Can only be one out of:
     *                      {@link ProjectData}, {@link DatasetData},
     *                      {@link CategoryGroupData}, {@link CategoryData}.
     * @param rootNodeIDs   The ids of the root nodes.
     * @param userID   		The id of the user.
     */
    public HierarchyLoader(Class rootNodeType, Set<Long> rootNodeIDs, 
    						long userID)
    {
    	this.userID = userID;
        validate(rootNodeType, rootNodeIDs);
    }
    
}
