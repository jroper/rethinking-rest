/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.chirper.friend.impl;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.lightbend.lagom.javadsl.server.HeaderServiceCall;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.NotFound;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;

import akka.NotUsed;
import sample.chirper.common.server.Authenticated;
import sample.chirper.friend.api.FriendRequest;
import sample.chirper.common.UserId;
import sample.chirper.friend.api.FriendService;
import sample.chirper.friend.api.User;
import sample.chirper.friend.impl.FriendCommand.*;

public class FriendServiceImpl implements FriendService {

  private final PersistentEntityRegistry persistentEntities;
  private final CassandraSession db;

  @Inject
  public FriendServiceImpl(PersistentEntityRegistry persistentEntities, CassandraReadSide readSide,
      CassandraSession db) {
    this.persistentEntities = persistentEntities;
    this.db = db;

    persistentEntities.register(FriendEntity.class);
    readSide.register(FriendEventProcessor.class);
  }

  @Override
  public ServiceCall<UserId, NotUsed, User> getUser() {
    return (id, request) -> {
      return friendEntityRef(id).ask(new GetUser()).thenApply(reply -> {
        if (reply.user.isPresent())
          return reply.user.get();
        else
          throw new NotFound(id + " not found");
      });
    };
  }

  @Override
  public ServiceCall<NotUsed, User, NotUsed> createUser() {
    return (id, request) -> {
      return friendEntityRef(request.userId).ask(new CreateUser(request))
          .thenApply(ack -> NotUsed.getInstance());
    };
  }

  @Override
  public ServiceCall<FriendRequest, NotUsed, NotUsed> addFriend() {
    return Authenticated.enforceUserId(FriendRequest::getUserId,
        (friendRequest, notUsed) ->
          friendEntityRef(friendRequest.userId)
              .ask(new AddFriend(friendRequest.friendId))
              .thenApply(ack -> NotUsed.getInstance())
    );
  }

  @Override
  public ServiceCall<UserId, NotUsed, PSequence<UserId>> getFollowers() {
    return (userId, req) -> {
      CompletionStage<PSequence<UserId>> result = db.selectAll("SELECT * FROM follower WHERE userId = ?", userId.userId)
        .thenApply(rows -> {
        List<UserId> followers = rows.stream().map(row -> new UserId(row.getString("followedBy"))).collect(Collectors.toList());
        return TreePVector.from(followers);
      });
      return result;
    };
  }

  private PersistentEntityRef<FriendCommand> friendEntityRef(UserId userId) {
    PersistentEntityRef<FriendCommand> ref = persistentEntities.refFor(FriendEntity.class, userId.userId);
    return ref;
  }

}
