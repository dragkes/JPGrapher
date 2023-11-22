package org.example.entity;

import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.*;

import static org.example.entity.Pixel.getByColor;

@Data
@Slf4j
public class Graph {
    @NonNull
    private List<Vertex> vertices;
    @Nullable
    @ToString.Exclude
    private boolean[][] adjMatrix;
    @ToString.Exclude
    private String[] labels;
    private List<Vertex> endpoints;

    public Graph(@NotNull List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public void addVertex(Vertex vertex) throws IllegalArgumentException {
        if (this.vertices.isEmpty()) {
            vertices.add(vertex);
            return;
        }
        List<Vertex> neighbours = vertex.getNeighbours();
        if (neighbours.isEmpty())
            throw new IllegalArgumentException("A vertex must have at least one neighbour");
        boolean flag = false;
        for (Vertex neighbour : neighbours) {
            if (this.vertices.contains(neighbour)) {
                flag = true;
                break;
            }
        }
        if (!flag)
            throw new IllegalArgumentException("A vertex is isolated");
        else {
            this.vertices.add(vertex);
            for (Vertex neighbour : neighbours) {
                if (!neighbour.equals(vertex)) {
                    neighbour.getNeighbours().add(vertex);
                }
            }
        }
    }

    public void check() throws Exception {
        Set<Vertex> checked = new HashSet<>();
        this.vertices.forEach(vertex -> checked.addAll(vertex.getNeighbours()));

        if (checked.size() != vertices.size())
            throw new Exception("There are some parts in the graph that are not connected with each other");
    }

    public void matrix() {
        labels = new String[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            labels[i] = (vertices.get(i).getName() == null || vertices.get(i).getName().isEmpty()) ? String.valueOf(i) : vertices.get(i).getName();
        }
        adjMatrix = new boolean[vertices.size()][vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            Vertex vertex = vertices.get(i);
            for (Vertex neighbour : vertex.getNeighbours()) {
                int index = vertices.indexOf(neighbour);
                adjMatrix[i][index] = true;
            }
        }
    }

    public static Graph fromFile(File file, Color pathColor, Color endpointColor, double epsilon) throws Exception {
        List<Pixel> pixels = getPixels(file);

        List<Vertex> vertices = getVertices(pathColor, epsilon, pixels);
        List<Vertex> endpointsList = getVertices(endpointColor, epsilon, pixels);
        List<Vertex> result = new ArrayList<>();

        List<List<Vertex>> grouped = groupByNeighbours(endpointsList);
        grouped.forEach(o -> result.add(o.get(0)));
        Graph graph = new Graph(vertices);
        graph.setEndpoints(result);
        graph.check();
        return graph;
    }

    @NotNull
    private static List<Pixel> getPixels(File file) {
        BufferedImage img;
        try {
            img = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int width = img.getWidth();
        int height = img.getHeight();
        List<Pixel> pixels = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color pixelColor = new Color(img.getRGB(i, j));
                pixels.add(new Pixel(new Point(i, j), pixelColor));
            }
        }
        return pixels;
    }

    @NotNull
    private static List<Vertex> getVertices(Color color, double epsilon, List<Pixel> pixels) {
        pixels = pixels.stream().filter(getByColor(color, epsilon)).toList();
        List<Vertex> vertices = new ArrayList<>();
        pixels.forEach(pixel -> vertices.add(new Vertex(new ArrayList<>(), null, pixel.getCoordinates())));
        findNeighbours(vertices);
        return vertices;
    }

    public void writeDataToFile(File data, File coordinates, File endpoints) throws IOException {
        try (FileWriter writer1 = new FileWriter(data);
             FileWriter writer2 = new FileWriter(coordinates)) {
            @NonNull List<Vertex> vertexList = getVertices();
            for (int j = 0; j < vertexList.size(); j++) {
                Vertex vertex = vertexList.get(j);
                writer2.write(String.format("%.0f %.0f", vertex.getCoordinates().getX(), vertex.getCoordinates().getY()));
                @NonNull List<Vertex> neighbours = vertex.getNeighbours();
                for (int i = 0; i < neighbours.size(); i++) {
                    Vertex neighbour = neighbours.get(i);
                    writer1.write(String.valueOf(vertices.indexOf(neighbour)));
                    if (i != neighbours.size() - 1)
                        writer1.write(" ");
                }
               if (j != vertexList.size() - 1) {
                   writer1.write("\n");
                   writer2.write("\n");
               }
            }
            writeEndpointsToFile(endpoints);
        } catch (IOException e) {
            log.error("Cannot write to file. Reason: " + e.getMessage());
            throw e;
        }
    }

    private void writeEndpointsToFile(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            for (int i = 0; i < endpoints.size(); i++) {
                Vertex vertex = endpoints.get(i);
                writer.write(String.format("%.0f %.0f", vertex.getCoordinates().getX(), vertex.getCoordinates().getY()));
                if (i != endpoints.size() - 1) {
                    writer.write("\n");
                }
            }
        } catch (IOException e) {
            log.error("Cannot write to file. Reason: " + e.getMessage());
        }
    }
    
    private static void findNeighbours(List<Vertex> vertices) {
        Map<Point, Vertex> vertexMap = new HashMap<>();
        vertices.forEach(o -> vertexMap.put(o.getCoordinates(), o));
        for (Vertex vertex : vertices) {
            Point coordinates = vertex.getCoordinates();
            int x = coordinates.x;
            int y = coordinates.y;
            List<Vertex> neighbours = new ArrayList<>();
            Vertex v1 = vertexMap.get(new Point(x + 1, y));
            if (v1 != null) neighbours.add(v1);
            Vertex v2 = vertexMap.get(new Point(x, y + 1));
            if (v2 != null) neighbours.add(v2);
            Vertex v3 = vertexMap.get(new Point(x - 1, y));
            if (v3 != null) neighbours.add(v3);
            Vertex v4 = vertexMap.get(new Point(x, y - 1));
            if (v4 != null) neighbours.add(v4);
            Vertex v5 = vertexMap.get(new Point(x + 1, y + 1));
            if (v5 != null) neighbours.add(v5);
            Vertex v6 = vertexMap.get(new Point(x - 1, y - 1));
            if (v6 != null) neighbours.add(v6);
            Vertex v7 = vertexMap.get(new Point(x + 1, y - 1));
            if (v7 != null) neighbours.add(v7);
            Vertex v8 = vertexMap.get(new Point(x - 1, y + 1));
            if (v8 != null) neighbours.add(v8);
            vertex.setNeighbours(neighbours);
        }
    }

    private static List<List<Vertex>> groupByNeighbours(List<Vertex> vertices) {
        List<List<Vertex>> grouped = new ArrayList<>();
        for (Vertex vertex : vertices) {
            boolean found = false;
            for (List<Vertex> group : grouped) {
                for (Vertex element : group) {
                    if (element.getNeighbours().contains(vertex)) {
                        group.add(vertex);
                        found = true;
                        break;
                    }
                }
                if (found) break;;
            }
            if (!found) {
                List<Vertex> toAdd = new ArrayList<>();
                toAdd.add(vertex);
                grouped.add(toAdd);
            }
        }
        return grouped;
    }
}
