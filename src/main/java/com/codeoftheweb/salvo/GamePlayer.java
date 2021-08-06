package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

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

    //Constructores
    public GamePlayer() { }

    public GamePlayer(LocalDateTime joinDate, Game gameID, Player playerID) {
        this.joinDate= joinDate;
        this.gameID= gameID;
        this.playerID= playerID;
    }

    //Getters and Setters
    public Player getPlayerID() {return playerID;}
    public Game getGameID() {return gameID;}
    public LocalDateTime getJoinDate() { return joinDate; }

    public void setPlayerID(Player playerID) {this.playerID = playerID;}
    public void setGameID(Game gameID) {this.gameID = gameID;}
    public void setJoinDate(LocalDateTime joinDate) { this.joinDate = joinDate; }

}
