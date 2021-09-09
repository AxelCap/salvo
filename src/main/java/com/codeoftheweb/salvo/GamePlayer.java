package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class GamePlayer {
    //Propiedades
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private LocalDateTime joinDate;

    @ElementCollection //En vez de crear una clase para con un solo atributo, creo un atributo solo
    @Column(name="hits_self")
    private List<String> self = new ArrayList<>();

    @ElementCollection //En vez de crear una clase para con un solo atributo, creo un atributo solo
    @Column(name="hits_opponent")
    private List<String> opponent = new ArrayList<>();

    //Muchos a uno
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    //Uno a muchos
    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    Set<Ship> ships;

    @OneToMany(mappedBy="gamePlayerID", fetch=FetchType.EAGER)
    Set<Salvo> salvos;

    //Constructores
    public GamePlayer() { }

    public GamePlayer(LocalDateTime joinDate, Game game, Player player) {
        this.joinDate= joinDate;
        this.game= game;
        this.player = player;
    }

    public Map<String, Object> makeHitsDTO() {
        Map<String, Object>     dto= new LinkedHashMap<>();
        dto.put("self", self);
        dto.put("opponent", opponent);
        return dto;
    }

    public Map<String, Object> makeGamePlayerDTO() {
        Map<String, Object>     dto= new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("player", this.getPlayer().makePlayerDTO()); //Devuelvo un solo elemento de player
        return dto;
    }

    public Optional<Score> getScore(){
        return this
                .getPlayer()
                .getScore(this.getGame());
    }

    public Map<String, Object> makeGameViewDTO(){
        Map<String, Object>     dto= new LinkedHashMap<>();
        dto.put("id", this.getGame().getId());
        dto.put("created", this.getGame().getCreationDate());
        dto.put("gameState", "PLACESHIPS");
        dto.put("gamePlayers", this.getGame().getGamePlayers()
                .stream()
                .map(gamePlayer -> gamePlayer.makeGamePlayerDTO())
                .collect(Collectors.toList()));
        dto.put("ships" ,this.getShips()         //Devuelvo un mapeo/lista  de ship
                .stream()
                .map(x -> x.makeShipDTO())
                .collect(Collectors.toList()));
        dto.put("salvoes" ,this.getGame().getGamePlayers()         //Devuelvo un mapeo/lista  de ship
                .stream()
                .flatMap(gamePlayer -> gamePlayer.getSalvos()
                        .stream()
                        .map(salvo -> salvo.makeSalvoDTO()))
                .collect(Collectors.toList()));
        dto.put("hits", this.makeHitsDTO());
        return dto;
    }


    //Getters and Setters
    public Player getPlayer() {return player;}
    public Game getGame() {return game;}
    public LocalDateTime getJoinDate() { return joinDate; }
    public Long getId() {return id;}
    public Set<Ship> getShips() {return ships;}
    public Set<Salvo> getSalvos() {return salvos;}

    public void setPlayer(Player player) {this.player = player;}
    public void setGame(Game game) {this.game = game;}
    public void setJoinDate(LocalDateTime joinDate) { this.joinDate = joinDate; }
    public void setId(Long id) {this.id = id;}
    public void setShips(Set<Ship> ships) {this.ships = ships;}
    public void setSalvos(Set<Salvo> salvos) {this.salvos = salvos;}

}

