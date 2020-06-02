// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  //private ArrayList<String> fruits;
  private ArrayList<String> comments;

/*
  @Override
  public void init(){
    fruits = new ArrayList<String>();
    fruits.add("Blueberry");
    fruits.add("Cherry");
    fruits.add("Peach");
    fruits.add("Apricot");
    fruits.add("Mango");
  }
*/

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");
    String json = new Gson().toJson(comments);
    response.getWriter().println(json);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      comments = new ArrayList<String>();
      String comment = getComment(request);
      comments.add(comment);

      response.setContentType("text/html");
      response.sendRedirect("/index.html");
  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getComment(HttpServletRequest request) {
    String value = request.getParameter("text-input");
    if (value == null) {
      return "";
    }
    return value;
  }
}



