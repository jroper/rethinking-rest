package sample.chirper.like.api;

import sample.chirper.common.UserId;

import javax.annotation.concurrent.Immutable;

@Immutable
public class LikeChirp {
  public final ChirpId chirpId;
  public final UserId liker;

  public LikeChirp(ChirpId chirpId, UserId liker) {
    this.chirpId = chirpId;
    this.liker = liker;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof LikeChirp)) return false;

    LikeChirp likeChirp = (LikeChirp) o;

    if (!chirpId.equals(likeChirp.chirpId)) return false;
    return liker.equals(likeChirp.liker);

  }

  @Override
  public int hashCode() {
    int result = chirpId.hashCode();
    result = 31 * result + liker.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "LikeChirp{" +
        "chirpId=" + chirpId +
        ", liker=" + liker +
        '}';
  }
}
