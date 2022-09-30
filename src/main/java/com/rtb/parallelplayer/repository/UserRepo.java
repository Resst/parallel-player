package com.rtb.parallelplayer.repository;

import com.rtb.parallelplayer.model.User;
import com.rtb.parallelplayer.model.RoomRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface UserRepo extends JpaRepository<User, Long> {

    @Query(
            value = "SELECT c.role FROM client_in_room c WHERE c.client = :clientId AND c.room = :roomId",
            nativeQuery = true
    )
    Optional<RoomRole> getRole(@Param("clientId") Long clientId, @Param("roomId") Long roomId);

    @Modifying
    @Query(
            value = "INSERT INTO client_in_room(client, room, role) VALUES (:clientId, :roomId, :role) ",
            nativeQuery = true
    )
    void addRole(@Param("clientId") Long clientId, @Param("roomId") Long roomId, @Param("role") String role);

    @Modifying
    @Query(
            value = "UPDATE client_in_room SET role = :role " +
                    "WHERE client = :clientId AND room = :roomId;",
            nativeQuery = true
    )
    void saveRole(@Param("clientId") Long clientId, @Param("roomId") Long roomId, @Param("role") String role);


    Optional<User> findClientByEmail(String email);

}
