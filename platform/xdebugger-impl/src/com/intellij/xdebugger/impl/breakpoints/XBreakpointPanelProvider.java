/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.xdebugger.impl.breakpoints;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.breakpoints.*;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.breakpoints.ui.BreakpointItem;
import com.intellij.xdebugger.impl.breakpoints.ui.AbstractBreakpointPanel;
import com.intellij.xdebugger.impl.breakpoints.ui.BreakpointPanelProvider;
import com.intellij.xdebugger.impl.breakpoints.ui.XBreakpointsPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nik
 */
public class XBreakpointPanelProvider extends BreakpointPanelProvider<XBreakpoint> {

  public int getPriority() {
    return 0;
  }

  @Nullable
  public XBreakpoint<?> findBreakpoint(@NotNull final Project project, @NotNull final Document document, final int offset) {
    XBreakpointManager breakpointManager = XDebuggerManager.getInstance(project).getBreakpointManager();
    int line = document.getLineNumber(offset);
    VirtualFile file = FileDocumentManager.getInstance().getFile(document);
    if (file == null) {
      return null;
    }
    for (XLineBreakpointType<?> type : XDebuggerUtil.getInstance().getLineBreakpointTypes()) {
      XLineBreakpoint<? extends XBreakpointProperties> breakpoint = breakpointManager.findBreakpointAtLine(type, file, line);
      if (breakpoint != null) {
        return breakpoint;
      }
    }

    return null;
  }

  @Override
  public GutterIconRenderer getBreakpointGutterIconRenderer(Object breakpoint) {
    if (breakpoint instanceof XLineBreakpointImpl) {
      RangeHighlighter highlighter = ((XLineBreakpointImpl)breakpoint).getHighlighter();
      if (highlighter != null) {
        return highlighter.getGutterIconRenderer();
      }
    }
    return null;
  }

  @NotNull
  public Collection<AbstractBreakpointPanel<XBreakpoint>> getBreakpointPanels(@NotNull final Project project, @NotNull final DialogWrapper parentDialog) {
    XBreakpointType<?,?>[] types = XBreakpointUtil.getBreakpointTypes();
    ArrayList<AbstractBreakpointPanel<XBreakpoint>> panels = new ArrayList<AbstractBreakpointPanel<XBreakpoint>>();
    for (XBreakpointType<? extends XBreakpoint<?>, ?> type : types) {
      if (type.shouldShowInBreakpointsDialog(project)) {
        XBreakpointsPanel<?> panel = createBreakpointsPanel(project, parentDialog, type);
        panels.add(panel);
      }
    }
    return panels;
  }

  @Override
  public AnAction[] getAddBreakpointActions(@NotNull Project project) {
    List<AnAction> result = new ArrayList<AnAction>();
    for (XBreakpointType<?, ?> type : XBreakpointUtil.getBreakpointTypes()) {
      if (type.isAddBreakpointButtonVisible()) {
        result.add(new AddXBreakpointAction(type));
      }
    }
    return new AnAction[0];
  }

  private static <B extends XBreakpoint<?>> XBreakpointsPanel<B> createBreakpointsPanel(final Project project, DialogWrapper parentDialog, final XBreakpointType<B, ?> type) {
    return new XBreakpointsPanel<B>(project, parentDialog, type);
  }

  public void onDialogClosed(final Project project) {
  }

  @Override
  public void provideBreakpointItems(Project project, Collection<BreakpointItem> items) {
    XBreakpoint<?>[] allBreakpoints = XDebuggerManager.getInstance(project).getBreakpointManager().getAllBreakpoints();
    for (XBreakpoint<?> breakpoint : allBreakpoints) {
      items.add(new XBreakpointItem(breakpoint));
    }
  }

  private class AddXBreakpointAction extends AnAction {

    private XBreakpointType<?, ?> myType;

    public AddXBreakpointAction(XBreakpointType<?, ?> type) {
      myType = type;
      getTemplatePresentation().setIcon(type.getEnabledIcon());
      getTemplatePresentation().setText(type.getTitle());
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
      myType.addBreakpoint(getEventProject(e), null);
    }
  }
}
