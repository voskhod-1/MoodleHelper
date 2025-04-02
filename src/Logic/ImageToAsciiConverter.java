package Logic;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.image.ConvolveOp;
import java.io.File;
import java.io.IOException;

public class ImageToAsciiConverter {
    private static final char[] BRAILLE_CHARS = new char[256];
    static {
        for (int i = 0; i < 256; i++) {
            BRAILLE_CHARS[i] = (char) (0x2800 + i);
        }
    }

    /**
     * Преобразует изображение в ASCII-арт с использованием символов Брайля.
     */
    public static String imageToBraille(BufferedImage image, int scaleWidth, int scaleHeight, double brightnessAdjust, int sharpnessLevel, boolean useDithering) {
        // Валидация входных параметров
        validateInputParameters(scaleWidth, scaleHeight, brightnessAdjust, sharpnessLevel);

        // Последовательная обработка изображения
        BufferedImage processedImage = processImage(image, scaleWidth, scaleHeight, brightnessAdjust, sharpnessLevel, useDithering);

        // Преобразование в символы Брайля
        return convertToBraille(processedImage);
    }

    /**
     * Проверяет входные параметры на корректность.
     */
    private static void validateInputParameters(int scaleWidth, int scaleHeight, double brightnessAdjust, int sharpnessLevel) {
        if (scaleWidth <= 0 || scaleHeight <= 0) {
            throw new IllegalArgumentException("Scale dimensions must be positive");
        }
        if (sharpnessLevel < 0 || sharpnessLevel > 100) {
            throw new IllegalArgumentException("Sharpness level must be between 0 and 100");
        }
        if (brightnessAdjust < -255 || brightnessAdjust > 255) {
            throw new IllegalArgumentException("Brightness adjust must be between -255 and 255");
        }
    }

    /**
     * Обрабатывает изображение: масштабирование, резкость, яркость и бинаризация.
     */
    private static BufferedImage processImage(BufferedImage image, int scaleWidth, int scaleHeight, double brightnessAdjust, int sharpnessLevel, boolean useDithering) {
        // Применяем фильтр резкости
        BufferedImage sharpenedImage = applySharpnessFilter(image, sharpnessLevel);

        // Масштабируем изображение
        BufferedImage resizedImage = resizeImage(sharpenedImage, scaleWidth * 2, scaleHeight * 4);

        // Корректируем яркость
        BufferedImage adjustedImage = adjustBrightness(resizedImage, brightnessAdjust);
        if (useDithering)
        // Применяем бинаризацию с дизерингом
        return applySoftFloydSteinbergDithering(adjustedImage); // Можно заменить на applySoftFloydSteinbergDithering
        else return applyThresholdWithNoise(adjustedImage);
    }

    /**
     * Преобразует бинаризованное изображение в строку ASCII-арта.
     */
    private static String convertToBraille(BufferedImage binarizedImage) {
        StringBuilder brailleArt = new StringBuilder();

        for (int y = 0; y < binarizedImage.getHeight(); y += 4) {
            for (int x = 0; x < binarizedImage.getWidth(); x += 2) {
                int mask = 0;
                for (int dy = 0; dy < 4 && (y + dy) < binarizedImage.getHeight(); dy++) {
                    for (int dx = 0; dx < 2 && (x + dx) < binarizedImage.getWidth(); dx++) {
                        Color color = new Color(binarizedImage.getRGB(x + dx, y + dy));
                        if (color.equals(Color.BLACK)) {
                            mask |= 1 << (dy * 2 + dx);
                        }
                    }
                }
                brailleArt.append(BRAILLE_CHARS[mask]);
            }
            brailleArt.append("\n");
        }

        return brailleArt.toString();
    }

    /**
     * Корректирует яркость изображения.
     */
    private static BufferedImage adjustBrightness(BufferedImage image, double brightnessAdjust) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));
                int red = Math.max(0, Math.min(255, color.getRed() + (int) brightnessAdjust));
                int green = Math.max(0, Math.min(255, color.getGreen() + (int) brightnessAdjust));
                int blue = Math.max(0, Math.min(255, color.getBlue() + (int) brightnessAdjust));
                result.setRGB(x, y, new Color(red, green, blue).getRGB());
            }
        }
        return result;
    }

    /**
     * Применяет ослабленный алгоритм дизеринга Флойда-Стейнберга.
     */
    private static BufferedImage applySoftFloydSteinbergDithering(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        float[][] brightness = new float[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));
                brightness[x][y] = (float) (color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114);
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float oldPixel = brightness[x][y];
                float newPixel = oldPixel < 128 ? 0 : 255;
                brightness[x][y] = newPixel;
                float error = oldPixel - newPixel;

                if (x + 1 < width) brightness[x + 1][y] += error * 3 / 16;
                if (y + 1 < height) {
                    if (x - 1 >= 0) brightness[x - 1][y + 1] += error * 1 / 16;
                    brightness[x][y + 1] += error * 2 / 16; // Тут можно регулировать коэфф-т дизеринга
                    if (x + 1 < width) brightness[x + 1][y + 1] += error * 1 / 16;
                }

                result.setRGB(x, y, newPixel == 0 ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }
        return result;
    }

    /**
     * Применяет пороговую бинаризацию с легким шумом.
     */
    private static BufferedImage applyThresholdWithNoise(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        float totalBrightness = 0;
        float[][] brightness = new float[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));
                brightness[x][y] = (float) (color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114);
                totalBrightness += brightness[x][y];
            }
        }
        float avgBrightness = totalBrightness / (width * height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float noise = (float) (Math.random() * 20 - 10);
                float adjustedBrightness = brightness[x][y] + noise;
                int threshold = (int) (avgBrightness * 0.9);
                result.setRGB(x, y, adjustedBrightness < threshold ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }
        return result;
    }

    /**
     * Применяет фильтр резкости.
     */
    private static BufferedImage applySharpnessFilter(BufferedImage image, int sharpnessLevel) {
        if (sharpnessLevel == 0) return image;

        float factor = (float) (1.0 + sharpnessLevel / 100.0);
        float[] kernelData = {
                0, -factor / 4, 0,
                -factor / 4, 1 + factor, -factor / 4,
                0, -factor / 4, 0
        };

        java.awt.image.Kernel kernel = new java.awt.image.Kernel(3, 3, kernelData);
        ConvolveOp convolveOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return convolveOp.filter(image, null);
    }

    /**
     * Масштабирует изображение.
     */
    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        outputImage.createGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

    public static void main(String[] args) {
        try {
            BufferedImage image = ImageIO.read(new File("input.png"));
            double brightnessAdjust = -50;
            int sharpnessLevel = 30;
            String brailleArt = imageToBraille(image, 80, 40, brightnessAdjust, sharpnessLevel, true);
            System.out.println(brailleArt);
        } catch (IOException e) {
            System.err.println("Ошибка чтения изображения: " + e.getMessage());
        }
    }
}