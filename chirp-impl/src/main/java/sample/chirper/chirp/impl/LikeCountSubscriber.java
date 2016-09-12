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
    chirpDao.getLikesOffset().thenCompose(uuid ->
        likeService.counts(uuid).invoke(NotUsed.getInstance())
            .whenComplete((subscription, error) -> {
              if (error != null) {
                // Send the failure to the actor so that we will get restarted
                self().tell(new Status.Failure(error), self());
              } else {
                // mapAsync with parallelism of 1 ensures that messages get processed one at a time, and the
                // offset only gets updated if it was successful
                subscription.mapAsync(1, chirpDao::updateChirpLikes)
                    // By feeding to ourselves, we ensure that when we shut down, the stream will be cancelled.
                    .runWith(Sink.actorRef(self(),
                        // If the stream terminates (it shouldn't), we need to restart, so send a failure.
                        new Status.Failure(new Exception("Stream terminated"))), mat);
              }
            })
    );
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
