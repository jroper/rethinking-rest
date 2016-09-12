package sample.chirper.like.api;

import static com.lightbend.lagom.javadsl.api.Service.*;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.deser.PathParamSerializers;
import com.lightbend.lagom.javadsl.api.transport.Method;
import org.pcollections.PCollection;
import sample.chirper.common.UserId;
import sample.chirper.common.UserIdentificationStrategy;

import java.util.Optional;
import java.util.UUID;

/**
 * The friend service.
 */
public interface LikeService extends Service {

  /**
   * Like a Chirp.
   */
  ServiceCall<NotUsed, NotUsed> likeChirp(UserId userId, String chirpId, UserId liker);

  /**
   * Unlike a Chirp.
   */
  ServiceCall<NotUsed, NotUsed> unlikeChirp(UserId userId, String chirpId, UserId liker);

  /**
   * Get all the users that like a chirp.
   */
  ServiceCall<NotUsed, PCollection<UserId>> getLikes(UserId userId, String chirpId);

  /**
   * A stream of like counts that is published every time the count of likes for a chirp is updated.
   */
  ServiceCall<NotUsed, Source<Likes, NotUsed>> counts(Optional<UUID> offset);

  @Override
  default Descriptor descriptor() {
    // @formatter:off
    return named("likeservice").withCalls(
        restCall(Method.PUT,    "/api/chirps/:userId/:uuid/likes/:likerId", this::likeChirp),
        restCall(Method.DELETE, "/api/chirps/:userId/:uuid/likes/:likerId", this::unlikeChirp),
        restCall(Method.GET,    "/api/chirps/:userId/:uuid/likes", this::getLikes),
        pathCall("/api/likecounts?offset", this::counts).withAutoAcl(false)
    ).withAutoAcl(true)
            .withPathParamSerializer(UUID.class, PathParamSerializers.required("UUID", UUID::fromString, UUID::toString))
        .withPathParamSerializer(UserId.class, PathParamSerializers.required("UserId", UserId::new, UserId::getUserId))
        .withHeaderFilter(UserIdentificationStrategy.INSTANCE);
    // @formatter:on
  }
}
