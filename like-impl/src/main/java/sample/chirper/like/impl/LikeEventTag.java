package sample.chirper.like.impl;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;

public class LikeEventTag {

  public static final AggregateEventTag<LikeEvent> INSTANCE =
      AggregateEventTag.of(LikeEvent.class);

}
