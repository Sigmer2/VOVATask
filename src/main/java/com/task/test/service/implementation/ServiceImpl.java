package com.task.test.service.implementation;

import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ServiceImpl implements com.task.test.service.Service {
    private static final int WHITE_RGB = -1;
    private static final int GRAY_RGB = -8882056;
    // The lower the value, the higher the accuracy
    public static final int ACCURACY = 1;
    private static final String PATH_TO_REFERENCE_IMAGES = "referenceImages/";

    @Override
    public int countPoints(BufferedImage img) {
        int count = 0;
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int verifiableRgb = img.getRGB(i, j);
                if (verifiableRgb == WHITE_RGB || verifiableRgb == GRAY_RGB) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public int determinateXPointForStartImg(BufferedImage img) {
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int verifiableRgb = img.getRGB(x, y);
                if (verifiableRgb != WHITE_RGB && verifiableRgb != GRAY_RGB) {
                    return x;
                }
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public int determinateImgWidth(BufferedImage img) {
        int xPointEndValue = 0;
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int verifiableRgb = img.getRGB(x, y);
                if (verifiableRgb != WHITE_RGB && verifiableRgb != GRAY_RGB) {
                    xPointEndValue = x;
                }
            }
        }
        return ++xPointEndValue - determinateXPointForStartImg(img);
    }

    @Override
    public int determinateYPointForStartImg(BufferedImage img) {
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int verifiableRgb = img.getRGB(x, y);
                if (verifiableRgb != WHITE_RGB && verifiableRgb != GRAY_RGB) {
                    return y;
                }
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public int determinateImgHeight(BufferedImage img) {
        int yPointEndValue = 0;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int verifiableRgb = img.getRGB(x, y);
                if (verifiableRgb != WHITE_RGB && verifiableRgb != GRAY_RGB) {
                    yPointEndValue = y;
                }
            }
        }
        return ++yPointEndValue - determinateYPointForStartImg(img);
    }

    @Override
    public String determinateSymbol(Map<String, int[]> referencePoints, int[] verifiablePoints, int accuracy) {
        for (Map.Entry<String, int[]> entry : referencePoints.entrySet()) {
            boolean flag = true;
            for (int i = 0; i < entry.getValue().length; i++) {
                if (Math.abs(entry.getValue()[i] - verifiablePoints[i]) > accuracy) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                return entry.getKey();
            }
        }
        return determinateSymbol(referencePoints, verifiablePoints, ++accuracy);
    }

    public Map<String, int[]> getReferences() throws URISyntaxException, IOException {
        final String VALUES = "(?<=\\p{Lower})(?=\\d)|(?<=\\d)(?=\\p{Lower})" +
                "|(?<=\\p{Upper})(?=\\p{Lower})|(?<=\\p{Lower})(?=\\p{Upper})";
        Map<String, int[]> references = new HashMap<>();
        Map<String, BufferedImage> referenceImgs = getReferenceImgs();
        referenceImgs.forEach((name, img) -> {
            String[] values = name.split(VALUES);
            List<BufferedImage> listAdvanceImgs = getListAdvanceImgs(img);
            for (int i = 0; i < values.length; i++) {
                BufferedImage accurateImg = getAccurateImg(listAdvanceImgs.get(i));
                int[] partPoints = getPartPoints(accurateImg);
                references.put(values[i], partPoints);
            }
        });
        return references;
    }

    @Override
    public int[] getPartPoints(BufferedImage img) {
        int[] partPoints = new int[4];
        int yPointForPart = 0;
        for (int j = 0; j < partPoints.length; j++) {
            int xPointForPart = 0;
            int widthForPart = img.getWidth() / 2;
            int heightForPart = img.getHeight() / 2;
            if (j == 1) {
                xPointForPart = img.getWidth() / 2;
                if (img.getWidth() % 2 > 0) {
                    widthForPart++;
                }
            }
            if (j == 2) {
                yPointForPart = img.getHeight() / 2;
                if (img.getHeight() % 2 > 0) {
                    heightForPart++;
                }
            }
            if (j == 3) {
                xPointForPart = img.getWidth() / 2;
                if (img.getHeight() % 2 > 0) {
                    heightForPart++;
                }
                if (img.getWidth() % 2 > 0) {
                    widthForPart++;
                }
            }
            BufferedImage partValueImg = img.getSubimage(
                    xPointForPart,
                    yPointForPart,
                    widthForPart,
                    heightForPart);

            int countPoints = countPoints(partValueImg);
            partPoints[j] = countPoints;
        }
        return partPoints;
    }

    @Override
    public BufferedImage getAccurateImg(BufferedImage advanceImg) {
        int xPointForValue = determinateXPointForStartImg(advanceImg);
        int yPointForValue = determinateYPointForStartImg(advanceImg);
        int widthForValue = determinateImgWidth(advanceImg);
        int heightForValue = determinateImgHeight(advanceImg);
        return advanceImg.getSubimage(
                xPointForValue,
                yPointForValue,
                widthForValue,
                heightForValue);
    }

    public List<BufferedImage> getListAdvanceImgs(BufferedImage img) {
        int[] xPointsAdvance = {146, 218, 289, 361, 432};
        int yPointForValueAdvance = 590;
        int widthForValueAdvance = 35;
        int heightForValueAdvance = 28;
        int yPointForSuitAdvance = 616;
        int widthForSuitAdvance = 25;
        int heightForSuitAdvance = 20;
        List<BufferedImage> advanceImgs = new LinkedList<>();
        for (int xPointAdvance : xPointsAdvance) {
            BufferedImage valueImgAdvance = img.getSubimage(
                    xPointAdvance,
                    yPointForValueAdvance,
                    widthForValueAdvance,
                    heightForValueAdvance);
            int verifiableRgb = valueImgAdvance.getRGB(widthForValueAdvance - 1, heightForValueAdvance - 1);
            if (verifiableRgb != WHITE_RGB && verifiableRgb != GRAY_RGB) {
                return advanceImgs;
            }
            BufferedImage suitImgAdvance = img.getSubimage(
                    xPointAdvance,
                    yPointForSuitAdvance,
                    widthForSuitAdvance,
                    heightForSuitAdvance);
            advanceImgs.add(valueImgAdvance);
            advanceImgs.add(suitImgAdvance);
        }
        return advanceImgs;
    }

    public String getFolder() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jFileChooser.showSaveDialog(null);
        return jFileChooser.getSelectedFile().toString();
    }

    private Map<String, BufferedImage> getReferenceImgs() throws IOException, URISyntaxException {
        File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        Map<String, BufferedImage> referenceImgs = new HashMap<>();
        if (jarFile.isFile()) { // Run with Jar
            try (JarFile jar = new JarFile(jarFile)) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    String filePath = entries.nextElement().getName();
                    if (filePath.startsWith(PATH_TO_REFERENCE_IMAGES)
                            && filePath.length() > PATH_TO_REFERENCE_IMAGES.length()) {
                        URL resource = this.getClass().getResource("/" + filePath);
                        BufferedImage img = ImageIO.read(resource);
                        String fileName = StringUtils.substringBetween(filePath, "/", ".");
                        referenceImgs.put(fileName, img);
                    }
                }
                return referenceImgs;
            }
        } else { // Run with IDE
            URL url = getClass().getResource("/" + PATH_TO_REFERENCE_IMAGES);
            if (url != null) {
                File resourceFolder = new File(url.toURI());
                for (File file : Objects.requireNonNull(resourceFolder.listFiles())) {
                    BufferedImage img = ImageIO.read(file);
                    String fileName = StringUtils.substringBefore(file.getName(), ".");
                    referenceImgs.put(fileName, img);
                }
            }
        }
        return referenceImgs;
    }
}
