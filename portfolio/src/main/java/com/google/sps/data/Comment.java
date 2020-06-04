package com.google.sps.data;

/** An item on a todo list. */
public final class Comment {

  private final long id;
  private final String timestamp_UTC;
  private final String name;
  private final String email;
  private final String subject;
  private final String comments;

  public Comment(long id, String timestamp_UTC, String name, String email, String subject, String comments) {
    this.id = id;
    this.timestamp_UTC = timestamp_UTC;
    this.name = name;
    this.email = email;
    this.subject = subject;
    this.comments = comments;
  }
}
