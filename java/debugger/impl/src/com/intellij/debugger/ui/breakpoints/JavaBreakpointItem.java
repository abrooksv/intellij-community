/*
 * Copyright 2000-2012 JetBrains s.r.o.
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
package com.intellij.debugger.ui.breakpoints;

import com.intellij.debugger.SourcePosition;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.popup.util.DetailView;
import com.intellij.xdebugger.breakpoints.ui.BreakpointItem;

import javax.swing.*;

/**
* Created with IntelliJ IDEA.
* User: intendia
* Date: 10.05.12
* Time: 3:16
* To change this template use File | Settings | File Templates.
*/
class JavaBreakpointItem implements BreakpointItem {
  private final Breakpoint myBreakpoint;
  private BreakpointFactory myBreakpointFactory;

  public JavaBreakpointItem(BreakpointFactory breakpointFactory, Breakpoint breakpoint) {
    myBreakpointFactory = breakpointFactory;
    myBreakpoint = breakpoint;
  }

  @Override
  public void setupRenderer(ColoredListCellRenderer renderer, Project project, boolean selected) {
    renderer.setIcon(myBreakpoint.getIcon());
    renderer.append(myBreakpoint.getDisplayName());
  }

  @Override
  public void updateMnemonicLabel(JLabel label) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void execute(Project project) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public String speedSearchText() {
    return myBreakpoint.getDisplayName();
  }

  @Override
  public String footerText() {
    return myBreakpoint.getDisplayName();
  }

  @Override
  public void updateDetailView(DetailView panel) {
    if (myBreakpoint instanceof LineBreakpoint) {
      SourcePosition sourcePosition = ((LineBreakpoint)myBreakpoint).getSourcePosition();
      VirtualFile virtualFile = sourcePosition.getFile().getVirtualFile();
      panel.navigateInPreviewEditor(virtualFile, new LogicalPosition(sourcePosition.getLine(), 0));
    }

    BreakpointPropertiesPanel breakpointPropertiesPanel = myBreakpointFactory
      .createBreakpointPropertiesPanel(myBreakpoint.getProject(), false);
    if (breakpointPropertiesPanel != null) {
      breakpointPropertiesPanel.initFrom(myBreakpoint, true);
      final JPanel mainPanel = breakpointPropertiesPanel.getPanel();
      panel.setDetailPanel(mainPanel);
    }
  }

  @Override
  public boolean allowedToRemove() {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Object getBreakpoint() {
    return myBreakpoint;
  }
}
