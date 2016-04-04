package sample.chirper.common.server;

import com.lightbend.lagom.javadsl.api.transport.Forbidden;
import com.lightbend.lagom.javadsl.server.HeaderServiceCall;
import com.lightbend.lagom.javadsl.server.ServerServiceCall;
import sample.chirper.common.UserId;

import java.security.Principal;
import java.util.function.Function;

public class Authenticated {

  /**
   * Enforce that the given UserId identified service call can only be done by that user.
   */
  public static <Request, Response> ServerServiceCall<UserId, Request, Response> enforceUserId(ServerServiceCall<UserId, Request, Response> serviceCall) {
    return enforceUserId(Function.identity(), serviceCall);
  }

  /**
   * Enforce that the given service call can only be done by the user id that gets extracted from the Id.
   */
  public static <Id, Request, Response> ServerServiceCall<Id, Request, Response> enforceUserId(
      Function<Id, UserId> userIdFromid, ServerServiceCall<Id, Request, Response> serviceCall) {
    return HeaderServiceCall.of((requestHeader, id, request) -> {
      UserId userId = userIdFromid.apply(id);
      Principal principal = requestHeader.principal()
          .orElseGet(() -> { throw new Forbidden("You must be authenticated"); });

      if (!userId.equals(principal)) {
        throw new Forbidden("You are not allowed to perform this service call");
      } else {
        return serviceCall.invokeWithHeaders(requestHeader, id, request);
      }
    });
  }

}
