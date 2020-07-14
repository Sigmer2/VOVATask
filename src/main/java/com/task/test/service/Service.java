package com.task.test.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
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

    Map<String, int[]> getReferences() throws URISyntaxException, IOException;

    int[] getPartPoints(BufferedImage img);

    BufferedImage getAccurateImg(BufferedImage advanceImg);

    List<BufferedImage> getListAdvanceImgs(BufferedImage img);

    String getFolder();
}
