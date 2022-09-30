package com.rtb.parallelplayer.repository;

import com.rtb.parallelplayer.model.Room;
import com.rtb.parallelplayer.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepo extends JpaRepository<Room, Long> {

    @Query(
            value = "SELECT * FROM room r JOIN client_in_room cir on r.id = cir.room WHERE cir.client = :userId",
            nativeQuery = true
    )
    List<Room> findAllByUser(@Param("userId") Long userId);

    Optional<Room> findByInvitationLink(String invitationLink);

}
