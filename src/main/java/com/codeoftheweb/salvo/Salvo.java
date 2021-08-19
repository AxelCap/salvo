package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private int turn;

    @ElementCollection //En vez de crear una clase para con un solo atributo, creo un atributo solo
    @Column(name="salvo_Locations")
    private List<String> salvoLocations = new ArrayList<>();

    //Muchos salvos a un gameplayer
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayerID;

    //Constructores

    public Salvo() { }

    public Salvo(GamePlayer gamePlayerID, int turn, List<String> salvoLocations) {
        this.gamePlayerID = gamePlayerID;
        this.turn = turn;
        this.salvoLocations = salvoLocations;
    }

    public Map<String, Object> makeSalvoDTO(){
        Map<String, Object>     dto= new LinkedHashMap<>();
        dto.put("turn", getTurn());
        dto.put("player", this.getGamePlayerID().getPlayer().getId()); //Obtengo datos a través de las relaciones
        dto.put("locations", getSalvoLocations());
        return dto;
    }

    //Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }

    public GamePlayer getGamePlayerID() {
        return gamePlayerID;
    }

    public void setGamePlayerID(GamePlayer gamePlayerID) {
        this.gamePlayerID = gamePlayerID;
    }
}
