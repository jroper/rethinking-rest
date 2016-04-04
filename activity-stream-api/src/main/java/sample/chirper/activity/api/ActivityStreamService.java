/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.chirper.activity.api;

import com.lightbend.lagom.javadsl.api.deser.IdSerializers;
import sample.chirper.chirp.api.Chirp;

import akka.stream.javadsl.Source;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import sample.chirper.common.UserId;

import static com.lightbend.lagom.javadsl.api.Service.*;

public interface ActivityStreamService extends Service {

  ServiceCall<UserId, NotUsed, Source<Chirp, ?>> getLiveActivityStream();

  ServiceCall<UserId, NotUsed, Source<Chirp, ?>> getHistoricalActivityStream();

  @Override
  default Descriptor descriptor() {
    // @formatter:off
    return named("activityservice").with(
        pathCall("/api/activity/:userId/live", getLiveActivityStream()),
        pathCall("/api/activity/:userId/history", getHistoricalActivityStream())
      ).withAutoAcl(true).with(UserId.class,
        IdSerializers.create("UserId", UserId::new, UserId::getUserId));
    // @formatter:on
  }
}
