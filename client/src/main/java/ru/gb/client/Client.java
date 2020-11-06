package ru.gb.client;

import ru.gb.common.Commands;
import ru.gb.common.Constants;

import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Scanner;

public class Client {
    static private Network network;

    private static void greetings() {
        System.out.println("Welcome to personal cloud storage by Lytov Dmitry");
    }

    private static void printHelp() {
        OptionalInt maxCommandLength = Arrays.stream(Commands.class.getEnumConstants()).mapToInt(field -> field.toString().length()).max();
        Arrays.stream(Commands.class.getEnumConstants()).forEach(field -> {
            String fieldName = field.toString().toLowerCase();
            int padding = maxCommandLength.getAsInt() + 1 - fieldName.length();
            System.out.format("%s %" + padding + "s%s\n", fieldName, "", field.params);
        });
        System.out.println();
    }

    private static void processCommand(String line) throws Exception {
        String[] args = line.trim().replaceAll("( )+", " ").split(" ");
        String command = args[0];

        Optional<Commands> matchedCommand = Arrays.stream(Commands.class.getEnumConstants()).filter(c -> command.equalsIgnoreCase(c.toString())).findAny();
        if (!matchedCommand.isPresent()) {
            System.out.println("Wrong command");
            return;
        }
        switch (matchedCommand.get()) {
            case HELP:
                printHelp();
                break;
            case POST_FILE: {
                network.postFiles(args[1]);
                break;
            }
            case GET_FILE: {
                network.getFiles(args[1]);
                break;
            }
            case GET_FILES_LIST: {
                String[] res = args.length > 1 ? network.getFilesList(args[1]) : network.getFilesList();
                System.out.println(String.join("\n", res));
                break;
            }
            case DELETE_FILE: {
                network.deleteFiles(args[1]);
                break;
            }
            default:
        }
    }

    public static void main(String[] args) {
        network = new Network("localhost", Constants.port);
        network.start();

        greetings();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nEnter command:");
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase(Commands.EXIT.toString())) {
                break;
            }
            try {
                processCommand(line);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
