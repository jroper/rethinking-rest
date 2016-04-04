/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.chirper.friend.api;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import sample.chirper.common.UserId;

@Immutable
public final class User {
  public final UserId userId;
  public final String name;
  public final PSequence<UserId> friends;

  public User(UserId userId, String name) {
    this(userId, name, Optional.empty());
  }

  @JsonCreator
  public User(UserId userId, String name, Optional<PSequence<UserId>> friends) {
    this.userId = Preconditions.checkNotNull(userId, "userId");
    this.name = Preconditions.checkNotNull(name, "name");
    this.friends = friends.orElse(TreePVector.empty());
  }

  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another)
      return true;
    return another instanceof User && equalTo((User) another);
  }

  private boolean equalTo(User another) {
    return userId.equals(another.userId) && name.equals(another.name) && friends.equals(another.friends);
  }

  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + userId.hashCode();
    h = h * 17 + name.hashCode();
    h = h * 17 + friends.hashCode();
    return h;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper("User").add("userId", userId).add("name", name).add("friends", friends)
        .toString();
  }
}
