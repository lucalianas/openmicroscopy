/*
 * org.openmicroscopy.shoola.agents.metadata.actions.ManageRndSettingsAction
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2010 University of Dundee. All rights reserved.
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
package org.openmicroscopy.shoola.agents.metadata.actions;


//Java imports
import java.awt.event.ActionEvent;

import javax.swing.Action;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.metadata.IconManager;
import org.openmicroscopy.shoola.agents.metadata.rnd.Renderer;
import org.openmicroscopy.shoola.util.ui.UIUtilities;

/** 
 * Handles the rendering settings.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 3.0-Beta4
 */
public class ManageRndSettingsAction 
	extends RndAction
{

	/** Indicates to set the minimum and maximum for all channels. */
	public static final int MIN_MAX = 0;
	
	/** Indicates to reset the rendering settings. */
	public static final int RESET = 1;
	
	/** Indicates to undo the changes. */
	public static final int UNDO = 2;
	
	/** Indicates to apply the settings to all selected images. */
	public static final int APPLY_TO_ALL = 3;
	
	/** Indicates to the settings of the owner. */
	public static final int SET_OWNER_SETTING = 4;
	
	/** The description of the action if {@link #MIN_MAX}. */
	private static final String DESCRIPTION_MIN_MAX = 
		"Set the Pixels Intensity interval to min/max for all channels.";
	
	/** The description of the action if {@link #UNDO}. */
	private static final String DESCRIPTION_UNDO = "Undo the changes.";
	
	/** The description of the action if {@link #RESET}. */
	private static final String DESCRIPTION_RESET = 
		"Reset the rendering settings created while importing.";
	
	/** The description of the action if {@link #APPLY_TO_ALL}. */
	private static final String DESCRIPTION_APPLY_TO_ALL = 
		"Apply the rendering settings to all images.";
	
    /** 
     * The description of the action if the index is {@link #SET_OWNER_SETTING}. 
     */
    private static final String DESCRIPTION_SET_OWNER_SETTING  = 
    									"Set the Owner's rendering settings.";
    
	/** One of the constants defined by this class. */
	private int index;
	
	/**
	 * Checks the passed value.
	 * 
	 * @param value The value to handle.
	 */
	private void checkIndex(int value)
	{
		IconManager icons = IconManager.getInstance();
		switch (value) {
			case MIN_MAX:
				setEnabled(model.getPixelsDimensionsC() < 
						Renderer.MAX_CHANNELS);
				putValue(Action.SHORT_DESCRIPTION, 
						UIUtilities.formatToolTipText(DESCRIPTION_MIN_MAX));
				putValue(Action.SMALL_ICON, 
						icons.getIcon(IconManager.RND_MIN_MAX));
				break;
			case RESET:
				putValue(Action.SHORT_DESCRIPTION, 
						UIUtilities.formatToolTipText(DESCRIPTION_RESET));
				putValue(Action.SMALL_ICON, 
						icons.getIcon(IconManager.RND_REDO));
				break;
			case UNDO:
				putValue(Action.SHORT_DESCRIPTION, 
						UIUtilities.formatToolTipText(DESCRIPTION_UNDO));
				putValue(Action.SMALL_ICON, 
						icons.getIcon(IconManager.RND_UNDO));
				break;
			case APPLY_TO_ALL:
				putValue(Action.SHORT_DESCRIPTION, 
						UIUtilities.formatToolTipText(
								DESCRIPTION_APPLY_TO_ALL));
				putValue(Action.SMALL_ICON, 
						icons.getIcon(IconManager.RND_APPLY_TO_ALL));
				break;
			case SET_OWNER_SETTING:
				putValue(Action.SHORT_DESCRIPTION, 
						UIUtilities.formatToolTipText(
								DESCRIPTION_SET_OWNER_SETTING));
				putValue(Action.SMALL_ICON, 
						icons.getIcon(IconManager.RND_OWNER));
				break;
			default:
				throw new IllegalArgumentException("Index not valid.");
		}
	}
	
	/**
	 * Creates a new instance.
	 * 
	 * @param model Reference to the model. Mustn't be <code>null</code>.
	 * @param index One of the constants defined by this class.
	 */
	public ManageRndSettingsAction(Renderer model, int index)
	{
		super(model);
		setEnabled(true);
		checkIndex(index);
		this.index = index;
	}
	
	/**
	 * Modifies the rendering settings according of the index
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		switch (index) {
			case MIN_MAX:
				model.setRangeAllChannels();
				break;
			case RESET:
				
				break;
			case UNDO:
				model.resetSettings(model.getInitialRndSettings(), true);
				break;
			case APPLY_TO_ALL:
				model.applyToAll();
				break;
			case SET_OWNER_SETTING:
				break;
		}
	}
	
}
