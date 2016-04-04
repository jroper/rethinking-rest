package sample.chirper.like.impl;

import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import org.pcollections.HashTreePSet;

import java.util.Optional;

public class LikeEntity extends PersistentEntity<LikeCommand, LikeEvent, LikeState> {

  @Override
  public Behavior initialBehavior(Optional<LikeState> snapshotState) {

    BehaviorBuilder b = newBehaviorBuilder(snapshotState.orElse(
        new LikeState(HashTreePSet.empty())));

    b.setCommandHandler(LikeCommand.Like.class, (cmd, ctx) -> {
      if (!state().getLikers().contains(cmd.userId)) {
        return ctx.thenPersist(new LikeEvent.Liked(entityId(), cmd.userId), a -> ctx.reply(Done.getInstance()));
      } else {
        ctx.reply(Done.getInstance());
        return ctx.done();
      }
    });

    b.setEventHandler(LikeEvent.Liked.class, evt -> state().withLiker(evt.userId));

    b.setCommandHandler(LikeCommand.UnLike.class, (cmd, ctx) -> {
      if (state().getLikers().contains(cmd.userId)) {
        return ctx.thenPersist(new LikeEvent.UnLiked(entityId(), cmd.userId), a -> ctx.reply(Done.getInstance()));
      } else {
        ctx.reply(Done.getInstance());
        return ctx.done();
      }
    });

    b.setEventHandler(LikeEvent.UnLiked.class, evt -> state().minusLiker(evt.userId));

    b.setReadOnlyCommandHandler(LikeCommand.GetLikes.class, (evt, ctx) ->
      ctx.reply(state().getLikers())
    );

    return b.build();
  }
}
