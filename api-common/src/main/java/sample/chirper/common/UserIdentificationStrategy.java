package sample.chirper.common;

import com.google.common.base.Splitter;
import com.lightbend.lagom.javadsl.api.transport.HeaderTransformer;
import com.lightbend.lagom.javadsl.api.transport.RequestHeader;
import com.lightbend.lagom.javadsl.api.transport.ResponseHeader;

import java.util.Optional;

public class UserIdentificationStrategy implements HeaderTransformer {
  public static final UserIdentificationStrategy INSTANCE = new UserIdentificationStrategy();

  @Override
  public RequestHeader transformClientRequest(RequestHeader request) {
    return request.principal().filter(p -> p instanceof UserId)
        .map(p -> request.withHeader("User-Token", p.getName()))
        .orElse(request);
  }

  @Override
  public RequestHeader transformServerRequest(RequestHeader request) {
    return request.getHeader("User-Token")
        .map(Optional::of)
        // Query parameter is used for WebSocket requests, since browsers don't allow custom headers added to WebSocket
        // requests.
        .orElseGet(() -> getQueryParam(request, "usertoken"))
        .map(UserId::new)
        .map(request::withPrincipal)
        .orElse(request);
  }

  private Optional<String> getQueryParam(RequestHeader request, String param) {
    String queryString = request.uri().getRawQuery();
    if (queryString == null) {
      return Optional.empty();
    } else {
      return Splitter.on('&').splitToList(request.uri().getRawQuery()).stream()
          .map(q -> q.split("=", 2))
          .filter(q -> q.length == 2 && q[0].equals(param))
          .map(q -> q[1])
          .findFirst();
    }
  }

  @Override
  public ResponseHeader transformServerResponse(ResponseHeader response, RequestHeader request) {
    return response;
  }

  @Override
  public ResponseHeader transformClientResponse(ResponseHeader response, RequestHeader request) {
    return response;
  }
}
