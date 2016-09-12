package sample.chirper.like.impl;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import org.pcollections.PCollection;
import sample.chirper.common.UserId;
import sample.chirper.like.api.*;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LikeServiceImpl implements LikeService {

  private final PersistentEntityRegistry persistentEntities;

  @Inject
  public LikeServiceImpl(PersistentEntityRegistry persistentEntities) {
    this.persistentEntities = persistentEntities;

    persistentEntities.register(LikeEntity.class);

  }

  @Override
  public ServiceCall<NotUsed, NotUsed> likeChirp(UserId userId, String chirpId, UserId liker) {
    return request ->
      likeEntityRef(chirpId).ask(new LikeCommand.Like(liker))
        .thenApply(a -> NotUsed.getInstance());
  }

  @Override
  public ServiceCall<NotUsed, NotUsed> unlikeChirp(UserId userId, String chirpId, UserId liker) {
    return request ->
        likeEntityRef(chirpId).ask(new LikeCommand.UnLike(liker))
          .thenApply(a -> NotUsed.getInstance());
  }

  @Override
  public ServiceCall<NotUsed, PCollection<UserId>> getLikes(UserId userId, String chirpId) {
    return request ->
        likeEntityRef(chirpId).ask(new LikeCommand.GetLikes());
  }

  @Override
  public ServiceCall<NotUsed, Source<Likes, NotUsed>> counts(Optional<UUID> offset) {
    return request -> {
      throw new UnsupportedOperationException();
    };

  }

  private PersistentEntityRef<LikeCommand> likeEntityRef(String chirpId) {
    PersistentEntityRef<LikeCommand> ref = persistentEntities.refFor(LikeEntity.class, chirpId);
    return ref;
  }

}
