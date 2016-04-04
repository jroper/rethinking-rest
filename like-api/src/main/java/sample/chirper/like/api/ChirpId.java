package sample.chirper.like.api;

import sample.chirper.common.UserId;

import javax.annotation.concurrent.Immutable;

@Immutable
public class ChirpId {
  public final UserId userId;
  public final String uuid;

  public ChirpId(UserId userId, String uuid) {
    this.userId = userId;
    this.uuid = uuid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ChirpId)) return false;

    ChirpId chirpId = (ChirpId) o;

    if (!userId.equals(chirpId.userId)) return false;
    return uuid.equals(chirpId.uuid);

  }

  @Override
  public int hashCode() {
    int result = userId.hashCode();
    result = 31 * result + uuid.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "ChirpId{" +
        "userId=" + userId +
        ", uuid=" + uuid +
        '}';
  }
}
