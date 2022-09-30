package com.rtb.parallelplayer.repository;

import com.rtb.parallelplayer.model.RoomRole;
import com.rtb.parallelplayer.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRepo extends JpaRepository<Track, Long> {

    @Query(
            value = "SELECT til.order_in_layer AS id, t.address, t.name, t.type, t.owner_id " +
                    "FROM track_in_layer til " +
                    "JOIN track t ON til.track = t.id " +
                    "WHERE til.layer = :layerId " +
                    "ORDER BY til.order_in_layer;",
            nativeQuery = true
    )
    List<Track> findAllByLayer(@Param("layerId") Long layerId);



}
