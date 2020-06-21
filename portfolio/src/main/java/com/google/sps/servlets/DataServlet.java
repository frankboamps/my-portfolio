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
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.util.List;
import com.google.sps.data.Comment;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/chat-room")
public class DataServlet extends HttpServlet {

  @Override
  public void init() {
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");

    Query query = new Query("Comment").addSort("sender", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    System.out.println(results);
    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String sender = (String) entity.getProperty("sender");
      String message = (String) entity.getProperty("message");
      String email = (String) entity.getProperty("email");

      Comment comment = new Comment(sender, message, email);
      comments.add(comment);
    }
    System.out.println(comments);
    Gson gson = new Gson();
    String json = new Gson().toJson(comments);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    // Only logged-in users can post messages
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/chat-room");
      return;
    }
    
    response.setContentType("text/html");
    response.getWriter().println("Please Add a comment.");
    addNewComment(request, userService);
    // Redirect back to the HTML page.
    response.sendRedirect("/chatroom.html");
  }

  private void addNewComment(HttpServletRequest request, UserService userService) {
      String tempSubject = request.getParameter("subject");
      String commentText = request.getParameter("text");
      String text = request.getParameter("text");
      String email = userService.getCurrentUser().getEmail();

      Entity commentEntity = new Entity("Comment");
     commentEntity.setProperty("sender", tempSubject);
     commentEntity.setProperty("message", commentText);
     commentEntity.setProperty("email", commentText);
     commentEntity.setProperty("timestamp", System.currentTimeMillis());

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);
  }

}
