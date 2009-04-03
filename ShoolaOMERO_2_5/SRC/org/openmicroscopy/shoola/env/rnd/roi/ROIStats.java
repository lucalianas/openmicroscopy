/*
 * org.openmicroscopy.shoola.env.rnd.roi.ROIStats
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

package org.openmicroscopy.shoola.env.rnd.roi;


//Java imports
import java.util.HashMap;
import java.util.Map;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.env.rnd.metadata.PixelsDimensions;

/** 
 * Collects some basic stats for each 2D-selection within a 5D ROI.
 * <p>This class implements {@link PointIteratorObserver} in order to collect
 * {@link ROIPlaneStats} for each 2D ROI contained in a given 5D ROI.  You
 * register an instance of this class with a {@link PointIterator} and then
 * you {@link PointIterator#iterate(ROI5D) iterate} a given 5D ROI.  As the
 * iteration advances, the <code>ROIStats</code> object computes the stats.
 * Because a second {@link PointIterator#iterate(ROI5D) iteratation} over a
 * different 5D ROI would overwrite the previous stats, you should normally
 * {@link PointIterator#remove(PointIteratorObserver) remove} the 
 * <code>ROIStats</code> object from the notification list after the iteration
 * has completed.</p>
 * <p>After iterating an ROI 5D, you can access the computed results plane by
 * plane through the {@link #getPlaneStats(int, int, int) getPlaneStats}
 * method.</p>
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
public class ROIStats
    implements PointIteratorObserver
{

    /** 
     * The dimensions of the pixels set over which the stats will be computed.
     */
    private PixelsDimensions    dims;
    
    /**
     * Maps a {@link #linearize(int, int, int) linearized} <code>(z, w, t)
     * </code> tuple identifying a plane onto the stats calculated for the
     * 2D-selection contained in that plane. 
     */
    private Map                 arrayMap;
    
    
    /**
     * Transforms 3D coords into linear coords.
     * The returned value <code>L</code> is calculated as follows: 
     * <nobr><code>L = sizeZ*sizeW*t + sizeZ*w + z</code></nobr>.
     * 
     * @param z The z coord.  Must be in the range <code>[0, sizeZ)</code>.
     * @param w The w coord.  Must be in the range <code>[0, sizeW)</code>.
     * @param t The t coord.  Must be in the range <code>[0, sizeT)</code>.
     * @return The linearized value corresponding to <code>(z, w, t)</code>.
     */
    private Integer linearize(int z, int w, int t) {
        if (z < 0 || dims.sizeZ <= z) 
            throw new IllegalArgumentException(
                    "z out of range [0, "+dims.sizeZ+"): "+z+".");
        if (w < 0 || dims.sizeW <= w) 
            throw new IllegalArgumentException(
                    "w out of range [0, "+dims.sizeW+"): "+w+".");
        if (t < 0 || dims.sizeT <= t) 
            throw new IllegalArgumentException(
                    "t out of range [0, "+dims.sizeT+"): "+t+".");
        return new Integer(dims.sizeZ*dims.sizeW*t + dims.sizeZ*w + z);
    }
    
    /**
     * Creates a new object to collect {@link ROIPlaneStats} for a given
     * 5D ROI.
     * 
     * @param dims The dimensions of the pixels set over which the stats will
     *              be computed.  Mustn't be <code>null</code>.
     */
    public ROIStats(PixelsDimensions dims)
    {
        if (dims == null) throw new NullPointerException("No dims.");
        this.dims = dims;
    }
    
    //Tempo
    public int getSize() { return arrayMap.size(); }
    
    /**
     * Returns the stats, if any, that were calculated against the 2D-selection
     * within the specified plane.
     * 
     * @param z The z coord.  Must be in the range <code>[0, sizeZ)</code>.
     * @param w The w coord.  Must be in the range <code>[0, sizeW)</code>.
     * @param t The t coord.  Must be in the range <code>[0, sizeT)</code>.
     * @return A {@link ROIPlaneStats} object holding the stats for the
     *          2D-selection in the specified plane.  If no selection was
     *          made in that plane, then <code>null</code> is returned instead.
     */
    public ROIPlaneStats getPlaneStats(int z, int w, int t)
    {
        Integer index = linearize(z, w, t);
        return (ROIPlaneStats) arrayMap.get(index);
    }
    
    /**
     * Creates a new map to store the {@link ROIPlaneStats} entries that are
     * about to be calculated.
     * @see PointIteratorObserver#iterationStarted()
     */
    public void iterationStarted() 
    {
        arrayMap = new HashMap();
    }

    /**
     * Creates a new {@link ROIPlaneStats} entry for the plane selection that
     * is about to be iterated.
     * @see PointIteratorObserver#onStartPlane(int, int, int, int)
     */
    public void onStartPlane(int z, int w, int t, int pointsCount)
    {
        ROIPlaneStats planeStats = new ROIPlaneStats();
        Integer index = linearize(z, w, t);
        arrayMap.put(index, planeStats);
    }

    /**
     * Updates the min, max, and sum values of the current {@link ROIPlaneStats}
     * entry as needed.
     * @see PointIteratorObserver#update(double, int, int, int)
     */
    public void update(double pixelValue, int z, int w, int t)
    {
        Integer index = linearize(z, w, t);
        ROIPlaneStats planeStats = (ROIPlaneStats) arrayMap.get(index);
        //planeStats can't be null, see onStartPlane().
        if (pixelValue < planeStats.min) planeStats.min = pixelValue;
        if (planeStats.max < pixelValue) planeStats.max = pixelValue;
        planeStats.sum += pixelValue;
        planeStats.sumOfSquares += pixelValue*pixelValue;
    }

    /** 
     * Calculates the mean and standard deviation for the current 
     * {@link ROIPlaneStats} entry.
     * @see PointIteratorObserver#onEndPlane(int, int, int, int)
     */
    public void onEndPlane(int z, int w, int t, int pointsCount)
    {
        Integer index = linearize(z, w, t);
        ROIPlaneStats ps = (ROIPlaneStats) arrayMap.get(index);
        //planeStats can't be null, see onStartPlane().
        if (0 < pointsCount) {
            ps.mean = ps.sum/pointsCount;
            ps.pointsCount = pointsCount;
            if (0 < pointsCount-1) {
                double sigmaSquare = 
                 (ps.sumOfSquares - ps.sum*ps.sum/pointsCount)/(pointsCount-1);
                if (sigmaSquare > 0)
                    ps.standardDeviation = Math.sqrt(sigmaSquare);
            }
                
        }
    }

    /**
     * No-op implementation.
     * Required by the observer interface, but not needed in our case.
     * @see PointIteratorObserver#iterationFinished()
     */
    public void iterationFinished() {}

}
