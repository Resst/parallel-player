package com.rtb.parallelplayer.websocket;

import com.rtb.parallelplayer.service.LayerService;
import com.rtb.parallelplayer.websocket.messages.PlayerStateMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketPlayerController {

    private final LayerService layerService;

    public WebSocketPlayerController(LayerService layerService) {
        this.layerService = layerService;
    }

    @MessageMapping("/player/{id}/{plId}/moderator")
    @SendTo("/ws/{id}/{plId}/owner")
    public PlayerStateMessage sendChangeToOwner(PlayerStateMessage playerStateMessage) {
        return playerStateMessage;
    }

    @MessageMapping("/player/{id}/{plId}/owner")
    @SendTo("/ws/{id}/{plId}/all")
    public PlayerStateMessage changePlayerData(PlayerStateMessage playerStateMessage,
                                               @DestinationVariable Long plId) {
        layerService.saveData(plId, playerStateMessage);
        return playerStateMessage;
    }

    @MessageMapping("/player/{id}/{plId}/owner-update")
    @SendTo("/ws/{id}/{plId}/all")
    public PlayerStateMessage synchronizeTracks(PlayerStateMessage playerStateMessage) {
        return playerStateMessage;
    }
}
