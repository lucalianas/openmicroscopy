/*
 * org.openmicroscopy.shoola.agents.util.annotator.view.AnnotatorFactory 
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
package org.openmicroscopy.shoola.agents.util.annotator.view;


//Java imports
import java.util.Set;
import javax.swing.JFrame;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.env.config.Registry;
import org.openmicroscopy.shoola.env.data.model.TimeRefObject;

import pojos.DataObject;

/** 
* Factory to create {@link Annotator} and {@link AnnotatorEditor} components.
*
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
public class AnnotatorFactory
{
	
	/** The sole instance. */
	private static final AnnotatorFactory  singleton = new AnnotatorFactory();

	/**
	 * Returns the {@link AnnotatorEditor}.
	 * @param ctx		A reference to the {@link Registry}.
	 * @param object 	The <code>DataObject</code> to annotate.
	 * @param layout	One of the following constants:
	 * 					{@link #HORIZONTAL_LAYOUT} or 
	 * 						{@link #VERTICAL_LAYOUT}.
	 * @return See above
	 */
	public static AnnotatorEditor getEditor(Registry ctx, DataObject object, 
											int layout)
	{
		if (registry == null) registry = ctx;
		return singleton.createEditor(object, layout);
	}
	
	/**
	 * Returns the {@link Annotator}.
	 * 
	 * @param parent	The owner of the dialog.
	 * @param objects 	Collection of <code>DataObject</code>s to annotate.
	 * @param ctx 		A reference to the {@link Registry}.
	 * @param type		The type of node, <code>ImageData</code>,
	 * 					<code>DatasetData</code> or <code>CategoryData</code>
	 * @return See above.
	 */
	public static Annotator getAnnotator(JFrame parent, Set objects, 
										Registry ctx, Class type)
	{
		if (registry == null) registry = ctx;
		if (parent == null) owner = parent;
		if (objects == null || objects.size() == 0) return null;
		return singleton.createAnnotator(objects, Annotator.ANNOTATE_MODE, 
										type);
	}

	/**
	 * Returns the {@link Annotator}.
	 * 
	 * @param parent	The owner of the dialog.
	 * @param ref 		The time ref object.
	 * @param ctx 		A reference to the {@link Registry}.
	 * @return See above.
	 */
	public static Annotator getAnnotator(JFrame parent, TimeRefObject ref,
										Registry ctx)
	{
		if (registry == null) registry = ctx;
		if (parent == null) owner = parent;
		if (ref == null) return null;
		return singleton.createAnnotator(ref);
	}
	
	/**
	 * Returns the {@link Annotator}.
	 * 
	 * @param parent	The owner of the dialog.
	 * @param objects 	Collection of <code>DataObject</code>s containing the
	 * 					objects to annotate.
	 * @param ctx 		A reference to the {@link Registry}.
	 * @param type		The type of node, either <code>ImageData</code> or
	 * 					either <code>DatasetData</code>
	 * @return See above.
	 */
	public static Annotator getChildrenAnnotator(JFrame parent, Set objects, 
										Registry ctx, Class type)
	{
		if (registry == null) registry = ctx;
		if (parent == null) owner = parent;
		if (objects == null || objects.size() == 0) return null;
		return singleton.createAnnotator(objects, Annotator.BULK_ANNOTATE_MODE,
										type);
	}
	
	/**
	 * Helper method. 
	 * 
	 * @return A reference to the {@link Registry}.
	 */
	public static Registry getRegistry() { return registry; }
	  
	/**
	 * Helper method. 
	 * 
	 * @return A reference to the {@link JFrame owner}.
	 */
	public static JFrame getOwner() { return owner; }
	  
	/** Reference to the registry. */
	private static Registry         registry;
	  
  /** The owner of the dialog. */
  private static JFrame			owner;
  
	/** Creates a new instance.*/
	private AnnotatorFactory() {}
	  
	/**
	 * Creates or recycles an annotator component for the specified 
	 * <code>model</code>.
	 * 
	 * @param objects	The <code>DataObject</code>s to annotate.
	 * @param mode		One of the following contants:
	 * 					{@link Annotator#BULK_ANNOTATE_MODE} or
	 * 					{@link Annotator#ANNOTATE_MODE}.
	 * @param type		The type of node, either <code>ImageData</code> or
	 * 					either <code>DatasetData</code>.
	 * @return A {@link Annotator}.
	 */
	private Annotator createAnnotator(Set objects, int mode, Class type)
	{
		AnnotatorModel model = new AnnotatorModel(objects, mode, type);
	  	AnnotatorComponent component = new AnnotatorComponent(model);
	  	model.initialize(component);
	  	component.initialize();
	    return component;
	}

	/**
	 * Creates or recycles an annotator component for the specified 
	 * <code>model</code>.
	 * 
	 * @param ref	The time ref objects.
	 * @return A {@link Annotator}.
	 */
	private Annotator createAnnotator(TimeRefObject ref)
	{
		AnnotatorModel model = new AnnotatorModel(ref);
	  	AnnotatorComponent component = new AnnotatorComponent(model);
	  	model.initialize(component);
	  	component.initialize();
	    return component;
	}
	
	/**
	 * Creates an editor for the passed object.
	 * 
	 * @param object	The object to edit, or <code>null</code> if no 
	 * 					oject yet selected.
	 * @param layout	The layout, one out of the following constants
	 * 					{@link AnnotatorEditor#HORIZONTAL_LAYOUT}
	 * 					or {@link AnnotatorEditor#VERTICAL_LAYOUT}.
	 * @return See above.
	 */
	private AnnotatorEditor createEditor(DataObject object, int layout)
	{
		AnnotatorEditorModel model = new AnnotatorEditorModel(object);
		AnnotatorEditorComponent 
			component = new AnnotatorEditorComponent(model, layout);
	  	model.initialize(component);
	  	component.initialize();
	    return component;
	}
	
}
