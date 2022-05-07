/*
 * Copyright 2021 The Bazel Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.idea.blaze.base.model;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Arrays.stream;

import com.google.common.collect.ImmutableSet;
import com.google.idea.blaze.base.model.primitives.Label;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import java.util.Optional;

/** Resolves a given library to a Blaze target */
public interface LibraryToTargetResolver {

  ExtensionPointName<LibraryToTargetResolver> EP_NAME =
      ExtensionPointName.create("com.google.idea.blaze.LibraryToTargetResolver");

  /**
   * Fetches the first Blaze library label that can be resolved from a LibraryKey using all
   * implementations of LibraryToTargetResolver.
   *
   * <p>Note: since the order is not guaranteed, having multiple implementations may produce
   * nondeterministic results.
   */
  static Optional<Label> findAnyTarget(Project project, LibraryKey library) {
    return stream(EP_NAME.getExtensions())
        .map(x -> x.resolveLibraryToTarget(project, library))
        .flatMap(Optional::stream)
        .findAny();
  }

  static ImmutableSet<Label> getAllLabelsForProject(Project project) {
    return stream(EP_NAME.getExtensions())
        .map(x -> x.doGetAllTargetLabelsForProject(project))
        .flatMap(ImmutableSet::stream)
        .collect(toImmutableSet());
  }

  ImmutableSet<Label> doGetAllTargetLabelsForProject(Project project);

  Optional<Label> resolveLibraryToTarget(Project project, LibraryKey library);
}
