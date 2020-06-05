package com.google.sps.data;

/** Class representing a comment object*/
public class Comment {
  private String content;
  private Comment parent;
  private long postTime;

  public Comment(String content, long postTime, Comment parent) {
    this.content = content;
    this.postTime = postTime;
    this.parent = parent;
  }

  public String getContent() {
    return content;
  }

  public long getPostTime() {
    return postTime;
  }

  public boolean isReply() {
    return this.parent!=null;
  }

  public void setParent(Comment p) {
    this.parent = p;
  }
}