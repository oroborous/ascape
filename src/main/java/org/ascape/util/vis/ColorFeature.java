/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;

import java.awt.Color;

import org.ascape.util.HasName;


/**
 * A Color feature returns a color based on the state of an object of known type provided to it.
 * Used to provide an appropriate color for some feature or aspect of an object.
 *
 * @author Miles Parker
 * @version 1.0.1
 * @history 1.0.1 3/10/99 renamed from ColorSource
 * @since 1.0
 */
public interface ColorFeature extends HasName {

    /**
     * Returns a color for the object as defined in implementions of this class.
     * @param object the object to get a color from.
     */
    public Color getColor(Object object);
}
