// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth,
// inexhaustible as the great rivers.
// When they come to an end,
// they begin again,
// like the days and months;
// they die and are reborn,
// like the four seasons."
//
// - Sun Tsu,
// "The Art of War"

package com.theartofdev.edmodo.cropper;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

/**
 * Handler to update crop window edges by the move type - Horizontal, Vertical, Corner or Center.
 * <br>
 */
final class CropWindowMoveHandler {

  // region: Fields and Consts

  /** Matrix used for rectangle rotation handling */
  private static final Matrix MATRIX = new Matrix();

  /** Minimum width in pixels that the crop window can get. */
  private final float mMinCropWidth;

  /** Minimum width in pixels that the crop window can get. */
  private final float mMinCropHeight;

  /** Maximum height in pixels that the crop window can get. */
  private final float mMaxCropWidth;

  /** Maximum height in pixels that the crop window can get. */
  private final float mMaxCropHeight;

  /** The type of crop window move that is handled. */
  private final Type mType;

  /**
   * Holds the x and y offset between the exact touch location and the exact handle location that is
   * activated. There may be an offset because we allow for some leeway (specified by mHandleRadius)
   * in activating a handle. However, we want to maintain these offset values while the handle is
   * being dragged so that the handle doesn't jump.
   */
  private final PointF mTouchOffset = new PointF();
  // endregion

  /**
   * @param edgeMoveType the type of move this handler is executing
   * @param horizontalEdge the primary edge associated with this handle; may be null
   * @param verticalEdge the secondary edge associated with this handle; may be null
   * @param cropWindowHandler main crop window handle to get and update the crop window edges
   * @param touchX the location of the initial toch possition to measure move distance
   * @param touchY the location of the initial toch possition to measure move distance
   */
  public CropWindowMoveHandler(
      Type type, CropWindowHandler cropWindowHandler, float touchX, float touchY) {
    mType = type;
    mMinCropWidth = cropWindowHandler.getMinCropWidth();
    mMinCropHeight = cropWindowHandler.getMinCropHeight();
    mMaxCropWidth = cropWindowHandler.getMaxCropWidth();
    mMaxCropHeight = cropWindowHandler.getMaxCropHeight();
    //calculateTouchOffset(cropWindowHandler.getRect(), touchX, touchY);
    calculateTouchOffset(cropWindowHandler.getPolygon(), touchX, touchY);
  }

  /**
   * Updates the crop window by change in the toch location.<br>
   * Move type handled by this instance, as initialized in creation, affects how the change in toch
   * location changes the crop window position and size.<br>
   * After the crop window position/size is changed by toch move it may result in values that
   * vialate contraints: outside the bounds of the shown bitmap, smaller/larger than min/max size or
   * missmatch in aspect ratio. So a series of fixes is executed on "secondary" edges to adjust it
   * by the "primary" edge movement.<br>
   * Primary is the edge directly affected by move type, secondary is the other edge.<br>
   * The crop window is changed by directly setting the Edge coordinates.
   *
   * @param x the new x-coordinate of this handle
   * @param y the new y-coordinate of this handle
   * @param bounds the bounding rectangle of the image
   * @param viewWidth The bounding image view width used to know the crop overlay is at view edges.
   * @param viewHeight The bounding image view height used to know the crop overlay is at view
   *     edges.
   * @param parentView the parent View containing the image
   * @param snapMargin the maximum distance (in pixels) at which the crop window should snap to the
   *     image
   * @param fixedAspectRatio is the aspect ration fixed and 'targetAspectRatio' should be used
   * @param aspectRatio the aspect ratio to maintain
   */
  public void move(RectF rect, float x, float y, RectF bounds, int viewWidth, int viewHeight, float snapMargin, boolean fixedAspectRatio, float aspectRatio)
  {

    // Adjust the coordinates for the finger position's offset (i.e. the
    // distance from the initial touch to the precise handle location).
    // We want to maintain the initial touch's distance to the pressed
    // handle so that the crop window size does not "jump".
    float adjX = x + mTouchOffset.x;
    float adjY = y + mTouchOffset.y;

    if (mType == Type.CENTER) {
      moveCenter(rect, adjX, adjY, bounds, viewWidth, viewHeight, snapMargin);
    } else {
      if (fixedAspectRatio) {
        moveSizeWithFixedAspectRatio(
            rect, adjX, adjY, bounds, viewWidth, viewHeight, snapMargin, aspectRatio);
      } else {
        moveSizeWithFreeAspectRatio(rect, adjX, adjY, bounds, viewWidth, viewHeight, snapMargin);
      }
    }
  }

  public void move(Polygon polygon, float x, float y, Polygon boundingPolygon, int viewWidth, int viewHeight, float snapMargin)
  {

    // Adjust the coordinates for the finger position's offset (i.e. the
    // distance from the initial touch to the precise handle location).
    // We want to maintain the initial touch's distance to the pressed
    // handle so that the crop window size does not "jump".
    float adjX = x + mTouchOffset.x;
    float adjY = y + mTouchOffset.y;
    Log.e(TAG, "move: call for type : "+mType);

    if (mType == Type.CENTER) {
      // TODO need to handle this
      moveCenter(polygon, adjX, adjY, boundingPolygon, viewWidth, viewHeight, snapMargin);
    } else {
      movePolygon(polygon, adjX, adjY, boundingPolygon, viewWidth, viewHeight, snapMargin);
    }
  }

  // region: Private methods

  /**
   * Calculates the offset of the touch point from the precise location of the specified handle.<br>
   * Save these values in a member variable since we want to maintain this offset as we drag the
   * handle.
   */
  private void calculateTouchOffset(RectF rect, float touchX, float touchY) {

    float touchOffsetX = 0;
    float touchOffsetY = 0;

    // Calculate the offset from the appropriate handle.
    switch (mType) {
      case TOP_LEFT:
        touchOffsetX = rect.left - touchX;
        touchOffsetY = rect.top - touchY;
        break;
      case TOP_RIGHT:
        touchOffsetX = rect.right - touchX;
        touchOffsetY = rect.top - touchY;
        break;
      case BOTTOM_LEFT:
        touchOffsetX = rect.left - touchX;
        touchOffsetY = rect.bottom - touchY;
        break;
      case BOTTOM_RIGHT:
        touchOffsetX = rect.right - touchX;
        touchOffsetY = rect.bottom - touchY;
        break;
      case LEFT:
        touchOffsetX = rect.left - touchX;
        touchOffsetY = 0;
        break;
      case TOP:
        touchOffsetX = 0;
        touchOffsetY = rect.top - touchY;
        break;
      case RIGHT:
        touchOffsetX = rect.right - touchX;
        touchOffsetY = 0;
        break;
      case BOTTOM:
        touchOffsetX = 0;
        touchOffsetY = rect.bottom - touchY;
        break;
      case CENTER:
        touchOffsetX = rect.centerX() - touchX;
        touchOffsetY = rect.centerY() - touchY;
        break;
      default:
        break;
    }

    mTouchOffset.x = touchOffsetX;
    mTouchOffset.y = touchOffsetY;
  }


  private void calculateTouchOffset(Polygon polygon, float touchX, float touchY) {

    float touchOffsetX = 0;
    float touchOffsetY = 0;

    // Calculate the offset from the appropriate handle.
    switch (mType) {
      case TOP_LEFT:
        touchOffsetX = polygon.topLeftX - touchX;
        touchOffsetY = polygon.topLeftY - touchY;
        break;
      case TOP_RIGHT:
        touchOffsetX = polygon.topRightX - touchX;
        touchOffsetY = polygon.topRightY - touchY;
        break;
      case BOTTOM_LEFT:
        touchOffsetX = polygon.bottomLeftX - touchX;
        touchOffsetY = polygon.bottomLeftY - touchY;
        break;
      case BOTTOM_RIGHT:
        touchOffsetX = polygon.bottomRightX - touchX;
        touchOffsetY = polygon.bottomRightY - touchY;
        break;
      case LEFT:
        touchOffsetX = polygon.topLeftX - touchX;
        touchOffsetY = 0;
        break;
      case TOP:
        touchOffsetX = 0;
        touchOffsetY = polygon.topLeftY - touchY;
        break;
      case RIGHT:
        touchOffsetX = polygon.topRightX - touchX;
        touchOffsetY = 0;
        break;
      case BOTTOM:
        touchOffsetX = 0;
        touchOffsetY = polygon.bottomRightY - touchY;
        break;
      case CENTER:
        // TODO need to handle this
        Log.e(TAG, "calculateTouchOffset: caseCenter");
        float centerX = (polygon.getTopLeftX() + polygon.getBottomRightX()) / 2;
        float centerY = (polygon.getTopLeftY() + polygon.getBottomRightY()) / 2;
        touchOffsetX = centerX - touchX;
        touchOffsetY = centerY - touchY;
        break;
      default:
        break;
    }

    mTouchOffset.x = touchOffsetX;
    mTouchOffset.y = touchOffsetY;
  }

  /** Center move only changes the position of the crop window without changing the size. */
  private void moveCenter(RectF rect, float x, float y, RectF bounds, int viewWidth, int viewHeight, float snapRadius) {
    float dx = x - rect.centerX();
    float dy = y - rect.centerY();
    if (rect.left + dx < 0
        || rect.right + dx > viewWidth
        || rect.left + dx < bounds.left
        || rect.right + dx > bounds.right) {
      dx /= 1.05f;
      mTouchOffset.x -= dx / 2;
    }
    if (rect.top + dy < 0
        || rect.bottom + dy > viewHeight
        || rect.top + dy < bounds.top
        || rect.bottom + dy > bounds.bottom) {
      dy /= 1.05f;
      mTouchOffset.y -= dy / 2;
    }
    rect.offset(dx, dy);
    snapEdgesToBounds(rect, bounds, snapRadius);
  }

  private void moveCenter(Polygon polygon, float x, float y, Polygon bounds, int viewWidth, int viewHeight, float snapRadius)
  {
    float centerX = (polygon.getTopLeftX() + polygon.getBottomRightX()) / 2;
    float centerY = (polygon.getTopLeftY() + polygon.getBottomRightY()) / 2;
    float dx = x - centerX;
    float dy = y - centerY;
    if (polygon.topLeftX + dx < 0
        || polygon.topRightX + dx > viewWidth
        || polygon.topLeftX + dx < bounds.topLeftX
        || polygon.topRightX + dx > bounds.topRightX) {
      dx /= 1.05f;
      mTouchOffset.x -= dx / 2;
    }
    if (polygon.topLeftY + dy < 0
        || polygon.bottomLeftY + dy > viewHeight
        || polygon.topLeftY + dy < bounds.topLeftY
        || polygon.bottomLeftY + dy > bounds.bottomLeftY) {
      dy /= 1.05f;
      mTouchOffset.y -= dy / 2;
    }
    polygon.topLeftX += dx;
    polygon.topRightX += dx;
    polygon.bottomLeftX += dx;
    polygon.bottomRightX += dx;

    polygon.topLeftY += dy;
    polygon.topRightY += dy;
    polygon.bottomLeftY += dy;
    polygon.bottomRightY += dy;

    //snapEdgesToBounds(rect, bounds, snapRadius);
  }

  /**
   * Change the size of the crop window on the required edge (or edges for corner size move) without
   * affecting "secondary" edges.<br>
   * Only the primary edge(s) are fixed to stay within limits.
   */
  private void moveSizeWithFreeAspectRatio(
      RectF rect, float x, float y, RectF bounds, int viewWidth, int viewHeight, float snapMargin) {
    switch (mType) {
      case TOP_LEFT:
        adjustTop(rect, y, bounds, snapMargin, 0, false, false);
        adjustLeft(rect, x, bounds, snapMargin, 0, false, false);
        break;
      case TOP_RIGHT:
        adjustTop(rect, y, bounds, snapMargin, 0, false, false);
        adjustRight(rect, x, bounds, viewWidth, snapMargin, 0, false, false);
        break;
      case BOTTOM_LEFT:
        adjustBottom(rect, y, bounds, viewHeight, snapMargin, 0, false, false);
        adjustLeft(rect, x, bounds, snapMargin, 0, false, false);
        break;
      case BOTTOM_RIGHT:
        adjustBottom(rect, y, bounds, viewHeight, snapMargin, 0, false, false);
        //adjustRight(rect, x, bounds, viewWidth, snapMargin, 0, false, false);
        break;
      case LEFT:
        adjustLeft(rect, x, bounds, snapMargin, 0, false, false);
        break;
      case TOP:
        adjustTop(rect, y, bounds, snapMargin, 0, false, false);
        break;
      case RIGHT:
        adjustRight(rect, x, bounds, viewWidth, snapMargin, 0, false, false);
        break;
      case BOTTOM:
        adjustBottom(rect, y, bounds, viewHeight, snapMargin, 0, false, false);
        break;
      default:
        break;
    }
  }


  private void movePolygon(Polygon polygon, float x, float y, Polygon boundingPolygon, int viewWidth, int viewHeight, float snapMargin)
  {
    Log.e(TAG, "movePolygon: Called for type : "+mType);
    switch (mType) {
      case TOP_LEFT:
        Log.e(TAG, "movePolygon: adjustTopLeft");
        adjustTopLeft(polygon,y,x,boundingPolygon,snapMargin);
        //adjustTop(rect, y, bounds, snapMargin, 0, false, false);
        //adjustLeft(rect, x, bounds, snapMargin, 0, false, false);
        break;
      case TOP_RIGHT:
        adjustTopRight(polygon,y,x,viewWidth,boundingPolygon,snapMargin);
        //adjustTop(rect, y, bounds, snapMargin, 0, false, false);
        //adjustRight(rect, x, bounds, viewWidth, snapMargin, 0, false, false);
        break;
      case BOTTOM_LEFT:
        adjustBottomLeft(polygon,y,x,boundingPolygon,viewHeight,snapMargin);
        //adjustBottom(rect, y, bounds, viewHeight, snapMargin, 0, false, false);
        //adjustLeft(rect, x, bounds, snapMargin, 0, false, false);
        break;
      case BOTTOM_RIGHT:
        adjustBottomRight(polygon,y,x,boundingPolygon,viewHeight,viewWidth,snapMargin);
        //adjustBottom(rect, y, bounds, viewHeight, snapMargin, 0, false, false);
        //adjustRight(rect, x, bounds, viewWidth, snapMargin, 0, false, false);
        break;
      case LEFT:
        adjustLeft(polygon, x, boundingPolygon, snapMargin);
        break;
      case TOP:
        adjustTop(polygon, y, boundingPolygon, snapMargin);
        break;
      case RIGHT:
        adjustRight(polygon,x,boundingPolygon,viewWidth,snapMargin);
        break;
      case BOTTOM:
        adjustBottom(polygon, y, boundingPolygon, viewHeight, snapMargin);
        break;
      default:
        break;
    }
  }

  /**
   * Change the size of the crop window on the required "primary" edge WITH affect to relevant
   * "secondary" edge via aspect ratio.<br>
   * Example: change in the left edge (primary) will affect top and bottom edges (secondary) to
   * preserve the given aspect ratio.
   */
  private void moveSizeWithFixedAspectRatio(
      RectF rect,
      float x,
      float y,
      RectF bounds,
      int viewWidth,
      int viewHeight,
      float snapMargin,
      float aspectRatio) {
    switch (mType) {
      case TOP_LEFT:
        if (calculateAspectRatio(x, y, rect.right, rect.bottom) < aspectRatio) {
          adjustTop(rect, y, bounds, snapMargin, aspectRatio, true, false);
          adjustLeftByAspectRatio(rect, aspectRatio);
        } else {
          adjustLeft(rect, x, bounds, snapMargin, aspectRatio, true, false);
          adjustTopByAspectRatio(rect, aspectRatio);
        }
        break;
      case TOP_RIGHT:
        if (calculateAspectRatio(rect.left, y, x, rect.bottom) < aspectRatio) {
          adjustTop(rect, y, bounds, snapMargin, aspectRatio, false, true);
          adjustRightByAspectRatio(rect, aspectRatio);
        } else {
          adjustRight(rect, x, bounds, viewWidth, snapMargin, aspectRatio, true, false);
          adjustTopByAspectRatio(rect, aspectRatio);
        }
        break;
      case BOTTOM_LEFT:
        if (calculateAspectRatio(x, rect.top, rect.right, y) < aspectRatio) {
          adjustBottom(rect, y, bounds, viewHeight, snapMargin, aspectRatio, true, false);
          adjustLeftByAspectRatio(rect, aspectRatio);
        } else {
          adjustLeft(rect, x, bounds, snapMargin, aspectRatio, false, true);
          adjustBottomByAspectRatio(rect, aspectRatio);
        }
        break;
      case BOTTOM_RIGHT:
        if (calculateAspectRatio(rect.left, rect.top, x, y) < aspectRatio) {
          adjustBottom(rect, y, bounds, viewHeight, snapMargin, aspectRatio, false, true);
          adjustRightByAspectRatio(rect, aspectRatio);
        } else {
          adjustRight(rect, x, bounds, viewWidth, snapMargin, aspectRatio, false, true);
          adjustBottomByAspectRatio(rect, aspectRatio);
        }
        break;
      case LEFT:
        adjustLeft(rect, x, bounds, snapMargin, aspectRatio, true, true);
        adjustTopBottomByAspectRatio(rect, bounds, aspectRatio);
        break;
      case TOP:
        adjustTop(rect, y, bounds, snapMargin, aspectRatio, true, true);
        adjustLeftRightByAspectRatio(rect, bounds, aspectRatio);
        break;
      case RIGHT:
        adjustRight(rect, x, bounds, viewWidth, snapMargin, aspectRatio, true, true);
        adjustTopBottomByAspectRatio(rect, bounds, aspectRatio);
        break;
      case BOTTOM:
        adjustBottom(rect, y, bounds, viewHeight, snapMargin, aspectRatio, true, true);
        adjustLeftRightByAspectRatio(rect, bounds, aspectRatio);
        break;
      default:
        break;
    }
  }

  /** Check if edges have gone out of bounds (including snap margin), and fix if needed. */
  private void snapEdgesToBounds(RectF edges, RectF bounds, float margin) {
    if (edges.left < bounds.left + margin) {
      edges.offset(bounds.left - edges.left, 0);
    }
    if (edges.top < bounds.top + margin) {
      edges.offset(0, bounds.top - edges.top);
    }
    if (edges.right > bounds.right - margin) {
      edges.offset(bounds.right - edges.right, 0);
    }
    if (edges.bottom > bounds.bottom - margin) {
      edges.offset(0, bounds.bottom - edges.bottom);
    }
  }

  /**
   * Get the resulting x-position of the left edge of the crop window given the handle's position
   * and the image's bounding box and snap radius.
   *
   * @param left the position that the left edge is dragged to
   * @param bounds the bounding box of the image that is being cropped
   * @param snapMargin the snap distance to the image edge (in pixels)
   */
  private void adjustLeft(RectF rect, float left, RectF bounds, float snapMargin, float aspectRatio, boolean topMoves, boolean bottomMoves) {

    float newLeft = left;

    if (newLeft < 0) {
      newLeft /= 1.05f;
      mTouchOffset.x -= newLeft / 1.1f;
    }

    if (newLeft < bounds.left) {
      mTouchOffset.x -= (newLeft - bounds.left) / 2f;
    }

    if (newLeft - bounds.left < snapMargin) {
      newLeft = bounds.left;
    }

    // Checks if the window is too small horizontally
    if (rect.right - newLeft < mMinCropWidth) {
      newLeft = rect.right - mMinCropWidth;
    }

    // Checks if the window is too large horizontally
    if (rect.right - newLeft > mMaxCropWidth) {
      newLeft = rect.right - mMaxCropWidth;
    }

    if (newLeft - bounds.left < snapMargin) {
      newLeft = bounds.left;
    }

    // check vertical bounds if aspect ratio is in play
    if (aspectRatio > 0) {
      float newHeight = (rect.right - newLeft) / aspectRatio;

      // Checks if the window is too small vertically
      if (newHeight < mMinCropHeight) {
        newLeft = Math.max(bounds.left, rect.right - mMinCropHeight * aspectRatio);
        newHeight = (rect.right - newLeft) / aspectRatio;
      }

      // Checks if the window is too large vertically
      if (newHeight > mMaxCropHeight) {
        newLeft = Math.max(bounds.left, rect.right - mMaxCropHeight * aspectRatio);
        newHeight = (rect.right - newLeft) / aspectRatio;
      }

      // if top AND bottom edge moves by aspect ratio check that it is within full height bounds
      if (topMoves && bottomMoves) {
        newLeft =
            Math.max(newLeft, Math.max(bounds.left, rect.right - bounds.height() * aspectRatio));
      } else {
        // if top edge moves by aspect ratio check that it is within bounds
        if (topMoves && rect.bottom - newHeight < bounds.top) {
          newLeft = Math.max(bounds.left, rect.right - (rect.bottom - bounds.top) * aspectRatio);
          newHeight = (rect.right - newLeft) / aspectRatio;
        }

        // if bottom edge moves by aspect ratio check that it is within bounds
        if (bottomMoves && rect.top + newHeight > bounds.bottom) {
          newLeft =
              Math.max(
                  newLeft,
                  Math.max(bounds.left, rect.right - (bounds.bottom - rect.top) * aspectRatio));
        }
      }
    }

    rect.left = newLeft;
  }

  private void adjustLeft(Polygon polygon, float left, Polygon boundingPolygon, float snapMargin)
  {

    float newLeft = left;

    if (newLeft < 0) {
      newLeft /= 1.05f;
      mTouchOffset.x -= newLeft / 1.1f;
    }

    if (newLeft < boundingPolygon.topLeftX) {
      mTouchOffset.x -= (boundingPolygon.topLeftX) / 2f;
    }

    if (newLeft - boundingPolygon.topLeftX < snapMargin) {
      newLeft = boundingPolygon.topLeftX;
    }

    // Checks if the window is too small horizontally
    if (polygon.topRightX - newLeft < mMinCropWidth) {
      newLeft = polygon.topRightX - mMinCropWidth;
    }

    // Checks if the window is too large horizontally
    if (polygon.topRightX - newLeft > mMaxCropWidth) {
      newLeft = polygon.topRightX - mMaxCropWidth;
    }

    if (newLeft - boundingPolygon.topLeftX < snapMargin) {
      newLeft = boundingPolygon.topLeftX;
    }


    polygon.topLeftX = newLeft;
    polygon.bottomLeftX = newLeft;
  }

  /**
   * Get the resulting x-position of the right edge of the crop window given the handle's position
   * and the image's bounding box and snap radius.
   *
   * @param right the position that the right edge is dragged to
   * @param bounds the bounding box of the image that is being cropped
   * @param viewWidth
   * @param snapMargin the snap distance to the image edge (in pixels)
   */
  private void adjustRight(RectF rect, float right, RectF bounds, int viewWidth, float snapMargin, float aspectRatio, boolean topMoves, boolean bottomMoves) {

    float newRight = right;

    if (newRight > viewWidth) {
      newRight = viewWidth + (newRight - viewWidth) / 1.05f;
      mTouchOffset.x -= (newRight - viewWidth) / 1.1f;
    }

    if (newRight > bounds.right) {
      mTouchOffset.x -= (newRight - bounds.right) / 2f;
    }

    // If close to the edge
    if (bounds.right - newRight < snapMargin) {
      newRight = bounds.right;
    }

    // Checks if the window is too small horizontally
    if (newRight - rect.left < mMinCropWidth) {
      newRight = rect.left + mMinCropWidth;
    }

    // Checks if the window is too large horizontally
    if (newRight - rect.left > mMaxCropWidth) {
      newRight = rect.left + mMaxCropWidth;
    }

    // If close to the edge
    if (bounds.right - newRight < snapMargin) {
      newRight = bounds.right;
    }

    // check vertical bounds if aspect ratio is in play
    if (aspectRatio > 0) {
      float newHeight = (newRight - rect.left) / aspectRatio;

      // Checks if the window is too small vertically
      if (newHeight < mMinCropHeight) {
        newRight = Math.min(bounds.right, rect.left + mMinCropHeight * aspectRatio);
        newHeight = (newRight - rect.left) / aspectRatio;
      }

      // Checks if the window is too large vertically
      if (newHeight > mMaxCropHeight) {
        newRight = Math.min(bounds.right, rect.left + mMaxCropHeight * aspectRatio);
        newHeight = (newRight - rect.left) / aspectRatio;
      }

      // if top AND bottom edge moves by aspect ratio check that it is within full height bounds
      if (topMoves && bottomMoves) {
        newRight =
            Math.min(newRight, Math.min(bounds.right, rect.left + bounds.height() * aspectRatio));
      } else {
        // if top edge moves by aspect ratio check that it is within bounds
        if (topMoves && rect.bottom - newHeight < bounds.top) {
          newRight = Math.min(bounds.right, rect.left + (rect.bottom - bounds.top) * aspectRatio);
          newHeight = (newRight - rect.left) / aspectRatio;
        }

        // if bottom edge moves by aspect ratio check that it is within bounds
        if (bottomMoves && rect.top + newHeight > bounds.bottom) {
          newRight =
              Math.min(
                  newRight,
                  Math.min(bounds.right, rect.left + (bounds.bottom - rect.top) * aspectRatio));
        }
      }
    }

    rect.right = newRight;
  }

  private void adjustRight(Polygon polygon, float right, Polygon boundingPolygon, int viewWidth, float snapMargin) {

    float newRight = right;

    if (newRight > viewWidth) {
      newRight = viewWidth + (newRight - viewWidth) / 1.05f;
      mTouchOffset.x -= (newRight - viewWidth) / 1.1f;
    }

    if (newRight > boundingPolygon.topRightX) {
      mTouchOffset.x -= (newRight - boundingPolygon.topRightX) / 2f;
    }

    // If close to the edge
    if (boundingPolygon.topRightX - newRight < snapMargin) {
      newRight = boundingPolygon.topRightX;
    }

    // Checks if the window is too small horizontally
    if (newRight - polygon.topLeftX < mMinCropWidth) {
      newRight = polygon.topLeftX + mMinCropWidth;
    }

    // Checks if the window is too large horizontally
    if (newRight - polygon.topLeftX > mMaxCropWidth) {
      newRight = polygon.topLeftX + mMaxCropWidth;
    }

    // If close to the edge
    if (boundingPolygon.topRightX - newRight < snapMargin) {
      newRight = boundingPolygon.topRightX;
    }

    polygon.topRightX = newRight;
    polygon.bottomRightX = newRight;
  }

  /**
   * Get the resulting y-position of the top edge of the crop window given the handle's position and
   * the image's bounding box and snap radius.
   *
   * @param top the x-position that the top edge is dragged to
   * @param bounds the bounding box of the image that is being cropped
   * @param snapMargin the snap distance to the image edge (in pixels)
   */
  private void adjustTop(RectF rect, float top, RectF bounds, float snapMargin, float aspectRatio, boolean leftMoves,
      boolean rightMoves)
  {

    float newTop = top;

    if (newTop < 0) {
      newTop /= 1.05f;
      mTouchOffset.y -= newTop / 1.1f;
    }

    if (newTop < bounds.top) {
      mTouchOffset.y -= (newTop - bounds.top) / 2f;
    }

    if (newTop - bounds.top < snapMargin) {
      newTop = bounds.top;
    }

    // Checks if the window is too small vertically
    if (rect.bottom - newTop < mMinCropHeight) {
      newTop = rect.bottom - mMinCropHeight;
    }

    // Checks if the window is too large vertically
    if (rect.bottom - newTop > mMaxCropHeight) {
      newTop = rect.bottom - mMaxCropHeight;
    }

    if (newTop - bounds.top < snapMargin) {
      newTop = bounds.top;
    }

    // check horizontal bounds if aspect ratio is in play
    if (aspectRatio > 0) {
      float newWidth = (rect.bottom - newTop) * aspectRatio;

      // Checks if the crop window is too small horizontally due to aspect ratio adjustment
      if (newWidth < mMinCropWidth) {
        newTop = Math.max(bounds.top, rect.bottom - (mMinCropWidth / aspectRatio));
        newWidth = (rect.bottom - newTop) * aspectRatio;
      }

      // Checks if the crop window is too large horizontally due to aspect ratio adjustment
      if (newWidth > mMaxCropWidth) {
        newTop = Math.max(bounds.top, rect.bottom - (mMaxCropWidth / aspectRatio));
        newWidth = (rect.bottom - newTop) * aspectRatio;
      }

      // if left AND right edge moves by aspect ratio check that it is within full width bounds
      if (leftMoves && rightMoves) {
        newTop = Math.max(newTop, Math.max(bounds.top, rect.bottom - bounds.width() / aspectRatio));
      } else {
        // if left edge moves by aspect ratio check that it is within bounds
        if (leftMoves && rect.right - newWidth < bounds.left) {
          newTop = Math.max(bounds.top, rect.bottom - (rect.right - bounds.left) / aspectRatio);
          newWidth = (rect.bottom - newTop) * aspectRatio;
        }

        // if right edge moves by aspect ratio check that it is within bounds
        if (rightMoves && rect.left + newWidth > bounds.right) {
          newTop =
              Math.max(
                  newTop,
                  Math.max(bounds.top, rect.bottom - (bounds.right - rect.left) / aspectRatio));
        }
      }
    }

    rect.top = newTop;
  }

  private void adjustTop(Polygon polygon, float top, Polygon boundingPolygon, float snapMargin)
  {

    float newTop = top;

    if (newTop < 0) {
      newTop /= 1.05f;
      mTouchOffset.y -= newTop / 1.1f;
    }

    if (newTop < boundingPolygon.topLeftY) {
      mTouchOffset.y -= (newTop - boundingPolygon.topLeftY) / 2f;
    }

    if (newTop - boundingPolygon.topLeftY < snapMargin) {
      newTop = boundingPolygon.topLeftY;
    }

    // Checks if the window is too small vertically
    if (polygon.bottomLeftY - newTop < mMinCropHeight) {
      newTop = polygon.bottomLeftY - mMinCropHeight;
    }

    // Checks if the window is too large vertically
    if (polygon.bottomLeftY - newTop > mMaxCropHeight) {
      newTop = polygon.bottomLeftY - mMaxCropHeight;
    }

    if (newTop - boundingPolygon.topLeftY < snapMargin) {
      newTop = boundingPolygon.topLeftY;
    }

    polygon.topLeftY = newTop;
    polygon.topRightY = newTop;
  }

  private void adjustTopLeft(Polygon polygon, float top, float left, Polygon boundingPolygon, float snapMargin)
  {
    Log.e(TAG, "adjustTopLeft: PolygonBefore : "+polygon.toString());

    float newTop = top;

    if (newTop < 0) {
      newTop /= 1.05f;
      mTouchOffset.y -= newTop / 1.1f;
    }

    if (newTop < boundingPolygon.topLeftY) {
      mTouchOffset.y -= (newTop - boundingPolygon.topLeftY) / 2f;
    }

    if (newTop - boundingPolygon.topLeftY < snapMargin) {
      newTop = boundingPolygon.topLeftY;
    }

    // Checks if the window is too small vertically
    if (polygon.bottomLeftY - newTop < mMinCropHeight) {
      newTop = polygon.bottomLeftY - mMinCropHeight;
    }

    // Checks if the window is too large vertically
    if (polygon.bottomLeftY - newTop > mMaxCropHeight) {
      newTop = polygon.bottomLeftY - mMaxCropHeight;
    }

    if (newTop - boundingPolygon.topLeftY < snapMargin) {
      newTop = boundingPolygon.topLeftY;
    }

    polygon.topLeftY = newTop;


    float newLeft = left;

    if (newLeft < 0) {
      newLeft /= 1.05f;
      mTouchOffset.x -= newLeft / 1.1f;
    }

    if (newLeft < boundingPolygon.topLeftX) {
      mTouchOffset.x -= (newLeft - boundingPolygon.topLeftX) / 2f;
    }

    if (newLeft - boundingPolygon.topLeftX < snapMargin) {
      newLeft = boundingPolygon.topLeftX;
    }

    // Checks if the window is too small horizontally
    if (polygon.topRightX - newLeft < mMinCropWidth) {
      newLeft = polygon.topRightX - mMinCropWidth;
    }

    // Checks if the window is too large horizontally
    if (polygon.topRightX - newLeft > mMaxCropWidth) {
      newLeft = polygon.topRightX - mMaxCropWidth;
    }

    if (newLeft - boundingPolygon.topLeftX < snapMargin) {
      newLeft = boundingPolygon.topLeftX;
    }
    polygon.topLeftX = newLeft;
    Log.e(TAG, "adjustTopLeft: PolygonAfter : "+polygon.toString());
  }

  private void adjustTopRight(Polygon polygon, float top, float right, int viewWidth, Polygon boundingPolygon, float snapMargin)
  {
    Log.e(TAG, "adjustTopLeft: PolygonBefore : "+polygon.toString());

    float newTop = top;

    if (newTop < 0) {
      newTop /= 1.05f;
      mTouchOffset.y -= newTop / 1.1f;
    }

    if (newTop < boundingPolygon.topRightY) {
      mTouchOffset.y -= (newTop - boundingPolygon.topRightY) / 2f;
    }

    if (newTop - boundingPolygon.topRightY < snapMargin) {
      newTop = boundingPolygon.topRightY;
    }

    // Checks if the window is too small vertically
    if (polygon.bottomRightY - newTop < mMinCropHeight) {
      newTop = polygon.bottomRightY - mMinCropHeight;
    }

    // Checks if the window is too large vertically
    if (polygon.bottomRightY - newTop > mMaxCropHeight) {
      newTop = polygon.bottomRightY - mMaxCropHeight;
    }

    if (newTop - boundingPolygon.topRightY < snapMargin) {
      newTop = boundingPolygon.topRightY;
    }

    polygon.topRightY = newTop;


    float newRight = right;

    if (newRight > viewWidth) {
      newRight = viewWidth + (newRight - viewWidth) / 1.05f;
      mTouchOffset.x -= (newRight - viewWidth) / 1.1f;
    }

    if (newRight > boundingPolygon.topRightX) {
      mTouchOffset.x -= (newRight - boundingPolygon.topRightX) / 2f;
    }

    // If close to the edge
    if (boundingPolygon.topRightX - newRight < snapMargin) {
      newRight = boundingPolygon.topRightX;
    }

    // Checks if the window is too small horizontally
    if (newRight - boundingPolygon.topLeftX < mMinCropWidth) {
      newRight = boundingPolygon.topLeftX + mMinCropWidth;
    }

    // Checks if the window is too large horizontally
    if (newRight - boundingPolygon.topLeftX > mMaxCropWidth) {
      newRight = boundingPolygon.topLeftX + mMaxCropWidth;
    }

    // If close to the edge
    if (boundingPolygon.topRightX - newRight < snapMargin) {
      newRight = boundingPolygon.topRightX;
    }

    polygon.topRightX = newRight;

    Log.e(TAG, "adjustTopLeft: PolygonAfter : "+polygon.toString());
  }

  private void adjustBottomLeft(Polygon polygon, float bottom, float left, Polygon boundingPolygon, int viewHeight, float snapMargin)
  {

    float newBottom = bottom;
    Log.e(TAG, "adjustBottom: newBottom Initial : "+newBottom);

    if (newBottom > viewHeight) {
      newBottom = viewHeight + (newBottom - viewHeight) / 1.05f;
      mTouchOffset.y -= (newBottom - viewHeight) / 1.1f;
    }

    if (newBottom > boundingPolygon.bottomLeftY) {
      mTouchOffset.y -= (newBottom - boundingPolygon.bottomLeftY) / 2f;
    }

    if (boundingPolygon.bottomLeftY - newBottom < snapMargin) {
      newBottom = boundingPolygon.bottomLeftY;
    }

    // Checks if the window is too small vertically
    if (newBottom - boundingPolygon.topLeftY < mMinCropHeight) {
      newBottom = boundingPolygon.topLeftY + mMinCropHeight;
    }

    // Checks if the window is too small vertically
    if (newBottom - boundingPolygon.topLeftY > mMaxCropHeight) {
      newBottom = boundingPolygon.topLeftY + mMaxCropHeight;
    }

    if (boundingPolygon.bottomLeftY - newBottom < snapMargin) {
      newBottom = boundingPolygon.bottomLeftY;
    }

    Log.e(TAG, "adjustBottom: newBottom Final : "+newBottom);

    polygon.bottomLeftY = newBottom;

    float newLeft = left;

    if (newLeft < 0) {
      newLeft /= 1.05f;
      mTouchOffset.x -= newLeft / 1.1f;
    }

    if (newLeft < boundingPolygon.bottomLeftX) {
      mTouchOffset.x -= (newLeft - boundingPolygon.bottomLeftX) / 2f;
    }

    if (newLeft - boundingPolygon.bottomLeftX < snapMargin) {
      newLeft = boundingPolygon.bottomLeftX;
    }

    // Checks if the window is too small horizontally
    if (polygon.bottomRightX - newLeft < mMinCropWidth) {
      newLeft = polygon.bottomRightX - mMinCropWidth;
    }

    // Checks if the window is too large horizontally
    if (polygon.bottomRightX - newLeft > mMaxCropWidth) {
      newLeft = polygon.bottomRightX - mMaxCropWidth;
    }

    if (newLeft - boundingPolygon.bottomLeftX < snapMargin) {
      newLeft = boundingPolygon.bottomLeftX;
    }
    polygon.bottomLeftX = newLeft;
  }


  private void adjustBottomRight(Polygon polygon, float bottom, float right, Polygon boundingPolygon, int viewHeight, int viewWidth, float snapMargin)
  {

    float newBottom = bottom;
    Log.e(TAG, "adjustBottomRight: newBottom Initial : "+newBottom);

    if (newBottom > viewHeight) {
      newBottom = viewHeight + (newBottom - viewHeight) / 1.05f;
      mTouchOffset.y -= (newBottom - viewHeight) / 1.1f;
    }

    if (newBottom > boundingPolygon.bottomRightY) {
      mTouchOffset.y -= (newBottom - boundingPolygon.bottomRightY) / 2f;
    }

    if (boundingPolygon.bottomRightY - newBottom < snapMargin) {
      newBottom = boundingPolygon.bottomRightY;
    }

    // Checks if the window is too small vertically
    if (newBottom - boundingPolygon.topRightY < mMinCropHeight) {
      newBottom = boundingPolygon.topRightY + mMinCropHeight;
    }

    // Checks if the window is too small vertically
    if (newBottom - boundingPolygon.topRightY > mMaxCropHeight) {
      newBottom = boundingPolygon.topRightY + mMaxCropHeight;
    }

    if (boundingPolygon.bottomRightY - newBottom < snapMargin) {
      newBottom = boundingPolygon.bottomRightY;
    }

    Log.e(TAG, "adjustBottom: newBottom Final : "+newBottom);

    polygon.bottomRightY = newBottom;


    float newRight = right;

    if (newRight > viewWidth) {
      newRight = viewWidth + (newRight - viewWidth) / 1.05f;
      mTouchOffset.x -= (newRight - viewWidth) / 1.1f;
    }

    if (newRight > boundingPolygon.bottomRightX) {
      mTouchOffset.x -= (newRight - boundingPolygon.bottomRightX) / 2f;
    }

    // If close to the edge
    if (boundingPolygon.bottomRightX - newRight < snapMargin) {
      newRight = boundingPolygon.bottomRightX;
    }

    // Checks if the window is too small horizontally
    if (newRight - polygon.bottomLeftX < mMinCropWidth) {
      newRight = polygon.bottomLeftX + mMinCropWidth;
    }

    // Checks if the window is too large horizontally
    if (newRight - polygon.bottomLeftX > mMaxCropWidth) {
      newRight = polygon.bottomLeftX + mMaxCropWidth;
    }

    // If close to the edge
    if (boundingPolygon.bottomRightX - newRight < snapMargin) {
      newRight = boundingPolygon.bottomRightX;
    }
    polygon.bottomRightX = newRight;
  }

  /**
   * Get the resulting y-position of the bottom edge of the crop window given the handle's position
   * and the image's bounding box and snap radius.
   *
   * @param bottom the position that the bottom edge is dragged to
   * @param bounds the bounding box of the image that is being cropped
   * @param viewHeight
   * @param snapMargin the snap distance to the image edge (in pixels)
   */
  private static final String TAG = "CropWindowMoveHandler";
  private void adjustBottom(RectF rect, float bottom, RectF bounds, int viewHeight, float snapMargin, float aspectRatio, boolean leftMoves, boolean rightMoves) {

    float newBottom = bottom;
    Log.e(TAG, "adjustBottom: newBottom Initial : "+newBottom);

    if (newBottom > viewHeight) {
      newBottom = viewHeight + (newBottom - viewHeight) / 1.05f;
      mTouchOffset.y -= (newBottom - viewHeight) / 1.1f;
    }

    if (newBottom > bounds.bottom) {
      mTouchOffset.y -= (newBottom - bounds.bottom) / 2f;
    }

    if (bounds.bottom - newBottom < snapMargin) {
      newBottom = bounds.bottom;
    }

    // Checks if the window is too small vertically
    if (newBottom - rect.top < mMinCropHeight) {
      newBottom = rect.top + mMinCropHeight;
    }

    // Checks if the window is too small vertically
    if (newBottom - rect.top > mMaxCropHeight) {
      newBottom = rect.top + mMaxCropHeight;
    }

    if (bounds.bottom - newBottom < snapMargin) {
      newBottom = bounds.bottom;
    }

    Log.e(TAG, "adjustBottom: newBottom Final : "+newBottom);

    // check horizontal bounds if aspect ratio is in play
    if (aspectRatio > 0) {
      float newWidth = (newBottom - rect.top) * aspectRatio;

      // Checks if the window is too small horizontally
      if (newWidth < mMinCropWidth) {
        newBottom = Math.min(bounds.bottom, rect.top + mMinCropWidth / aspectRatio);
        newWidth = (newBottom - rect.top) * aspectRatio;
      }

      // Checks if the window is too large horizontally
      if (newWidth > mMaxCropWidth) {
        newBottom = Math.min(bounds.bottom, rect.top + mMaxCropWidth / aspectRatio);
        newWidth = (newBottom - rect.top) * aspectRatio;
      }

      // if left AND right edge moves by aspect ratio check that it is within full width bounds
      if (leftMoves && rightMoves) {
        newBottom =
            Math.min(newBottom, Math.min(bounds.bottom, rect.top + bounds.width() / aspectRatio));
      } else {
        // if left edge moves by aspect ratio check that it is within bounds
        if (leftMoves && rect.right - newWidth < bounds.left) {
          newBottom = Math.min(bounds.bottom, rect.top + (rect.right - bounds.left) / aspectRatio);
          newWidth = (newBottom - rect.top) * aspectRatio;
        }

        // if right edge moves by aspect ratio check that it is within bounds
        if (rightMoves && rect.left + newWidth > bounds.right) {
          newBottom =
              Math.min(
                  newBottom,
                  Math.min(bounds.bottom, rect.top + (bounds.right - rect.left) / aspectRatio));
        }
      }
    }

    rect.bottom = newBottom;
  }


  private void adjustBottom(Polygon polygon, float bottom, Polygon boundingPolygon, int viewHeight, float snapMargin)
  {

    float newBottom = bottom;
    Log.e(TAG, "adjustBottom: newBottom Initial : "+newBottom);

    if (newBottom > viewHeight) {
      newBottom = viewHeight + (newBottom - viewHeight) / 1.05f;
      mTouchOffset.y -= (newBottom - viewHeight) / 1.1f;
    }

    if (newBottom > boundingPolygon.bottomLeftY) {
      mTouchOffset.y -= (newBottom - boundingPolygon.bottomLeftY) / 2f;
    }

    if (boundingPolygon.bottomLeftY - newBottom < snapMargin) {
      newBottom = boundingPolygon.bottomLeftY;
    }

    // Checks if the window is too small vertically
    if (newBottom - polygon.topLeftY < mMinCropHeight) {
      newBottom = polygon.topLeftY + mMinCropHeight;
    }

    // Checks if the window is too small vertically
    if (newBottom - polygon.topLeftY > mMaxCropHeight) {
      newBottom = polygon.topLeftY + mMaxCropHeight;
    }

    if (boundingPolygon.bottomLeftY - newBottom < snapMargin) {
      newBottom = boundingPolygon.bottomLeftY;
    }

    polygon.bottomLeftY = newBottom;
    polygon.bottomRightY = newBottom;
  }

  /**
   * Adjust left edge by current crop window height and the given aspect ratio, the right edge
   * remains in possition while the left adjusts to keep aspect ratio to the height.
   */
  private void adjustLeftByAspectRatio(RectF rect, float aspectRatio) {
    rect.left = rect.right - rect.height() * aspectRatio;
  }

  /**
   * Adjust top edge by current crop window width and the given aspect ratio, the bottom edge
   * remains in possition while the top adjusts to keep aspect ratio to the width.
   */
  private void adjustTopByAspectRatio(RectF rect, float aspectRatio) {
    rect.top = rect.bottom - rect.width() / aspectRatio;
  }

  /**
   * Adjust right edge by current crop window height and the given aspect ratio, the left edge
   * remains in possition while the left adjusts to keep aspect ratio to the height.
   */
  private void adjustRightByAspectRatio(RectF rect, float aspectRatio) {
    rect.right = rect.left + rect.height() * aspectRatio;
  }

  /**
   * Adjust bottom edge by current crop window width and the given aspect ratio, the top edge
   * remains in possition while the top adjusts to keep aspect ratio to the width.
   */
  private void adjustBottomByAspectRatio(RectF rect, float aspectRatio) {
    rect.bottom = rect.top + rect.width() / aspectRatio;
  }

  /**
   * Adjust left and right edges by current crop window height and the given aspect ratio, both
   * right and left edges adjusts equally relative to center to keep aspect ratio to the height.
   */
  private void adjustLeftRightByAspectRatio(RectF rect, RectF bounds, float aspectRatio) {
    rect.inset((rect.width() - rect.height() * aspectRatio) / 2, 0);
    if (rect.left < bounds.left) {
      rect.offset(bounds.left - rect.left, 0);
    }
    if (rect.right > bounds.right) {
      rect.offset(bounds.right - rect.right, 0);
    }
  }

  /**
   * Adjust top and bottom edges by current crop window width and the given aspect ratio, both top
   * and bottom edges adjusts equally relative to center to keep aspect ratio to the width.
   */
  private void adjustTopBottomByAspectRatio(RectF rect, RectF bounds, float aspectRatio) {
    rect.inset(0, (rect.height() - rect.width() / aspectRatio) / 2);
    if (rect.top < bounds.top) {
      rect.offset(0, bounds.top - rect.top);
    }
    if (rect.bottom > bounds.bottom) {
      rect.offset(0, bounds.bottom - rect.bottom);
    }
  }

  /** Calculates the aspect ratio given a rectangle. */
  private static float calculateAspectRatio(float left, float top, float right, float bottom) {
    return (right - left) / (bottom - top);
  }
  // endregion

  // region: Inner class: Type

  /** The type of crop window move that is handled. */
  public enum Type {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    LEFT,
    TOP,
    RIGHT,
    BOTTOM,
    CENTER
  }
  // endregion
}
