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

package com.michelin.cert.javaentrypointsenumerator.input;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * Analyze class.
 *
 * @author Maxime ESCOURBIAC
 */
public class Analyze {

  private File warFile;
  private File explodedWarLocation;
  private File outputFile;
  private final List<String> libsToAnalyze;

  /**
   * Private constructor.
   */
  private Analyze() {
    this.libsToAnalyze = new ArrayList<>();
  }

  /**
   * War file to analyze.
   *
   * @return War file to analyze.
   */
  public File getWarFile() {
    return this.warFile;
  }

  /**
   * Exploded War location.
   *
   * @return Exploded War location.
   */
  public File getExplodedWarLocation() {
    return this.explodedWarLocation;
  }

  /**
   * Export location.
   *
   * @return Export location.
   */
  public File getOutputFile() {
    return this.outputFile;
  }

  /**
   * Additional libraries to analyze.
   *
   * @return Additional libraries to analyze.
   */
  public List<String> getLibsToAnalyze() {
    return this.libsToAnalyze;
  }

  /**
   * Load analyze from XML file.
   *
   * @param xmlFile XML file to analyze.
   * @return Analyze instance parsed.
   */
  public static Analyze loadFromXml(File xmlFile) {
    Analyze analyze = new Analyze();
    try {
      SAXBuilder sax = new SAXBuilder();

      // https://rules.sonarsource.com/java/RSPEC-2755
      // prevent xxe
      sax.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
      sax.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

      // XML is a local file
      Document doc = sax.build(xmlFile);
      Element rootNode = doc.getRootElement();

      analyze.warFile = new File(rootNode.getChildText("war-file-location"));
      analyze.explodedWarLocation = new File(rootNode.getChildText("exploded-war-location"));
      analyze.outputFile = new File(rootNode.getChildText("output-file-location"));
      Element libs = rootNode.getChild("lib-to-analyze");
      for (Element lib : libs.getChildren("lib")) {
        analyze.libsToAnalyze.add(lib.getText());
      }
    } catch (JDOMException | IOException ex) {
      Logger.getLogger(Analyze.class.getName()).log(Level.SEVERE, null, ex);
      analyze = null;
    }
    return analyze;
  }
}
