/*
 * Copyright 2023 Michelin CERT (https://cert.michelin.com/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.michelin.cert.javaentrypointsenumerator.analyzer;

import com.michelin.cert.javaentrypointsenumerator.classloader.Classloader;
import com.michelin.cert.javaentrypointsenumerator.entrypoint.Entrypoints;
import java.io.File;

/**
 * Analyzer class.
 *
 * @author Maxime ESCOURBIAC
 */
public abstract class Analyzer {

  protected final Classloader classloader;
  protected final Entrypoints inputs;
  protected final File outputFolder;

  /**
   * Analyzer constructor.
   *
   * @param classloader Classloader util instance.
   * @param entrypoints Entrypoints.
   * @param outputFolder Output folder.
   */
  public Analyzer(Classloader classloader, Entrypoints entrypoints, File outputFolder) {
    this.classloader = classloader;
    this.inputs = entrypoints;
    this.outputFolder = outputFolder;
  }

  /**
   * Analyzer implementation.
   */
  public abstract void analyze();

}
