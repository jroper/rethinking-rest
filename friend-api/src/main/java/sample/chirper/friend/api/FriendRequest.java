package sample.chirper.friend.api;

import com.google.common.base.MoreObjects;
import sample.chirper.common.UserId;

import javax.annotation.concurrent.Immutable;

@Immutable
public class FriendRequest {
  public final UserId userId;
  public final UserId friendId;

  public FriendRequest(UserId userId, UserId friendId) {
    this.userId = userId;
    this.friendId = friendId;
  }

  public UserId getUserId() {
    return userId;
  }

  public UserId getFriendId() {
    return friendId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FriendRequest)) return false;

    FriendRequest that = (FriendRequest) o;

    if (!userId.equals(that.userId)) return false;
    return friendId.equals(that.friendId);

  }

  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + userId.hashCode();
    return h;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper("FriendRequest").add("userId", userId).add("friendId", friendId).toString();
  }

}
