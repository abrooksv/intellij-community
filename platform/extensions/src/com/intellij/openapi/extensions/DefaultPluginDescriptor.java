// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.openapi.extensions;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Date;
import java.util.List;

public final class DefaultPluginDescriptor implements PluginDescriptor {
  @NotNull
  private final PluginId myPluginId;
  private final ClassLoader myPluginClassLoader;

  public DefaultPluginDescriptor(@NotNull String pluginId) {
    myPluginId = PluginId.getId(pluginId);
    myPluginClassLoader = null;
  }

  public DefaultPluginDescriptor(@NotNull PluginId pluginId) {
    myPluginId = pluginId;
    myPluginClassLoader = null;
  }

  public DefaultPluginDescriptor(@NotNull PluginId pluginId, @Nullable ClassLoader pluginClassLoader) {
    myPluginId = pluginId;
    myPluginClassLoader = pluginClassLoader;
  }

  @Override
  @NotNull
  public PluginId getPluginId() {
    return myPluginId;
  }

  @Override
  public ClassLoader getPluginClassLoader() {
    return myPluginClassLoader;
  }

  @Override
  public File getPath() {
    return null;
  }

  @Nullable
  @Override
  public String getDescription() {
    return null;
  }

  @Override
  public String getChangeNotes() {
    return null;
  }

  @Override
  public String getName() {
    return null;
  }

  @Nullable
  @Override
  public String getProductCode() {
    return null;
  }

  @Nullable
  @Override
  public Date getReleaseDate() {
    return null;
  }

  @Override
  public int getReleaseVersion() {
    return 0;
  }

  @NotNull
  @Override
  public PluginId[] getDependentPluginIds() {
    return PluginId.EMPTY_ARRAY;
  }

  @NotNull
  @Override
  public PluginId[] getOptionalDependentPluginIds() {
    return PluginId.EMPTY_ARRAY;
  }

  @Override
  public String getVendor() {
    return null;
  }

  @Override
  public String getVersion() {
    return null;
  }

  @Override
  public String getResourceBundleBaseName() {
    return null;
  }

  @Override
  public String getCategory() {
    return null;
  }

  @Nullable
  @Override
  public List<Element> getActionDescriptionElements() {
    return null;
  }

  @Override
  public String getVendorEmail() {
    return null;
  }

  @Override
  public String getVendorUrl() {
    return null;
  }

  @Override
  public String getUrl() {
    return null;
  }

  @Override
  public String getSinceBuild() {
    return null;
  }

  @Override
  public String getUntilBuild() {
    return null;
  }

  @Override
  public boolean isEnabled() {
    return false;
  }

  @Override
  public void setEnabled(boolean enabled) {

  }
}
