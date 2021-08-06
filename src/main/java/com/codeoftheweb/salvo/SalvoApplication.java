package com.codeoftheweb.salvo;

//Elementos importados
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository) {    //playerRepository es el repositorio
		return (args) -> {
			Player player1 = new Player("axelcaputo89@gmail.com"); //Creo jugadores
			Player player2 = new Player("acaputo@pioix.edu.ar");
			Player player3 = new Player("Axo");
			Player player4 = new Player("Cap");
			playerRepository.save(player1); //Los guardo en el repositorio
			playerRepository.save(player2);
			playerRepository.save(player3);
			playerRepository.save(player4);

			Game game1= new Game(LocalDateTime.now());
			Game game2= new Game(LocalDateTime.now().plusHours(1));
			Game game3= new Game(LocalDateTime.now().plusHours(2));
			Game game4= new Game(LocalDateTime.now().plusHours(3));
			Game game5= new Game(LocalDateTime.now().plusHours(4));
			Game game6= new Game(LocalDateTime.now().plusHours(5));
			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);
			gameRepository.save(game4);
			gameRepository.save(game5);
			gameRepository.save(game6);

			GamePlayer gamePlayer1= new GamePlayer(LocalDateTime.now(), game1, player1);
			GamePlayer gamePlayer2= new GamePlayer(LocalDateTime.now(), game1, player2);
			GamePlayer gamePlayer3= new GamePlayer(LocalDateTime.now().plusHours(1), game2, player1);
			GamePlayer gamePlayer4= new GamePlayer(LocalDateTime.now().plusHours(1), game2, player2);
			GamePlayer gamePlayer5= new GamePlayer(LocalDateTime.now().plusHours(2), game3, player1);
			GamePlayer gamePlayer6= new GamePlayer(LocalDateTime.now().plusHours(2), game3, player2);
			GamePlayer gamePlayer7= new GamePlayer(LocalDateTime.now().plusHours(3), game4, player1);
			GamePlayer gamePlayer8= new GamePlayer(LocalDateTime.now().plusHours(3), game4, player2);
			GamePlayer gamePlayer9= new GamePlayer(LocalDateTime.now().plusHours(4), game5, player1);
			GamePlayer gamePlayer10= new GamePlayer(LocalDateTime.now().plusHours(4), game5, player2);
			GamePlayer gamePlayer11= new GamePlayer(LocalDateTime.now().plusHours(5), game6, player1);
			gamePlayerRepository.save(gamePlayer1);
			gamePlayerRepository.save(gamePlayer2);
			gamePlayerRepository.save(gamePlayer3);
			gamePlayerRepository.save(gamePlayer4);
			gamePlayerRepository.save(gamePlayer5);
			gamePlayerRepository.save(gamePlayer6);
			gamePlayerRepository.save(gamePlayer7);
			gamePlayerRepository.save(gamePlayer8);
			gamePlayerRepository.save(gamePlayer9);
			gamePlayerRepository.save(gamePlayer10);
			gamePlayerRepository.save(gamePlayer11);

		};
	}
}
