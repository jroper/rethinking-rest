package sample.chirper.like.api;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.UUID;

public class Likes {
  public final String chirpId;
  public final int likes;
  public final UUID eventId;

  @JsonCreator
  public Likes(String chirpId, int likes, UUID eventId) {
    this.chirpId = chirpId;
    this.likes = likes;
    this.eventId = eventId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Likes)) return false;

    Likes likes1 = (Likes) o;

    if (likes != likes1.likes) return false;
    if (!chirpId.equals(likes1.chirpId)) return false;
    return eventId.equals(likes1.eventId);

  }

  @Override
  public int hashCode() {
    int result = chirpId.hashCode();
    result = 31 * result + likes;
    result = 31 * result + eventId.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Likes{" +
        "chirpId='" + chirpId + '\'' +
        ", likes=" + likes +
        ", eventId=" + eventId +
        '}';
  }
}
