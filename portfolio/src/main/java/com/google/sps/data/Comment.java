package com.google.sps.data;

import com.google.cloud.vision.v1.EntityAnnotation;
import java.util.List;

/** Class containing server statistics. */
public final class Comment {
  private final String messageSubject;
  private final String text;
  private final String email;
  private final String imageUrl;
  private final float score;

  public Comment(String subject, String text, String email, String imageUrl, float score) {
    this.messageSubject = subject;
    this.text = text;
    this.email = email;
    this.imageUrl = imageUrl;
    this.score = score;
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

   public float getScore() {
      return score;
  }
  
}