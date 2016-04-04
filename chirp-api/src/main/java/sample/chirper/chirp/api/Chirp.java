/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.chirper.chirp.api;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.serialization.Jsonable;
import sample.chirper.common.UserId;

@SuppressWarnings("serial")
@Immutable
public final class Chirp implements Jsonable {
  public final UserId userId;
  public final String message;
  public final Instant timestamp;
  public final String uuid;
  public final int likes;

  public Chirp (UserId userId, String message) {
    this(userId, message, Optional.empty(), Optional.empty(), 0);
  }

  @JsonCreator
  public Chirp(UserId userId, String message, Optional<Instant> timestamp, Optional<String> uuid, int likes) {
    this.userId = Preconditions.checkNotNull(userId, "userId");
    this.message = Preconditions.checkNotNull(message, "message");
    this.timestamp = timestamp.orElseGet(() -> Instant.now());
    this.uuid = uuid.orElseGet(() -> UUID.randomUUID().toString());
    this.likes = likes;
  }

  private Chirp(UserId userId, String message, Instant timestamp, String uuid, int likes) {
    this.userId = userId;
    this.message = message;
    this.timestamp = timestamp;
    this.uuid = uuid;
    this.likes = likes;
  }

  public Chirp withLikes(int likes) {
    return new Chirp(userId, message, timestamp, uuid, likes);
  }

  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another)
      return true;
      return another instanceof Chirp && equalTo((Chirp) another);
  }

  private boolean equalTo(Chirp another) {
    return userId.equals(another.userId) && message.equals(another.message) && timestamp.equals(another.timestamp)
      && uuid.equals(another.uuid) && likes == another.likes;
  }

  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + userId.hashCode();
    h = h * 17 + message.hashCode();
    h = h * 17 + timestamp.hashCode();
    h = h * 17 + uuid.hashCode();
    h = h * 17 + likes;
    return h;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper("Chirp")
      .add("userId", userId)
      .add("message", message)
      .add("timestamp", timestamp)
      .add("uuid", uuid)
      .add("likes", likes)
      .toString();
  }
}
