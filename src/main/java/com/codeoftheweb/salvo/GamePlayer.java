package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.Authentication;

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
    //@OrderBy
    Set<Salvo> salvos;

    //Constructores
    public GamePlayer() { }

    public GamePlayer(LocalDateTime joinDate, Game game, Player player) {
        this.joinDate= joinDate;
        this.game= game;
        this.player = player;
    }

    public List<Map<String,Object>> getSelfOponnentDTO() {   //no es una lista porque necesito clave y valor
        List<Map<String, Object>>     SelfOponnent= new ArrayList<>();

        GamePlayer gpEnemigo= this.getGame().getGamePlayers().stream().filter(gp -> gp.getId()!=this.getId()).findFirst().get();

        /*
        List<String> misLocations= new ArrayList<>();
        for(Ship ship: this.getShips())
        {
            for(String location : ship.getShipLocations())
            {
                misLocations.add(location);
            }
        }
         */

        List<String> carrierLocations = this.getShips().stream().filter(ship -> ship.getType().equals("carrier")).findFirst().get().getShipLocations();
        List<String> battleshipLocations = this.getShips().stream().filter(ship -> ship.getType().equals("battleship")).findFirst().get().getShipLocations();
        List<String> submarineLocations = this.getShips().stream().filter(ship -> ship.getType().equals("submarine")).findFirst().get().getShipLocations();
        List<String> destroyerLocations = this.getShips().stream().filter(ship -> ship.getType().equals("destroyer")).findFirst().get().getShipLocations();
        List<String> patrolboatLocations = this.getShips().stream().filter(ship -> ship.getType().equals("patrolboat")).findFirst().get().getShipLocations();


        for(Salvo salvoActual: gpEnemigo.getSalvos())
        {
            Map<String, Object>     dto= new LinkedHashMap<>();
            Map<String, Object>  damage = new LinkedHashMap<>();
            List<String> hitLocations= new ArrayList<>();

            int miss=salvoActual.getSalvoLocations().size(); //Cantidad de veces que tiró

            //Inicio variables acumulables
            int carrier=0;
            int battleship=0;
            int submarine=0;
            int destroyer=0;
            int patrolboat=0;

            List<Salvo> Salvoes_ant= gpEnemigo.getSalvos().stream().filter(gp -> gp.getTurn()<=salvoActual.getTurn()).collect(Collectors.toList());

            //Inicio variables por turno
            int carrierHits=0;
            int battleshipHits=0;
            int submarineHits=0;
            int destroyerHits=0;
            int patrolboatHits=0;

            for(Salvo salvoGlobal : Salvoes_ant)
            {
                for(String location : salvoGlobal.getSalvoLocations())
                {
                    if(carrierLocations.contains(location))
                    {
                        carrier++;
                    }

                    if(battleshipLocations.contains(location))
                    {
                        battleship++;
                    }

                    if(submarineLocations.contains(location))
                    {
                        submarine++;
                    }

                    if(destroyerLocations.contains(location))
                    {
                        destroyer++;
                    }

                    if(patrolboatLocations.contains(location))
                    {
                        patrolboat++;
                    }
                }
            }

            for(String location : salvoActual.getSalvoLocations())
            {
                for(String miLocation : carrierLocations)
                {
                    if(location==miLocation)
                    {
                        hitLocations.add(location);
                        carrierHits++;
                        miss--;
                        //System.out.println(miss);
                    }
                }

                for(String miLocation : battleshipLocations)
                {
                    if(location==miLocation)
                    {
                        hitLocations.add(location);
                        battleshipHits++;
                        miss--;
                        //System.out.println(miss);
                    }
                }

                for(String miLocation : submarineLocations)
                {
                    if(location==miLocation)
                    {
                        hitLocations.add(location);
                        submarineHits++;
                        miss--;
                        //System.out.println(miss);
                    }
                }

                for(String miLocation : destroyerLocations)
                {
                    if(location==miLocation)
                    {
                        hitLocations.add(location);
                        destroyerHits++;
                        miss--;
                        //System.out.println(miss);
                    }
                }

                for(String miLocation : patrolboatLocations)
                {
                    if(location==miLocation)
                    {
                        hitLocations.add(location);
                        patrolboatHits++;
                        miss--;
                        //System.out.println(miss);
                    }
                }

            }

            damage.put("carrierHits", carrierHits);
            damage.put("battleshipHits", battleshipHits);
            damage.put("submarineHits", submarineHits);
            damage.put("destroyerHits", destroyerHits);
            damage.put("patrolboatHits", patrolboatHits);
            damage.put("carrier", carrier);
            damage.put("battleship", battleship);
            damage.put("submarine", submarine);
            damage.put("destroyer", destroyer);
            damage.put("patrolboat", patrolboat);

            dto.put("turn", salvoActual.getTurn());
            dto.put("hitLocation", hitLocations);
            dto.put("damages", damage);
            dto.put("missed", miss);

            SelfOponnent.add(dto);
        }

        //dto.put("turn", this.getSalvos().stream().filter(salvo -> salvo.getTurn()).fin);
        /*
        dto.put("hitLocation", this.getShips().stream().filter(ship -> ship.getShipLocations().equals(gpEnemigo.getSalvos().stream().filter(salvo -> salvo.getTurn()==a).findFirst().get().getSalvoLocations())).findAny().get().getShipLocations());
        dto.put("damages", "");
        dto.put("missed", (gpEnemigo.getSalvos().stream().filter(salvo -> salvo.getTurn()==a).findFirst().get().getSalvoLocations()!=this.getShips().stream().map(ship -> ship.getShipLocations());
        */

        return SelfOponnent;
    }

    public Map<String, Object> makeHitsDTO() {
        Map<String, Object>     dto= new LinkedHashMap<>();

        GamePlayer gpEnemigo= this.getGame().getGamePlayers().stream().filter(gp -> gp.getId()!=this.getId()).findFirst().get();

        if(gpEnemigo == null)
        {
            dto.put("self", new ArrayList<>());
            dto.put("opponent", new ArrayList<>());
        }
        else
        {
            dto.put("self", this.getSelfOponnentDTO());
            dto.put("opponent", gpEnemigo.getSelfOponnentDTO());
        }

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
    public Set<Salvo> getSalvos() {
        return salvos;
    }

    public void setPlayer(Player player) {this.player = player;}
    public void setGame(Game game) {this.game = game;}
    public void setJoinDate(LocalDateTime joinDate) { this.joinDate = joinDate; }
    public void setId(Long id) {this.id = id;}
    public void setShips(Set<Ship> ships) {this.ships = ships;}
    public void setSalvos(Set<Salvo> salvos) {
        this.salvos = salvos;
    }
}

