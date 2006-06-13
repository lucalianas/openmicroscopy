/*
 * ome.services.query.CollectionCountQueryDefinition
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2005 Open Microscopy Environment
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
 * Written by:    Josh Moore <josh.moore@gmx.de>
 *
 *------------------------------------------------------------------------------
 */

package ome.services.query;

//Java imports
import java.sql.SQLException;
import java.util.Collection;

// Third-party libraries
import org.hibernate.HibernateException;
import org.hibernate.Session;

// Application-internal dependencies
import ome.parameters.Parameters;
import ome.tools.lsid.LsidUtils;

/**
 * counts the number of members in a collection. This query is used 
 * by the {@link ome.api.IPojos IPojos} interface (possbly among others) to
 * add information to outgoing results.  
 * 
 * @author Josh Moore, <a href="mailto:josh.moore@gmx.de">josh.moore@gmx.de</a>
 * @version 1.0 <small> (<b>Internal version:</b> $Rev$ $Date$) </small>
 * @since OMERO 3.0
 * @see Details#getCounts()
 */
public class CollectionCountQueryDefinition extends Query
{

    static Definitions defs = new Definitions(
            new IdsQueryParameterDef(),
            new QueryParameterDef("field", String.class, false)
            );
    
    public CollectionCountQueryDefinition(Parameters parameters)
    {
        super( defs, parameters );
    }

    @Override
    protected void buildQuery(Session session)
            throws HibernateException, SQLException
    {
        String s_field = (String) value("field"); // TODO Generics??? if not
        // in arrays!
        String s_target = LsidUtils.parseType(s_field);
        String s_collection = LsidUtils.parseField(s_field);
        String s_query = String.format(
                "select target.id, count(collection) from %s target "
                        + "join target.%s collection "
                        + ( check("ids") ? "where target.id in (:ids)" : "" )
                        + "group by target.id",
                        s_target, s_collection);

        org.hibernate.Query q = session.createQuery(s_query);
        if (check("ids")){
            q.setParameterList("ids",(Collection) value("ids"));
        }
        setQuery( q );

    }

    // TODO filters...?
}
