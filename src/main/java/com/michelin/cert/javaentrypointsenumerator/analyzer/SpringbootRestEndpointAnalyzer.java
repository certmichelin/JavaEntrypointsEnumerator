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
import com.michelin.cert.javaentrypointsenumerator.entrypoint.http.HttpParameter;
import com.michelin.cert.javaentrypointsenumerator.entrypoint.http.HttpParameterType;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Springboot Rest Endpoint analyzer class.
 *
 * @author Maxime ESCOURBIAC
 * @TODO: Add RestController annotation
 */
public class SpringbootRestEndpointAnalyzer extends Analyzer {

  /**
   * Analyzer constructor.
   *
   * @param classloader Classloader util instance.
   * @param entrypoints Entrypoints.
   * @param outputFolder Output folder.
   */
  public SpringbootRestEndpointAnalyzer(Classloader classloader, Entrypoints entrypoints, File outputFolder) {
    super(classloader, entrypoints, outputFolder);
  }

  @Override
  public void analyze() {

    List<Class> loadedClassesList = classloader.getLoadedClasses();

    //Load Springboot annotation class.
    Class controllerClass = classloader.getClass("org.springframework.stereotype.Controller");
    Class requestMappingClass = classloader.getClass("org.springframework.web.bind.annotation.RequestMapping");
    Class requestMethodClass = classloader.getClass("org.springframework.web.bind.annotation.RequestMethod");
    Class pathVariableAnnotationClass = classloader.getClass("org.springframework.web.bind.annotation.PathVariable");
    Class requestParamAnnotationClass = classloader.getClass("org.springframework.web.bind.annotation.RequestParam");
    Class requestBodyAnnotationClass = classloader.getClass("org.springframework.web.bind.annotation.RequestBody");

    if (controllerClass != null
        && requestMappingClass != null
        && requestMethodClass != null
        && pathVariableAnnotationClass != null
        && requestParamAnnotationClass != null
        && requestBodyAnnotationClass != null) {

      try {
        Class[] parameterClasses = {
          pathVariableAnnotationClass,
          requestParamAnnotationClass,
          requestBodyAnnotationClass
        };

        HttpParameterType[] httpParameterTypes = {
          HttpParameterType.PATH,
          HttpParameterType.REQUEST,
          HttpParameterType.BODY
        };

        //Load Springboot annotation methods.
        Method requestMappingClassValueMethod = requestMappingClass.getMethod("value", (Class[]) null);
        Method requestMappingClassMethodMethod = requestMappingClass.getMethod("method", (Class[]) null);
        Method requestMethodClassNameMethod = requestMethodClass.getMethod("name", (Class[]) null);

        for (Class loadedClass : loadedClassesList) {
          //Get @Controller annotation.
          Annotation annotation = loadedClass.getAnnotation(controllerClass);
          if (annotation != null) {
            String[] paths = {""};
            HttpMethod[] classHttpMethods = {HttpMethod.ALL};
            //Check if @RequestMapping is used on the class.
            annotation = loadedClass.getAnnotation(requestMappingClass);
            if (annotation != null) {
              paths = getPathFromRequestMapping(requestMappingClassValueMethod, annotation);

              classHttpMethods = getMethodsFromRequestMapping(requestMappingClassMethodMethod, requestMethodClassNameMethod, annotation);
              classHttpMethods = (classHttpMethods == null) ? new HttpMethod[]{HttpMethod.ALL} : classHttpMethods;
            }

            //Look for all GET, POST etc in all methods.
            Method[] loadedClassMethods = loadedClass.getMethods();
            for (Method loadedClassMethod : loadedClassMethods) {
              annotation = loadedClassMethod.getAnnotation(requestMappingClass);
              if (annotation != null) {
                //Endpoint found.
                String[] methodPaths = getPathFromRequestMapping(requestMappingClassValueMethod, annotation);

                HttpMethod[] methodHttpMethods = getMethodsFromRequestMapping(requestMappingClassMethodMethod, requestMethodClassNameMethod, annotation);
                methodHttpMethods = (methodHttpMethods == null) ? classHttpMethods : methodHttpMethods;

                Parameter[] loadedClassParameters = loadedClassMethod.getParameters();

                List<HttpParameter> httpParameters = new ArrayList<>();
                for (Parameter loadedClassParameter : loadedClassParameters) {
                  for (int i = 0; i < parameterClasses.length; ++i) {
                    Annotation paramAnnotation = loadedClassParameter.getAnnotation(parameterClasses[i]);
                    if (paramAnnotation != null) {
                      HttpParameter httpParameter = new HttpParameter();
                      httpParameter.setModel(loadedClassParameter.getType());
                      httpParameter.setName(loadedClassParameter.getName());
                      httpParameter.setType(httpParameterTypes[i]);
                      httpParameters.add(httpParameter);
                    }
                  }
                }

                for (String path : paths) {
                  for (String methodPath : methodPaths) {
                    for (HttpMethod httpMethod : methodHttpMethods) {
                      RestEndpoint restEndpoint = new RestEndpoint();
                      restEndpoint.setClassName(loadedClass.getName());
                      restEndpoint.setMethodName(loadedClassMethod.getName());
                      restEndpoint.setMethod(httpMethod);
                      restEndpoint.setUrl(path + methodPath);
                      restEndpoint.setParameters(httpParameters);
                      inputs.addRestEndpoint(restEndpoint);
                    }
                  }
                }
              }
            }
          }
        }
      } catch (NoSuchMethodException | SecurityException ex) {
        Logger.getLogger(SpringbootRestEndpointAnalyzer.class.getName()).log(Level.SEVERE, "", ex);
      }
    }
  }

  private String[] getPathFromRequestMapping(Method requestMappingClassValueMethod, Annotation annotation) {
    String[] paths = null;
    try {
      paths = (String[]) requestMappingClassValueMethod.invoke(annotation);
      for (int i = 0; i < paths.length; ++i) {
        paths[i] = (paths[i].startsWith("/")) ? paths[i] : "/" + paths[i];
      }
      return paths;
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      Logger.getLogger(SpringbootRestEndpointAnalyzer.class.getName()).log(Level.SEVERE, "Error in getPathFromRequestMapping : {0}", ex.getMessage());
    }
    return paths;
  }

  private HttpMethod[] getMethodsFromRequestMapping(Method requestMappingMethod, Method requestMethodClassNameMethod, Annotation annotation) {
    HttpMethod[] httpMethods = null;
    try {
      Object[] methods = (Object[]) requestMappingMethod.invoke(annotation);
      httpMethods = new HttpMethod[methods.length];
      for (int i = 0; i < methods.length; ++i) {
        String methodStr = (String) requestMethodClassNameMethod.invoke(methods[i]);
        switch (methodStr) {
          case "GET":
            httpMethods[i] = HttpMethod.GET;
            break;
          case "DELETE":
            httpMethods[i] = HttpMethod.DELETE;
            break;
          case "HEAD":
            httpMethods[i] = HttpMethod.HEAD;
            break;
          case "OPTIONS":
            httpMethods[i] = HttpMethod.OPTIONS;
            break;
          case "PATCH":
            httpMethods[i] = HttpMethod.PATCH;
            break;
          case "POST":
            httpMethods[i] = HttpMethod.POST;
            break;
          case "PUT":
            httpMethods[i] = HttpMethod.PUT;
            break;
          case "TRACE":
            httpMethods[i] = HttpMethod.TRACE;
            break;
          default:
            break;
        }
      }
      return httpMethods;
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      Logger.getLogger(SpringbootRestEndpointAnalyzer.class.getName()).log(Level.SEVERE, "Error in getMethodsFromRequestMapping : {0}", ex.getMessage());
    }
    return httpMethods;
  }

}
