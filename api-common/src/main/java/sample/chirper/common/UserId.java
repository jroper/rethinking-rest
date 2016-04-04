/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.chirper.common;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.security.auth.Subject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import java.security.Principal;

@Immutable
public final class UserId implements Principal {

  public final String userId;

  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public UserId(String userId) {
    this.userId = Preconditions.checkNotNull(userId, "userId");
  }

  @JsonValue
  public String getUserId() {
    return userId;
  }

  @Override
  public String getName() {
    return userId;
  }

  @Override
  public boolean implies(Subject subject) {
    return false;
  }

  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another)
      return true;
    return another instanceof UserId && equalTo((UserId) another);
  }

  private boolean equalTo(UserId another) {
    return userId.equals(another.userId);
  }

  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + userId.hashCode();
    return h;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper("UserId").add("userId", userId).toString();
  }

}
