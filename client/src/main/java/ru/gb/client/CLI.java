package ru.gb.client;

import ru.gb.client.exceptions.*;
import ru.gb.common.Commands;
import ru.gb.common.Constants;

import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Scanner;

public class CLI {
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

    private static void checkArgumentsNumber(String[] args, int argumentsNumber) throws InvalidArgumentsNumberException {
        if (args.length < argumentsNumber + 1) {
            throw new InvalidArgumentsNumberException(argumentsNumber);
        }
    }

    private static void processCommand(String line) throws Exception {
        String[] args = line.trim().replaceAll("( )+", " ").split(" ");
        String command = args[0];

        Optional<Commands> matchedCommand = Arrays.stream(Commands.class.getEnumConstants()).filter(c -> command.equalsIgnoreCase(c.toString())).findAny();
        if (!matchedCommand.isPresent()) {
            throw new WrongCommandException();
        }
        switch (matchedCommand.get()) {
            case LOGIN:
                checkArgumentsNumber(args, 2);
                network.login(args[1], args[2]);
                break;
            case HELP:
                printHelp();
                break;
            case POST: {
                checkArgumentsNumber(args, 1);
                network.postFiles(args[1]);
                break;
            }
            case GET: {
                checkArgumentsNumber(args, 1);
                network.getFiles(args[1]);
                break;
            }
            case GET_LIST: {
                String[] res = args.length > 1 ? network.getFilesList(args[1]) : network.getFilesList();
                System.out.println(String.join("\n", res));
                break;
            }
            case DELETE: {
                checkArgumentsNumber(args, 1);
                network.deleteFiles(args[1]);
                break;
            }
            case PATCH: {
                checkArgumentsNumber(args, 2);
                network.patchFiles(args[1], args[2]);
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
            } catch (UnauthorizedException e) {
                System.out.println("Unauthorized");
            } catch (ServerException e) {
                System.out.println("Server error");
            } catch (WrongCommandException e) {
                System.out.println("Wrong command");
            } catch (InvalidArgumentsNumberException e) {
                System.out.println("Invalid arguments number, required " + e.getRequiredArgumentsNumber());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

// todo: GUI

// todo: handle all exceptions
// todo: store users in DB
// todo: creating users
// todo: hash sum
// todo: tree-view server storage
// todo: logout
// todo: transferring progress
// todo: sync
// todo: sharing
// todo: session token
// todo: refactor server handler