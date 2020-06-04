package com.google.sps.data;

/** An item on a todo list. */
public final class Comment {

  private final long id;
  private final String timestampUTC;
  private final String name;
  private final String email;
  private final String subject;
  private final String comments;

  public Comment(
      long id, String timestampUTC, String name, String email, String subject, String comments) {
    this.id = id;
    this.timestampUTC = timestampUTC;
    this.name = name;
    this.email = email;
    this.subject = subject;
    this.comments = comments;
  }
}
