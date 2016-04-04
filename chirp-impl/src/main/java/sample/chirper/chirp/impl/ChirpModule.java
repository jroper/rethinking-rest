/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.chirper.chirp.impl;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.cluster.singleton.ClusterSingletonManager;
import akka.cluster.singleton.ClusterSingletonManagerSettings;
import akka.pattern.Backoff;
import akka.pattern.BackoffSupervisor;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.name.Names;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

import play.libs.akka.AkkaGuiceSupport;
import sample.chirper.chirp.api.ChirpService;
import sample.chirper.like.api.LikeService;
import scala.concurrent.duration.Duration;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class ChirpModule extends AbstractModule implements ServiceGuiceSupport, AkkaGuiceSupport {
  @Override
  protected void configure() {
    bindServices(serviceBinding(ChirpService.class, ChirpServiceImpl.class));
    bindClient(LikeService.class);

    // Bind the likeCountSubscriber as an eager singleton
    bind(ActorRef.class)
        .annotatedWith(Names.named("likeCountSubscriber"))
        .toProvider(LikeCountSubscriberProvider.class)
        .asEagerSingleton();
  }

  public static class LikeCountSubscriberProvider implements Provider<ActorRef> {
    private final ActorSystem actorSystem;
    private final Provider<LikeCountSubscriber> likeCountSubscriber;

    @Inject
    public LikeCountSubscriberProvider(ActorSystem actorSystem,
        Provider<LikeCountSubscriber> likeCountSubscriber) {
      this.actorSystem = actorSystem;
      this.likeCountSubscriber = likeCountSubscriber;
    }

    public ActorRef get() {
      return null;
    }
  }
}
