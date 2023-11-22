package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.Graph;

import java.awt.*;
import java.io.File;
import java.util.Objects;


@Slf4j
public class Main {
    public static void main(String[] args) {
//        String pathToImg = "";
//        String pathToNeighbours = "";
//        String pathToCoordinates = "";
//        String pathToEndpoints = "";
//        Color pathColor = null;
//        Color endpointColor = null;
//        for (int i = 0; i < args.length; i++) {
//            if (args[i].startsWith("/")) {
//                String temp = args[i].substring(1);
//                try {
//                    switch (temp) {
//                        case "img" -> pathToImg = args[i + 1];
//                        case "dataTxt" -> pathToNeighbours = args[i + 1];
//                        case "coordTxt" -> pathToCoordinates = args[i + 1];
//                        case "endpointTxt" -> pathToEndpoints = args[i + 1];
//                        case "pathColor" -> pathColor = new Color(Integer.parseInt(args[i + 1]),
//                                Integer.parseInt(args[i + 2]),
//                                Integer.parseInt(args[i + 3]));
//                        case "endpointColor" -> endpointColor = new Color(Integer.parseInt(args[i + 1]),
//                                Integer.parseInt(args[i + 2]),
//                                Integer.parseInt(args[i + 3]));
//                    }
//                } catch (Exception ignored) {}
//            }
//        }
//
//        if (Objects.equals(pathToImg, "") ||
//                Objects.equals(pathToEndpoints, "") ||
//                Objects.equals(pathToNeighbours, "") ||
//                Objects.equals(pathToCoordinates, "") ||
//                pathColor == null ||
//                endpointColor == null) {
//            System.out.println("Using: JPGrapher /img *path to image file* " +
//                    "/pathColor *color of path line, in RGB format* " +
//                    "/endpointColor *color of endpoints, in RGB format* " +
//                    "/dataTxt *path to output data txt file* " +
//                    "/coordTxt *path to output coordinates txt file* " +
//                    "/endpointTxt *path to output endpoints txt file*");
//
//            System.out.println("Example: ");
//            System.exit(0);
//        }
        File folder = new File("C:\\Users\\andre\\IdeaProjects\\JPGrapher\\images");
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                try {
                    Graph graph = Graph.fromFile(file, new Color(35, 177, 77), new Color(63, 72, 204), 6);
                    if (graph.getVertices().isEmpty()) throw new Exception("No color match");
                    File data = new File("C:\\Users\\andre\\IdeaProjects\\JPGrapher\\output\\data(" + file.getName() + ").txt");
                    if (data.createNewFile())
                        System.out.println("File '" + data.getPath() + "' created.");
                    File coordinates = new File("C:\\Users\\andre\\IdeaProjects\\JPGrapher\\output\\coordinates(" + file.getName() + ").txt");
                    if (coordinates.createNewFile())
                        System.out.println("File '" + coordinates.getPath() + "' created.");
                    File endpoints = new File("C:\\Users\\andre\\IdeaProjects\\JPGrapher\\output\\endpoints(" + file.getName() + ").txt");
                    if (endpoints.createNewFile())
                        System.out.println("File '" + endpoints.getPath() + "' created.");
                    graph.writeDataToFile(data, coordinates, endpoints);
                } catch (Exception e) {
                    System.out.println("Failed to create graph with file '" + file.getName() +"'. Reason: " + e.getMessage());
                }
            }
        }

    }
}