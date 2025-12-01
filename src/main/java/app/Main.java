package app;

import client.Client;
import server.Server;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * @class Main
 * @brief Entry point to launch the Blackjack game in server or client mode.
 *
 * Uses Picocli to provide two subcommands:
 *  - server : start the TCP server
 *  - client : start the client CLI
 */
@Command(
        name = "Blackjack",
        description = "Start the Blackjack game as server or client.",
        mixinStandardHelpOptions = true,
        subcommands = {
                Main.CreateServer.class,
                Main.CreateClient.class
        }
)
public class Main implements Callable<Integer> {

    @Override
    public Integer call() {
        // No subcommand specified → display help
        CommandLine.usage(this, System.out);
        return 0;
    }

    /**
     * @class CreateServer
     * @brief Picocli subcommand to start the TCP server.
     */
    @Command(
            name = "server",
            description = "Start the Blackjack TCP server.",
            mixinStandardHelpOptions = true
    )
    static class CreateServer implements Callable<Integer> {

        @Option(names = "-p", arity = "--port", description = "Port number", defaultValue = "1234")
        int port;

        @Override
        public Integer call() {
            System.out.printf("Starting server on port %d%n", port);

            Server server = new Server(port); // ta classe Server existante
            server.start(); // boucle infinie pour accepter les clients

            return 0;
        }
    }

    /**
     * @class CreateClient
     * @brief Picocli subcommand to start the client CLI.
     */
    @Command(
            name = "client",
            description = "Start a Blackjack client in interactive mode.",
            mixinStandardHelpOptions = true
    )
    static class CreateClient implements Callable<Integer> {

        @Option(names = {"-h", "--host"}, description = "Server hostname", defaultValue = "localhost")
        String host;

        @Option(names = {"-p", "--port"}, description = "Server port", defaultValue = "1234")
        int port;

        @Override
        public Integer call() {
            System.out.printf("Starting client connecting to %s:%d%n", host, port);

            Client client = new Client(host, port); // ta classe Client existante
            client.start(); // mode interactif → > JOIN, > HIT, > BET ...

            return 0;
        }
    }

    /**
     * @brief Picocli entry point.
     */
    public static void main(String[] args) {
        String jarName = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
        int exitCode = new CommandLine(new Main()).setCommandName(jarName).execute(args);
        System.exit(exitCode);
    }
}
