 /*
 * org.openmicroscopy.shoola.agents.chainbuilder.piccolo.ChainView
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

package org.openmicroscopy.shoola.agents.chainbuilder.piccolo;

//Java imports
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

//Third-party libraries
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PNodeFilter;
import edu.umd.cs.piccolo.PLayer;

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.chainbuilder.data.ChainFormalInputData;
import org.openmicroscopy.shoola.agents.chainbuilder.data.ChainFormalOutputData;
import org.openmicroscopy.shoola.agents.chainbuilder.data.ChainModuleData;
import org.openmicroscopy.shoola.agents.chainbuilder.data.layout.LayoutChainData;
import org.openmicroscopy.shoola.agents.chainbuilder.data.layout.LayoutLinkData;
import org.openmicroscopy.shoola.agents.chainbuilder.data.layout.LayoutNodeData;
import org.openmicroscopy.shoola.env.data.model.AnalysisNodeData;
import org.openmicroscopy.shoola.util.ui.Constants;
import org.openmicroscopy.shoola.util.ui.graphlayout.DummyNode;
import org.openmicroscopy.shoola.util.ui.graphlayout.GraphLayoutNode;
import org.openmicroscopy.shoola.util.ui.graphlayout.Layering;
import org.openmicroscopy.shoola.util.ui.piccolo.BufferedObject;
import org.openmicroscopy.shoola.util.ui.piccolo.GenericEventHandler;
import org.openmicroscopy.shoola.util.ui.piccolo.ModuleView;
import org.openmicroscopy.shoola.util.ui.piccolo.MouseableNode;


/** 
 * A class for the rendering of a {@link LayoutChainData} from OME. Note that this 
 * is not a node in itself - it is simply a convenience class that holds 
 * the logic for rendering the components of a chain
 * 
 * @author  Harry Hochheiser &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:hsh@nih.gov">hsh@nih.gov</a>
 *
 * @version 2.2
 * <small>
 * (<b>Internal version:</b> $Revision$ $Date$)
 * </small>
 */
public class ChainView extends PNode implements BufferedObject, MouseableNode, 
	Comparable, ToolTipNode {

	/**
	 * The Chain to be rendered
	 */
	private LayoutChainData chain;
	/*
	 * The Height of the chain
	 */
	private float chainHeight = 0;
	
	/** 
	 * The number of nodes in the layer with the most nodes.
	 */
	private int maxLayerSize = 0;
	
	/**
	 * The {@link Layering} describing the layout for this chain
	 */
	private Layering layering;
	
	/**
	 * Horizontal and verical gaps between layers and nodes (respectively)
	 */
	private static float HGAP=50f;
	private static float VGAP=25f;
	
	/**
	 * A vertical offset of curve components, chosen to improve aesthetics
	 */
	private static float CURVE_OFFSET=25f;
	
	/**
	 * Some parameters of the stateo f the display
	 */
	private float x=0;  /// was HGAP
	private float y = 0;
	private float top=0;
	
	/**
	 *A record of which nodes are in which layers
	 */
	private NodeLayers nodeLayers;
	
	protected LinkLayer linkLayer;

	
	/**
	 * 
	 * @param chain		the chain to be drawn
	 * @param pickable  can the links be picked?
	 */
	public ChainView(LayoutChainData chain){
		
		this.chain = chain;
		this.layering = chain.getLayering();
	}

	protected void drawChain() {
		linkLayer = createLinkLayer();
		addChild(linkLayer);
		
		drawNodes();
		layoutNodes();
		drawLinks(linkLayer);	
		//clear it out so it can be garbage-collected
		nodeLayers = null;
		linkLayer.moveToFront();
		setBounds(getUnionOfChildrenBounds(null));
		setBounds(getUnionOfChildrenBounds(null));
		
	}
	
	protected LinkLayer createLinkLayer() {
		return new LinkLayer();
	}
	
	public LinkLayer getLinkLayer() {
		return linkLayer;
	}
		
	public PBounds getBufferedBounds() {
		PBounds b = getGlobalFullBounds();
		return new PBounds(b.getX()-Constants.BORDER,
			b.getY()-Constants.BORDER,
			b.getWidth()+2*Constants.BORDER,
			b.getHeight()+2*Constants.BORDER);
	}
	
	
	public LayoutChainData getChain() {
		return chain;
	}
	
	
	/**
	 * To draw the nodes, start at the highest numbered layer and continue 
	 * until we hit layer zero. Keep track of the height of thhe tallest layer
	 * The nodes in each layer will go into the {@link NodeLayers} object,
	 * which will contain the highest # layer first, etc.
	 * @param connection
	 */
	private void drawNodes() {
		
		int layers = layering.getLayerCount(); 
		nodeLayers = new NodeLayers(layers);
		
		for (int i=layers-1; i >=0; i--) {
			Collection v = drawLayer(i);
			nodeLayers.addLayer(v,i);
			float height = getLayerHeight(v);
			if (height > chainHeight)
				chainHeight=height;
		}
	}
	
	/**
	 * To draw a layer, draw each of the nodes 
	 * @param connection
	 * @param layerNumber
	 * @return a list containing the nodes in the layer
	 */
	private Collection drawLayer(int layerNumber) {
		
		Vector v = new Vector();
		int layerSize=layering.getLayerSize(layerNumber);
		
		if (layerSize > maxLayerSize) 
			maxLayerSize = layerSize;
		
		for (int i =0; i < layerSize; i++) {
			GraphLayoutNode node = layering.getNode(layerNumber,i);
			//somehow draw it, and advance x as need be.
			Object obj = drawNode(node);
			v.add(obj);
		} 
		
		return v;
	}
	
	/**
	 * Draw a node. If the node is a dummy layout node (instance of {@link
	 * CLayoutNode}, make it a {@link LayoutModule}, but don't add it to
	 * the scenegraph. Otherwise, create a {@link ModuleView}, and add it. In 
	 * any case, return it so it gets included in the list of nodes for the 
	 * layer
	 * 
	 * @param node
	 * @param layer
	 * @return
	 */
	private Object drawNode(GraphLayoutNode node) {
		
		ModuleView mNode = null;
		if (node instanceof DummyNode)  {
			mNode = new LayoutModule();
		}
		else { // must be a real LayoutNodeData
			//ChainModuleData mod = (ChainModuleData) ((LayoutNodeData) node).getModule();
			LayoutNodeData layoutNode = (LayoutNodeData) node;
			ChainModuleData mod = (ChainModuleData) layoutNode.getModule();
			mNode = getModuleView(layoutNode);
			mod.addModuleNode(mNode);
			mNode.showDetails();
			// must show detail and overview of each node for links to be drawn
			// correctly
			addChild(mNode);
		}
		node.setModuleView(mNode);
		return mNode;
	}
	
	
	/*
	 * Get all of the modules 
	 */
	public Collection getModuleViews() {
		PNodeFilter filter = new PNodeFilter() {
			public boolean accept(PNode aNode) {
				return ((aNode instanceof ModuleView) &&
						!(aNode instanceof LayoutModule));
			}
			public boolean acceptChildrenOf(PNode aNode) {
				return true;
			}
		};
		return getAllNodes(filter,null);
	}
	
	protected ModuleView getModuleView(AnalysisNodeData node) {
		return new SingleModuleView((ChainModuleData) node.getModule());
	}
	
	/**
	 * To layout the nodes, start with the nodes in the first layer.
	 * Lay them out at the appropriate horizonal coordinate, move to the right
	 * by the width of the layer, and continue. Note the horizontal mid-point
	 * of each layer.
	 *
	 */
	private void layoutNodes() {
		int layerCount = layering.getLayerCount();
		for (int i = layerCount-1; i >= 0; i--) {
			Collection v=  nodeLayers.getLayer(i);
			// set the x position for the current layer
			float origX = x;
			
			double layerWidth= layoutLayer(v);
			x += layerWidth+HGAP;
			float mid = (origX+x)/2;
			nodeLayers.setXPosition(i,mid);
		}
	}
	
	/**
	 * To layout a layer, find the difference between the height of the layer
	 * and the height of the tallest layer. Lay the nodes in the layer out in 
	 * a manner that divides that difference into spaces betwen the nodes. 
	 * 
	 * Track the width of the widest module in the layer. This is where 
	 * it's useful to have {@link LayoutModule} as a subclass of {@link
	 * ModuleView} - no special case-handling is required here.
	 * 
	 * @param v
	 */
	private double layoutLayer(Collection v) {
		int size = v.size();
		double layerWidth = 0;
		// iterate out to find height
		float height = getLayerHeight(v);
		float remainder = chainHeight - height;
		float delta = remainder/(size+1);
		Iterator iter = v.iterator();
		y = top+VGAP;
		while (iter.hasNext()) {
			y +=delta;
			
			ModuleView mod = (ModuleView) iter.next();
			mod.setOffset(x,y);
			y+= (float) mod.getHeight()+VGAP;
			if (mod.getWidth() > layerWidth)
				layerWidth = (float) mod.getBounds().getWidth();
		}
		return layerWidth;
	}
	
	/**
	 * The height of a layer is just the sum of all of the modules in that
	 * layer, plus some spacing between each.
	 * @param v a layer
	 * @return the height of the layer
	 */
	private float getLayerHeight(Collection v) {
		float total = 0;
		Iterator iter = v.iterator();
		ModuleView mod;
		while (iter.hasNext()) {
			mod = (ModuleView) iter.next();
			total += (float) mod.getHeight()+VGAP;
		}
		return total;
	}
	
	/**
	 * Draw the links in the chain 
	 * 
	 * @param linkLayer the {@link PLayer} to add the links to
	 */
	public void drawLinks(LinkLayer linkLayer) {
		List links = chain.getLinks();
		Iterator iter = links.iterator();
		while (iter.hasNext()) {
			LayoutLinkData link = (LayoutLinkData) iter.next();
			drawLink(link,linkLayer);
		}
	}
	
	
	/**
	 * To draw a link, we find the {@link LayoutNodeData}s for the endpoints, 
	 * the {@llink ModuleView} for those nodes, and then the correspponding
	 * {@link PFormalParameter} instances.
	 * 
	 * We then create instances of {@link PParmLink} as needed, create the 
	 * module link between the two layers, and then adjust
	 * links that connect nodes in non-adjacent layers.
	 *  
	 * @param link the link to draw
	 * @param linkLayer the parent node
	 */
	private void drawLink(LayoutLinkData link,LinkLayer linkLayer) {
		LayoutNodeData from = (LayoutNodeData) link.getFromNode();
		LayoutNodeData to = (LayoutNodeData) link.getToNode();

		ModuleView fromPMod = from.getModuleView();
		ModuleView toPMod = to.getModuleView();
			
		if (fromPMod == null || toPMod ==null)  {
			return;
		}
			
		ChainFormalInputData input = (ChainFormalInputData)link.getToInput();
		ChainFormalOutputData output = (ChainFormalOutputData) 
			link.getFromOutput();
		
		FormalInput inputPNode = toPMod.getFormalInputNode(input);
		FormalOutput outputPNode = fromPMod.getFormalOutputNode(output);
		
		
		if (inputPNode != null && outputPNode != null) {
			ParamLink newLinkNode = getParamLink(inputPNode,outputPNode);
			linkLayer.addChild(newLinkNode);
			// create the module link between the two modules
			ModuleLink modLink = getModuleLink(linkLayer,newLinkNode);
			if (from.getLayer() > (to.getLayer()+1)) 
				adjustLink(link,from,to,newLinkNode,modLink);
		} 
	}
	
	protected ParamLink getParamLink(FormalInput inputPNode,
			FormalOutput outputPNode) {
		return new ParamLink(inputPNode,outputPNode);
	}
	
	protected ModuleLink getModuleLink(LinkLayer linkLayer,ParamLink newLinkNode) {
		return linkLayer.completeLink(newLinkNode);
	}
	
	/**
	 * To adjust a link, add points for every layer between the layer of the 
	 * source and the layer of the destination. Thus, if the source is in layer 
	 * n, and the destination is in p, we adjust the link at n-1, n-2..p+1
	 * @param LayoutLinkData
	 * @param from
	 * @param to
	 * @param link
	 * @param modLink
	 */
	private void adjustLink(LayoutLinkData LayoutLinkData,LayoutNodeData from,LayoutNodeData to,ParamLink link,
			ModuleLink modLink) {
		// remember, layer numbers go down as we get to leaves
		// j is the index of where in the PLink the new point is added.
		// the first new point goes at j=1, then j=2, etc
	
		for (int i = from.getLayer()-1, j = 1; i > to.getLayer();i--,j++) {
			adjustLink(LayoutLinkData,link,modLink,i,j);
		}
	}
	
	/**  
	 * Adjust a specific point on the links
	 * @param LayoutLinkData
	 * @param from
	 * @param to
	 * @param link
	 * @param modLink
	 * @param i i is the position in the layering (n...0)
	 * @param j index of the new point to be added. (1 for the first point,
	 * 	then 2, etc.
	 */
	private void adjustLink(LayoutLinkData layoutLinkData,ParamLink link, ModuleLink modLink,int i, int j) {
		
		
		//find appropriate node
		//System.err.println("...adjusting link at position "+j);
		GraphLayoutNode node = layoutLinkData.getIntermediateNode(j);
		ModuleView mod = node.getModuleView();
	
		float xpos = nodeLayers.getXPosition(i);//?
		
		// find y coordinate.
		float ypos = (float) mod.getY()+CURVE_OFFSET;
		// insert x,y into link somehow.
		link.insertIntermediatePoint(j,xpos,ypos);
		if (modLink != null)
			modLink.insertIntermediatePoint(j,xpos,ypos);
	}
	
	
	
	public void mouseClicked(GenericEventHandler handler,PInputEvent e) {
		((ModuleNodeEventHandler) handler).animateToNode(this);
		mouseEntered(handler,e);
	}

	public void mouseDoubleClicked(GenericEventHandler handler,PInputEvent e) {
	}

	public void mouseEntered(GenericEventHandler handler,PInputEvent e) {
		((ModuleNodeEventHandler) handler).setLastEntered(this);
	} 
	public void mouseExited(GenericEventHandler handler,PInputEvent e) {
	}
	
	public void mousePopup(GenericEventHandler handler,PInputEvent e) {
		mouseClicked(handler,e);
	}
	
	
	
	public double getArea() {
		double area = getWidth()*getHeight();
		return area;
	}
	
	public int compareTo(Object o) {
		if (!(o instanceof ChainView))
			return -1;
		double areaDiff;
		areaDiff = getArea() - ((ChainView) o).getArea();
		return (int) areaDiff;
	}
	
	public PNode getToolTip() {
		return null;
	}
	
	/**
	 * A convenience class that tracks the nodes in each layer,
	 * and the x position of each layer
	 * @author Harry Hochheiser
	 * @version 2.1
	 * @since 	OME2.1	
	 */
	class NodeLayers {
		private Collection[] nodeLayers;
		private float[] layerXPositions;
		
		public NodeLayers(int size) {
			nodeLayers = new Collection[size];
			layerXPositions = new float[size];
		}
		
		public void addLayer(Collection v,int i) {
			nodeLayers[i]=v;
		}
		
		public Collection getLayer(int i) {
			return nodeLayers[i];
		}
		
		public void setXPosition(int i,float mid) {
			layerXPositions[i]=mid;
		}
		
		public float getXPosition(int i) {
			return layerXPositions[i];
		}
	}
	
	
}

