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
package com.intellij.ui;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseListener;

public interface TabbedPane {
  JComponent getComponent();

  void putClientProperty(@NotNull Object key, Object value);

  void setKeyboardNavigation(@NotNull PrevNextActionsDescriptor installKeyboardNavigation);

  void addChangeListener(@NotNull ChangeListener listener);

  int getTabCount();

  void insertTab(@NotNull String title, Icon icon, @NotNull Component c, String tip, int index);

  void setTabPlacement(int tabPlacement);

  void addMouseListener(@NotNull MouseListener listener);

  int getSelectedIndex();

  Component getSelectedComponent();

  void setSelectedIndex(int index);

  void removeTabAt(int index);

  void revalidate();

  Color getForegroundAt(int index);

  void setForegroundAt(int index, Color color);

  Component getComponentAt(int i);

  Component getTabComponentAt(int index);

  void setTitleAt(int index, @NotNull String title);

  void setToolTipTextAt(int index, String toolTipText);

  void setComponentAt(int index, Component c);

  void setIconAt(int index, Icon icon);

  void setEnabledAt(int index, boolean enabled);

  int getTabLayoutPolicy();

  void setTabLayoutPolicy(int policy);

  void scrollTabToVisible(int index);

  String getTitleAt(int i);

  void removeAll();

  void updateUI();

  void removeChangeListener(ChangeListener listener);

  boolean isDisposed();
}