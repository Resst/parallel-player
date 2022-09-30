package com.rtb.parallelplayer.service;

import com.rtb.parallelplayer.model.Track;
import com.rtb.parallelplayer.repository.TrackRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrackService {

    private final TrackRepo trackRepo;

    public TrackService(TrackRepo trackRepo) {
        this.trackRepo = trackRepo;
    }

    public List<Track> getAllForLayer(Long layerId){
        return trackRepo.findAllByLayer(layerId);
    }
}
