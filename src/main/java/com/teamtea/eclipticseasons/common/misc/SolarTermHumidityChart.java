package com.teamtea.eclipticseasons.common.misc;

import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import java.awt.Color;
import java.awt.Graphics2D;

public class SolarTermHumidityChart {


    private final double[] humidity;
    private final String biomeName;

    public SolarTermHumidityChart(String biomeName, double[] humidity) {
        this.biomeName = biomeName;
        this.humidity = humidity;
    }


    public BufferedImage renderChart(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        int padding = 50;
        int chartWidth = width - 2 * padding;
        int chartHeight = height - 2 * padding;

        // title
        g2d.setColor(Color.BLACK);
        String title = biomeName + " - Humid in a Year";
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        int titleX = (width - titleWidth) / 2; // 居中显示标题
        g2d.drawString(title, titleX, padding - 10);

        // solar term
        g2d.setColor(Color.BLACK);
        g2d.drawLine(padding, chartHeight + padding, chartWidth + padding, chartHeight + padding);
        SolarTerm[] solarTerms = SolarTerm.collectValues();
        int xStep = chartWidth / (solarTerms.length - 2);
        for (int i = 0; i < solarTerms.length-1; i++) {
            int x = padding + i * xStep;
            g2d.drawString(solarTerms[i].getTranslation().getString(), x - 10, chartHeight + padding + 20);
            g2d.drawLine(x, chartHeight + padding - 5, x, chartHeight + padding + 5);
        }

        // humid
        g2d.drawLine(padding, padding, padding, chartHeight + padding);
        Humidity[] humidityLabels = Humidity.collectValues();
        int yStep = chartHeight / (humidityLabels.length - 1);
        for (int i = 0; i < humidityLabels.length; i++) {
            int y = chartHeight + padding - i * yStep;
            g2d.drawString(humidityLabels[i].getTranslation().getString(), padding - 40, y + 5);
            g2d.drawLine(padding - 5, y, padding + 5, y);
        }

        // lines
        for (int i = 0; i < humidity.length - 1; i++) {
            int x1 = padding + i * xStep;
            int y1 = (int) (chartHeight + padding - humidity[i] * yStep);
            int x2 = padding + (i + 1) * xStep;
            int y2 = (int) (chartHeight + padding - humidity[i + 1] * yStep);

            g2d.setColor(Color.black);
            g2d.drawLine(x1, y1, x2, y2);

            g2d.fillOval(x1 - 3, y1 - 3, 6, 6);
        }

        g2d.dispose();
        return image;
    }


    public void exportToImage(String filePath, String format, int width, int height) {
        BufferedImage image = renderChart(width, height);
        try {
            File file = new File(filePath);
            ImageIO.write(image, format, file);
            System.out.println("Image saved to: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}