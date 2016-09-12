/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.chirper.chirp.api;

import akka.stream.javadsl.Source;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.deser.PathParamSerializers;
import sample.chirper.common.UserId;
import sample.chirper.common.UserIdentificationStrategy;

import static com.lightbend.lagom.javadsl.api.Service.*;

public interface ChirpService extends Service {

  ServiceCall<Chirp, NotUsed> addChirp(UserId userId);
  
  ServiceCall<LiveChirpsRequest, Source<Chirp, ?>> getLiveChirps();
  
  ServiceCall<HistoricalChirpsRequest, Source<Chirp, ?>> getHistoricalChirps();

  @Override
  default Descriptor descriptor() {
    // @formatter:off
    return named("chirpservice").withCalls(
        pathCall("/api/chirps/:userId", this::addChirp),
        pathCall("/api/chirpstream/live", this::getLiveChirps),
        pathCall("/api/chirpstream/history", this::getHistoricalChirps)
      ).withAutoAcl(true).withPathParamSerializer(UserId.class,
        PathParamSerializers.required("UserId", UserId::new, UserId::getUserId))
        .withHeaderFilter(UserIdentificationStrategy.INSTANCE);
    // @formatter:on
  }
}
