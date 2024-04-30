/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Bjorn Freeman-Benson - initial API and implementation
 *******************************************************************************/
package wrimsv2_plugin.editor;

import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

/**
 * WPP editor
 */
public class WPPEditor extends AbstractDecoratedTextEditor {

    /**
     * Creates a WPP editor
     */
    public WPPEditor() {
        super();
        setSourceViewerConfiguration(new WPPSourceViewerConfiguration());
        setRulerContextMenuId("wpp.editor.rulerMenu");
        setEditorContextMenuId("wpp.editor.editorMenu");
    }
}
