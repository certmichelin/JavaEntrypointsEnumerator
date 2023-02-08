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
import com.michelin.cert.javaentrypointsenumerator.entrypoint.Filter;
import com.michelin.cert.javaentrypointsenumerator.entrypoint.Servlet;
import com.michelin.cert.javaentrypointsenumerator.entrypoint.http.HttpMethod;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * Web xml analyzer class.
 *
 * @author Maxime ESCOURBIAC
 * @TODO: Add listeners.
 * @TODO: Add error list.
 * @TODO: Add Welcome file.
 * @TODO: Add dispatchers for filters.
 */
public class WebXmlAnalyzer extends Analyzer {

  /**
   * Analyzer constructor.
   *
   * @param classloader Classloader util instance.
   * @param entrypoints Entrypoints.
   * @param outputFolder Output folder.
   */
  public WebXmlAnalyzer(Classloader classloader, Entrypoints entrypoints, File outputFolder) {
    super(classloader, entrypoints, outputFolder);
  }

  @Override
  public void analyze() {
    try {
      SAXBuilder sax = new SAXBuilder();

      // https://rules.sonarsource.com/java/RSPEC-2755
      // prevent xxe
      sax.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
      sax.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

      //XML is a local file
      Document doc = sax.build(new File(outputFolder.getAbsolutePath() + File.separator + "web.xml"));
      Element rootNode = doc.getRootElement();

      Map<String, Filter> filters = new LinkedHashMap<>();
      Map<String, Servlet> servlets = new LinkedHashMap<>();

      List<Element> children = rootNode.getChildren();
      for (Element child : children) {
        switch (child.getName()) {
          case "filter":
            String filterName = "";
            String filterClass = "";
            for (Element field : child.getChildren()) {
              switch (field.getName()) {
                case "filter-name":
                  filterName = field.getTextTrim();
                  break;
                case "filter-class":
                  filterClass = field.getTextTrim();
                  break;
                default:
                  break;
              }
            }
            filters.put(filterName, new Filter(filterName, filterClass));
            break;
          case "filter-mapping":
            filterName = "";
            List<String> urlPatterns = new ArrayList<>();
            for (Element field : child.getChildren()) {
              switch (field.getName()) {
                case "filter-name":
                  filterName = field.getTextTrim();
                  break;
                case "url-pattern":
                  urlPatterns.add(field.getTextTrim());
                  break;
                default:
                  break;
              }
            }
            filters.get(filterName).setUrls(urlPatterns);
            break;
          case "servlet":
            String servletName = "";
            String servletClass = "";
            for (Element field : child.getChildren()) {
              switch (field.getName()) {
                case "servlet-name":
                  servletName = field.getTextTrim();
                  break;
                case "servlet-class":
                  servletClass = field.getTextTrim();
                  break;
                default:
                  break;
              }
            }
            servlets.put(servletName, new Servlet(servletName, servletClass));
            break;
          case "servlet-mapping":
            servletName = "";
            urlPatterns = new ArrayList<>();
            for (Element field : child.getChildren()) {
              switch (field.getName()) {
                case "servlet-name":
                  servletName = field.getTextTrim();
                  break;
                case "url-pattern":
                  urlPatterns.add(field.getTextTrim());
                  break;
                default:
                  break;
              }
            }
            servlets.get(servletName).setUrls(urlPatterns);
            break;
          default:
            break;
        }
      }

      Collection<Filter> filterValues = filters.values();
      for (Filter filter : filterValues) {
        this.inputs.addFilter(filter);
      }

      Collection<Servlet> servletValues = servlets.values();
      for (Servlet servlet : servletValues) {
        analyzeServletClass(servlet);
        this.inputs.addServlet(servlet);
      }
    } catch (JDOMException | IOException ex) {
      Logger.getLogger(WebXmlAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Analyze servlet class.
   *
   * @param servlet Servlet to analyze.
   */
  private void analyzeServletClass(Servlet servlet) {
    Class servletClass = classloader.getClass(servlet.getServletClass());
    if (servletClass != null) {
      for (Method method : servletClass.getMethods()) {
        if (method.getParameterCount() == 2) {
          switch (method.getName()) {
            case "doGet":
              servlet.addMethod(HttpMethod.GET);
              break;
            case "doDelete":
              servlet.addMethod(HttpMethod.DELETE);
              break;
            case "doHead":
              servlet.addMethod(HttpMethod.HEAD);
              break;
            case "doOptions":
              servlet.addMethod(HttpMethod.OPTIONS);
              break;
            case "doPost":
              servlet.addMethod(HttpMethod.POST);
              break;
            case "doPut":
              servlet.addMethod(HttpMethod.PUT);
              break;
            case "doTrace":
              servlet.addMethod(HttpMethod.TRACE);
              break;
            case "service":
              servlet.addMethod(HttpMethod.ALL);
              break;
            default:
              break;
          }
        }
      }
    }
  }
}
