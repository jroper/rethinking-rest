/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.chirper.chirp.api;

import akka.stream.javadsl.Source;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.deser.IdSerializers;
import sample.chirper.common.UserId;
import sample.chirper.common.UserIdentificationStrategy;

import static com.lightbend.lagom.javadsl.api.Service.*;

public interface ChirpService extends Service {

  ServiceCall<UserId, Chirp, NotUsed> addChirp();
  
  ServiceCall<NotUsed, LiveChirpsRequest, Source<Chirp, ?>> getLiveChirps();
  
  ServiceCall<NotUsed, HistoricalChirpsRequest, Source<Chirp, ?>> getHistoricalChirps();

  @Override
  default Descriptor descriptor() {
    // @formatter:off
    return named("chirpservice").with(
        pathCall("/api/chirps/:userId", addChirp()),
        pathCall("/api/chirpstream/live", getLiveChirps()),
        pathCall("/api/chirpstream/history", getHistoricalChirps())
      ).withAutoAcl(true).with(UserId.class,
        IdSerializers.create("UserId", UserId::new, UserId::getUserId))
        .withServiceIdentificationStrategy(UserIdentificationStrategy.INSTANCE);
    // @formatter:on
  }
}
