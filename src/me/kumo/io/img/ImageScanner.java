package me.kumo.io.img;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * A utility class that scans damaged image files
 * <p>
 * This is based on BadPeggy GUI Tool https://github.com/llaith-oss/BadPeggy
 *
 * @author Clyde Velasquez
 * @version 1.0
 */
public final class ImageScanner {

    private ImageScanner() {
    }

    /**
     * Scans an image and returns the Result object
     *
     * @param imgInputStream InputStream of an image file
     * @param imgFormat      ImageFormat enum type
     * @return Result object
     */
    public static Result scan(InputStream imgInputStream, ImageFormat imgFormat) {

        Result result = new Result();
        ImageReader imgReader = ImageIO.getImageReadersByFormatName(imgFormat.toString()).next();

        try (imgInputStream) {

            // Remove all Listener objects
            imgReader.removeAllIIOReadProgressListeners();
            imgReader.removeAllIIOReadUpdateListeners();
            imgReader.removeAllIIOReadWarningListeners();

            imgReader.addIIOReadWarningListener((final ImageReader source, final String warning) -> {
                result.messagesSb.append(warning).append("\n");
                result.isOk = false;
                result.resultType = Result.Type.WARNING;
            });
            imgReader.setInput(ImageIO.createImageInputStream(imgInputStream));

            int imgCount = imgReader.getNumImages(true);
            for (int i = 0; i < imgCount; i++) {
                BufferedImage read = imgReader.read(i);
                read.flush();
            }
        } catch (NegativeArraySizeException ex) {
            result.messagesSb.append("Internal decoder error 1");
            result.messagesSb.append(ex.getMessage()).append("\n");
            result.isOk = false;
            result.resultType = Result.Type.ERROR;
        } catch (ArrayIndexOutOfBoundsException ex) {
            result.messagesSb.append("Internal decoder error 2");
            result.messagesSb.append(ex.getMessage()).append("\n");
            result.isOk = false;
            result.resultType = Result.Type.ERROR;
        } catch (IOException ex) {
            result.messagesSb.append(ex.getMessage()).append("\n");
            result.isOk = false;
            result.resultType = Result.Type.ERROR;
        } catch (Exception e) {
            result.messagesSb.append(e.getMessage()).append("\n");
            result.isOk = false;
            result.resultType = Result.Type.UNEXPECTED_ERROR;
        } finally {
            imgReader.dispose();
        }

        return result;
    }

    /**
     * Scans an image and returns the Result object
     *
     * @param imgPath Path of an image
     * @return Result object
     * @throws IOException If I/O error occurs when opening a Path then creating
     *                     a new InputStream from it
     */
    public static Result scan(Path imgPath) throws IOException {
        return scan(Files.newInputStream(imgPath),
                Objects.requireNonNull(ImageFormat.fromFileName(imgPath.getFileName().toString())));
    }

    /**
     * Result object that contains some information after scanning an image
     */
    public static class Result {

        public enum Type {
            OK,
            WARNING,
            ERROR,
            UNEXPECTED_ERROR
        }

        private final StringBuilder messagesSb;
        private Boolean isOk;
        private Type resultType;

        public Result() {
            this.messagesSb = new StringBuilder();
            this.isOk = true;
            this.resultType = Type.OK;
        }

        public StringBuilder getMessages() {
            return messagesSb;
        }

        public Boolean isOk() {
            return isOk;
        }

        public Type resultType() {
            return resultType;
        }

        public Boolean isCorrupt() {
            return !isOk;
        }

        @Override
        public String toString() {
            return "Is image OK? " + isOk +
                    ", Result type: " + resultType +
                    "\n" + messagesSb.toString();
        }
    }
}