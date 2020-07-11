package com.task.test.service;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface Service {
    int countPoints(BufferedImage img);

    int determinateXPointForStartImg(BufferedImage img);

    int determinateImgWidth(BufferedImage img);

    int determinateYPointForStartImg(BufferedImage img);

    int determinateImgHeight(BufferedImage img);

    String determinateSymbol(Map<String, int[]> referenceValues, int[] verifiablePoints, int accuracy);

    Map<String, int[]> getReferences();

    int[] getPartPoints(BufferedImage img);

    BufferedImage getAccurateImg(BufferedImage advanceImg);

    List<BufferedImage> getListAdvanceImgs(Path path);

    String getFolder();
}
