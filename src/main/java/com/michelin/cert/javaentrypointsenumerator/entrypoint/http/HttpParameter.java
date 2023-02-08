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

package com.michelin.cert.javaentrypointsenumerator.entrypoint.http;

/**
 * HTTP parameter model.
 *
 * @author Maxime ESCOURBIAC
 */
public class HttpParameter {

  private String name;
  private Class model;
  private HttpParameterType type;

  public HttpParameter() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Class getModel() {
    return model;
  }

  public void setModel(Class model) {
    this.model = model;
  }

  public HttpParameterType getType() {
    return type;
  }

  public void setType(HttpParameterType type) {
    this.type = type;
  }

}
