/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.chirper.friend.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.deser.PathParamSerializers;
import com.lightbend.lagom.javadsl.api.transport.Method;
import org.pcollections.PSequence;
import sample.chirper.common.UserId;
import sample.chirper.common.UserIdentificationStrategy;

/**
 * The friend service.
 */
public interface FriendService extends Service {

  /**
   * Service call for getting a user.
   *
   * The ID of this service call is the user name, and the response message is the User object.
   */
  ServiceCall<NotUsed, User> getUser(UserId userId);

  /**
   * Service call for creating a user.
   *
   * The request message is the User to create.
   */
  ServiceCall<User, NotUsed> createUser();

  /**
   * Service call for adding a friend.
   */
  ServiceCall<NotUsed, NotUsed> addFriend(UserId userId, UserId friendId);

  /**
   * Service call for getting the followers of a user.
   *
   * The ID for this service call is the Id of the user to get the followers for.
   * The response message is the list of follower IDs.
   */
  ServiceCall<NotUsed, PSequence<UserId>> getFollowers(UserId userId);

  @Override
  default Descriptor descriptor() {
    // @formatter:off
    return named("friendservice").withCalls(
        restCall(Method.GET,    "/api/users/:id", this::getUser),
        restCall(Method.POST,   "/api/users", this::createUser),
        restCall(Method.PUT,    "/api/users/:userId/friends/:friendId", this::addFriend),
        restCall(Method.GET,    "/api/users/:id/followers", this::getFollowers)
      ).withAutoAcl(true).withPathParamSerializer(UserId.class,
            PathParamSerializers.required("UserId", UserId::new, UserId::getUserId))
            .withHeaderFilter(UserIdentificationStrategy.INSTANCE);
    // @formatter:on
  }
}
