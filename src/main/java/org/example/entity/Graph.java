package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.List;

import static org.example.entity.Pixel.getByColor;

@Data
public class Graph {
    @NonNull
    private List<Vertex> vertices;
    private boolean[][] adjMatrix;
    private String[] labels;


    public Graph(Vertex... vertices) {
        this.vertices = new ArrayList<>(List.of(vertices));
    }

    public Graph() {
        this.vertices = new ArrayList<>();
    }

    public Graph(List<Vertex> vertices) {
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

    public static Graph fromFile(File file, Color color, double epsilon) throws Exception {
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
                int value = img.getRGB(i, j);
                Color pixelColor = new Color(img.getRGB(i, j));
                pixels.add(new Pixel(new Point(i, j), pixelColor));
            }
        }
        List<Pixel> filtered = new ArrayList<>(pixels);
//        for (int i = 0; i < 256; i++) {
//            filtered = filtered.stream().filter(getByColor(new Color(i,i,i), epsilon).negate()).toList();
//        }
        filtered = filtered.stream().filter(getByColor(color, epsilon)).toList();
        List<Vertex> vertices = new ArrayList<>();
        filtered.forEach(pixel -> vertices.add(new Vertex(new ArrayList<>(), null, pixel.getCoordinates())));

        for (int i = 0; i < vertices.size(); i++) {
            Vertex current = vertices.get(i);
            List<Vertex> neighbours = new ArrayList<>();
            for (Vertex vertex : vertices) {
                if (!current.equals(vertex)) {
                    double distance = vertex.getDistance(current);
                    if (distance < Math.sqrt(2) + 0.1) neighbours.add(vertex);

                }
            }
            current.setNeighbours(neighbours);
        }
        Graph graph = new Graph(vertices);
        graph.check();
        graph.matrix();
        return graph;
    }

    public String getNeighborsList() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < adjMatrix.length; i++) {
            boolean[] matrix = adjMatrix[i];
            StringBuilder temp = new StringBuilder();
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[j]) {
                    temp.append(" ").append(labels[j]);
                }
            }
            if (i != adjMatrix.length - 1) temp.append("\n");
            output.append(temp.substring(1));
        }
        return output.toString();
    }
}
