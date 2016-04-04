package sample.chirper.like.impl;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;
import org.pcollections.PCollection;
import sample.chirper.common.UserId;

import javax.annotation.concurrent.Immutable;

public interface LikeCommand extends Jsonable {

  @Immutable
  final class Like implements LikeCommand, PersistentEntity.ReplyType<Done> {
    public final UserId userId;

    @JsonCreator
    public Like(UserId userId) {
      this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Like)) return false;

      Like like = (Like) o;

      return userId.equals(like.userId);

    }

    @Override
    public int hashCode() {
      return userId.hashCode();
    }

    @Override
    public String toString() {
      return "Like{" +
          "userId=" + userId +
          '}';
    }
  }

  @Immutable
  final class UnLike implements LikeCommand, PersistentEntity.ReplyType<Done> {
    public final UserId userId;

    @JsonCreator
    public UnLike(UserId userId) {
      this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof UnLike)) return false;

      UnLike unlike = (UnLike) o;

      return userId.equals(unlike.userId);

    }

    @Override
    public int hashCode() {
      return userId.hashCode();
    }

    @Override
    public String toString() {
      return "UnLike{" +
          "userId=" + userId +
          '}';
    }
  }

  @Immutable
  final class GetLikes implements LikeCommand, PersistentEntity.ReplyType<PCollection<UserId>> {

    @Override
    public boolean equals(Object o) {
      return o instanceof GetLikes;
    }

    @Override
    public int hashCode() {
      return GetLikes.class.hashCode();
    }

    @Override
    public String toString() {
      return "GetLikes";
    }
  }

}
