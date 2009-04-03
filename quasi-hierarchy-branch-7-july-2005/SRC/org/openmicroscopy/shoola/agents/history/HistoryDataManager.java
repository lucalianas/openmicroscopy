/*
 * org.openmicroscopy.shoola.agents.chainbuilder.HistoryDataManager
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 Open Microscopy Environment
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
 * Written by:    Harry Hochheiser <hsh@nih.gov>
 *
 *------------------------------------------------------------------------------
 */
 
package org.openmicroscopy.shoola.agents.history;


//Java imports
import java.util.Collection;
import java.util.List;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.chainbuilder.ChainDataManager;
import org.openmicroscopy.shoola.agents.history.data.HistoryModuleData;
import org.openmicroscopy.shoola.env.config.Registry;
import org.openmicroscopy.shoola.env.data.DSAccessException;
import org.openmicroscopy.shoola.env.data.DSOutOfServiceException;
import org.openmicroscopy.shoola.env.data.DataManagementService;
import org.openmicroscopy.shoola.env.data.events.ServiceActivationRequest;
import org.openmicroscopy.shoola.env.data.model.ActualInputData;
import org.openmicroscopy.shoola.env.data.model.FormalInputData;
import org.openmicroscopy.shoola.env.data.model.FormalOutputData;
import org.openmicroscopy.shoola.env.data.model.ModuleExecutionData;
import org.openmicroscopy.shoola.env.data.model.SemanticTypeData;

/**
 * A utility class for managing communications with registry and 
 * retrieving data for histories
 *  
 * @author  Harry Hochheiser &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:hsh@nih.gov">hsh@nih.gov</a>
 *
 * @version 2.2
 * <small>
 * (<b>Internal version:</b> $Revision$ $Date$)
 * </smalbl>
 * @since OME2.2
 */

public class HistoryDataManager extends ChainDataManager {

	
	public HistoryDataManager(Registry registry) {
		super(registry);
	}
		
	public Registry getRegistry() {return registry; }
	
	// get modules of the right type\
	public Collection getModules() {
		return getModules(new HistoryModuleData());
	}
	
	public List getChainExecutionHistory(int chexid) {
		try {
			ModuleExecutionData mexProto = new ModuleExecutionData();
			HistoryModuleData modProto = new HistoryModuleData();
			ActualInputData inProto = new ActualInputData();
			FormalInputData finProto = new FormalInputData();
			FormalOutputData foutProto = new FormalOutputData();
			SemanticTypeData stProto = new SemanticTypeData();
			DataManagementService dms = registry.getDataManagementService();
			long start = System.currentTimeMillis();
			List result = 
				dms.getChainExecutionHistory(chexid,mexProto,modProto,inProto,
						finProto,foutProto,stProto);
			long elapsed = System.currentTimeMillis()-start;
			System.err.println("Elapsed time for execution history..."+elapsed);
			return result;
		} catch(DSAccessException dsae) {
			String s = "Can't retrieve user's modules.";
			registry.getLogger().error(this, s+" Error: "+dsae);
			registry.getUserNotifier().notifyError("Data Retrieval Failure",
													s, dsae);	
		} catch(DSOutOfServiceException dsose) {
			ServiceActivationRequest 
			request = new ServiceActivationRequest(
								ServiceActivationRequest.DATA_SERVICES);
			registry.getEventBus().post(request);
		}
		return null;
	}
}
