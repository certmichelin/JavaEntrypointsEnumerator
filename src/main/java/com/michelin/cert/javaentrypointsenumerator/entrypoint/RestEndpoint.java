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

package com.michelin.cert.javaentrypointsenumerator.entrypoint;

import com.michelin.cert.javaentrypointsenumerator.entrypoint.http.HttpMethod;
import com.michelin.cert.javaentrypointsenumerator.entrypoint.http.HttpParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * REST endpoint class.
 *
 * @author Maxime ESCOURBIAC
 */
public class RestEndpoint {

  private String className;
  private String methodName;
  private String url;
  private HttpMethod method;
  private List<HttpParameter> parameters;

  /**
   * Default constructor.
   */
  public RestEndpoint() {
    this.parameters = new ArrayList<>();
  }

  /**
   * Endpoint class name.
   *
   * @return Endpoint class name.
   */
  public String getClassName() {
    return className;
  }

  /**
   * Endpoint class name.
   *
   * @param className Endpoint class name.
   */
  public void setClassName(String className) {
    this.className = className;
  }

  /**
   * Endpoint method name.
   *
   * @return Endpoint method name.
   */
  public String getMethodName() {
    return methodName;
  }

  /**
   * Endpoint method name.
   *
   * @param methodName Endpoint method name.
   */
  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  /**
   * Endpoint url.
   *
   * @return Endpoint url.
   */
  public String getUrl() {
    return url;
  }

  /**
   * Endpoint url.
   *
   * @param url Endpoint url.
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Endpoint http method.
   *
   * @return Endpoint http method.
   */
  public HttpMethod getMethod() {
    return method;
  }

  /**
   * Endpoint http method.
   *
   * @param method Endpoint http method.
   */
  public void setMethod(HttpMethod method) {
    this.method = method;
  }

  /**
   * Endpoint http parameters.
   *
   * @return Endpoint http parameters.
   */
  public List<HttpParameter> getParameters() {
    return parameters;
  }

  /**
   * Endpoint http parameters.
   *
   * @param parameters Endpoint http parameters.
   */
  public void setParameters(List<HttpParameter> parameters) {
    this.parameters = parameters;
  }

  /**
   * Endpoint http parameters.
   *
   * @param parameter Endpoint http parameter.
   */
  public void addParameter(HttpParameter parameter) {
    this.parameters.add(parameter);
  }
}
