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
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns hard-coded comments */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  // default values without user input
  private ArrayList<String> messages = new ArrayList<String>(
      Arrays.asList("No comments")
  );
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    Query query = new Query("Comment").addSort("timestamp_UTC", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    ArrayList<ArrayList<String>> comment_lst = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
        String timestamp_UTC = (String)entity.getProperty("timestamp_UTC");
        String name = (String)entity.getProperty("name");
        String email = (String)entity.getProperty("email");
        String subject = (String)entity.getProperty("subject");
        String comments = (String)entity.getProperty("comments");
        
        ArrayList<String> comment = new ArrayList<String>();
        comment.add(name);
        comment.add(email);
        comment.add(subject);
        comment.add(comments);
        comment.add(timestamp_UTC);
        comment_lst.add(comment);
    }

    String json = convertToJsonList(comment_lst);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
  /**
   * Converts an Arraylist<String> instance into a JSON string using gson
   */
  private String convertToJson(ArrayList<String> messages) {
    Gson gson = new Gson();
    return gson.toJson(messages);
  }

    /**
   * Converts an Arraylist<String> instance into a JSON string using gson
   */
  private String convertToJsonList(ArrayList<ArrayList<String>> messages) {
    Gson gson = new Gson();
    return gson.toJson(messages);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String name = getParameter(request, "name", "");
    String email = getParameter(request, "email", "");
    String subject = getParameter(request, "subject", "");
    String comments = getParameter(request, "comments", "");
    String timestamp_UTC = new Timestamp(System.currentTimeMillis()).toString();
    
    messages = new ArrayList<String>();
    messages.add(name);
    messages.add(email);
    messages.add(subject);
    messages.add(comments);
    messages.add(timestamp_UTC);

    // Respond with the result.
    response.setContentType("text/html;");
    response.getWriter().println(convertToJson(messages));

    // Store comments.
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("timestamp_UTC", timestamp_UTC);
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("email", email);
    commentEntity.setProperty("subject", subject);
    commentEntity.setProperty("comments", comments);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect("/index.html");
  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
