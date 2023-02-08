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

package com.michelin.cert.javaentrypointsenumerator.classloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classloader util.
 *
 * @author Maxime ESCOURBIAC
 */
public class Classloader {

  private final URLClassLoader classLoader;
  private final Map<String, Class> loadedClasses;

  /**
   * Classloader constructor. 
   * 
   * @param explodedWarFolder Exploded war folder.
   * @throws IOException IOException.
   */
  public Classloader(File explodedWarFolder) throws IOException {
    this.loadedClasses = new LinkedHashMap<>();

    //Build classloader.
    File libFolder = new File(explodedWarFolder.getCanonicalPath() + File.separator + "jars");
    File[] libs = libFolder.listFiles();
    URL[] urls = new URL[libs.length + 1];
    urls[0] = new URL("jar:file:" + explodedWarFolder.getCanonicalPath() + File.separator + "classes.jar!/");
    for (int i = 0; i < libs.length; ++i) {
      urls[i + 1] = new URL("jar:file:" + explodedWarFolder.getCanonicalPath() + File.separator + "jars" + File.separator + libs[i].getName() + "!/");
    }
    classLoader = URLClassLoader.newInstance(urls);
  }

  /**
   * Get class from loaded classes.
   *
   * @param className Class name.
   * @return Loaded class instances.
   */
  public Class getClass(String className) {
    Class loadedClass = null;
    //Get class from loaded classes.
    if (loadedClasses.containsKey(className)) {
      loadedClass = loadedClasses.get(className);
    } else {
      try {
        //Get class from class loader.
        loadedClass = classLoader.loadClass(className);
      } catch (ClassNotFoundException | NoClassDefFoundError ex) {
        Logger.getLogger(Classloader.class.getName()).log(Level.WARNING, "Class not found in classloader: {0}", className);
      }
    }
    return loadedClass;
  }

  /**
   * Loaded classes.
   *
   * @return Loaded classes.
   */
  public List<Class> getLoadedClasses() {
    return new ArrayList<>(loadedClasses.values());
  }

  /**
   * Load class from JarFile.
   *
   * @param file File to load.
   */
  public void loadClassesFromJar(File file) {
    try {
      if (file.exists()) {
        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
          JarEntry je = entries.nextElement();
          if (je.isDirectory() || je.getName().endsWith("_jsp.class") || !je.getName().endsWith(".class")) {
            //Do not load compile jsp class.
            continue;
          }

          // -6 because of .class
          String className = je.getName().substring(0, je.getName().length() - 6);
          className = className.replace('/', '.');
          try {
            Class<?> loadedClass = this.classLoader.loadClass(className);
            if (loadedClass != null) {
              loadedClasses.put(className, loadedClass);
            } else {
              Logger.getLogger(Classloader.class.getName()).log(Level.WARNING, "Class was null : {0}", className);
            }
          } catch (ClassNotFoundException | NoClassDefFoundError ex) {
            Logger.getLogger(Classloader.class.getName()).log(Level.WARNING, "Class not found : {0}", className);
          } catch (Throwable ex) {
            Logger.getLogger(Classloader.class.getName()).log(Level.SEVERE, "Class not loaded : {0}", className);
          }
        }
      } else {
        Logger.getLogger(Classloader.class.getName()).log(Level.SEVERE, "Jar file not found : {0}", file.getAbsolutePath());
      }
    } catch (IOException ex) {
      Logger.getLogger(Classloader.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

}
