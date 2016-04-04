package sample.chirper.chirp.impl;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.datastax.driver.core.Row;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.chirper.chirp.api.Chirp;
import sample.chirper.common.UserId;
import sample.chirper.like.api.Likes;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Singleton
public class ChirpDao {
  private final Logger log = LoggerFactory.getLogger(getClass());

  private final CassandraSession db;

  @Inject
  public ChirpDao(CassandraSession db) {
    this.db = db;

    createTables();
  }

  private void createTables() {
    // @formatter:off
    CompletionStage<Done> result =
      db.executeCreateTable(
        "CREATE TABLE IF NOT EXISTS chirp ("
            + "userId text, timestamp bigint, uuid text, message text, likes int, "
            + "PRIMARY KEY (userId, timestamp, uuid))"
      ).thenCompose(d -> db.executeCreateTable(
        "CREATE TABLE IF NOT EXISTS chirp_uuids ("
            + "uuid text, userId text, timestamp bigint, "
            + "PRIMARY KEY (uuid))"
      )).thenCompose(d -> db.executeCreateTable(
        "CREATE TABLE IF NOT EXISTS likes_offset ("
            + "partition int, offset timeuuid, "
            + "PRIMARY KEY (partition))"
      ));
    // @formatter:on
    result.whenComplete((ok, err) -> {
      if (err != null) {
        log.error("Failed to create chirp table, due to: " + err.getMessage(), err);
      }
    });
  }

  public CompletionStage<Done> addChirp(Chirp chirp) {
    return db.executeWrite("INSERT INTO chirp (userId, uuid, timestamp, message, likes) VALUES (?, ?, ?, ?, 0)",
            chirp.userId.userId, chirp.uuid, chirp.timestamp.toEpochMilli(),
            chirp.message)
        .thenCompose(d -> db.executeWrite("INSERT INTO chirp_uuids (uuid, userId, timestamp) VALUES (?, ?, ?)", chirp.uuid,
            chirp.userId.userId, chirp.timestamp.toEpochMilli()));

  }

  private Chirp mapChirp(Row row) {
    return new Chirp(new UserId(row.getString("userId")), row.getString("message"),
        Optional.of(Instant.ofEpochMilli(row.getLong("timestamp"))), Optional.of(row.getString("uuid")),
        row.getInt("likes"));
  }

  public Source<Chirp, NotUsed> getHistoricalChirps(UserId userId, Instant fromTime) {
    return db.select("SELECT * FROM chirp WHERE userId = ? AND timestamp >= ? ORDER BY timestamp ASC", userId.userId,
            fromTime.toEpochMilli())
        .map(this::mapChirp);
  }

  public CompletionStage<PSequence<Chirp>> getRecentChirps(UserId userId, int limit) {
    return db.selectAll("SELECT * FROM chirp WHERE userId = ? ORDER BY timestamp DESC LIMIT ?", userId.userId, limit)
        .thenApply(rows -> {
          List<Chirp> chirps = rows.stream().map(this::mapChirp).collect(Collectors.toList());
          return TreePVector.from(chirps);
        });
  }

  public CompletionStage<Done> updateChirpLikes(Likes likes) {
    return db.selectOne("SELECT * FROM chirp_uuids where uuid = ?", likes.chirpId).thenCompose(maybeChirp ->
        maybeChirp.map(row -> {
          String userId = row.getString("userId");
          long timestamp = row.getLong("timestamp");
          return db.executeWrite("UPDATE chirp SET likes = ? WHERE userId = ? AND timestamp = ? AND uuid = ?",
              likes.likes, userId, timestamp, likes.chirpId);
        }).orElse(CompletableFuture.completedFuture(Done.getInstance()))
    ).thenCompose(d ->
          db.executeWrite("INSERT INTO likes_offset (partition, offset) VALUES (1, ?)", likes.eventId)
    );
  }

  public CompletionStage<Optional<UUID>> getLikesOffset() {
    return db.selectOne("SELECT offset FROM likes_offset").thenApply(maybeRow ->
      maybeRow.map(row -> row.getUUID("offset")));
  }

}
