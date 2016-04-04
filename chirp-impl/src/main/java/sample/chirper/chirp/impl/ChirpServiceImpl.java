/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.chirper.chirp.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.pubsub.PubSubRef;
import com.lightbend.lagom.javadsl.pubsub.PubSubRegistry;
import com.lightbend.lagom.javadsl.pubsub.TopicId;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import sample.chirper.chirp.api.Chirp;
import sample.chirper.chirp.api.ChirpService;
import sample.chirper.chirp.api.HistoricalChirpsRequest;
import sample.chirper.chirp.api.LiveChirpsRequest;
import sample.chirper.common.UserId;
import sample.chirper.common.server.Authenticated;
import sample.chirper.like.api.ChirpId;
import sample.chirper.like.api.LikeService;

public class ChirpServiceImpl implements ChirpService {

  private static final int MAX_TOPICS = 1024;
  private final PubSubRegistry topics;
  private final ChirpDao chirpDao;
  private final LikeService likeService;

  @Inject
  public ChirpServiceImpl(PubSubRegistry topics, ChirpDao chirpDao, LikeService likeService) {
    this.topics = topics;
    this.chirpDao = chirpDao;
    this.likeService = likeService;
  }

  @Override
  public ServiceCall<UserId, Chirp, NotUsed> addChirp() {
    return Authenticated.enforceUserId((userId, chirp) -> {
      if (!userId.equals(chirp.userId))
        throw new IllegalArgumentException("UserId " + userId + " did not match userId in " + chirp);
      PubSubRef<Chirp> topic = topics.refFor(TopicId.of(Chirp.class, topicQualifier(userId)));
      topic.publish(chirp);
      return chirpDao.addChirp(chirp).thenApply(done -> NotUsed.getInstance());
    });
  }

  private String topicQualifier(UserId userId) {
    return String.valueOf(Math.abs(userId.hashCode()) % MAX_TOPICS);
  }

  @Override
  public ServiceCall<NotUsed, LiveChirpsRequest, Source<Chirp, ?>> getLiveChirps() {
    return (id, req) -> {
      return recentChirps(req.userIds).thenApply(recentChirps -> {
        List<Source<Chirp, ?>> sources = new ArrayList<>();
        for (UserId userId : req.userIds) {
          PubSubRef<Chirp> topic = topics.refFor(TopicId.of(Chirp.class, topicQualifier(userId)));
          sources.add(topic.subscriber());
        }
        HashSet<UserId> users = new HashSet<>(req.userIds);
        Source<Chirp, ?> publishedChirps = Source.from(sources).flatMapMerge(sources.size(), s -> s)
          .filter(c -> users.contains(c.userId));

        // We currently ignore the fact that it is possible to get duplicate chirps
        // from the recent and the topic. That can be solved with a de-duplication stage.
        return Source.from(recentChirps).concat(publishedChirps)
            .mapAsync(2, this::updateChirpLikes);
      });
    };
  }

  @Override
  public ServiceCall<NotUsed, HistoricalChirpsRequest, Source<Chirp, ?>> getHistoricalChirps() {
    return (id, req) -> {
      List<Source<Chirp, ?>> sources = new ArrayList<>();
      for (UserId userId : req.userIds) {
        Source<Chirp, NotUsed> select = chirpDao.getHistoricalChirps(userId, req.fromTime);
        sources.add(select);
      }
        // Chirps from one user are ordered by timestamp, but chirps from different
        // users are not ordered. That can be improved by implementing a smarter
        // merge that takes the timestamps into account.
      Source<Chirp, ?> result = Source.from(sources).flatMapMerge(sources.size(), s -> s)
        .mapAsync(2, this::updateChirpLikes);
      return CompletableFuture.completedFuture(result);
    };
  }

  private CompletionStage<PSequence<Chirp>> recentChirps(PSequence<UserId> userIds) {
    int limit = 10;
    PSequence<CompletionStage<PSequence<Chirp>>> results = TreePVector.empty();
    for (UserId userId : userIds) {
      CompletionStage<PSequence<Chirp>> result = chirpDao.getRecentChirps(userId, limit);
      results = results.plus(result);
    }

    CompletionStage<PSequence<Chirp>> combined = null;
    for (CompletionStage<PSequence<Chirp>> chirpsFromOneUser : results) {
      if (combined == null) {
        combined = chirpsFromOneUser;
      } else {
        combined = combined.thenCombine(chirpsFromOneUser, (a, b) -> a.plusAll(b));
      }
    }

    CompletionStage<PSequence<Chirp>> sortedLimited = combined.thenApply(all -> {
      List<Chirp> allSorted = new ArrayList<>(all);
      // reverse order
      Collections.sort(allSorted, (a, b) -> b.timestamp.compareTo(a.timestamp));
      List<Chirp> limited = allSorted.stream().limit(limit).collect(Collectors.toList());
      List<Chirp> reversed = new ArrayList<>(limited);
      Collections.reverse(reversed);
      return TreePVector.from(reversed);
    });

    return sortedLimited;
  }

  private CompletionStage<Chirp> updateChirpLikes(Chirp chirp) {
    return likeService.getLikes()
        .invoke(new ChirpId(chirp.userId, chirp.uuid), NotUsed.getInstance())
        .thenApply(likers -> chirp.withLikes(likers.size()));
  }
}
