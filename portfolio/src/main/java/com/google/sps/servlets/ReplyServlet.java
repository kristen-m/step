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
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.List;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.sps.data.Comment;


@WebServlet("/reply")
public class ReplyServlet extends HttpServlet {
  static final int DEFUALT_REPLY_LIMIT = 2;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ArrayList<Comment> replies = new ArrayList<Comment>();  
    Query query = new Query("Reply").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    List<Entity> replyList = results.asList(FetchOptions.Builder.withLimit(DEFUALT_REPLY_LIMIT)); 
    for (Entity entity : replyList) {
      long id = entity.getKey().getId();
      String replyText = (String) entity.getProperty("reply");
      String timestamp = String.valueOf(entity.getProperty("timestamp"));
      Comment reply = new Comment(replyText, timestamp, id);

      replies.add(reply);
    }
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(replies));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String reply = getReply(request);
    long timestamp = System.currentTimeMillis();
    Entity replyEntity = new Entity("Reply");
    replyEntity.setProperty("reply", reply);
    replyEntity.setProperty("timestamp", timestamp);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(replyEntity);
    response.setContentType("text/html");
    response.sendRedirect("/index.html");
  }

  private String getReply(HttpServletRequest request) {
    String reply = request.getParameter("reply-text");
    if (reply == null) {
      return "";
    }
    return reply;
    }

}