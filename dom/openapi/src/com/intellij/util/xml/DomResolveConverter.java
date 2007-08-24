/*
 * Copyright 2000-2006 JetBrains s.r.o.
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
 *
 */
package com.intellij.util.xml;

import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.util.containers.FactoryMap;
import com.intellij.util.containers.SoftFactoryMap;
import com.intellij.util.xml.reflect.DomCollectionChildDescription;
import com.intellij.util.xml.reflect.DomGenericInfo;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Converter which resolves {@link com.intellij.util.xml.DomElement}s by name in a defined scope. The scope is taken
 * from corresponding {@link com.intellij.util.xml.DomFileDescription#getResolveScope(GenericDomValue)}.
 *
 * @author peter
 */
public class DomResolveConverter<T extends DomElement> extends ResolvingConverter<T>{
  private static final FactoryMap<Class<? extends DomElement>,DomResolveConverter> ourCache = new FactoryMap<Class<? extends DomElement>, DomResolveConverter>() {
    @NotNull
    protected DomResolveConverter create(final Class<? extends DomElement> key) {
      return new DomResolveConverter(key);
    }
  };
  private final SoftFactoryMap<DomElement, CachedValue<Map<String, DomElement>>> myResolveCache = new SoftFactoryMap<DomElement, CachedValue<Map<String, DomElement>>>() {
    @NotNull
    protected CachedValue<Map<String, DomElement>> create(final DomElement scope) {
      final DomManager domManager = scope.getManager();
      final Project project = domManager.getProject();
      return PsiManager.getInstance(project).getCachedValuesManager().createCachedValue(new CachedValueProvider<Map<String, DomElement>>() {
        public Result<Map<String, DomElement>> compute() {
          final Map<String, DomElement> map = new THashMap<String, DomElement>();
          scope.acceptChildren(new DomElementVisitor() {
            public void visitDomElement(DomElement element) {
              if (myClass.isInstance(element)) {
                final String name = ElementPresentationManager.getElementName(element);
                if (name != null && !map.containsKey(name)) {
                  map.put(name, element);
                }
              } else {
                element.acceptChildren(this);
              }
            }
          });
          return new Result<Map<String, DomElement>>(map, PsiModificationTracker.OUT_OF_CODE_BLOCK_MODIFICATION_COUNT);
        }
      }, false);
    }
  };

  private final Class<T> myClass;

  public DomResolveConverter(final Class<T> aClass) {
    myClass = aClass;
  }

  public static <T extends DomElement> DomResolveConverter<T> createConverter(Class<T> aClass) {
    return ourCache.get(aClass);
  }

  public final T fromString(final String s, final ConvertContext context) {
    if (s == null) return null;
    return (T) myResolveCache.get(getResolvingScope(context)).getValue().get(s);
  }

  private static DomElement getResolvingScope(final ConvertContext context) {
    final DomElement invocationElement = context.getInvocationElement();
    return invocationElement.getManager().getResolvingScope((GenericDomValue)invocationElement);
  }

  public String getErrorMessage(final String s, final ConvertContext context) {
    return CodeInsightBundle.message("error.cannot.resolve.0.1", ElementPresentationManager.getTypeName(myClass), s);
  }

  public final String toString(final T t, final ConvertContext context) {
    if (t == null) return null;
    return ElementPresentationManager.getElementName(t);
  }

  @NotNull
  public Collection<? extends T> getVariants(final ConvertContext context) {
    final DomElement reference = context.getInvocationElement();
    final DomElement scope = reference.getManager().getResolvingScope((GenericDomValue)reference);
    return (Collection<T>)myResolveCache.get(scope).getValue().values();
  }

  @Nullable
  protected DomCollectionChildDescription getChildDescription(final List<DomElement> contexts) {

    if (contexts.size() == 0) {
        return null;
    }
    final DomElement context = contexts.get(0);
    final DomGenericInfo genericInfo = context.getGenericInfo();
    final List<? extends DomCollectionChildDescription> descriptions = genericInfo.getCollectionChildrenDescriptions();
    for (DomCollectionChildDescription description : descriptions) {
      final Type type = description.getType();
      if (type.equals(myClass)) {
        return description;
      }
    }
    return null;
  }

  @Nullable
  protected DomElement chooseParent(final List<DomElement> contexts) {
    if (contexts.size() == 0) {
        return null;
    }
    return contexts.get(0);
  }

  public LocalQuickFix[] getQuickFixes(final ConvertContext context) {
    final DomElement element = context.getInvocationElement();
    final GenericDomValue value = ((GenericDomValue)element).createStableCopy();
    final String newName = value.getStringValue();
    assert newName != null;
    final DomElement scope = value.getManager().getResolvingScope(value);
    final List<DomElement> contexts = ModelMergerUtil.getImplementations(scope);
    final LocalQuickFix fix = createFix(newName, contexts);
    if (fix != null) {
      return new LocalQuickFix[] { fix };
    }
    return LocalQuickFix.EMPTY_ARRAY;

  }

  @Nullable
  public LocalQuickFix createFix(final String newName, final List<DomElement> parents) {
    final DomCollectionChildDescription childDescription = getChildDescription(parents);
    if (newName.length() > 0 && childDescription != null) {
      return new LocalQuickFix() {
        @NotNull
        public String getName() {
          return DomBundle.message("create.new.element", ElementPresentationManager.getTypeName(myClass), newName);
        }

        @NotNull
        public String getFamilyName() {
          return DomBundle.message("quick.fixes.family");
        }

        public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
          final DomElement parent = chooseParent(parents);
          final DomElement domElement = childDescription.addValue(parent);
          final GenericDomValue nameDomElement = domElement.getGenericInfo().getNameDomElement(domElement);
          assert nameDomElement != null;
          nameDomElement.setStringValue(newName);
        }
      };
    }
    return null;
  }

}
