import client.Client;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "join",
        description = "Join the Blackjack game"
)
public class Join implements Callable<Integer> {

    @CommandLine.ParentCommand
    Client.ClientCommands parent; // le parent côté client

    @CommandLine.Parameters(index = "0", description = "Player name")
    String playerName; // récupéré directement depuis la commande

    @Override
    public Integer call() throws IOException {
        System.out.println("[Client] Sending JOIN command as " + playerName);

        // Envoi de la commande au serveur via le client TCP
        String response = parent.client.send("JOIN " + playerName);
        System.out.println("[Server] " + response);

        return 0;
    }
}
