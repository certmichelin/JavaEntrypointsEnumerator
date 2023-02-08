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
import com.michelin.cert.javaentrypointsenumerator.entrypoint.RestEndpoint;
import com.michelin.cert.javaentrypointsenumerator.entrypoint.http.HttpMethod;

import java.io.File;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Rest Endpoint analyzer class.
 *
 * @author Maxime ESCOURBIAC
 * @TODO: Fetch parameters.
 */
public class RestEndpointAnalyzer extends Analyzer {

  /**
   * Analyzer constructor.
   *
   * @param classloader Classloader util instance.
   * @param entrypoints Entrypoints.
   * @param outputFolder Output folder.
   */
  public RestEndpointAnalyzer(Classloader classloader, Entrypoints entrypoints, File outputFolder) {
    super(classloader, entrypoints, outputFolder);
  }

  @Override
  public void analyze() {

    List<Class> loadedClassesList = classloader.getLoadedClasses();
    Class pathAnnotationClass = classloader.getClass("javax.ws.rs.Path");
    Class deleteAnnotationClass = classloader.getClass("javax.ws.rs.DELETE");
    Class getAnnotationClass = classloader.getClass("javax.ws.rs.GET");
    Class headAnnotationClass = classloader.getClass("javax.ws.rs.HEAD");
    Class optionsAnnotationClass = classloader.getClass("javax.ws.rs.OPTIONS");
    Class postAnnotationClass = classloader.getClass("javax.ws.rs.POST");
    Class putAnnotationClass = classloader.getClass("javax.ws.rs.PUT");

    if (pathAnnotationClass != null
        && deleteAnnotationClass != null
        && getAnnotationClass != null
        && headAnnotationClass != null
        && optionsAnnotationClass != null
        && postAnnotationClass != null
        && putAnnotationClass != null) {
      try {

        Method value = pathAnnotationClass.getMethod("value", (Class[]) null);
        Class[] methodsAnnotationClasses = {
          deleteAnnotationClass,
          getAnnotationClass,
          headAnnotationClass,
          optionsAnnotationClass,
          postAnnotationClass,
          putAnnotationClass
        };

        HttpMethod[] httpMethods = {
          HttpMethod.DELETE,
          HttpMethod.GET,
          HttpMethod.HEAD,
          HttpMethod.OPTIONS,
          HttpMethod.POST,
          HttpMethod.PUT
        };

        for (Class loadedClass : loadedClassesList) {
          Annotation annotation = loadedClass.getAnnotation(pathAnnotationClass);
          if (annotation != null) {
            try {
              String classPath;
              classPath = (String) value.invoke(annotation);
              classPath = (classPath.startsWith("/")) ? classPath : "/" + classPath;

              //Look for all GET, POST etc in all methods.
              Method[] loadedClassMethods = loadedClass.getMethods();
              for (Method loadedClassMethod : loadedClassMethods) {
                for (int i = 0; i < 6; ++i) {
                  Annotation loadedClassMethodAnnotation = loadedClassMethod.getAnnotation(methodsAnnotationClasses[i]);
                  if (loadedClassMethodAnnotation != null) {

                    //HTTP ENDPOINT FOUND
                    RestEndpoint restEndpoint = new RestEndpoint();
                    restEndpoint.setClassName(loadedClass.getName());
                    restEndpoint.setMethodName(loadedClassMethod.getName());
                    restEndpoint.setMethod(httpMethods[i]);
                    String path = classPath;
                    loadedClassMethodAnnotation = loadedClassMethod.getAnnotation(pathAnnotationClass);
                    if (loadedClassMethodAnnotation != null) {
                      path = (String) value.invoke(loadedClassMethodAnnotation);
                      path = (path.startsWith("/")) ? path : "/" + path;
                    }
                    restEndpoint.setUrl(path);

                    inputs.addRestEndpoint(restEndpoint);
                  }
                }
              }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
              Logger.getLogger(RestEndpointAnalyzer.class.getName()).log(Level.SEVERE, "Error in invoke operation");
            }
          }
        }
      } catch (NoSuchMethodException ex) {
        Logger.getLogger(RestEndpointAnalyzer.class.getName()).log(Level.SEVERE, "Value method was not found in javax.ws.rs.Path");
      } catch (SecurityException ex) {
        Logger.getLogger(RestEndpointAnalyzer.class.getName()).log(Level.SEVERE, "Value method found in javax.ws.rs.Path raised security exception");
      }
    } else {
      Logger.getLogger(RestEndpointAnalyzer.class.getName()).log(Level.WARNING, "PathAnnotationClass or HttpMethodAnnotationClass was null");
    }
  }

}
