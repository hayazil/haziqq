/*
 * Copyright (c) 2010, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.ui.editors.sql.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.jkiss.dbeaver.core.DBeaverCore;
import org.jkiss.dbeaver.ui.editors.sql.SQLConstants;
import org.jkiss.dbeaver.ui.editors.sql.SQLPreferenceConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * SQLAnnotationHover
 */
public class SQLAnnotationHover extends AbstractSQLEditorTextHover
    implements ITextHover, IAnnotationHover, ITextHoverExtension, ITextHoverExtension2
{
    static final Log log = LogFactory.getLog(SQLAnnotationHover.class);

    private List<Annotation> annotations = new ArrayList<Annotation>();
    private IEditorPart editor;

    public SQLAnnotationHover(IEditorPart editor)
    {
        setEditor(editor);
    }

    /**
     * Returns the information which should be presented when a hover popup is shown for the specified hover region. The
     * hover region has the same semantics as the region returned by <code>getHoverRegion</code>. If the returned
     * information is <code>null</code> or empty no hover popup will be shown.
     *
     * @deprecated
     * @param textViewer  the viewer on which the hover popup should be shown
     * @param hoverRegion the text range in the viewer which is used to determine the hover display information
     * @return the hover popup display information
     */
    public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion)
    {
        Object hoverInfo2 = getHoverInfo2(textViewer, hoverRegion);
        return hoverInfo2 == null ? null : hoverInfo2.toString();
    }

    public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion)
    {
        IAnnotationModel model;
        model = textViewer instanceof ISourceViewer ? ((ISourceViewer) textViewer).getAnnotationModel() : null;
        //avoids finding annotations again
        if (annotations.size() == 0) {
            findAnnotations(hoverRegion.getOffset(), model, null, 0);
        }

        return getHoverInfo();
    }

    /**
     * Returns the text region which should serve as the source of information to compute the hover popup display
     * information. The popup has been requested for the given offset.
     * <p/>
     * For example, if hover information can be provided on a per method basis in a source viewer, the offset should be
     * used to find the enclosing method and the source range of the method should be returned.
     *
     * @param textViewer the viewer on which the hover popup should be shown
     * @param offset     the offset for which the hover request has been issued
     * @return the hover region used to compute the hover display information
     */
    public IRegion getHoverRegion(ITextViewer textViewer, int offset)
    {
        IDocument document = textViewer.getDocument();
        int start;
        int end;
        int lineNumber = 0;
        try {
            lineNumber = document.getLineOfOffset(offset);
        }
        catch (BadLocationException e) {
            log.error(e); //$NON-NLS-1$
        }

        findAnnotations(offset, textViewer instanceof ISourceViewer ? ((ISourceViewer) textViewer).getAnnotationModel()
            : null, textViewer.getDocument(), lineNumber);
        for (Annotation annotation : annotations) {
            if (annotation instanceof MarkerAnnotation) {
                MarkerAnnotation markerAnnotation = (MarkerAnnotation) annotation;
                try {
                    start = ((Integer) markerAnnotation.getMarker().getAttribute(IMarker.CHAR_START));
                    end = ((Integer) markerAnnotation.getMarker().getAttribute(IMarker.CHAR_END));
                    if (start <= offset && end >= offset) {
                        return new Region(offset, 0);
                    }
                }
                catch (CoreException e1) {
                    log.error(e1);
                }
            }
        }

        return null;
    }

    /**
     * Returns the text which should be presented in the a hover popup window. This information is requested based on
     * the specified line number.
     *
     * @param sourceViewer the source viewer this hover is registered with
     * @param lineNumber   the line number for which information is requested
     * @return the requested information or <code>null</code> if no such information exists
     */
    public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber)
    {
        if (annotations.size() == 0) {
            findAnnotations(-1, sourceViewer.getAnnotationModel(), sourceViewer.getDocument(), lineNumber);
        }

        return getHoverInfo();
    }

    /**
     * Finds annotations either by offset or by lineNumber
     */
    private void findAnnotations(int offset, IAnnotationModel model, IDocument document, int lineNumber)
    {
        annotations.clear();
        if (model == null) {
            if (editor instanceof ITextEditor) {
                ITextEditor editor = (ITextEditor) this.editor;
                model = editor.getDocumentProvider().getAnnotationModel(editor.getEditorInput());
            }
        }
        if (model == null) {
            return;
        }
        for (Iterator<?> it = model.getAnnotationIterator(); it.hasNext();) {
            Annotation annotation = (Annotation) it.next();
            Position position = model.getPosition(annotation);

            //if position is null, just return.
            if (position == null) {
                return;
            }
            try {
                if (position.overlapsWith(offset, 1) || document != null
                    && document.getLineOfOffset(position.offset) == lineNumber) {
                    annotations.add(annotation);
                }
            }
            catch (BadLocationException e) {
                log.error(e);
            }
        }
    }

    private String getHoverInfo()
    {
        String text = null;
        IPreferenceStore store = DBeaverCore.getInstance().getGlobalPreferenceStore();
        for (Annotation annotation : annotations) {
            if (annotation instanceof MarkerAnnotation) {
                try {
                    IMarker marker = ((MarkerAnnotation) annotation).getMarker();
                    if (marker.getType().equals(SQLConstants.SYNTAX_MARKER_TYPE)
                        || marker.getType().equals(SQLConstants.PORTABILITY_MARKER_TYPE)) {
                        if (store.getBoolean(SQLPreferenceConstants.SHOW_SYNTAX_ERROR_DETAIL)) {
                            text = (String) marker.getAttribute(IMarker.MESSAGE);
                        } else {
                            text = (String) marker.getAttribute(SQLConstants.SHORT_MESSAGE);
                        }
                        //TODO: consider combine multiple annotations
                        break;
                    }

                }
                catch (CoreException e) {
                    log.error(e);
                }
            }
        }
        annotations.clear();
        return text;
    }

    public void setEditor(IEditorPart editor)
    {
        this.editor = editor;
    }

}