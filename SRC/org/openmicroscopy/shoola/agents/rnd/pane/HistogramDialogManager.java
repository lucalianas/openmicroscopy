/*
 * org.openmicroscopy.shoola.agents.rnd.pane.HistogramDialogManager
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

package org.openmicroscopy.shoola.agents.rnd.pane;


//Java imports
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

//Third-party libraries

//Application-internal dependencies

/** 
 * 
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
class HistogramDialogManager
	implements MouseListener, MouseMotionListener
{
	
	/** Graphics constants. */
	private static final int		heightStat = HistogramPanel.heightStat,
									widthStat = HistogramPanel.widthStat, 
									topBorder = HistogramPanel.topBorder,
									leftBorder = HistogramPanel.leftBorder,
									rightBorder = HistogramPanel.rightBorder,
									lS = leftBorder+widthStat, 
									tS = topBorder+heightStat,
									triangleW = HistogramPanel.triangleW,
									length = 2*triangleW, 
									window = HistogramPanel.window,
									rangeGraphics = heightStat-2*window;
   	private static final int    	absEnd = topBorder+window, 
   									absStart = tS-window;
   	
   	private final int				tW = topBorder+window;
   	
   	private int                     maxStartInputY, minEndInputY;
   	
   	/** Rectangle used to listen to the knobs. */
   	private Rectangle               boxInputStart, boxInputEnd;
   	
   	/** Control mouse pressed and dragged events. */
	private boolean					dragging;
	
	private int						curRealValue;
	
	/** Controls to determine which knob has been selected. */
	private boolean					inputStartKnob, inputEndKnob;
	
	/** Reference to the view. */
	private HistogramDialog			view;
	
	/** Reference to the main manager {@link QuantumPaneManager}. */
	private QuantumPaneManager 		control;
	
	HistogramDialogManager(HistogramDialog view, QuantumPaneManager control)
	{
		this.view = view;
		this.control = control;
		inputStartKnob = false;
		inputEndKnob = false;
	}
	
	/** 
	 * Initialize the rectangles which control the cursors.
	 * 
	 * @param yStart	graphical input start value.
	 * @param yEnd		graphical input end value.
	 */
	void initRectangles(int yStart, int yEnd)
	{
		//Size the rectangle used to control the OutputWindow knobs
		boxInputStart = new Rectangle(lS, yStart-2*triangleW, rightBorder, 
										2*length);
		boxInputEnd = new Rectangle(lS, yEnd-2*triangleW, rightBorder, 
									2*length);
		maxStartInputY = yStart-triangleW;
		minEndInputY = yEnd+triangleW; 
		
	}
	
	/** Attach the listeners. */
	void attachListeners()
	{
		view.getHistogramPanel().addMouseListener(this);
		view.getHistogramPanel().addMouseMotionListener(this);
	}
	
	/**
	 * Convert a real value into a graphical one.
	 * 
	 * @param x	real value
	 * @return graphics coordinate.
	 */
	int convertRealIntoGraphics(int x) 
	{
		int b = control.getGlobalMinimum();
		int c = control.getGlobalMaximum();
		double a = (double) rangeGraphics/(b-c);
		return (int) (a*(x-c)+tW);
	}
	
	/** 
	 * Converts a graphics value into a real value. 
	 *
	 * @param x     graphics coordinate.
	 */    
	int convertGraphicsIntoReal(int x)
	{
		int b = control.getGlobalMaximum(); 
		int c = control.getGlobalMinimum();
		int y = x-tW;
		double a =  (c-b)/ (double) rangeGraphics;
		//b/c of the way values are computed.
		int r = (int) (a*y+b);
		if (r < c) r = c;
		if (r > b) r = b;
		return r;
	}
	
	/**
	 * Resize the input window.
	 * 
	 * @param v		real input window value
	 */
	void setInputWindowStart(int v)
	{
		int gv = convertRealIntoGraphics(v);
		setInputStartBox(gv);
		view.getHistogramPanel().updateInputStart(gv, v);
	}
	
	/**
	 * Resize the input window.
	 * 
	 * @param v		real input window value
	 */
	void setInputWindowEnd(int v)
	{
		int gv = convertRealIntoGraphics(v);
		setInputEndBox(gv);
		view.getHistogramPanel().updateInputEnd(gv, v);
	} 
	
	/** Handles events fired the graphics knobs. */
	public void mousePressed(MouseEvent e)
	{
		Point p = e.getPoint();
		if (!dragging) {
			dragging = true;
			if (boxInputStart.contains(p) && p.y >= minEndInputY &&
				p.y <= absStart) {
				inputStartKnob = true;
				curRealValue = convertGraphicsIntoReal(p.y);
				control.setInputWindowStart(curRealValue);
			} 
			if (boxInputEnd.contains(p) && p.y <= maxStartInputY &&
				p.y >= absEnd) {
				inputEndKnob = true;
				curRealValue = convertGraphicsIntoReal(p.y);
				control.setInputWindowEnd(curRealValue);	
			}
			 
		 }  //else dragging already in progress 
	}
	
	/** Handles events fired the graphics knobs. */    
	public void mouseDragged(MouseEvent e)
	{
		Point p = e.getPoint();
	   	if (dragging) {  
			if (boxInputStart.contains(p) && p.y >= minEndInputY &&
				p.y <= absStart) {
				curRealValue = convertGraphicsIntoReal(p.y);
				control.setInputWindowStart(curRealValue);	
			}
		   	if (boxInputEnd.contains(p) && p.y <= maxStartInputY &&
			   	p.y >= absEnd) {
				curRealValue = convertGraphicsIntoReal(p.y);
				control.setInputWindowEnd(curRealValue);
			}
		}
	}
	
	/** Resets the dragging control to false. */      
	public void mouseReleased(MouseEvent e)
	{ 
		if (inputStartKnob) control.setChannelWindowStart(curRealValue);
		else if (inputEndKnob) control.setChannelWindowEnd(curRealValue);
		dragging = false; 
		inputStartKnob = false;
		inputEndKnob = false;
	}

	/** 
	 * Resizes the outputStart rectangle.
	 *
	 * @param y     y-coordinate.
	 */    
    void setInputStartBox(int y)
    {
        maxStartInputY = y-2*triangleW;
        boxInputStart.setBounds(lS, y-2*triangleW, rightBorder, 2*length);
    }  
      
	/** 
	 * Resize the inputEnd rectangle.
	 *
	 * @param y     y-coordinate.
	 */ 
    void setInputEndBox(int y)
    {
        minEndInputY = y+2*triangleW;
        boxInputEnd.setBounds(lS, y-2*triangleW,  rightBorder, 2*length);
    }

	/**
	 * Required by I/F but not actually needed in our case, 
	 * no op implementation.
	 */	
	public void mouseClicked(MouseEvent e) {}

	/**
	 * Required by I/F but not actually needed in our case, 
	 * no op implementation.
	 */
	public void mouseEntered(MouseEvent e) {}

	/**
	 * Required by I/F but not actually needed in our case, 
	 * no op implementation.
	 */ 
	public void mouseExited(MouseEvent e) {}

	/**
	 * Required by I/F but not actually needed in our case, 
	 * no op implementation.
	 */
	public void mouseMoved(MouseEvent e) {}

}
