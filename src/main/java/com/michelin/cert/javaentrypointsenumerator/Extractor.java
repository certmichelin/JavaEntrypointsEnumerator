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

package com.michelin.cert.javaentrypointsenumerator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

import org.zeroturnaround.zip.ZipUtil;

/**
 * Java Archive extractor.
 *
 * @author Maxime ESCOURBIAC
 */
public class Extractor {

  private final File file;
  private final File outputFolder;

  /**
   * JavaArchiveExtractor constructor.
   *
   * @param file Jar/War file to analyze.
   * @param outputFolder Output folder.
   */
  public Extractor(File file, File outputFolder) {
    this.file = file;
    this.outputFolder = outputFolder;

    if (!outputFolder.exists()) {
      outputFolder.mkdirs();
    }
  }

  /**
   * Extract the web.xml file from the archive.
   *
   * @return True if the Web.xml files is well extracts.
   */
  public boolean extractWebXml() {
    boolean result = false;
    try {
      JarFile jarFile = new JarFile(file);
      ZipEntry entry = jarFile.getEntry("WEB-INF/web.xml");
      if (entry != null) {
        extractFile(jarFile.getInputStream(entry), this.outputFolder.getAbsolutePath() + File.separator + "web.xml");
        result = true;
      }
    } catch (IOException ex) {
      Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
    }
    return result;
  }

  /**
   * Extract all jar files.
   *
   * @return true if jar files was extracted.
   */
  public boolean extractJars() {
    boolean result = false;
    try {
      File extractFolder = new File(this.outputFolder.getAbsolutePath() + File.separator + "jars");
      extractFolder.mkdir();

      JarFile jarFile = new JarFile(file);
      Enumeration<JarEntry> entries = jarFile.entries();
      while (entries.hasMoreElements()) {
        JarEntry je = entries.nextElement();
        if (je.isDirectory() || !je.getName().endsWith(".jar")) {
          continue;
        }
        String[] temp = je.getName().split("/");
        extractFile(jarFile.getInputStream(je), extractFolder.getAbsolutePath() + File.separator + temp[temp.length - 1]);
      }
      result = true;
    } catch (IOException ex) {
      Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
    }
    return result;
  }

  /**
   * Extract all Jsp files.
   *
   * @return true if jsp files was extracted.
   */
  public boolean extractJsps() {
    boolean result = false;
    try {
      File extractFolder = new File(this.outputFolder.getAbsolutePath() + File.separator + "jsps");
      extractFolder.mkdir();

      JarFile jarFile = new JarFile(file);
      Enumeration<JarEntry> entries = jarFile.entries();
      while (entries.hasMoreElements()) {
        JarEntry je = entries.nextElement();
        if (je.isDirectory() || !je.getName().endsWith(".jsp")) {
          continue;
        }
        File extracted = new File(extractFolder.getAbsolutePath() + File.separator + je.getName());
        extracted.getParentFile().mkdirs();
        extractFile(jarFile.getInputStream(je), extracted.getAbsolutePath());
      }
      result = true;
    } catch (IOException ex) {
      Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
    }
    return result;
  }

  /**
   * Extract all resources files.
   *
   * @return true if resources files was extracted.
   */
  public boolean extractResources() {
    boolean result = false;
    try {
      File extractFolder = new File(this.outputFolder.getAbsolutePath() + File.separator + "resources");
      extractFolder.mkdir();

      JarFile jarFile = new JarFile(file);
      Enumeration<JarEntry> entries = jarFile.entries();
      while (entries.hasMoreElements()) {
        JarEntry je = entries.nextElement();
        if (je.isDirectory() || je.getName().endsWith(".jsp") || je.getName().startsWith("WEB-INF") || je.getName().startsWith("META-INF")) {
          continue;
        }
        File extracted = new File(extractFolder.getAbsolutePath() + File.separator + je.getName());
        extracted.getParentFile().mkdirs();
        extractFile(jarFile.getInputStream(je), extracted.getAbsolutePath());
      }
      result = true;
    } catch (IOException ex) {
      Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
    }
    return result;
  }

  /**
   * Extract all classes present in War file.
   *
   * @return true if class files was extracted.
   */
  public boolean extractClasses() {
    boolean result = false;
    try {
      File extractFolder = new File(this.outputFolder.getAbsolutePath() + File.separator + "classes");
      extractFolder.mkdir();

      JarFile jarFile = new JarFile(file);
      Enumeration<JarEntry> entries = jarFile.entries();
      while (entries.hasMoreElements()) {
        JarEntry je = entries.nextElement();
        if (je.isDirectory() || !je.getName().endsWith(".class")) {
          continue;
        }
        File extracted = new File(extractFolder.getAbsolutePath() + File.separator + je.getName().replaceAll("WEB-INF/classes", ""));
        extracted.getParentFile().mkdirs();
        extractFile(jarFile.getInputStream(je), extracted.getAbsolutePath());
      }
      result = true;
    } catch (IOException ex) {
      Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
    }
    return result;
  }

  /**
   * Extract all resources present in WEB-INF folder.
   *
   * @return true if class files was extracted.
   */
  public boolean extractWebInfResources() {
    boolean result = false;
    try {
      File extractFolder = new File(this.outputFolder.getAbsolutePath() + File.separator + "web-inf-resources");
      extractFolder.mkdir();

      JarFile jarFile = new JarFile(file);
      Enumeration<JarEntry> entries = jarFile.entries();
      while (entries.hasMoreElements()) {
        JarEntry je = entries.nextElement();
        if (je.isDirectory() || !je.getName().startsWith("WEB-INF") || je.getName().endsWith(".class") || je.getName().endsWith(".jar")) {
          continue;
        }
        File extracted = new File(extractFolder.getAbsolutePath() + File.separator + je.getName().replaceAll("WEB-INF/", ""));
        extracted.getParentFile().mkdirs();
        extractFile(jarFile.getInputStream(je), extracted.getAbsolutePath());
      }
      result = true;
    } catch (IOException ex) {
      Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
    }
    return result;
  }

  /**
   * Generate a specific jar for classes contained in war file.
   *
   * @return true.
   */
  public boolean generateClassJar() {
    File inputFolder = new File(this.outputFolder.getAbsolutePath() + File.separator + "classes" + File.separator);
    if (inputFolder.exists() && !isEmptyDirectory(inputFolder.toPath().toAbsolutePath())) {
      ZipUtil.pack(new File(this.outputFolder.getAbsolutePath() + File.separator + "classes" + File.separator), new File(this.outputFolder.getAbsolutePath() + File.separator + "classes.jar"));
    }
    return true;
  }

  /**
   * Extracts a zip entry (file entry)
   *
   * @param zipIn Zip input stream.
   * @param filePath File path inside zip.
   * @return True is extraction succeed.
   */
  private boolean extractFile(InputStream zipIn, String filePath) {
    boolean success = false;
    try {
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
      byte[] bytesIn = new byte[2048];
      int read = 0;
      while ((read = zipIn.read(bytesIn)) != -1) {
        bos.write(bytesIn, 0, read);
      }
      bos.close();
      success = true;
    } catch (IOException ex) {
      Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
    }
    return success;
  }

  /**
   * Test is empty directory.
   *
   * @param path Path to test.
   * @return True if empty.
   */
  private boolean isEmptyDirectory(Path path) {
    boolean result = false;
    if (Files.isDirectory(path)) {
      try ( Stream<Path> entries = Files.list(path)) {
        return !entries.findFirst().isPresent();
      } catch (IOException ex) {
        Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return result;
  }
}
