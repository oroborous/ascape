package org.ascape.runtime.swing.navigator;

import java.awt.event.ActionListener;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 * A Tree Selection Listener that will call the actionPerformed method on the
 * currently selected node if that node implements the ActionListener interface.
 * 
 * @author Oliver Mannion
 * @version $Revision: 299 $
 */
public class PeformActionOnSelectedNode implements TreeSelectionListener {

	public void valueChanged(TreeSelectionEvent e) {

		// Get the path to the selection.
		TreePath selPath = e.getNewLeadSelectionPath();

		if (selPath != null) {
			// Get the selected node.
			Object node = selPath.getLastPathComponent();

			// if node exists and node is an ActionListener, execute the
			// actionPerformed method
			if (node instanceof ActionListener) {
				((ActionListener) node).actionPerformed(null);
			}
		}
	}
}
