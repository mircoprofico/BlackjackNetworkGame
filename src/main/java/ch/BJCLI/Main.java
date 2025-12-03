package ch.BJCLI;

import ch.BJCLI.client.clientGUI;
import java.io.File;

import ch.BJCLI.server.Server;
import picocli.CommandLine;

@CommandLine.Command(
        name = "bjcli",
        description = "The famous game of BlackJack! Now inside your terminal!",
        subcommands = {clientGUI.class, Server.class},
        mixinStandardHelpOptions = true
)
public class Main {
    public static void main(String[] args) {
        String jarFilename =
                new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath())
                        .getName();

        // Create client command
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}