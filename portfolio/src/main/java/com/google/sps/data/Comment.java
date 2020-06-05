package com.google.sps.data;

/** Class representing a comment object*/
public class Comment {
  private String content;
  private Comment parent;
  private String postTime;
  private long id;

  public Comment(String content, String postTime, Comment parent, long id) {
    this.content = content;
    this.postTime = postTime;
    this.parent = parent;
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public String getPostTime() {
    return postTime;
  }

  public boolean isReply() {
    return this.parent!=null;
  }

  public long getId(){
    return this.id;
  }

  public void setId(long newId) {
    this.id = newId;
  }

  public void setParent(Comment p) {
    this.parent = p;
  }
}