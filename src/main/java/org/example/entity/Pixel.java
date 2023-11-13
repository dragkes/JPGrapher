package org.example.entity;


import lombok.AllArgsConstructor;
import lombok.Data;

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
                double val1 = Math.sqrt(pixelColor.getRed() * pixelColor.getRed() +
                        pixelColor.getGreen() * pixelColor.getGreen() +
                        pixelColor.getBlue() * pixelColor.getBlue());
                double val2 = Math.sqrt(color.getRed() * color.getRed() +
                        color.getGreen() * color.getGreen() +
                        color.getBlue() * color.getBlue());
                return Math.abs(val1 - val2) <= epsilon;
            } else {
                return color.equals(pixelColor);
            }
        };
    }

    public double getDistance(Pixel pixel) {
        return Math.sqrt(Math.pow(this.coordinates.x - pixel.getCoordinates().x, 2) +
                Math.pow(this.coordinates.y - pixel.getCoordinates().y, 2));
    }
}
