package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class GamePlayer {
    //Propiedades
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private LocalDateTime joinDate;

    //Muchos a uno
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game gameID;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player playerID;

    //Uno a muchos
    @OneToMany(mappedBy="gamePlayerID", fetch=FetchType.EAGER)
    Set<Ship> ships;

    //Constructores
    public GamePlayer() { }

    public GamePlayer(LocalDateTime joinDate, Game gameID, Player playerID) {
        this.joinDate= joinDate;
        this.gameID= gameID;
        this.playerID= playerID;
    }

    public Map<String, Object> makeGamePlayerDTO() {
        Map<String, Object>     dto= new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("player", this.getPlayerID().makePlayerDTO()); //Devuelvo un solo elemento de player
        dto.put("ship" ,this.getShips()         //Devuelvo un mapeo/lista  de ship
                .stream()
                .map(x -> x.makeShipDTO())
                .collect(Collectors.toList()));
        return dto;
    }


    //Getters and Setters
    public Player getPlayerID() {return playerID;}
    public Game getGameID() {return gameID;}
    public LocalDateTime getJoinDate() { return joinDate; }
    public Long getId() {return id;}
    public Set<Ship> getShips() {return ships;}

    public void setPlayerID(Player playerID) {this.playerID = playerID;}
    public void setGameID(Game gameID) {this.gameID = gameID;}
    public void setJoinDate(LocalDateTime joinDate) { this.joinDate = joinDate; }
    public void setId(Long id) {this.id = id;}
    public void setShips(Set<Ship> ships) {this.ships = ships;}
}
