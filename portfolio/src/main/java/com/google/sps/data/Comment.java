package com.google.sps.data;

/** Class representing a comment object*/
public class Comment {
  private String content;
  private String postTime;
  private long id;

  public Comment(String content, String postTime, long id) {
    this.content = content;
    this.postTime = postTime;
    this.id = id;
  }

}