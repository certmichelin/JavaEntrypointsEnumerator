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

import java.util.ArrayList;
import java.util.List;

/**
 * Entrypoints class.
 *
 * @author Maxime ESCOURBIAC
 */
public class Entrypoints {

  private List<Filter> filters;
  private List<Servlet> servlets;
  private List<RestEndpoint> restEndpoints;

  /**
   * Default constructor.
   */
  public Entrypoints() {
    this.filters = new ArrayList<>();
    this.servlets = new ArrayList<>();
    this.restEndpoints = new ArrayList<>();
  }

  /**
   * All filters.
   *
   * @return All filters.
   */
  public List<Filter> getFilters() {
    return filters;
  }

  /**
   * All filters.
   *
   * @param filters All filters.
   */
  public void setFilters(List<Filter> filters) {
    this.filters = filters;
  }

  /**
   * All servlets.
   *
   * @return All servlets.
   */
  public List<Servlet> getServlets() {
    return servlets;
  }

  /**
   * All servlets.
   *
   * @param servlets All servlets.
   */
  public void setServlets(List<Servlet> servlets) {
    this.servlets = servlets;
  }

  /**
   * All REST endpoints.
   *
   * @return All REST endpoints.
   */
  public List<RestEndpoint> getRestEndpoints() {
    return restEndpoints;
  }

  /**
   * All REST endpoints.
   *
   * @param restEndpoints All REST endpoints.
   */
  public void setRestEndpoints(List<RestEndpoint> restEndpoints) {
    this.restEndpoints = restEndpoints;
  }

  /**
   * Add filter.
   *
   * @param filter Filter to add.
   */
  public void addFilter(Filter filter) {
    this.filters.add(filter);
  }

  /**
   * Add servlet.
   *
   * @param servlet Servlet to add.
   */
  public void addServlet(Servlet servlet) {
    this.servlets.add(servlet);
  }

  /**
   * Add REST endpoint.
   *
   * @param restEndpoint REST endpoint to add.
   */
  public void addRestEndpoint(RestEndpoint restEndpoint) {
    this.restEndpoints.add(restEndpoint);
  }

}
