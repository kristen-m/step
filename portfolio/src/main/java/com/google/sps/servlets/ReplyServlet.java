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
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/reply")
public class ReplyServlet extends HttpServlet {
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String reply = getReply(request);
    long timestamp = System.currentTimeMillis();
    Entity replyEntity = new Entity("Reply");
    replyEntity.setProperty("reply", comment);
    replyEntity.setProperty("timestamp", timestamp);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    response.setContentType("text/html");
    response.sendRedirect("/index.html");
  }

  private String getReply(HttpServletRequest request) {
  String reply = request.getParameter("text-input");
  if (reply == null) {
    return "";
  }
  return reply;
  }

}