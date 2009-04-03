/*
 * org.openmicroscopy.shoola.agents.viewer.util.ImageSaver
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

package org.openmicroscopy.shoola.agents.viewer.util;

//Java imports
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.UIManager;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.viewer.IconManager;
import org.openmicroscopy.shoola.agents.viewer.ViewerCtrl;
import org.openmicroscopy.shoola.util.ui.TitlePanel;
import org.openmicroscopy.shoola.util.ui.UIUtilities;

/** 
 * Save the current image.
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
public class ImageSaver
	extends JDialog
{
	
	static final String			MESSAGE = "A file with the same name and " +
										"extension already exists in " +
										"this directory. Do you " +
										"still want to save the image?";
	
	static final String			TITLE = "Save Image";
	
	static final String 		SUMMARY = "Save the currrent image as a " +
											"tiff, png or jpeg.";
											
	/** Reference to the {@link ViewerCtrl controller}. */
	private ViewerCtrl		controller;

	private ImageChooser	chooser;
	
	private BufferedImage	imageToSave;
	
	public ImageSaver(ViewerCtrl controller, BufferedImage imageToSave)
	{
		super(controller.getReferenceFrame(), "Save image As", true);
		this.controller = controller;
		this.imageToSave = imageToSave;
		buildGUI();
		pack();
		UIUtilities.centerAndShow(this);
	}

	void isDisplay(boolean b) { chooser.isDisplay(b); }
	
	ViewerCtrl getController() { return controller; }
	
	BufferedImage getImageToSave() { return imageToSave; }
	
	/** Build and layout the GUI. */
	private void buildGUI()
	{
		IconManager im = IconManager.getInstance(controller.getRegistry());
		getContentPane().setLayout(new BorderLayout(0, 0));
		TitlePanel tp = new TitlePanel(TITLE, SUMMARY, 
								im.getIcon(IconManager.SAVEAS_BIG));
		chooser = new ImageChooser(this);			
		getContentPane().add(tp, BorderLayout.NORTH);
		getContentPane().add(chooser, BorderLayout.CENTER);
		if (JDialog.isDefaultLookAndFeelDecorated()) {
			boolean supportsWindowDecorations = 
			UIManager.getLookAndFeel().getSupportsWindowDecorations();
			if (supportsWindowDecorations)
				getRootPane().setWindowDecorationStyle(
							JRootPane.FILE_CHOOSER_DIALOG);
		}
	}
		
}
