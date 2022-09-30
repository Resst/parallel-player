package com.rtb.parallelplayer.repository;

import com.rtb.parallelplayer.model.Layer;
import com.rtb.parallelplayer.model.Room;
import com.rtb.parallelplayer.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LayerRepo extends JpaRepository<Layer, Long> {

    List<Layer> findAllByRoom(Room room);

    List<Layer> findAllByRoomOrderByOrderInList(Room room);
}
