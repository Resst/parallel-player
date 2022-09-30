package com.rtb.parallelplayer.service;

import com.rtb.parallelplayer.exception.NoSuchLayerException;
import com.rtb.parallelplayer.model.Layer;
import com.rtb.parallelplayer.model.Room;
import com.rtb.parallelplayer.repository.LayerRepo;
import com.rtb.parallelplayer.repository.TrackRepo;
import com.rtb.parallelplayer.websocket.messages.PlayerStateMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LayerService {
    private final LayerRepo layerRepo;
    private final TrackRepo trackRepo;

    public LayerService(LayerRepo layerRepo, TrackRepo trackRepo) {
        this.layerRepo = layerRepo;
        this.trackRepo = trackRepo;
    }

    public List<Layer> getAllForRoom(Room room) {
        List<Layer> layers = layerRepo.findAllByRoomOrderByOrderInList(room);
        layers.forEach(layer -> {
            layer.setTracks(trackRepo.findAllByLayer(layer.getId()));
            layer.setNowPlaying(layer.getTracks().stream()
                    .filter(track -> track.getId().equals(layer.getNowPlayingOrderInList()))
                    .findFirst().orElse(null));
        });
        return layers;
    }


    public void saveData(Long plId, PlayerStateMessage playerStateMessage) {
        Layer layer = layerRepo.findById(plId).orElseThrow(NoSuchLayerException::new);
        if (playerStateMessage.getShuffled() != null)
            layer.setRandom(playerStateMessage.getShuffled());
        if (playerStateMessage.getRepeated() != null)
            layer.setCycled(playerStateMessage.getRepeated());
        if (playerStateMessage.getNowPlayingId() != null)
            layer.setNowPlayingOrderInList(playerStateMessage.getNowPlayingId());
        layerRepo.save(layer);
    }
}
