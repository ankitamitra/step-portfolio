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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns hard-coded comments */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private ArrayList<Comment> messages = new ArrayList<>();
  private static final String HOMEPAGE = "/index.html";
  // properties of the Comments
  private static final String CLASS_TYPE = "Comment";
  private static final String COMMENT_TIMESTAMP = "timestampUTC";
  private static final String COMMENT_NAME = "name";
  private static final String COMMENT_EMAIL = "email";
  private static final String COMMENT_SUBJECT = "subject";
  private static final String FULL_COMMENTS = "comments";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query(CLASS_TYPE).addSort(COMMENT_TIMESTAMP, SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    int numResults = Integer.parseInt(request.getParameter("num-results"));

    ArrayList<Comment> commentList = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      numResults--;
      long id = entity.getKey().getId();
      String timestampUTC = (String) entity.getProperty(COMMENT_TIMESTAMP);
      String name = (String) entity.getProperty(COMMENT_NAME);
      String email = (String) entity.getProperty(COMMENT_EMAIL);
      String subject = (String) entity.getProperty(COMMENT_SUBJECT);
      String comments = (String) entity.getProperty(FULL_COMMENTS);

      Comment comment = new Comment(id, timestampUTC, name, email, subject, comments);
      commentList.add(comment);
      if (numResults <= 0) {
        break;
      }
    }

    String json = convertToJsonList(commentList);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /** Converts an Arraylist<Comment> instance into a JSON string using gson */
  private String convertToJsonList(ArrayList<Comment> messages) {
    Gson gson = new Gson();
    return gson.toJson(messages);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String name = getParameter(request, COMMENT_NAME, "");
    String email = getParameter(request, COMMENT_EMAIL, "");
    String subject = getParameter(request, COMMENT_SUBJECT, "");
    String comments = getParameter(request, FULL_COMMENTS, "");
    String timestampUTC = new Timestamp(System.currentTimeMillis()).toString();

    // Store comments.
    Entity commentEntity = new Entity(CLASS_TYPE);
    commentEntity.setProperty(COMMENT_TIMESTAMP, timestampUTC);
    commentEntity.setProperty(COMMENT_NAME, name);
    commentEntity.setProperty(COMMENT_EMAIL, email);
    commentEntity.setProperty(COMMENT_SUBJECT, subject);
    commentEntity.setProperty(FULL_COMMENTS, comments);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    response.sendRedirect(HOMEPAGE);
  }

  /**
   * @return the request parameter, or the default value if the parameter was not specified by the
   *     client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
