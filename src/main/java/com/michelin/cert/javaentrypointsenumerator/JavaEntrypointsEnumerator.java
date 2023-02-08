/*
 * Copyright 2021 Michelin CERT (https://cert.michelin.com/)
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

import com.michelin.cert.javaentrypointsenumerator.analyzer.RestEndpointAnalyzer;
import com.michelin.cert.javaentrypointsenumerator.analyzer.SpringbootRestEndpointAnalyzer;
import com.michelin.cert.javaentrypointsenumerator.analyzer.WebXmlAnalyzer;
import com.michelin.cert.javaentrypointsenumerator.classloader.Classloader;
import com.michelin.cert.javaentrypointsenumerator.entrypoint.Entrypoints;
import com.michelin.cert.javaentrypointsenumerator.export.ExcelExporter;
import com.michelin.cert.javaentrypointsenumerator.input.Analyze;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class.
 *
 * @TODO: Manage SOAP
 *
 * @author Maxime ESCOURBIAC
 */
public class JavaEntrypointsEnumerator {

  /**
   * Main method.
   *
   * @param args Arguments.
   */
  public static void main(String[] args) {
    try {

      String xml = "sample.xml";

      System.out.println("Load analyze from xml: " + xml);
      Analyze analyze = Analyze.loadFromXml(new File(xml));
      System.out.println("War file to analyze: " + analyze.getWarFile().getAbsolutePath());
      System.out.println("Exploded war folder: " + analyze.getExplodedWarLocation().getAbsolutePath());
      for (String lib : analyze.getLibsToAnalyze()) {
        System.out.println("Extra library to analyze: " + lib);
      }

      Extractor extractor = new Extractor(analyze.getWarFile(), analyze.getExplodedWarLocation());

      //Prepare the war file to be analyzed.
      System.out.println("Explode war file...");
      extractor.extractWebXml();
      extractor.extractJars();
      extractor.extractJsps();
      extractor.extractResources();
      extractor.extractWebInfResources();
      extractor.extractClasses();
      extractor.generateClassJar();

      //Load classes.
      System.out.println("Load war classes...");
      Classloader classloader = new Classloader(analyze.getExplodedWarLocation());
      classloader.loadClassesFromJar(new File(analyze.getExplodedWarLocation().getAbsolutePath() + File.separator + "classes.jar"));
      for (String lib : analyze.getLibsToAnalyze()) {
        System.out.println("Load lib: " + lib);
        classloader.loadClassesFromJar(new File(analyze.getExplodedWarLocation().getAbsolutePath() + File.separator + "jars" + File.separator + lib));
      }

      //Analyze war.
      System.out.println("Analyze war...");
      Entrypoints entrypoints = new Entrypoints();
      WebXmlAnalyzer webXmlAnalyzer = new WebXmlAnalyzer(classloader, entrypoints, analyze.getExplodedWarLocation());
      RestEndpointAnalyzer restEndpointAnalyzer = new RestEndpointAnalyzer(classloader, entrypoints, analyze.getExplodedWarLocation());
      SpringbootRestEndpointAnalyzer springbootRestEndpointAnalyzer = new SpringbootRestEndpointAnalyzer(classloader, entrypoints, analyze.getExplodedWarLocation());

      webXmlAnalyzer.analyze();
      restEndpointAnalyzer.analyze();
      springbootRestEndpointAnalyzer.analyze();

      //Display results.
      ExcelExporter excelExporter = new ExcelExporter();
      excelExporter.export(analyze.getOutputFile(), entrypoints);
      System.out.println("End of analyze");

    } catch (IOException ex) {
      Logger.getLogger(JavaEntrypointsEnumerator.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
