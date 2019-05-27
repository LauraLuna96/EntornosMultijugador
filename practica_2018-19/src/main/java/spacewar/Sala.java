package spacewar;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Sala {

	private SpacewarGame game;
	private Map<String, Player> players = new ConcurrentHashMap<>();
	private String name;
	private AtomicInteger numPlayers = new AtomicInteger(0);
	private final static int MAX_PLAYERS = 3;
	
	Sala(String name) {
		this.name = name;
		this.game = new SpacewarGame(this);
	}
	
	public int getMaxPlayers() {
		return MAX_PLAYERS;
	}
	
	public int getNumPlayers() {
		return numPlayers.get();
	}

	public SpacewarGame getGame() {
		return game;
	}

	public Map<String, Player> getPlayersMap() {
		return players;
	}
	
	public Collection<Player> getPlayers() {
		return players.values();
	}

	public String getName() {
		return name;
	}
	
	public synchronized void startGameIfFull() throws Exception {
		if (!game.getIsRunning()) {
			int count = numPlayers.get();
			if (count == MAX_PLAYERS) {
				System.out.println("[GAME] Room " + this.name + " is full, starting game now.");
				game.sendBeginningMessages();
				game.startGameLoop();
			}
		} else {
			System.out.println("[ERROR] Error while starting game in room " + this.name + ", there is a game already in progress.");
		}
	}
	
	// No queremos que, mientras estamos comprobando si un jugador puede meterse o no,
	// se le cuele otro. Por eso lo marcamos como synchronized.
	public synchronized boolean addPlayer(Player player) throws Exception {
		boolean metido = false;
		if (players.size() < MAX_PLAYERS) {
			players.put(player.getSession().getId(), player);
			game.addPlayer(player);
			numPlayers.incrementAndGet();
			//System.out.println("[ROOM] Room " + this.name + " now has " + count + " players.");
			metido = true;
		} else {
			metido = false;
		}
		return metido;
	}
	
	public synchronized void removePlayer(Player player) {
		players.remove(player.getSession().getId());
		game.removePlayer(player);
		player.reset();

		int count = this.numPlayers.decrementAndGet();
		if (count == 0) {
			game.stopGameLoop();
		}
	}
	
	// Método que manda un mensaje específico a TODOS los jugadores (de la sala)
	public void broadcast(String message) {
		for (Player player : getPlayers()) {
			try {
				player.sendMessage(message.toString()); // Usamos el método sendMessage del player porque está protegido por EM
			} catch (Throwable ex) {
				System.err.println("Execption sending message to player " + player.getSession().getId());
				ex.printStackTrace(System.err);
				this.removePlayer(player);
			}
		}
	}
	
	// Método que manda un mensaje específico a todos menos uno
		public void broadcastExcept(String message, String playerName) {
			for (Player player : getPlayers()) {
				try {
					if (player.getPlayerName() != playerName) player.sendMessage(message.toString()); // Usamos el método sendMessage del player porque está protegido por EM
				} catch (Throwable ex) {
					System.err.println("Execption sending message to player " + player.getSession().getId());
					ex.printStackTrace(System.err);
					this.removePlayer(player);
				}
			}
		}
		
		public ObjectNode getSalaAsObjectNode(ObjectNode jsonSala) {
			jsonSala.put("roomName", getName());
			jsonSala.put("numPlayers", getNumPlayers());
			jsonSala.put("maxPlayers", MAX_PLAYERS);
			return jsonSala;
		}
	
	// Método que devuelve la colección de jugadores escrita en JSON
	/*public String playerString() {
		String result = "";
		for (Player player : getPlayers()) {
			result+=player.getPlayerName()+",";
		}
		result=result.substring(0,result.length()-1);
		return result;
	}*/

}
