package sample.chirper.chirp.impl;

import akka.NotUsed;
import akka.actor.*;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import sample.chirper.like.api.LikeService;

import javax.inject.Inject;

public class LikeCountSubscriber extends UntypedActor {

  private final LikeService likeService;
  private final ChirpDao chirpDao;
  private final Materializer mat;

  @Inject
  public LikeCountSubscriber(LikeService likeService, ChirpDao chirpDao, Materializer mat) {
    this.likeService = likeService;
    this.chirpDao = chirpDao;
    this.mat = mat;
  }

  @Override
  public void preStart() throws Exception {

  }

  @Override
  public void onReceive(Object message) throws Exception {

    if (message instanceof Status.Failure) {

      // Just fail, and let the backoff supervisor restart us
      throw new Exception("Stream failed, restarting", ((Status.Failure) message).cause());

    }

    // A lot of done messages will come through here, they can be ignored.
  }
}
