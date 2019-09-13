package net.rptools.maptool.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuadTree<T extends ITwoDimensionalObject> {

  private static final Logger log = LogManager.getLogger(QuadTree.class);


  private static final int MAX_OBJECTS = 10;
  private static final int MAX_LEVELS = 5;

  private final int level;
  private final List<T> objects;
  private final Rectangle bounds;
  private final Object[] nodes;

  /**
   * Initializes a new instance of the {@link QuadTree} class.
   *
   * @param withLevel  The level of the {@code QuadTree} being created.
   * @param withBounds The bounds of the {@code QuadTree} being created.
   */
  public QuadTree(int withLevel, Rectangle withBounds) {
    level = withLevel;
    objects = new ArrayList<>();
    bounds = withBounds;
    nodes = new Object[4];
  }

  /**
   * Clears the {@link QuadTree}.
   */
  public void clear() {
    objects.clear();

    for (int i = 0; i < nodes.length; i++) {
      if (nodes[i] != null) {
        @SuppressWarnings("unchecked") final var qt = (QuadTree<T>) nodes[i];
        qt.clear();
      }
      nodes[i] = null;
    }
  }


  /**
   * Splits the {@link QuadTree} node into quads.
   */
  private void split() {
    var subWidth = (int) (bounds.getWidth() / 2.0);
    var subHeight = (int) (bounds.getHeight() / 2.0);
    var x = (int) bounds.getX();
    var y = (int) bounds.getY();

    // Top right
    nodes[0] = new QuadTree<>(level + 1, new Rectangle(x + subWidth, y, subWidth, subHeight));
    // Top left
    nodes[1] = new QuadTree<>(level + 1, new Rectangle(x, y, subWidth, subHeight));
    // Bottom left
    nodes[2] = new QuadTree<>(level + 1, new Rectangle(x, y + subHeight, subWidth, subHeight));
    // Bottom right
    nodes[3] = new QuadTree<>(level + 1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
  }

  /**
   * Determine which node a bounding box belongs to.  This  is a helper method to for insertion and retrieval.
   *
   * @param x      The horizontal location of the bounding box.
   * @param y      The vertical location of the bounding box.
   * @param width  The width of the bounding box.
   * @param height The height of the bounding box.
   * @return The index of the child node that the bounding box falls within, or -1 if the bounding box cannot fit completely within a child node.
   */
  private int getIndex(int x, int y, int width, int height) {
    int index = -1;
    double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
    double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

    // The bounds can completely fit within the top quadrants
    boolean topQuadrant = (y < horizontalMidpoint && y + height < horizontalMidpoint);
    // The bounds can completely fit within the bottom quadrants
    boolean bottomQuadrant = (y > horizontalMidpoint);

    // The bounds can completely fit within the left quadrants
    if (x < verticalMidpoint && x + width < verticalMidpoint) {
      if (topQuadrant) {
        index = 1;
      } else if (bottomQuadrant) {
        index = 2;
      }
    }
    // The bounds can completely fit within the right quadrants
    else if (x > verticalMidpoint) {
      if (topQuadrant) {
        index = 0;
      } else if (bottomQuadrant) {
        index = 3;
      }
    }

    return index;
  }

  /**
   * Inserts an object into the quadtree.  If the node exceeds capacity, it will split and add all objects to their appropriate quads.
   *
   * @param newObject The object to add.
   */
  public void insert(T newObject) {
    // If the node has been quadded, find a quad that can contain the rectangle.
    if (nodes[0] != null) {
      int index = getIndex(newObject.getX(), newObject.getY(), newObject.getWidth(), newObject.getHeight());

      // If a quad was found, insert the rectangle there and return.
      if (index != -1) {
        @SuppressWarnings("unchecked") final var qt = (QuadTree<T>) nodes[index];
        qt.insert(newObject);
        return;
      }
    }
    objects.add(newObject);

    // Split, if it's appropriate
    if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
      if (nodes[0] == null) {
        split();
      }
      int i = 0;
      while (i < objects.size()) {
        T movingObject = objects.get(i);
        int index = getIndex(movingObject.getX(), movingObject.getY(), movingObject.getWidth(), movingObject.getHeight());
        if (index != -1) {
          @SuppressWarnings("unchecked") final var qt = (QuadTree<T>) nodes[index];
          qt.insert(objects.remove(i));
        } else {
          i++;
        }
      }
    }
  }

  /**
   * Returns a list of objects within the specified bounds.
   *
   * @param x      The horizontal location of the left edge of the search area.
   * @param y      The vertical location of the top edge of the search area.
   * @param width  The width of the search area.
   * @param height The height of the search area.
   * @param addTo  If not null, search results will be added to this {@link List<T>}.  Otherwise, a new list will be created and returned.
   * @return A {@link List<T>} of the objects within the search area.
   */
  public List<T> search(int x, int y, int width, int height, List<T> addTo) {
    log.info(String.format("Searching %d, %d, %d, %d", x, y, width, height));
    List<T> results = (addTo == null) ? new ArrayList<>() : addTo;
    x = Math.max(x, bounds.x);
    y = Math.max(y, bounds.y);
    width = Math.min(width, bounds.width);
    height = Math.min(height, bounds.height);
    int index = getIndex(x, y, width, height);
    if (index != -1 && nodes[0] != null) {
      @SuppressWarnings("unchecked") final var qt = (QuadTree<T>) nodes[index];
      qt.search(x, y, width, height, results);
    } else if(nodes[0] != null) {
      for(int i = 0; i < 4; i++) {
        @SuppressWarnings("unchecked")
        final var qt = (QuadTree<T>)nodes[i];
        qt.search(x, y, width, height, results);
      }
    }
    log.info(String.format("Returned %d objects from level %d of the QuadTree", objects.size(), level));
    results.addAll(objects);
    return results;
  }
}
