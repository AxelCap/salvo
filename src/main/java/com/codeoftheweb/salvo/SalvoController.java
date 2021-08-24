package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    @RequestMapping("/games")
    public Map<String,Object> getGameAll() {   //no es una lista porque necesito clave y valor
        Map<String, Object>     dto= new LinkedHashMap<>();
        dto.put("games", gameRepository.findAll()
                .stream()
                .map(game -> game.makeGameDTO())
                .collect(Collectors.toList()));

        return dto;
    }

    @RequestMapping("/game_view/{nn}")
    public Map<String, Object> findGamePlayer(@PathVariable Long nn) {

        GamePlayer gamePlayer = gamePlayerRepository.findById(nn).get();

        return gamePlayer.makeGameViewDTO(); //Devuelvo para este gameplayer id, su juego ejecutando el makeGameDTO
    }

}
