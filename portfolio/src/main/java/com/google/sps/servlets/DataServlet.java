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
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

@WebServlet("/data")
public class DataServlet extends HttpServlet {
  static final int DEFUALT_COMMENT_LIMIT = 3;
  static final String DEFAULT_LANG = "en";
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ArrayList<Comment> comments = new ArrayList<Comment>();  
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    int limit = getCommentLimit(request);
    String languageCode = getLanguage(request);
    PreparedQuery results = datastore.prepare(query);
    List<Entity> commentList = results.asList(FetchOptions.Builder.withLimit(limit)); 
    for (Entity entity : commentList) {
      long id = entity.getKey().getId();
      String commentText = (String) entity.getProperty("comment");
      String timestamp = String.valueOf(entity.getProperty("timestamp"));
      Translate translate = TranslateOptions.getDefaultInstance().getService();
      Translation translation = translate.translate(commentText, Translate.TranslateOption.targetLanguage(languageCode));
      String translatedText = translation.getTranslatedText();
      Comment comment = new Comment(translatedText, timestamp, id);
      comments.add(comment);
    }
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String comment = getComment(request);
    long timestamp = System.currentTimeMillis();
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("comment", comment);
    commentEntity.setProperty("timestamp", timestamp);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    response.setContentType("text/html");
    response.sendRedirect("/index.html");
  }

  private String getComment(HttpServletRequest request) {
    String value = request.getParameter("text-input");
    if (value == null) {
      return "";
    }
    return value;
  }

  private int getCommentLimit(HttpServletRequest request) {
    String commentLimitString = request.getParameter("limit");
    int commentLimit;
    if (commentLimitString == null) {
      commentLimit = DEFUALT_COMMENT_LIMIT;
    }
    try {
      commentLimit = Integer.parseInt(commentLimitString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + commentLimitString);
      return DEFUALT_COMMENT_LIMIT;
    }
    if (commentLimit < 1 || commentLimit > 15) {
      System.err.println("User request out of range: " + commentLimitString);
      return DEFUALT_COMMENT_LIMIT;
    }
    return commentLimit;
  }

  private String getLanguage(HttpServletRequest request) {
    String lang = request.getParameter("language");
    if(lang == null) {
      return DEFAULT_LANG;
    }
    return lang;
  }

}


