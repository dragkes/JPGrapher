package org.example;

import org.example.entity.Graph;
import org.example.entity.Pixel;
import org.example.entity.Vertex;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.example.entity.Pixel.getByColor;

public class Main {
    public static void main(String[] args) throws Exception {
        File file = new File("./6.bmp");
        Graph graph = Graph.fromFile(file, new Color(237, 28, 36), 0);
        //printMatrix(graph.getAdjMatrix(), graph.getLabels());
        try(FileWriter writer = new FileWriter(new File("data.txt"))) {
            writer.write(graph.getNeighborsList());
        }
        System.out.println();
    }

    public static void printMatrix(boolean[][] matrix, String[] indices) {
        System.out.print("  ");
        for (String index : indices) {
            System.out.print(index + " ");
        }
        System.out.println();
        for (int i = 0; i < matrix.length; i++) {
            System.out.print(indices[i] + " ");
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print((matrix[i][j] ? 1 : 0) + " ");
            }
            System.out.println();
        }
    }
}