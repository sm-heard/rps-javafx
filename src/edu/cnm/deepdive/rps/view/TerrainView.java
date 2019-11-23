/*
 *  Copyright 2019 Nicholas Bennett & Deep Dive Coding/CNM Ingenuity
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.rps.view;

import edu.cnm.deepdive.rps.model.Arena;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Extends {@link Canvas} to display the contents of all cells of the terrain of an {@link Arena}.
 *
 * @author Nicholas Bennett
 */
public class TerrainView extends Canvas {

  private static final double MAX_HUE = 360;

  private byte[][] terrain;
  private Color[] breedColors;
  private Arena arena;
  private boolean bound;

  /**
   * Returns a {@code true} flag indicating that this view is resizable, after binding its size to
   * the size of its container {@link Pane}.
   *
   * @return {@code true}, indicating that {@code this} is resizable.
   */
  @Override
  public boolean isResizable() {
    if (!bound) {
      widthProperty().bind(((Pane) getParent()).widthProperty());
      heightProperty().bind(((Pane) getParent()).heightProperty());
      bound = true;
    }
    return true;
  }

  /**
   * Resizes {@code this} instance to the specified dimensions, and re-draws the {@link Arena}
   * terrain contents.
   *
   * @param width
   * @param height
   */
  @Override
  public void resize(double width, double height) {
    super.resize(width, height);
    if (arena != null) {
      draw();
    }
  }

  /**
   * Sets the {@link Arena} instance to be used as the source of terrain cells. As a side effect,
   * the number of breeds initially in {@code arena} (returned by {@link Arena#getNumBreeds()
   * arena.getNumBreeds()}, allows this method to compute the hue that {@link #draw()} will use for
   * rendering members of each breed, as equally spaced values on the color wheel.
   *
   * @param arena source of terrain cells and initial number of breeds.
   */
  public void setArena(Arena arena) {
    this.arena = arena;
    int numBreeds = arena.getNumBreeds();
    int size = arena.getArenaSize();
    terrain = new byte[size][size];
    breedColors = new Color[numBreeds];
    for (int i = 0; i < numBreeds; i++) {
      breedColors[i] = Color.hsb(i * MAX_HUE / numBreeds, 1, 0.9);
    }
  }

  /**
   * Renders all of the cells copied from {@link Arena#copyTerrain(byte[][])}. Each cell is rendered
   * as an oval (a circle if the displayed cells are square), filled with a color corresponding to
   * the breed occupying the cell.
   */
  public void draw() {
    if (terrain != null) {
      GraphicsContext context = getGraphicsContext2D();
      arena.copyTerrain(terrain);
      double cellWidth = getWidth() / terrain[0].length;
      double cellHeight = getHeight() / terrain.length;
      context.clearRect(0, 0, getWidth(), getHeight());
      for (int row = 0; row < terrain.length; row++) {
        double cellTop = row * cellHeight;
        for (int col = 0; col < terrain[row].length; col++) {
          context.setFill(breedColors[terrain[row][col]]);
          context.fillOval(col * cellWidth, cellTop, cellWidth, cellHeight);
        }
      }
    }
  }

}
