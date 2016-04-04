package sample.chirper.like.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.pcollections.HashTreePSet;
import org.pcollections.PCollection;
import sample.chirper.common.UserId;

import javax.annotation.concurrent.Immutable;

@Immutable
public class LikeState {

  private final PCollection<UserId> likers;

  @JsonCreator
  public LikeState(PCollection<UserId> likers) {
    this.likers = likers == null ? HashTreePSet.empty() : likers;
  }

  public PCollection<UserId> getLikers() {
    return likers;
  }

  public LikeState withLiker(UserId liker) {
    return new LikeState(likers.plus(liker));
  }

  public LikeState minusLiker(UserId liker) {
    return new LikeState(likers.minus(liker));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof LikeState)) return false;

    LikeState likeState = (LikeState) o;

    return likers.equals(likeState.likers);

  }

  @Override
  public int hashCode() {
    return likers.hashCode();
  }

  @Override
  public String toString() {
    return "LikeState{" +
        "likers=" + likers +
        '}';
  }
}
