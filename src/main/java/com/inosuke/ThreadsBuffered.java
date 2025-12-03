package com.inosuke;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

public class ThreadsBuffered {
    /*Crear el tamaño del BUFFER y la QUEUE*/
    private static final int sizeBuffer = 1024 * 1024; //1MB
    private static final BlockingQueue<byte[]> queue = new ArrayBlockingQueue<>(10);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting ...");

        final String origin = "C:/Users/BLS Consultores/IdeaProjects/BringGhostThat/src/main/java/com/inosuke/Files/File.bin";
        final String destiny = "C:/Users/BLS Consultores/Downloads/Copia.bin";

        Thread reader = new Thread(() -> readFile(origin));
        Thread writer = new Thread(() -> writeFile(destiny));

        reader.start();
        writer.start();

        reader.join();
        queue.put(new byte[0]);
        writer.join();

        System.out.println("Finished");

    }

    public static void readFile(String route) {
        try (
                FileInputStream fis = new FileInputStream(route);
                BufferedInputStream bis = new BufferedInputStream(fis, sizeBuffer);
        ) {
            byte[] buffer = new byte[sizeBuffer];
            int bytesRead;

            while ((bytesRead = bis.read(buffer)) != -1) {
                byte[] chunk = Arrays.copyOf(buffer, bytesRead);
                queue.put(chunk);
            }

        } catch (Exception err) {
            System.out.println("Error: " + err.getMessage());
        }
    }

    public static void writeFile(String route) {
        try (
                FileOutputStream fos = new FileOutputStream(route);
                BufferedOutputStream bos = new BufferedOutputStream(fos, sizeBuffer);
        ) {
            while (true) {
                byte[] chunk = queue.take();

                if (chunk.length == 0) {
                    break;
                }

                bos.write(chunk);
            }

        } catch (Exception err) {
            System.out.println("Error: " + err.getMessage());
        }
    }


}

