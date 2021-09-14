package com.codeoftheweb.salvo;

import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")    //para separar los repositorios de los controladores
public class SalvoController {

    @Autowired // Contstruye conexiones entre los elementos
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SalvoRepository salvoRepository;

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    @RequestMapping("/games")
    public Map<String,Object> getGameAll(Authentication authentication) {   //no es una lista porque necesito clave y valor
        Map<String, Object>     dto= new LinkedHashMap<>();

        if (isGuest(authentication)) {
            dto.put("player","Guest");
        }
        else
        {
            dto.put("player",playerRepository.findByUserName(authentication.getName()).makePlayerDTO());
        }

        dto.put("games", gameRepository.findAll()
                .stream()
                .map(game -> game.makeGameDTO())
                .collect(Collectors.toList()));

        return dto;
    }

    @PostMapping("/games")
    public ResponseEntity<Map<String,Object>> getGames(Authentication authentication){

        if(isGuest(authentication)){
            return new ResponseEntity<>(makeMap("Error", "Debe iniciar sesión"), HttpStatus.UNAUTHORIZED);
        }
        else
        {
            Game newGame= new Game(LocalDateTime.now());
            gameRepository.save(newGame);
            Player currentPlayer= playerRepository.findByUserName(authentication.getName());
            GamePlayer newGamePlayer= new GamePlayer(LocalDateTime.now(), newGame, currentPlayer);
            gamePlayerRepository.save(newGamePlayer);
            return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);
        }
    }

    @PostMapping("/game/{nn}/players")
    public ResponseEntity<Map<String,Object>> joinGame(@PathVariable Long nn,  Authentication authentication){

        if(isGuest(authentication)){ // Si el jugador no inicio sesión
            return new ResponseEntity<>(makeMap("Error", "Debe iniciar sesión"), HttpStatus.UNAUTHORIZED);
        }
        else if(!gameRepository.findById(nn).isPresent()) { //Si no existe el juego
            return new ResponseEntity<>(makeMap("Error", "Ingrese un juego válido"), HttpStatus.FORBIDDEN);
        }
        else if(gameRepository.findById(nn).get().getGamePlayers().size()>1){ // si el juego tiene más de 1 jugador

            return new ResponseEntity<>(makeMap("Error", "El juego está lleno"), HttpStatus.FORBIDDEN);
        }
        else if(gameRepository.findById(nn).get().getGamePlayers().stream().findFirst().get().getPlayer().getId()==playerRepository.findByUserName(authentication.getName()).getId()){ // Si estas en el juego

            return new ResponseEntity<>(makeMap("Error", "Ya estas en el juego"), HttpStatus.FORBIDDEN);
        }
        else
        {
            Player currentPlayer= playerRepository.findByUserName(authentication.getName());
            Game joinGame= gameRepository.findById(nn).get();
            GamePlayer newGamePlayer= new GamePlayer(LocalDateTime.now(), joinGame, currentPlayer);
            gamePlayerRepository.save(newGamePlayer);
            return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);
        }
    }

    @GetMapping("/games/players/{gamePlayerId}/salvoes")
    public ResponseEntity<Map<String, Object>> getSalvos (@PathVariable  Long gamePlayerId, @RequestBody Salvo salvo,Authentication authentication) {

        if(isGuest(authentication))
        {
            return new ResponseEntity<>(makeMap("Error", "Debe iniciar sesión"), HttpStatus.UNAUTHORIZED);
        }

        Optional<GamePlayer> currentGameplayer = gamePlayerRepository.findById(gamePlayerId);
        Optional<GamePlayer> currentGameplayer2 = currentGameplayer.get().getGame().getGamePlayers().stream().filter(gp -> gp!=currentGameplayer.get()).findFirst();

        if(currentGameplayer.isEmpty() || currentGameplayer2.isEmpty())
        {
            return new ResponseEntity<>(makeMap("Error", "Este juego no está completo"), HttpStatus.UNAUTHORIZED);
        }

        if(playerRepository.findByUserName(authentication.getName()).getGamePlayers().stream().noneMatch(gp -> gp.equals(currentGameplayer.get())))
        {
            return new ResponseEntity<>(makeMap("Error", "No tiene acceso a este GamePlayer"), HttpStatus.UNAUTHORIZED);
        }

        if(currentGameplayer.get().getShips().size()!=5)
        {
            return new ResponseEntity<>(makeMap("Error", "Le faltan barcos"), HttpStatus.FORBIDDEN);
        }

        if(currentGameplayer2.get().getShips().size()!=5)
        {
            return new ResponseEntity<>(makeMap("Error", "Le faltan barcos a su rival"), HttpStatus.FORBIDDEN);
        }

        if(salvo.getSalvoLocations().size()<1 || salvo.getSalvoLocations().size()>5)
        {
            return new ResponseEntity<>(makeMap("Error", "Puede hacer de 1 a 5 disparos"), HttpStatus.FORBIDDEN);
        }

        if(currentGameplayer.get().getId()<currentGameplayer2.get().getId())
        {
            if(currentGameplayer.get().getSalvos().size() == currentGameplayer2.get().getSalvos().size())
            {
                salvo.setGamePlayerID(currentGameplayer.get());
                salvo.setTurn(currentGameplayer.get().getSalvos().size()+1);
                salvoRepository.save(salvo);
            }
            else
            {
                return new ResponseEntity<>(makeMap("error", "No es su turno"), HttpStatus.FORBIDDEN);
            }
        }
        else
        {
            if(currentGameplayer.get().getSalvos().size() < currentGameplayer2.get().getSalvos().size())
            {
                salvo.setGamePlayerID(currentGameplayer.get());
                salvo.setTurn(currentGameplayer.get().getSalvos().size()+1);
                salvoRepository.save(salvo);
            }
            else{
                return new ResponseEntity<>(makeMap("error", "No es su turno"), HttpStatus.FORBIDDEN);
            }
        }

        return new ResponseEntity<>(makeMap("Ok", "Misiles lanzados"), HttpStatus.ACCEPTED);

    }

    @GetMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> getShips (@PathVariable  Long gamePlayerId,Authentication authentication){

        Optional<GamePlayer> currentGameplayer = gamePlayerRepository.findById(gamePlayerId);

        if(isGuest(authentication))
        {
            return new ResponseEntity<>(makeMap("Error", "Debe iniciar sesión"), HttpStatus.UNAUTHORIZED);
        }

        if(currentGameplayer.isEmpty())
        {
            return new ResponseEntity<>(makeMap("Error", "No existe este gamePlayer"), HttpStatus.UNAUTHORIZED);
        }

        if(!playerRepository.findByUserName(authentication.getName()).getGamePlayers().stream().anyMatch(gp -> gp==currentGameplayer.get()))
        {
            return new ResponseEntity<>(makeMap("Error", "No tiene acceso a este GamePlayer"), HttpStatus.UNAUTHORIZED);
        }

        if(currentGameplayer.get().getShips().size()==0)
        {
            return new ResponseEntity<>(makeMap("error", "No hay barcos colocados"), HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(makeMap("ships", currentGameplayer.get().getShips()
                                    .stream()
                                    .map(ship -> ship.makeShipDTO())
                                    .collect(Collectors.toList())), HttpStatus.ACCEPTED);
    }

    //Bad request 400 --> No cumple con los parámetros necesarios
    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> getShips (@PathVariable Long gamePlayerId, @RequestBody List<Ship> ships, Authentication authentication){

        if(isGuest(authentication))
        {
            return new ResponseEntity<>(makeMap("Error", "Debe iniciar sesión"), HttpStatus.UNAUTHORIZED);
        }

        Optional <GamePlayer> currentGamePlayer= gamePlayerRepository.findById(gamePlayerId);

        if(!currentGamePlayer.isPresent())
        {
            return new ResponseEntity<>(makeMap("Error", "El gamePlayer no existe"), HttpStatus.UNAUTHORIZED);
        }

        if(!playerRepository.findByUserName(authentication.getName()).getGamePlayers().stream().anyMatch(gp -> gp==currentGamePlayer.get()))
        {
            return new ResponseEntity<>(makeMap("Error", "No tiene acceso a este GamePlayer"), HttpStatus.UNAUTHORIZED);
        }

        if(ships.size()!=5)
        {
            return new ResponseEntity<>(makeMap("Error", "Debe Tener 5 Barcos"), HttpStatus.FORBIDDEN);
        }

        if(currentGamePlayer.get().getShips().size()!=0)
        {
            return new ResponseEntity<>(makeMap("Error", "Ya agregó barcos"), HttpStatus.FORBIDDEN);
        }

        if(ships.stream().filter(ship -> ship.getType().equals("carrier")).count()!=1)
        {
            return new ResponseEntity<>(makeMap("Error", "Tiene que tener un barco carrier"), HttpStatus.FORBIDDEN);
        }

        if(ships.stream().filter(ship -> ship.getType().equals("battleship")).count()!=1)
        {
            return new ResponseEntity<>(makeMap("Error", "Tiene que tener un barco battleship"), HttpStatus.FORBIDDEN);
        }

        if(ships.stream().filter(ship -> ship.getType().equals("submarine")).count()!=1)
        {
            return new ResponseEntity<>(makeMap("Error", "Tiene que tener un barco submarine"), HttpStatus.FORBIDDEN);
        }

        if(ships.stream().filter(ship -> ship.getType().equals("destroyer")).count()!=1)
        {
            return new ResponseEntity<>(makeMap("Error", "Tiene que tener un barco destroyer"), HttpStatus.FORBIDDEN);
        }

        if(ships.stream().filter(ship -> ship.getType().equals("patrolboat")).count()!=1)
        {
            return new ResponseEntity<>(makeMap("Error", "Tiene que tener un barco patrolboat"), HttpStatus.FORBIDDEN);
        }

        for(Ship newShip:ships)
        {
            if((newShip.getType().equals("carrier")) && newShip.getShipLocations().size()!=5)
            {
                return new ResponseEntity<>(makeMap("Error", "Su barco carrier, tiene más o menos posiciones de las que debería(5)"), HttpStatus.FORBIDDEN);
            }

            if(newShip.getType().equals("battleship")&& newShip.getShipLocations().size()!=4)
            {
                return new ResponseEntity<>(makeMap("Error", "Su barco battleship, tiene más o menos posiciones de las que debería(4)"), HttpStatus.FORBIDDEN);
            }

            if(newShip.getType().equals("submarine")&& newShip.getShipLocations().size()!=3)
            {
                return new ResponseEntity<>(makeMap("Error", "Su barco submarine, tiene más o menos posiciones de las que debería(3)"), HttpStatus.FORBIDDEN);
            }

            if(newShip.getType().equals("destroyer")&& newShip.getShipLocations().size()!=3)
            {
                return new ResponseEntity<>(makeMap("Error", "Su barco destroyer, tiene más o menos posiciones de las que debería(3)"), HttpStatus.FORBIDDEN);
            }

            if(newShip.getType().equals("patrolboat")&& newShip.getShipLocations().size()!=2)
            {
                return new ResponseEntity<>(makeMap("Error", "Su barco patrolboat, tiene más o menos posiciones de las que debería(2)"), HttpStatus.FORBIDDEN);
            }
        }

        for(Ship newShip:ships)
        {
            newShip.setGamePlayer(currentGamePlayer.get());
            shipRepository.save(newShip);
        }
        return new ResponseEntity<>(makeMap("Bien!", "Colocó todo correctamente"), HttpStatus.ACCEPTED);
    }

    @RequestMapping("/game_view/{nn}")
    public ResponseEntity<Map<String, Object>> findGamePlayer(@PathVariable Long nn, Authentication authentication) {

        Optional<GamePlayer>gamePlayer = gamePlayerRepository.findById(nn);

        if(!gamePlayer.isPresent()){
            return new ResponseEntity<>(makeMap("Error", "No existe este gameplayer"), HttpStatus.UNAUTHORIZED);
        }else{

            if(gamePlayer.get().getPlayer().getId() != playerRepository.findByUserName(authentication.getName()).getId()){
                return new ResponseEntity<>(makeMap("Error", "No hagas trampa"), HttpStatus.UNAUTHORIZED);
            }
            else
            {
                return new ResponseEntity<>(gamePlayer.get().makeGameViewDTO(), HttpStatus.ACCEPTED);
            }
        }
    }

    @PostMapping(path = "/players")
    public ResponseEntity<Map<String, Object>> createUser(@RequestParam String email, @RequestParam String password) {
        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No se agregó email o password"), HttpStatus.FORBIDDEN);
        }
        Player player = playerRepository.findByUserName(email);
        if (player != null) {
            return new ResponseEntity<>(makeMap("error", "El jugador ya existe"), HttpStatus.FORBIDDEN);
        }
        Player newPlayer = playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(makeMap("name", newPlayer.getUserName()), HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
