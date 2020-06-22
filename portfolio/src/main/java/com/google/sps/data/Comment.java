package com.google.sps.data;

import com.google.cloud.vision.v1.EntityAnnotation;
import java.util.List;

/** Class containing server statistics. */
public final class Comment {
  private final String messageSubject;
  private final String text;
  private final String email;
  private final String imageUrl;
  private List<EntityAnnotation> entityAnnotations;

  public Comment(String subject, String text, String email, String imageUrl) {
    this.messageSubject = subject;
    this.text = text;
    this.email = email;
    this.imageUrl = imageUrl;
  }

  public Comment(String subject, String text, String email, String imageUrl, List<EntityAnnotation> annotations) {
    this.messageSubject = subject;
    this.text = text;
    this.email = email;
    this.imageUrl = imageUrl;
    this.entityAnnotations = annotations;
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

  public List<EntityAnnotation> getEntityAnnotations() {
      return entityAnnotations;
  }
  
}