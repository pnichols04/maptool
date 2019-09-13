package net.rptools.maptool.client.ui.zone;

import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.ui.TokenLocation;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.Objects;
import java.util.function.Predicate;

public class TokenPlyRenderer {

  private static final Predicate<Token> nullPredicate = token -> true;

  private BufferedImage bufferImage;
  private Predicate<Token> predicate = nullPredicate;
  private int clientWidth;
  private int clientHeight;
  private double scale;
  private Graphics2D graphics;
  private Graphics2D clippedGraphics;
  private boolean viewingAsGM;
  private Area visibleScreenArea;
  private ZoneRenderer parentRenderer;

  public TokenPlyRenderer() {
  }

  public void setPredicate(Predicate<Token> newPredicate) {
    this.predicate = Objects.requireNonNullElse(newPredicate, nullPredicate);
  }

  public void configureRenderPass(
        GraphicsConfiguration config,
        ZoneRenderer renderer,
        boolean isGMView,
        Area visibleScreenArea) {
    clientWidth = renderer.getWidth();
    clientHeight = renderer.getHeight();
    scale = renderer.getScale();
    viewingAsGM = isGMView;
    parentRenderer = renderer;

    if (Objects.isNull(bufferImage) || bufferImage.getWidth() != clientWidth || bufferImage.getHeight() != clientHeight) {
      if (!Objects.isNull(graphics)) {
        graphics.dispose();
      }
      bufferImage = config.createCompatibleImage(clientWidth, clientHeight);
      graphics = bufferImage.createGraphics();
    } else {
      var savedCompositing = graphics.getComposite();
      graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
      graphics.setColor(Color.black);
      graphics.fillRect(0, 0, clientWidth, clientHeight);
      graphics.setComposite(savedCompositing);
    }
    var clipBounds = Objects.requireNonNullElseGet(graphics.getClip(), () -> new Rectangle(clientWidth, clientHeight));
    if(!viewingAsGM && !Objects.isNull(visibleScreenArea)) {
      var visibleArea = new Area(clipBounds);
      visibleArea.intersect(visibleScreenArea);
      clippedGraphics.setClip(new GeneralPath(visibleArea));
    }

  }

  public void MaybeRenderToken(Token token) {
    if (!predicate.test(token)) {
      return;
    }

    Rectangle tokenFootprint = token.getBounds(parentRenderer.getZone());
    //
    // Get the token image
    //
    var tokenImage = parentRenderer.getTokenImage(token);
    var scaledWidth = (tokenFootprint.width * scale);
    var scaledHeight = (tokenFootprint.height * scale);
    var screenLocation = ScreenPoint.fromZonePoint(parentRenderer, tokenFootprint.x, tokenFootprint.y);
    var screenBounds = new Rectangle2D.Double(screenLocation.x, screenLocation.y, scaledWidth, scaledHeight);
    var screenBoundsArea = new Area(screenBounds);
    if (token.hasFacing() && token.getShape() == Token.TokenShape.TOP_DOWN) {
      var sx = scaledWidth / 2 + screenLocation.x - (token.getAnchorX() * scale);
      var sy = scaledHeight / 2 + screenLocation.y - (token.getAnchorY() * scale);
      screenBoundsArea.transform(AffineTransform.getRotateInstance(Math.toRadians(-token.getFacing() - 90), sx, sy));
    }
    if (scaledHeight < 1 || scaledWidth < 1) {
      return;
    }
    if (viewingAsGM && token.isToken() && parentRenderer.getZoneView().isUsingVision()) {
      if (!visibleScreenArea.intersects(screenBounds)) {
        return;
      }
    }
    // TODO: [PNICHOLS04] Handle markers.
  }

  private BufferedImage done() {
    return this.bufferImage;
  }

  public void Dispose() {
    if (!Objects.isNull(graphics)) {
      graphics.dispose();
    }
    if(!Objects.isNull(clippedGraphics)) {
      graphics.dispose();
    }
  }
}