/*
 *------------------------------------------------------------------------------
 *  Copyright (C) 2015 University of Dundee. All rights reserved.
 *
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package omero.gateway.facility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import omero.api.IContainerPrx;
import omero.api.IUpdatePrx;
import omero.cmd.Request;
import omero.gateway.Gateway;
import omero.gateway.SecurityContext;
import omero.gateway.exception.DSAccessException;
import omero.gateway.exception.DSOutOfServiceException;
import omero.gateway.util.Requests;
import omero.model.IObject;
import omero.sys.Parameters;

//Java imports

/**
 *
 * @author Dominik Lindner &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:d.lindner@dundee.ac.uk">d.lindner@dundee.ac.uk</a>
 * @since 5.1
 */

public class DataManagerFacility extends Facility {

    private BrowseFacility browse;
    
    DataManagerFacility(Gateway gateway) throws ExecutionException {
        super(gateway);
        this.browse = gateway.getFacility(BrowseFacility.class);
    }

    /**
     * Deletes the specified object.
     *
     * @param ctx The security context.
     * @param object    The object to delete.
     * @throws DSOutOfServiceException If the connection is broken, or logged in
     * @throws DSAccessException If an error occurred while trying to
     * retrieve data from OMERO service.
     */
    void deleteObject(SecurityContext ctx, IObject object)
        throws DSOutOfServiceException, DSAccessException
    {
        deleteObjects(ctx, Collections.singletonList(object));
    }

    /**
     * Deletes the specified objects.
     *
     * @param ctx The security context.
     * @param objects                  The objects to delete.
     * @throws DSOutOfServiceException If the connection is broken, or logged in
     * @throws DSAccessException       If an error occurred while trying to
     *                                 retrieve data from OMERO service.
     */
    void deleteObjects(SecurityContext ctx, List<IObject> objects)
        throws DSOutOfServiceException, DSAccessException
    {
        try {
            /* convert the list of objects to lists of IDs by OMERO model class name */
            final Map<String, List<Long>> objectIds = new HashMap<String, List<Long>>();
            for (final IObject object : objects) {
                /* determine actual model class name for this object */
                Class<? extends IObject> objectClass = object.getClass();
                while (true) {
                    final Class<?> superclass = objectClass.getSuperclass();
                    if (IObject.class == superclass) {
                        break;
                    } else {
                        objectClass = superclass.asSubclass(IObject.class);
                    }
                }
                final String objectClassName = objectClass.getSimpleName();
                /* then add the object's ID to the list for that class name */
                final Long objectId = object.getId().getValue();
                List<Long> idsThisClass = objectIds.get(objectClassName);
                if (idsThisClass == null) {
                    idsThisClass = new ArrayList<Long>();
                    objectIds.put(objectClassName, idsThisClass);
                }
                idsThisClass.add(objectId);
            }
            /* now delete the objects */
            final Request request = Requests.delete(objectIds);
            gateway.submit(ctx, request).loop(50, 250);
        } catch (Throwable t) {
            handleException(this, t, "Cannot delete the object.");
        }
    }

    /**
     * Updates the specified object.
     *
     * @param ctx The security context.
     * @param object The object to update.
     * @param options Options to update the data.
     * @return The updated object.
     * @throws DSOutOfServiceException If the connection is broken, or logged in
     * @throws DSAccessException If an error occurred while trying to
     * retrieve data from OMERO service.
     * @see IPojos#updateDataObject(IObject, Map)
     */
    IObject saveAndReturnObject(SecurityContext ctx, IObject object,
            Map options)
        throws DSOutOfServiceException, DSAccessException
    {
        try {
            IUpdatePrx service = gateway.getUpdateService(ctx);
            if (options == null) return service.saveAndReturnObject(object);
            return service.saveAndReturnObject(object, options);
        } catch (Throwable t) {
            handleException(this, t, "Cannot update the object.");
        }
        return null;
    }

    /**
     * Updates the specified object.
     *
     * @param ctx The security context.
     * @param object The object to update.
     * @param options Options to update the data.
     * @param userName The name of the user to create the data for.
     * @return The updated object.
     * @throws DSOutOfServiceException If the connection is broken, or logged in
     * @throws DSAccessException If an error occurred while trying to
     * retrieve data from OMERO service.
     * @see IPojos#updateDataObject(IObject, Map)
     */
    IObject saveAndReturnObject(SecurityContext ctx, IObject object,
            Map options, String userName)
        throws DSOutOfServiceException, DSAccessException
    {
        try {
            IUpdatePrx service = gateway.getUpdateService(ctx, userName);

            if (options == null) return service.saveAndReturnObject(object);
            return service.saveAndReturnObject(object, options);
        } catch (Throwable t) {
            handleException(this, t, "Cannot update the object.");
        }
        return null;
    }

    /**
     * Updates the specified object.
     *
     * @param ctx The security context.
     * @param objects The objects to update.
     * @param options Options to update the data.
     * @return The updated object.
     * @throws DSOutOfServiceException If the connection is broken, or logged in
     * @throws DSAccessException If an error occurred while trying to
     * retrieve data from OMERO service.
     * @see IPojos#updateDataObject(IObject, Map)
     */
    List<IObject> saveAndReturnObject(SecurityContext ctx,
            List<IObject> objects, Map options, String userName)
        throws DSOutOfServiceException, DSAccessException
    {
        try {
            IUpdatePrx service = gateway.getUpdateService(ctx, userName);
            return service.saveAndReturnArray(objects);
        } catch (Throwable t) {
            handleException(this, t, "Cannot update the object.");
        }
        return new ArrayList<IObject>();
    }

    /**
     * Updates the specified object.
     *
     * @param ctx The security context.
     * @param object The object to update.
     * @param options Options to update the data.
     * @return The updated object.
     * @throws DSOutOfServiceException If the connection is broken, or logged in
     * @throws DSAccessException If an error occurred while trying to
     * retrieve data from OMERO service.
     * @see IPojos#updateDataObject(IObject, Map)
     */
    IObject updateObject(SecurityContext ctx, IObject object,
            Parameters options)
        throws DSOutOfServiceException, DSAccessException
    {
        try {
            IContainerPrx service = gateway.getPojosService(ctx);
            IObject r = service.updateDataObject(object, options);
            return browse.findIObject(ctx, r);
        } catch (Throwable t) {
            handleException(this, t, "Cannot update the object.");
        }
        return null;
    }

    /**
     * Updates the specified <code>IObject</code>s and returned the
     * updated <code>IObject</code>s.
     *
     * @param ctx The security context.
     * @param objects The array of objects to update.
     * @param options Options to update the data.
     * @return See above.
     * @throws DSOutOfServiceException If the connection is broken, or logged in
     * @throws DSAccessException If an error occurred while trying to
     * retrieve data from OMERO service.
     * @see IPojos#updateDataObjects(IObject[], Map)
     */
    List<IObject> updateObjects(SecurityContext ctx, List<IObject> objects,
            Parameters options)
        throws DSOutOfServiceException, DSAccessException
    {
        try {
            IContainerPrx service = gateway.getPojosService(ctx);
            List<IObject> l = service.updateDataObjects(objects, options);
            if (l == null) return l;
            Iterator<IObject> i = l.iterator();
            List<IObject> r = new ArrayList<IObject>(l.size());
            IObject io;
            while (i.hasNext()) {
                io = browse.findIObject(ctx, i.next());
                if (io != null) r.add(io);
            }
            return r;
        } catch (Throwable t) {
            handleException(this, t, "Cannot update the object.");
        }
        return new ArrayList<IObject>();
    }
}
