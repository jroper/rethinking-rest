/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.chirper.friend.impl;

import java.time.Instant;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.serialization.Jsonable;
import sample.chirper.common.UserId;

public interface FriendEvent extends Jsonable, AggregateEvent<FriendEvent> {

  @Override
  default AggregateEventTag<FriendEvent> aggregateTag() {
    return FriendEventTag.INSTANCE;
  }

  @SuppressWarnings("serial")
  @Immutable
  final class UserCreated implements FriendEvent {
    public final UserId userId;
    public final String name;
    public final Instant timestamp;

    public UserCreated(UserId userId, String name) {
      this(userId, name, Optional.empty());
    }

    @JsonCreator
    private UserCreated(UserId userId, String name, Optional<Instant> timestamp) {
      this.userId = Preconditions.checkNotNull(userId, "userId");
      this.name = Preconditions.checkNotNull(name, "name");
      this.timestamp = timestamp.orElseGet(() -> Instant.now());
    }

    @Override
    public boolean equals(@Nullable Object another) {
      if (this == another)
        return true;
      return another instanceof UserCreated && equalTo((UserCreated) another);
    }

    private boolean equalTo(UserCreated another) {
      return userId.equals(another.userId) && name.equals(another.name) && timestamp.equals(another.timestamp);
    }

    @Override
    public int hashCode() {
      int h = 31;
      h = h * 17 + userId.hashCode();
      h = h * 17 + name.hashCode();
      h = h * 17 + timestamp.hashCode();
      return h;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper("UserCreated").add("userId", userId).add("name", name)
          .add("timestamp", timestamp).toString();
    }
  }

  @SuppressWarnings("serial")
  @Immutable
  final class FriendAdded implements FriendEvent {
    public final UserId userId;
    public final UserId friendId;
    public final Instant timestamp;

    public FriendAdded(UserId userId, UserId friendId) {
      this(userId, friendId, Optional.empty());
    }

    @JsonCreator
    public FriendAdded(UserId userId, UserId friendId, Optional<Instant> timestamp) {
      this.userId = Preconditions.checkNotNull(userId, "userId");
      this.friendId = Preconditions.checkNotNull(friendId, "friendId");
      this.timestamp = timestamp.orElseGet(() -> Instant.now());
    }

    @Override
    public boolean equals(@Nullable Object another) {
      if (this == another)
        return true;
      return another instanceof FriendAdded && equalTo((FriendAdded) another);
    }

    private boolean equalTo(FriendAdded another) {
      return userId.equals(another.userId) && friendId.equals(another.friendId) && timestamp.equals(another.timestamp);
    }

    @Override
    public int hashCode() {
      int h = 31;
      h = h * 17 + userId.hashCode();
      h = h * 17 + friendId.hashCode();
      h = h * 17 + timestamp.hashCode();
      return h;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper("FriendAdded").add("userId", userId).add("friendId", friendId)
          .add("timestamp", timestamp).toString();
    }
  }
}
