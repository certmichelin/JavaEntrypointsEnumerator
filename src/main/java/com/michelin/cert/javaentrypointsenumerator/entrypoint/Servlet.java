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

import java.util.ArrayList;
import java.util.List;

/**
 * Servlet class.
 *
 * @author Maxime ESCOURBIAC
 */
public class Servlet {

  private String servletName;
  private String servletClass;
  private List<String> urls;
  private List<HttpMethod> methods;

  /**
   * Default constructor.
   */
  public Servlet() {
    this.urls = new ArrayList<>();
    this.methods = new ArrayList<>();
  }

  /**
   * Servlet constructor.
   *
   * @param servletName Servlet Name.
   * @param servletClass Servlet class.
   */
  public Servlet(String servletName, String servletClass) {
    this();
    this.servletName = servletName;
    this.servletClass = servletClass;
  }

  /**
   * Servlet Name.
   *
   * @return Servlet Name.
   */
  public String getServletName() {
    return servletName;
  }

  /**
   * Servlet Name.
   *
   * @param servletName Servlet Name.
   */
  public void setServletName(String servletName) {
    this.servletName = servletName;
  }

  /**
   * Servlet class.
   *
   * @return Servlet class.
   */
  public String getServletClass() {
    return servletClass;
  }

  /**
   * Servlet class.
   *
   * @param servletClass Servlet class.
   */
  public void setServletClass(String servletClass) {
    this.servletClass = servletClass;
  }

  /**
   * Servlet urls.
   *
   * @return Servlet urls.
   */
  public List<String> getUrls() {
    return urls;
  }

  /**
   * Servlet urls.
   *
   * @param url Servlet urls.
   */
  public void addUrl(String url) {
    this.urls.add(url);
  }

  /**
   * Servlet urls.
   *
   * @param urls Servlet urls.
   */
  public void setUrls(List<String> urls) {
    this.urls = urls;
  }

  /**
   * Servlet http methods.
   *
   * @return Servlet http methods.
   */
  public List<HttpMethod> getMethods() {
    return methods;
  }

  /**
   * Servlet http methods.
   *
   * @param method Servlet http methods.
   */
  public void addMethod(HttpMethod method) {
    if (!this.methods.contains(method)) {
      this.methods.add(method);
    }
  }

  /**
   * Servlet http methods.
   *
   * @param methods Servlet http methods.
   */
  public void setMethods(List<HttpMethod> methods) {
    this.methods = methods;
  }
}
