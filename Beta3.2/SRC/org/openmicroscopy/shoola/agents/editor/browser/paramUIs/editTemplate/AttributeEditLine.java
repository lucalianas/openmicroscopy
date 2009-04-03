 /*
 * org.openmicroscopy.shoola.agents.editor.browser.paramUIs.editTemplate
 * .DefaultTextField 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2008 University of Dundee. All rights reserved.
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
package org.openmicroscopy.shoola.agents.editor.browser.paramUIs.editTemplate;

//Java imports

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

//Third-party libraries

//Application-internal dependencies

import org.openmicroscopy.shoola.agents.editor.browser.paramUIs.AbstractParamEditor;
import org.openmicroscopy.shoola.agents.editor.browser.paramUIs.ITreeEditComp;
import org.openmicroscopy.shoola.agents.editor.browser.paramUIs.TextFieldEditor;
import org.openmicroscopy.shoola.agents.editor.model.IAttributes;
import org.openmicroscopy.shoola.agents.editor.uiComponents.CustomLabel;

/** 
 * This is a UI component with a {@link TextFieldEditor}
 *  used for editing a named attribute.
 * It uses an instance of TextFieldEditor to provide a text field that 
 * fires propertyChangeEvents when edited.
 * These are forwarded by calling the attributeEdited method of the
 * DefaultTextField's superclass.
 *
 * @author  William Moore &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:will@lifesci.dundee.ac.uk">will@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME3.0
 */
public class AttributeEditLine 
	extends AbstractParamEditor
	implements PropertyChangeListener 
{
	/**
	 * This is the name of the attribute being edited by this UI
	 */
	private String 			attributeName;
	
	/**
	 * A string for the label beside the text field. e.g. "Name".
	 * This is also used for the {@link #getEditDisplayName()} to provide 
	 * an undo/redo display name. 
	 */
	private String 			labelText;
	
	/**
	 * Builds the UI. 
	 */
	private void buildUI() 
	{
		setLayout(new BorderLayout());
		
		add(new CustomLabel (labelText + ": "), BorderLayout.NORTH);
		
		// Add a text field
		JComponent textField = new TextFieldEditor(getParameter(), 
				attributeName);
		// listen for changes to the Value property, indicating that the 
		// field has been edited.
		textField.addPropertyChangeListener(ITreeEditComp.VALUE_CHANGED_PROPERTY, 
				this);
		
		add(textField, BorderLayout.CENTER);
	}
	
	/**
	 * Creates an instance, and builds the UI. 
	 * 
	 * @param param		The parameter you're editing. 
	 */
	public AttributeEditLine(IAttributes param, String attributeName, String label) 
	{	
		super(param);
		
		this.attributeName = attributeName;
		this.labelText = label;
		
		buildUI();
	}

	/**
	 * If the value property of the text field changes, 
	 * call attributeEdited(attribute, value) to notify listeners.
	 * 
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
	public void propertyChange(PropertyChangeEvent evt) 
	{	
		if (ITreeEditComp.VALUE_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
			attributeEdited(attributeName, evt.getNewValue());
		}
	}

	/**
	 * A display name for undo/redo
	 * 
	 * @return String 	see above.
	 */
	public String getEditDisplayName() {
		return "Edit " + labelText;
	}

}
