/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.chirper.friend.impl;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;

import akka.Done;
import sample.chirper.friend.api.User;
import sample.chirper.common.UserId;

public interface FriendCommand extends Jsonable {

  @SuppressWarnings("serial")
  @Immutable
  final class CreateUser implements FriendCommand, PersistentEntity.ReplyType<Done> {
    public final User user;

    @JsonCreator
    public CreateUser(User user) {
      this.user = Preconditions.checkNotNull(user, "user");
    }

    @Override
    public boolean equals(@Nullable Object another) {
      if (this == another)
        return true;
      return another instanceof CreateUser && equalTo((CreateUser) another);
    }

    private boolean equalTo(CreateUser another) {
      return user.equals(another.user);
    }

    @Override
    public int hashCode() {
      int h = 31;
      h = h * 17 + user.hashCode();
      return h;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper("CreateUser").add("user", user).toString();
    }
  }


  @SuppressWarnings("serial")
  @Immutable
  final class GetUser implements FriendCommand,PersistentEntity.ReplyType<GetUserReply> {

    @Override
    public boolean equals(@Nullable Object another) {
      return this instanceof GetUser;
    }

    @Override
    public int hashCode() {
      return 2053226012;
    }

    @Override
    public String toString() {
      return "GetUser{}";
    } 
  }

  @SuppressWarnings("serial")
  @Immutable
  final class GetUserReply implements Jsonable {
    public final Optional<User> user;

    @JsonCreator
    public GetUserReply(Optional<User> user) {
      this.user = Preconditions.checkNotNull(user, "user");
    }

    @Override
    public boolean equals(@Nullable Object another) {
      if (this == another)
        return true;
      return another instanceof GetUserReply && equalTo((GetUserReply) another);
    }

    private boolean equalTo(GetUserReply another) {
      return user.equals(another.user);
    }

    @Override
    public int hashCode() {
      int h = 31;
      h = h * 17 + user.hashCode();
      return h;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper("GetUserReply").add("user", user).toString();
    }
  }

  @SuppressWarnings("serial")
  @Immutable
  final class AddFriend implements FriendCommand,PersistentEntity.ReplyType<Done> {
    public final UserId friendUserId;

    @JsonCreator
    public AddFriend(UserId friendUserId) {
      this.friendUserId = Preconditions.checkNotNull(friendUserId, "friendUserId");
    }
    
    @Override
    public boolean equals(@Nullable Object another) {
      if (this == another)
        return true;
      return another instanceof AddFriend && equalTo((AddFriend) another);
    }

    private boolean equalTo(AddFriend another) {
      return friendUserId.equals(another.friendUserId);
    }

    @Override
    public int hashCode() {
      int h = 31;
      h = h * 17 + friendUserId.hashCode();
      return h;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper("AddFriend").add("friendUserId", friendUserId).toString();
    }
  }
}
