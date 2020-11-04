package ru.gb.client;

import ru.gb.common.Commands;
import ru.gb.common.Status;
import ru.gb.common.StringReceiver;
import ru.gb.common.messages.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Scanner;
import java.util.stream.Collectors;

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
            case POST_FILES: {
                network.sendObject(new PostFileRequest("client_storage/" + args[1]));
                Response response = (Response) network.waitForAnswer();
                System.out.println(response);
                if (response.getStatus() == Status.Failure) {
                    System.out.println("Server error");
                } else {
                    System.out.println("Uploaded file: " + args[1]);
                }
                break;
            }
            case GET_FILES: {
                network.sendObject(new GetFileRequest(args[1]));
                GetFileResponse response = (GetFileResponse) network.waitForAnswer();
                System.out.println(response);
                if (response.getStatus() == Status.Failure) {
                    System.out.println("Server error");
                } else {
                    FileOutputStream fos = new FileOutputStream("client_storage/" + response.getFileName());
                    fos.write(response.getData());
                    fos.close();
                    System.out.println("File received: " + response.getFileName());
                }
                break;
            }
            case GET_FILES_LIST: {
                if (args.length > 1) {
                    network.sendObject(new GetFilesListRequest(args[1]));
                } else {
                    network.sendObject(new GetFilesListRequest());
                }
                GetFilesListResponse response = (GetFilesListResponse) network.waitForAnswer();
                System.out.println(response);
                if (response.getStatus() == Status.Failure) {
                    System.out.println("Server error");
                } else {
                    System.out.println(String.join("\n", response.getFilesList()));
                }
                break;
            }
            case DELETE_FILES: {
                network.sendObject(new DeleteFileRequest(args[1]));
                Response response = (Response) network.waitForAnswer();
                System.out.println(response);
                if (response.getStatus() == Status.Failure) {
                    System.out.println("Server error");
                } else {
                    System.out.println("Deleted file: " + args[1]);
                }
                break;
            }
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
