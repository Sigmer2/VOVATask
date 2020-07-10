package com.task.test;

import com.task.test.service.Service;
import com.task.test.service.implementation.ServiceImpl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        Service service = new ServiceImpl();

        String folder = service.getFolder();
        try (Stream<Path> paths = Files.walk(Paths.get(folder))) {
            paths.filter(path -> path.toFile().isFile()).forEach(path -> {
                List<BufferedImage> advanceImgs = service.getListAdvanceImgs(path);
                Map<String, int[]> references = service.getReferences();
                StringBuilder sb = new StringBuilder();
                sb.append(path.getFileName());
                sb.append(": ");
                for (BufferedImage advanceImg : advanceImgs) {
                    BufferedImage valueImg = service.getAccurateImg(advanceImg);
                    int[] partValuePoints = service.getPartPoints(valueImg);
                    String symbol = service.determinateSymbol(references, partValuePoints);
                    sb.append(symbol);
                }
                System.out.println(sb.toString());
            });
        }
    }
}
