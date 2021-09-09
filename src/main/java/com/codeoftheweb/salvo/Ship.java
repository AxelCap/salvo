package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private String type;

    //Crea una tabla que se divide en id y en "location" donde para cada id se muestran las posiciones forzadas en el commandlinerunner para cada id
    @ElementCollection
    @Column(name="shipLocation")
    private List<String> shipLocations = new ArrayList<>();

    //Muchos a uno
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    //Constructores
    public Ship() { }

    public Ship(String type, GamePlayer gamePlayer, List<String> shipLocations) {
        this.type = type;
        this.gamePlayer = gamePlayer;
        this.shipLocations = shipLocations;
    }

    public Map<String, Object> makeShipDTO(){
        Map<String, Object>     dto= new LinkedHashMap<>();
        dto.put("type", getType());
        dto.put("locations", getShipLocations());
        return dto;
    }

    //Getters and Setters
    public Long getId() {return id;}
    public GamePlayer getGamePlayer() {return gamePlayer;}
    public String getType() {return type;}
    public List<String> getShipLocations() {return shipLocations;}

    public void setId(Long id) {this.id = id;}
    public void setGamePlayer(GamePlayer gamePlayer) {this.gamePlayer = gamePlayer;}
    public void setType(String type) {this.type = type;}
    public void setShipLocations(List<String> shipLocations) {this.shipLocations = shipLocations;}
}
