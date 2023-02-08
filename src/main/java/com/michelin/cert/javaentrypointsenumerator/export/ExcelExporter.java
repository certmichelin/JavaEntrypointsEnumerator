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

package com.michelin.cert.javaentrypointsenumerator.export;

import com.michelin.cert.javaentrypointsenumerator.entrypoint.Entrypoints;
import com.michelin.cert.javaentrypointsenumerator.entrypoint.Filter;
import com.michelin.cert.javaentrypointsenumerator.entrypoint.RestEndpoint;
import com.michelin.cert.javaentrypointsenumerator.entrypoint.Servlet;
import com.michelin.cert.javaentrypointsenumerator.entrypoint.http.HttpMethod;
import com.michelin.cert.javaentrypointsenumerator.entrypoint.http.HttpParameter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Excel exporter class.
 *
 * @author Maxime ESCOURBIAC
 */
public class ExcelExporter extends Exporter {

  @Override
  public void export(File outputFile, Entrypoints inputs) {

    XSSFWorkbook workbook = new XSSFWorkbook();

    //Export filters.
    XSSFSheet sheet = workbook.createSheet("Filters");
    int rowCount = 0;
    for (Filter filter : inputs.getFilters()) {
      for (String url : filter.getUrls()) {
        int columnCount = 0;
        Row row = sheet.createRow(rowCount++);
        Cell cell = row.createCell(columnCount++);
        cell.setCellValue(filter.getFilterName());
        cell = row.createCell(columnCount++);
        cell.setCellValue(filter.getFilterClass());
        cell = row.createCell(columnCount++);
        cell.setCellValue(url);
      }
    }

    //Export servlets.
    sheet = workbook.createSheet("Servlets");
    rowCount = 0;
    for (Servlet servlet : inputs.getServlets()) {
      for (String url : servlet.getUrls()) {
        for (HttpMethod httpMethod : servlet.getMethods()) {
          int columnCount = 0;
          Row row = sheet.createRow(rowCount++);
          Cell cell = row.createCell(columnCount++);
          cell.setCellValue(servlet.getServletName());
          cell = row.createCell(columnCount++);
          cell.setCellValue(servlet.getServletClass());
          cell = row.createCell(columnCount++);
          cell.setCellValue(httpMethod.name());
          cell = row.createCell(columnCount++);
          cell.setCellValue(url);
        }
      }
    }

    //Export REST Endpoints.
    sheet = workbook.createSheet("REST Endpoints");
    rowCount = 0;
    for (RestEndpoint restEndpoints : inputs.getRestEndpoints()) {
      int columnCount = 0;
      Row row = sheet.createRow(rowCount++);
      Cell cell = row.createCell(columnCount++);
      cell.setCellValue(restEndpoints.getClassName());
      cell = row.createCell(columnCount++);
      cell.setCellValue(restEndpoints.getMethodName());
      cell = row.createCell(columnCount++);
      cell.setCellValue(restEndpoints.getMethod().name());
      cell = row.createCell(columnCount++);
      cell.setCellValue(restEndpoints.getUrl());
      for (HttpParameter httpParameter : restEndpoints.getParameters()) {
        cell = row.createCell(columnCount++);
        cell.setCellValue(httpParameter.getType() + " " + httpParameter.getModel().getName() + " " + httpParameter.getName());
      }
    }

    try ( FileOutputStream outputStream = new FileOutputStream(outputFile)) {
      workbook.write(outputStream);
    } catch (FileNotFoundException ex) {
      Logger.getLogger(ExcelExporter.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(ExcelExporter.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

}
