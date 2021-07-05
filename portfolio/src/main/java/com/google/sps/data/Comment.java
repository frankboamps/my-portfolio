package com.google.sps.data;

import java.util.Date;

/** Class containing server statistics. */
public final class Comment {
  private final String messageSubject;
  private final String text;

  public Comment(String subject, String text) {
    this.messageSubject = subject;
    this.text = text;
  }

  public String getMessageSubject() {
      return messageSubject;
  }

  public String getText() {
      return text;
  }
  
}