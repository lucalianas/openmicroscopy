
/*
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
 *	author Will Moore will@lifesci.dundee.ac.uk
 */

package actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import calendar.CalendarMain;

import ui.IModel;
import util.ImageFactory;

public class CalendarOpenAction extends ProtocolEditorAction {
	
	public CalendarOpenAction(IModel model) {

		super(model);
	
		putValue(Action.NAME, "Show Calendar");
		putValue(Action.SHORT_DESCRIPTION, "Display a calendar showing Date-Time events from your OMERO.editor files");
		putValue(Action.SMALL_ICON, ImageFactory.getInstance().getIcon(ImageFactory.CALENDAR_ICON)); 
	}
	
	public void actionPerformed(ActionEvent e) {
		
		model.displayCalendar();
	}
	
}
