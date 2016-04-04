package sample.chirper.like.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.serialization.Jsonable;
import jdk.nashorn.internal.ir.annotations.Immutable;
import sample.chirper.common.UserId;

public interface LikeEvent extends Jsonable, AggregateEvent<LikeEvent> {

  @Override
  default AggregateEventTag<LikeEvent> aggregateTag() {
    return LikeEventTag.INSTANCE;
  }

  String chirpId();

  @Immutable
  final class Liked implements LikeEvent {
    public final String chirpId;
    public final UserId userId;

    @JsonCreator
    public Liked(String chirpId, UserId userId) {
      this.chirpId = chirpId;
      this.userId = userId;
    }

    public String chirpId() {
      return chirpId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Liked)) return false;

      Liked liked = (Liked) o;

      if (!chirpId.equals(liked.chirpId)) return false;
      return userId.equals(liked.userId);

    }

    @Override
    public int hashCode() {
      int result = chirpId.hashCode();
      result = 31 * result + userId.hashCode();
      return result;
    }

    @Override
    public String toString() {
      return "Liked{" +
          "chirpId='" + chirpId + '\'' +
          ", userId=" + userId +
          '}';
    }
  }

  @Immutable
  final class UnLiked implements LikeEvent {
    public final String chirpId;
    public final UserId userId;

    @JsonCreator
    public UnLiked(String chirpId, UserId userId) {
      this.chirpId = chirpId;
      this.userId = userId;
    }

    public String chirpId() {
      return chirpId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof UnLiked)) return false;

      UnLiked unliked = (UnLiked) o;

      if (!chirpId.equals(unliked.chirpId)) return false;
      return userId.equals(unliked.userId);

    }

    @Override
    public int hashCode() {
      int result = chirpId.hashCode();
      result = 31 * result + userId.hashCode();
      return result;
    }

    @Override
    public String toString() {
      return "UnLiked{" +
          "chirpId='" + chirpId + '\'' +
          ", userId=" + userId +
          '}';
    }
  }

}
