package ru.gb.client;

import ru.gb.common.Commands;
import ru.gb.common.FileReceiver;
import ru.gb.common.StringReceiver;

import java.io.IOException;
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

    private static void processCommand(String line) throws IOException {
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
                network.send(Commands.POST_FILES.code);
                network.send(Paths.get("client_storage/" + args[1]));
                break;
            case GET_FILES:
                network.send(Commands.GET_FILES.code);
                network.send(args[1]);
                FileReceiver fr = new FileReceiver("client_storage");
                do {
                    fr.put(network.waitForAnswer());
                } while (!fr.fileIsReceived());
                break;
            case GET_FILES_LIST:
                network.send(Commands.GET_FILES_LIST.code);
                network.send(args.length > 1 ? args[1] : " ");
                StringReceiver sr = new StringReceiver();
                do {
                    sr.put(network.waitForAnswer());
                } while (!sr.received());
                System.out.println(sr);
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
            try {
                processCommand(scanner.nextLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
