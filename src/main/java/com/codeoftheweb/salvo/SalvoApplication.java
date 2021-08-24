package com.codeoftheweb.salvo;

//Elementos importados
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {SpringApplication.run(SalvoApplication.class, args);}//Permite runear el programa/Método que inicializa el proyecto

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository,
									  SalvoRepository salvoRepository, ScoreRepository scoreRepository) {    //playerRepository es el repositorio
		return (args) -> {
			Player player1 = new Player("j.bauer@ctu.gov"); //Creo jugadores
			Player player2 = new Player("c.obrian@ctu.gov");
			Player player3 = new Player("t.almeida@ctu.gov");
			Player player4 = new Player("d.palmer@whitehouse.gov");
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
			GamePlayer gamePlayer5= new GamePlayer(LocalDateTime.now().plusHours(2), game3, player2);
			GamePlayer gamePlayer6= new GamePlayer(LocalDateTime.now().plusHours(2), game3, player3);
			GamePlayer gamePlayer7= new GamePlayer(LocalDateTime.now().plusHours(3), game4, player1);
			GamePlayer gamePlayer8= new GamePlayer(LocalDateTime.now().plusHours(3), game4, player2);
			GamePlayer gamePlayer9= new GamePlayer(LocalDateTime.now().plusHours(4), game5, player3);
			GamePlayer gamePlayer10= new GamePlayer(LocalDateTime.now().plusHours(4), game5, player1);
			GamePlayer gamePlayer11= new GamePlayer(LocalDateTime.now().plusHours(5), game6, player4);
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

			Ship ship1= new Ship("Carrier", gamePlayer1, Arrays.asList("A1" , "A2", "A3", "A4", "A5"));
			Ship ship2= new Ship("Battleship", gamePlayer1, Arrays.asList("B1", "B2", "B3", "B4"));
			Ship ship3= new Ship("Cruiser", gamePlayer1, Arrays.asList("C1", "C2", "C3"));
			Ship ship4= new Ship("Submarine", gamePlayer1, Arrays.asList("D1", "D2", "D3"));
			Ship ship5= new Ship("Destroyer", gamePlayer1, Arrays.asList("E1", "E2"));
			Ship ship6= new Ship("Carrier", gamePlayer2, Arrays.asList("A1" , "A2", "A3", "A4", "A5"));
			Ship ship7= new Ship("Battleship", gamePlayer2, Arrays.asList("B1", "B2", "B3", "B4"));
			Ship ship8= new Ship("Cruiser", gamePlayer2, Arrays.asList("C1", "C2", "C3"));
			Ship ship9= new Ship("Submarine", gamePlayer2, Arrays.asList("D1", "D2", "D3"));
			Ship ship10= new Ship("Destroyer", gamePlayer2, Arrays.asList("E1", "E2"));
			shipRepository.save(ship1);
			shipRepository.save(ship2);
			shipRepository.save(ship3);
			shipRepository.save(ship4);
			shipRepository.save(ship5);
			shipRepository.save(ship6);
			shipRepository.save(ship7);
			shipRepository.save(ship8);
			shipRepository.save(ship9);
			shipRepository.save(ship10);

			Salvo salvo1= new Salvo(gamePlayer1, 1, Arrays.asList("A1", "A2", "A3", "A4", "A5"));
			Salvo salvo2= new Salvo(gamePlayer2, 1, Arrays.asList("B1", "B2", "B3", "B4", "B5"));
			Salvo salvo3= new Salvo(gamePlayer1, 2, Arrays.asList("C1", "C2", "C3", "C4", "C5"));
			Salvo salvo4= new Salvo(gamePlayer2, 2, Arrays.asList("D1", "D2", "D3", "D4", "D5"));
			salvoRepository.save(salvo1);
			salvoRepository.save(salvo2);
			salvoRepository.save(salvo3);
			salvoRepository.save(salvo4);

			Score score1= new Score(game1, player1, 1f, LocalDateTime.now());
			Score score2= new Score(game1, player2, 0f, LocalDateTime.now());
			Score score3= new Score(game2, player1, 1f, LocalDateTime.now());
			Score score4= new Score(game2, player2, 0f, LocalDateTime.now());
			scoreRepository.save(score1);
			scoreRepository.save(score2);
			scoreRepository.save(score3);
			scoreRepository.save(score4);

		};
	}
}
