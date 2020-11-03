package ru.gb.client;

import ru.gb.common.Commands;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Scanner;

public class Client {
    static private Network network;

    private static void greetings() {
        System.out.println("Welcome to personal cloud storage by Lytov Dmitry\n");
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

    private static void processCommand(String line) {
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
            case POST_FILES:
                for (int i = 1; i < args.length; i++) {
                    network.send(Paths.get("client_storage/" + args[i]));
                }
                break;

            default:
        }
    }

    public static void main(String[] args) {
        network = new Network("localhost", 8189);
        network.start();

//        network.send(Paths.get("client_storage/1.jpg"));
//        network.send(Paths.get("client_storage/2.jpg"));
//        network.send(Paths.get("client_storage/3.bmp"));
//        network.send(Paths.get("client_storage/4.bmp"));

//        network.waitForAnswer();
//        network.waitForAnswer();
//        network.waitForAnswer();
//        network.waitForAnswer();

        greetings();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter command:");
            processCommand(scanner.nextLine());
        }
    }
}
