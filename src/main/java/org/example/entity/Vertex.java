package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Vertex {
    @NonNull
    @EqualsAndHashCode.Exclude private List<Vertex> neighbours;
    @EqualsAndHashCode.Exclude private String name;
    @NonNull
    private Point coordinates;

    @Override
    public String toString() {
        return "Vertex{" +
                "name='" + name + '\'' +
                ", coordinates=" + coordinates +
                '}';
    }
}
