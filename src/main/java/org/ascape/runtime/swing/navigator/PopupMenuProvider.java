package org.ascape.runtime.swing.navigator;

import javax.swing.JPopupMenu;

/**
 * Provides a {@link JPopupMenu}.
 * 
 * @author Oliver Mannion
 * @version $Revision: 302 $
 */
public interface PopupMenuProvider {

	/**
	 * Provides a {@link JPopupMenu}.
	 *  
	 * @return popup menu
	 */
	JPopupMenu getPopupMenu();
}
