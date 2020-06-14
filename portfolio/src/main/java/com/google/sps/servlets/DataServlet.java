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
import java.util.List;
import com.google.sps.data.Comments;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/chat-room")
public class DataServlet extends HttpServlet {
    private List<String> comments;

  @Override
  public void init() {
    comments = new ArrayList<String>();
    comments.add("Hello Franklin, could you write a blog on your steps to Computer Science. Specifically which books and resources did you use. -Joe");
    comments.add("Hey, I was going through your profile and realized that you were passionate about photography. When can we expect the release of the cool images. -Doe");
    comments.add("Hey Frank!, it's been a long time, how do I connect with you. Can't seem to see any of your social media handles to connect with you. -Alex");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Comments commentObject = new Comments(comments.get(0), comments.get(1), comments.get(2));
    String json = convertToJsonUsingGson(commentObject);
    response.getWriter().println(json);
    response.getWriter().flush();
  }

  private String convertToJsonUsingGson(Comments comment) {
    Gson gson = new Gson();
    String json = gson.toJson(comment);
    return json;
  }

}
