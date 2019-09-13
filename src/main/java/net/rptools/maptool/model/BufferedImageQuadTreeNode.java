package net.rptools.maptool.model;

import java.awt.image.BufferedImage;

public class BufferedImageQuadTreeNode implements ITwoDimensionalObject {

  private final int x;
  private final int y;

  private final BufferedImage image;

  private BufferedImageQuadTreeNode(BufferedImage ofImage, int atX, int atY) {
    image = ofImage;
    x = atX;
    y = atY;
  }

  public static BufferedImageQuadTreeNode of(BufferedImage image, int atX, int atY) {
    if (image == null) {
      return null;
    }
    return new BufferedImageQuadTreeNode(image, atX, atY);
  }

  public BufferedImage getImage() {
    return image;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getWidth() {
    return image.getWidth();
  }

  public int getHeight() {
    return image.getHeight();
  }
}
