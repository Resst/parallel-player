package com.rtb.parallelplayer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "track")
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    @Enumerated(EnumType.STRING)
    private TrackType type;

    @ManyToOne
    private User owner;

    public Track(String name, String address, TrackType type, User owner) {
        this.name = name;
        this.address = address;
        this.type = type;
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Track{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", type=" + type +
                ", owner=" + owner +
                '}';
    }

    public enum TrackType {
        AUDIO,
        YOUTUBE
    }
}
