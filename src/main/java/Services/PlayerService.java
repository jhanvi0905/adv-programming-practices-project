package Services;

import Constants.ApplicationConstants;
import Exceptions.InvalidCommand;
import Models.*;
import Utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerService {

	/**
	 * Checks if player name is exists in given existing player list
	 * 
	 * @param p_existingPlayerList existing players list present in game
	 * @param p_playerName         player name which needs to be looked upon
	 * @return boolean true if player name is unique, false if its not
	 */
	public boolean isPlayerNameUnique(List<Player> p_existingPlayerList, String p_playerName) {
		boolean l_isUnique = true;
		if (!CommonUtil.isCollectionEmpty(p_existingPlayerList)) {
			for (Player l_player : p_existingPlayerList) {
				if (l_player.getPlayerName().equalsIgnoreCase(p_playerName)) {
					l_isUnique = false;
					break;
				}
			}
		}
		return l_isUnique;
	}

	/**
	 * This method is used to add and remove players.
	 *
	 * @param p_existingPlayerList current player list.
	 * @param p_operation          operation to add or remove player.
	 * @param p_argument           name of player to add or remove.
	 * @return return updated list of player.
	 */
	public List<Player> addRemovePlayers(List<Player> p_existingPlayerList, String p_operation, String p_argument) {
		List<Player> l_updatedPlayers = new ArrayList<>();
		if (!CommonUtil.isCollectionEmpty(p_existingPlayerList))
			l_updatedPlayers.addAll(p_existingPlayerList);

		String l_enteredPlayerName = p_argument.split(" ")[0];
		boolean l_playerNameAlreadyExist = !isPlayerNameUnique(p_existingPlayerList, l_enteredPlayerName);

		switch (p_operation.toLowerCase()) {
		case "add":
			addGamePlayer(l_updatedPlayers, l_enteredPlayerName, l_playerNameAlreadyExist);
			break;
		case "remove":
			removeGamePlayer(p_existingPlayerList, l_updatedPlayers, l_enteredPlayerName, l_playerNameAlreadyExist);
			break;
		default:
			System.out.println("Invalid Operation on Players list");
		}
		return l_updatedPlayers;
	}

	/**
	 * Remove player from the game if it exists
	 * 
	 * @param p_existingPlayerList     Existing player list present in game
	 * @param p_updatedPlayers         Updated player list with removal to be done
	 * @param p_enteredPlayerName      Player name which is to be removed
	 * @param p_playerNameAlreadyExist true if player already exists
	 */
	private void removeGamePlayer(List<Player> p_existingPlayerList, List<Player> p_updatedPlayers,
			String p_enteredPlayerName, boolean p_playerNameAlreadyExist) {
		if (p_playerNameAlreadyExist) {
			for (Player l_player : p_existingPlayerList) {
				if (l_player.getPlayerName().equalsIgnoreCase(p_enteredPlayerName)) {
					p_updatedPlayers.remove(l_player);
					System.out.println("Player with name : " + p_enteredPlayerName + " has been removed successfully.");
				}
			}
		} else {
			System.out.print("Player with name : " + p_enteredPlayerName + " does not Exist. Changes are not made.");
		}
	}

	/**
	 * Adds player to Game if its not there already
	 * 
	 * @param p_updatedPlayers         updated player list with newly added player
	 * @param p_enteredPlayerName      new player name to be added
	 * @param p_playerNameAlreadyExist true if player to be added already exists
	 */
	private void addGamePlayer(List<Player> p_updatedPlayers, String p_enteredPlayerName,
			boolean p_playerNameAlreadyExist) {
		if (p_playerNameAlreadyExist) {
			System.out.print("Player with name : " + p_enteredPlayerName + " already Exists. Changes are not made.");
		} else {
			Player l_addNewPlayer = new Player(p_enteredPlayerName);
			p_updatedPlayers.add(l_addNewPlayer);
			System.out.println("Player with name : " + p_enteredPlayerName + " has been added successfully.");
		}
	}

	/**
	 * Check whether players are loaded or not
	 *
	 * @param p_gameState current game state with map and player information
	 * @return boolean players exists or not
	 */
	public boolean checkPlayersAvailability(GameState p_gameState) {
		if (p_gameState.getD_players() == null || p_gameState.getD_players().isEmpty()) {
			System.out.println("Kindly add players before assigning countries");
			return false;
		}
		return true;
	}

	/**
	 * This method is used to assign countries randomly among players
	 *
	 * @param p_gameState current game state with map and player information
	 */
	public void assignCountries(GameState p_gameState) {
		if (!checkPlayersAvailability(p_gameState))
			return;

		List<Country> l_countries = p_gameState.getD_map().getD_countries();
		int l_countriesPerPlayer = Math.floorDiv(l_countries.size(), p_gameState.getD_players().size());

		this.performRandomCountryAssignment(l_countriesPerPlayer, l_countries, p_gameState.getD_players());
		this.performContinentAssignment(p_gameState.getD_players(), p_gameState.getD_map().getD_continents());
		System.out.println("Countries have been assigned to Players.");

	}

	/**
	 * Performs random country assignment to all players
	 *
	 * @param p_countriesPerPlayer countries which are to be assigned to each player
	 * @param p_countries          list of all countries present in map
	 * @param p_players            list of all available players
	 */
	private void performRandomCountryAssignment(int p_countriesPerPlayer, List<Country> p_countries,
			List<Player> p_players) {
		List<Country> l_unassignedCountries = new ArrayList<>(p_countries);
		for (Player l_pl : p_players) {
			if (l_unassignedCountries.isEmpty())
				break;
			// Based on number of countries to be assigned to player, it generates random
			// country and assigns to player
			for (int i = 0; i < p_countriesPerPlayer; i++) {
				Random l_random = new Random();
				int l_randomIndex = l_random.nextInt(l_unassignedCountries.size());
				Country l_randomCountry = l_unassignedCountries.get(l_randomIndex);

				if (l_pl.getD_coutriesOwned() == null)
					l_pl.setD_coutriesOwned(new ArrayList<>());
				l_pl.getD_coutriesOwned().add(l_randomCountry);
				System.out.println("Player : " + l_pl.getPlayerName() + " is assigned with country : "
						+ l_randomCountry.getD_countryName());
				l_unassignedCountries.remove(l_randomCountry);
			}
		}
		// If any countries are still left for assignment, it will redistribute those
		// among players
		if (!l_unassignedCountries.isEmpty()) {
			System.out.println(l_unassignedCountries);
			performRandomCountryAssignment(1, l_unassignedCountries, p_players);
		}
	}

	/**
	 * Checks if player is having any continent as a result of random country
	 * assignment
	 *
	 * @param p_players    list of all available players
	 * @param p_continents list of all available continents
	 */
	private void performContinentAssignment(List<Player> p_players, List<Continent> p_continents) {
		for (Player l_pl : p_players) {
			List<String> l_countriesOwned = new ArrayList<>();
			l_pl.getD_coutriesOwned().forEach(l_country -> l_countriesOwned.add(l_country.getD_countryName()));

			for (Continent l_cont : p_continents) {
				List<String> l_countriesOfContinent = new ArrayList<>();
				l_cont.getD_countries().forEach(l_count -> l_countriesOfContinent.add(l_count.getD_countryName()));
				if (l_countriesOwned.containsAll(l_countriesOfContinent)) {
					if (l_pl.getD_continentsOwned() == null)
						l_pl.setD_continentsOwned(new ArrayList<>());

					l_pl.getD_continentsOwned().add(l_cont);
					System.out.println("Player : " + l_pl.getPlayerName() + " is assigned with continent : "
							+ l_cont.getD_continentName());
				}
			}
		}
	}

	/**
	 * @param p_commandEntered command entered by the user
	 * @param p_player         player to create deploy order
	 */
	public void createDeployOrder(String p_commandEntered, Player p_player) {
		List<Order> l_orders = CommonUtil.isCollectionEmpty(p_player.getD_ordersToExecute()) ? new ArrayList<>()
				: p_player.getD_ordersToExecute();
		String l_countryName = p_commandEntered.split(" ")[1];
		String l_noOfArmies = p_commandEntered.split(" ")[2];
		if (validateDeployOrderArmies(p_player, l_noOfArmies)) {
			System.out.println(
					"Given deploy order cant be executed as armies in deploy order exceeds player's unallocated armies");
		} else {
			Order l_orderObject = new Order(p_commandEntered.split(" ")[0], l_countryName,
					Integer.parseInt(l_noOfArmies));
			l_orders.add(l_orderObject);
			p_player.setD_ordersToExecute(l_orders);
			Integer l_unallocatedarmies = p_player.getD_noOfUnallocatedArmies() - Integer.parseInt(l_noOfArmies);
			p_player.setD_noOfUnallocatedArmies(l_unallocatedarmies);
			System.out.println("Order has been added to queue for execution.");
		}
	}

	/**
	 * 
	 * Used to test number of armies entered in deploy command to check that player
	 * cannot deploy more armies that there is in their reinforcement pool.
	 *
	 * @param p_player     player to create deploy order
	 * @param p_noOfArmies number of armies to deploy
	 * @return boolean to validate armies to deploy
	 */
	public boolean validateDeployOrderArmies(Player p_player, String p_noOfArmies) {
		return p_player.getD_noOfUnallocatedArmies() < Integer.parseInt(p_noOfArmies) ? true : false;
	}

	/**
	 * Calculates armies of player based on countries and continents owned
	 *
	 * @param p_player player for which armies have to be calculated
	 * @return Integer armies to be assigned to player
	 */
	public int calculateArmiesForPlayer(Player p_player) {
		int l_armies = 0;
		if (!CommonUtil.isCollectionEmpty(p_player.getD_coutriesOwned())) {
			l_armies = Math.max(3, Math.round((p_player.getD_coutriesOwned().size()) / 3));
		}
		if (!CommonUtil.isCollectionEmpty(p_player.getD_continentsOwned())) {
			int l_continentCtrlValue = 0;
			for (Continent l_continent : p_player.getD_continentsOwned()) {
				l_continentCtrlValue = l_continentCtrlValue + l_continent.getD_continentValue();
			}
			l_armies = l_armies + l_continentCtrlValue;
		}
		return l_armies;
	}

	/**
	 * Assigns armies to each player of the game
	 *
	 * @param p_gameState current game state with map and player information
	 */
	public void assignArmies(GameState p_gameState) {
		for (Player l_pl : p_gameState.getD_players()) {
			Integer l_armies = this.calculateArmiesForPlayer(l_pl);
			System.out.println("Player : " + l_pl.getPlayerName() + " has been assigned with " + l_armies + " armies");

			l_pl.setD_noOfUnallocatedArmies(l_armies);
		}
	}

	/**
	 * Check if unexecuted orders exists in the game
	 *
	 * @param p_playersList players involved in game
	 * @return boolean true if unexecuted orders exists with any of the players or
	 *         else false
	 */
	public boolean unexecutedOrdersExists(List<Player> p_playersList) {
		int l_totalUnexecutedOrders = 0;
		for (Player l_player : p_playersList) {
			l_totalUnexecutedOrders = l_totalUnexecutedOrders + l_player.getD_ordersToExecute().size();
		}
		return l_totalUnexecutedOrders != 0;
	}

	/**
	 * Check if any of the players have unassigned armies
	 *
	 * @param p_playersList players involved in game
	 * @return boolean true if unassigned armies exists with any of the players or
	 *         else false
	 */
	public boolean unassignedArmiesExists(List<Player> p_playersList) {
		int l_unassignedArmies = 0;
		for (Player l_player : p_playersList) {
			l_unassignedArmies = l_unassignedArmies + l_player.getD_noOfUnallocatedArmies();
		}
		return l_unassignedArmies != 0;
	}

	/**
	 * This method is called by controller to add players, update gameState.
	 *
	 * @param p_gameState update game state with players information.
	 * @param p_operation operation to add or remove player.
	 * @param p_argument  name of player to add or remove.
	 */
	public void updatePlayers(GameState p_gameState, String p_operation, String p_argument) {
		if (!isMapLoaded(p_gameState)) {
			System.out.println("Kindly load the map first to add player: " + p_argument);
			return;
		}
		List<Player> l_updatedPlayers = this.addRemovePlayers(p_gameState.getD_players(), p_operation, p_argument);

		if (!CommonUtil.isNull(l_updatedPlayers)) {
			p_gameState.setD_players(l_updatedPlayers);
		}
	}

	/**
	 * Check whether map is loaded or not.
	 * 
	 * @param p_gameState current game state with map and player information
	 * @return boolean map is loaded or not
	 */
	public boolean isMapLoaded(GameState p_gameState) {
		return !CommonUtil.isNull(p_gameState.getD_map()) ? true : false;
	}
}
