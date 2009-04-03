/*
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2007 University of Dundee. All rights reserved.
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

package tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.swing.JPanel;
import javax.swing.undo.AbstractUndoableEdit;


public class DataFieldNode 
	implements INode {
	
	DataField dataField;
	
	private boolean highlighted = false; 	// is this node/dataField selected (displayed blue)
	
	Tree tree;		// class that manages tree structure (takes click commands)
	ArrayList<DataFieldNode> children;
	DataFieldNode parent;
	
	// constructor 
	public DataFieldNode(HashMap<String, String> allAttributesMap, DataFieldNode parent, Tree tree) {
		this.parent = parent;
		this.tree = tree;
		children = new ArrayList<DataFieldNode>();
		dataField = new DataField(allAttributesMap, this);
	}
	
	// this constructor used for root node (no parent)
	public DataFieldNode(HashMap<String, String> allAttributesMap,  ITreeModel tree) {
		this.parent = null;
		if (tree instanceof Tree)
		this.tree = (Tree)tree;
		children = new ArrayList<DataFieldNode>();
		dataField = new DataField(allAttributesMap, this);
	}
	// this constructor used for blank root node (no parent)
	public DataFieldNode( Tree tree) {
		children = new ArrayList<DataFieldNode>();
		dataField = new DataField(this);
		this.parent = null;
		this.tree = tree;
	}
	
	// constructor to make a copy of existing Node
	// retuns duplicate node with no parent
	public DataFieldNode(DataFieldNode copyThisNode,  Tree tree) {
		
		children = new ArrayList<DataFieldNode>();
		this.tree = tree;
	
		dataField = new DataField(copyThisNode.getDataField(), this);
	}
	
	// constructor to make a copy of existing Node
	// retuns duplicate node with no parent
	public DataFieldNode(DataFieldNode copyThisNode) {
		
		children = new ArrayList<DataFieldNode>();
		// get ref to tree from parent (when setParent is called)
	
		dataField = new DataField(copyThisNode.getDataField(), this);
	}
	
	public int getMyIndexWithinSiblings() {
		if (parent == null)
			throw (new NullPointerException("Can't getMyIndexWithinSiblings because parent == null"));
			
		return parent.indexOfChild(this);
	}
	
	/**
	 * When adding a new node to a tree, it needs a reference to it's parent, to 
	 * allow complete tree traversal. This method sets the parent of a node. 
	 * 
	 * A node should also have a reference to the Tree that contains it.
	 * @see tree
	 * In order that new nodes can be added to existing nodes to build a 
	 * tree, without first needing a reference to tree...
	 * This method updates the child node with a reference to the Tree class 
	 * via the existing node (parent) in the tree (which already should 
	 * have a reference to tree). 
	 * 
	 * @param parent
	 */
	public void setParent(INode parent) {
		if (parent instanceof DataFieldNode) {
			this.parent = (DataFieldNode)parent;
			if (tree == null) tree = ((DataFieldNode)parent).getTree();
		}
	}
	
	// iterator code, from http://www.cs.bc.edu/~sciore/courses/cs353/coverage.html  chapter 23
	public Iterator<DataFieldNode> childIterator() {
		return children.iterator();
	}
	public Iterator<DataFieldNode> iterator() {
		return new TreeIterator(this);
	}
	
	/**
	 * Add a child to the end of the child list. 
	 * Also, the child's parent is set as (this). 
	 * 
	 * @param dataFieldNode
	 */
	public void addChild(INode dataFieldNode) {
		if (dataFieldNode instanceof DataFieldNode) {
			children.add((DataFieldNode)dataFieldNode);
		}
		dataFieldNode.setParent(this);
	}
	
	
	public void addChild(int index, DataFieldNode dataFieldNode) {
		children.add(index, dataFieldNode);
	}
	public void removeChild(DataFieldNode child) {
		children.remove(child);
	}
	public void removeChild(int index){
		children.remove(index);
	}
	public int indexOfChild(DataFieldNode child) {
		return children.indexOf(child);
	}
	public DataFieldNode getChild(int index) {
		return children.get(index);
	}

	public DataField getDataField() {
		return dataField;
	}
	public JPanel getFieldEditor() {
		return dataField.getFieldEditor();
	}
	public JPanel getFormField() {
		return dataField.getFormField();
	}
	public String getName() {
		if (dataField == null) return "No field";
		return dataField.getName();
	}
	public DataFieldNode getParentNode() {
		return parent;
	}
	
	public ArrayList<DataFieldNode> getChildren() {
		return children;
	}
	
	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
		dataField.setHighlighted(highlighted);
	}
	public boolean isHighlighted() {
		return highlighted;
	}
	public void nodeClicked(boolean clearOthers) {
		getTree().nodeSelected(this, clearOthers);
	}
	// let the tree know that a dataField has changed - and pass reference for undo() action
	public void dataFieldUpdated(AbstractUndoableEdit  undoDataFieldAction) {
		getTree().dataFieldUpdated(undoDataFieldAction);
	}
	// notification that UI needs updating. eg. due to dataField inputType change
	public void xmlUpdated() {
		getTree().xmlUpdated();
	}
	
	// expandAllAncestors() 	used to show a field that may be hidden
	public void expandAllAncestors() {
		// recursively call expandAllAncestors up the tree.
		if (parent != null) {
			parent.expandAllAncestors();
		}
		// Then call collpaseChildren.
		// This will be called for root first, then children, and the tree is build via
		// lazy loading, down to the child that originated this call. 
		// Lazy loading: A parent builds a childBox for each child. 
		// Child cannot showChildren() and populate its childBox before it's parent does!
		dataField.collapseChildren(false);
	}
	
	public Tree getTree() {
		if ((tree == null) && (parent != null)) tree = parent.getTree();
		return tree;
	}
}
