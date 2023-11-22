package org.example.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.function.Predicate;

@Data
@AllArgsConstructor
public class Pixel {
    private Point coordinates;
    private Color color;

    public static Predicate<Pixel> getByColor(Color color, double epsilon) {
        return pixel -> {
            Color pixelColor = pixel.getColor();
            if (epsilon != 0) {
                double compareBy = Math.sqrt(Math.pow(pixelColor.getRed() - color.getRed(), 2) +
                        Math.pow(pixelColor.getGreen() - color.getGreen(), 2) +
                        Math.pow(pixelColor.getBlue() - color.getBlue(), 2));
                return compareBy <= epsilon;
            } else {
                return color.equals(pixelColor);
            }
        };
    }
}
