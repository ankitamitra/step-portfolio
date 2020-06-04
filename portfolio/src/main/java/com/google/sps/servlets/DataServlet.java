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
  private String homePage = "/index.html";
  //properties of the Comments
  private String classType = "Comment";
  private String commentTimestamp = "timestampUTC";
  private String commentName = "name";
  private String commentEmail = "email";
  private String commentSubject = "subject";
  private String fullComments = "comments";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    Query query = new Query(classType).addSort(commentTimestamp, SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    String numResultsString = (request.getQueryString());
    int numResults;
    if (numResultsString == null) {
      numResults = 1;
    } else if (numResultsString.equals("num-results=1")) {
      numResults = 1;
    } else if (numResultsString.equals("num-results=2")) {
      numResults = 2;
    } else if (numResultsString.equals("num-results=3")) {
      numResults = 3;
    } else if (numResultsString.equals("num-results=4")) {
      numResults = 4;
    } else {
      numResults = 1;
    }

    ArrayList<Comment> commentList = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      numResults--;
      long id = entity.getKey().getId();
      String timestampUTC = (String) entity.getProperty(commentTimestamp);
      String name = (String) entity.getProperty(commentName);
      String email = (String) entity.getProperty(commentEmail);
      String subject = (String) entity.getProperty(commentSubject);
      String comments = (String) entity.getProperty(fullComments);

      Comment comment = new Comment(id, timestampUTC, name, email, subject, comments);
      commentList.add(comment);
      if (numResults <= 0){
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
    String name = getParameter(request, commentName, "");
    String email = getParameter(request, commentEmail, "");
    String subject = getParameter(request, commentSubject, "");
    String comments = getParameter(request, fullComments, "");
    String timestampUTC = new Timestamp(System.currentTimeMillis()).toString();

    // Store comments.
    Entity commentEntity = new Entity(classType);
    commentEntity.setProperty(commentTimestamp, timestampUTC);
    commentEntity.setProperty(commentName, name);
    commentEntity.setProperty(commentEmail, email);
    commentEntity.setProperty(commentSubject, subject);
    commentEntity.setProperty(fullComments, comments);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    response.sendRedirect(homePage);
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
