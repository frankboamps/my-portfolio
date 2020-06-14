package com.google.sps.data;

import java.util.Date;

/** Class containing server statistics. */
public final class Comments {

  private final String comment1;
  private final String comment2;
  private final String comment3;

  public Comments(String comment1, String comment2, String comment3) {
    this.comment1 = comment1;
    this.comment2 = comment2;
    this.comment3 = comment3;
  }

  public String getComment1() {
      return comment1;
  }

  public String getComment2() {
      return comment2;
  }

  public String getComment3() {
      return comment3;
  }

}