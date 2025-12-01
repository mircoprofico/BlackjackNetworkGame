package server.serverCommands;

import server.PlayerConnection;

import java.util.concurrent.Callable;

/**
 * @class Join
 * @brief Handles the JOIN command for a player.
 */
public class Join implements Callable<Integer> {

    private final PlayerConnection playerConnection; // Player connection instance
    private final String playerName;                  // Name passed by the client

    /**
     * Constructor
     *
     * @param playerConnection the player's connection object
     * @param playerName       the name provided by the client
     */
    public Join(PlayerConnection playerConnection, String playerName) {
        this.playerConnection = playerConnection;
        this.playerName = playerName;
    }

    /**
     * @brief Called from PlayerConnection to execute the JOIN command
     *
     * @return 0 always (success)
     */
    @Override
    public Integer call() {
        try {
            // Store the player name in the connection object
            playerConnection.setPlayerName(playerName);

            // Log on the server console
            System.out.println("[Server] Player joined: " + playerName);

            // Send response to the client
            playerConnection.sendPacket("WELCOME " + playerName);

        } catch (Exception e) {
            try {
                playerConnection.sendPacket("ERROR could not join");
            } catch (Exception ex) {
                // Ignore nested errors
            }
        }
        return 0;
    }
}
