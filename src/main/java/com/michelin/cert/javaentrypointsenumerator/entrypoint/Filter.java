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
 * Filter class.
 *
 * @author Maxime ESCOURBIAC
 */
public class Filter {

  private String filterName;
  private String filterClass;
  private List<String> urls;

  /**
   * Default constructor.
   */
  public Filter() {
    urls = new ArrayList<>();
  }

  /**
   * Filter constructor.
   *
   * @param filterName Filter name.
   * @param filterClass Filter class name.
   */
  public Filter(String filterName, String filterClass) {
    this();
    this.filterName = filterName;
    this.filterClass = filterClass;
  }

  /**
   * Filter name.
   *
   * @return Filter name.
   */
  public String getFilterName() {
    return filterName;
  }

  /**
   * Filter name.
   * 
   * @param filterName Filter name.
   */
  public void setFilterName(String filterName) {
    this.filterName = filterName;
  }

  /**
   * Filter class name.
   *
   * @return Filter class name.
   */
  public String getFilterClass() {
    return filterClass;
  }

  /**
   * Filter class name.
   *
   * @param filterClass Filter class name.
   */
  public void setFilterClass(String filterClass) {
    this.filterClass = filterClass;
  }

  /**
   * Filter urls.
   *
   * @return Filter urls.
   */
  public List<String> getUrls() {
    return urls;
  }

  /**
   * Filter urls.
   *
   * @param url Filter url.
   */
  public void addUrl(String url) {
    this.urls.add(url);
  }

  /**
   * Filter urls.
   *
   * @param urls Filter urls.
   */
  public void setUrls(List<String> urls) {
    this.urls = urls;
  }
}
