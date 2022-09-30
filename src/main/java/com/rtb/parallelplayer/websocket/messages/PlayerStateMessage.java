package com.rtb.parallelplayer.websocket.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PlayerStateMessage {
    private Boolean paused;
    private Boolean shuffled;
    private Boolean repeated;
    private Double time;
    private Long nowPlayingId;
}
