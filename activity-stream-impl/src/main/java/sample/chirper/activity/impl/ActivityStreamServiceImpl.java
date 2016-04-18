/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.chirper.activity.impl;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import sample.chirper.activity.api.ActivityStreamService;
import sample.chirper.chirp.api.Chirp;
import sample.chirper.chirp.api.ChirpService;
import sample.chirper.chirp.api.HistoricalChirpsRequest;
import sample.chirper.chirp.api.LiveChirpsRequest;
import sample.chirper.friend.api.FriendService;

import akka.stream.javadsl.Source;
import sample.chirper.common.UserId;

public class ActivityStreamServiceImpl implements ActivityStreamService {

  private final FriendService friendService;
  private final ChirpService chirpService;

  @Inject
  public ActivityStreamServiceImpl(FriendService friendService, ChirpService chirpService) {
    this.friendService = friendService;
    this.chirpService = chirpService;
  }

  @Override
  public ServiceCall<UserId, NotUsed, Source<Chirp, ?>> getLiveActivityStream() {
    return (id, req) -> {
      return friendService.getUser().invoke(id, NotUsed.getInstance()).thenCompose(user -> {
        PSequence<UserId> userIds = user.friends.plus(id);
        LiveChirpsRequest chirpsReq =  new LiveChirpsRequest(userIds);
        // Note that this stream will not include changes to friend associates,
        // e.g. adding a new friend.
        CompletionStage<Source<Chirp, ?>> result = chirpService.getLiveChirps().invoke(chirpsReq);
        return result;
      });
    };
  }

  @Override
  public ServiceCall<UserId, NotUsed, Source<Chirp, ?>> getHistoricalActivityStream() {
    return (id, req) ->
      friendService.getUser().invoke(id, NotUsed.getInstance()).thenCompose(user -> {
        PSequence<UserId> userIds = user.friends.plus(id);
        // FIXME we should use HistoricalActivityStreamReq request parameter
        Instant fromTime = Instant.now().minus(Duration.ofDays(7));
        HistoricalChirpsRequest chirpsReq = new HistoricalChirpsRequest(fromTime, userIds);
        CompletionStage<Source<Chirp, ?>> result = chirpService.getHistoricalChirps().invoke(chirpsReq);
        return result;
      });
  }

}
