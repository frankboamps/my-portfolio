package com.google.sps.data;

import java.util.Date;

/** Class containing server statistics. */
public final class Comment {
  private final String messageSubject;
  private final String text;
  private final String email;
  private final String imageUrl;

  public Comment(String subject, String text, String email, String imageUrl) {
    this.messageSubject = subject;
    this.text = text;
    this.email = email;
    this.imageUrl = imageUrl;
  }

  public String getMessageSubject() {
      return messageSubject;
  }

  public String getText() {
      return text;
  }

  public String getEmail() {
      return email;
  }

  public String getImageUrl() {
      return imageUrl;
  }
  
}