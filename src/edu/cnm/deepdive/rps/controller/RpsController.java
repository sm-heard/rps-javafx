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
package edu.cnm.deepdive.rps.controller;

import edu.cnm.deepdive.rps.Main;
import edu.cnm.deepdive.rps.model.Arena;
import edu.cnm.deepdive.rps.view.TerrainView;
import java.util.ResourceBundle;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Text;

/**
 * Handles user interaction to control execution of a simulation implemented by an instance of
 * {@link Arena}, as well as scheduling periodic display updates of a corresponding {@link
 * TerrainView}.
 *
 * @author Nicholas Bennett
 */
public class RpsController {

  private static final String STOP_TEXT_KEY = "stop_text";
  private static final String START_TEXT_KEY = "start_text";
  private static final String GENERATION_FORMAT_KEY = "generation_format";
  private static final byte NUM_BREEDS = 5;
  private static final int ARENA_SIZE = 100;
  private static final int ITERATIONS_PER_SLEEP = ARENA_SIZE * ARENA_SIZE / 50;
  private static final int SLEEP_INTERVAL = 1;

  @FXML private Button reset;
  @FXML private ToggleButton toggleRun;
  @FXML private TerrainView terrainView;
  @FXML private ResourceBundle resources;
  @FXML private Text generation;
  private Arena arena;
  private ViewUpdater updater;
  private boolean running;
  private String generationFormat;

  @FXML
  private void initialize() {
    arena = new Arena.Builder()
        .setNumBreeds(NUM_BREEDS)
        .setArenaSize(ARENA_SIZE)
        .build();
    terrainView.setArena(arena);
    updater = new ViewUpdater();
    generationFormat = resources.getString(GENERATION_FORMAT_KEY);
    reset(null);
  }

  /**
   * Halts execution of the simulation in the attached {@link Arena}. This method is exposed as
   * {@code public}, primarily to allow the {@link Main#stop()} lifecycle method to stop the
   * simulation thread, in the event that the application is terminated while the simulation is
   * running.
   */
  public void stop() {
    running = false;
    updater.stop();
  }

  @FXML
  private void reset(ActionEvent actionEvent) {
    arena.init();
    updateView();
  }

  @FXML
  private void toggleRun(ActionEvent actionEvent) {
    if (toggleRun.isSelected()) {
      start();
    } else {
      stop();
    }
  }

  private void start() {
    running = true;
    updateControls();
    new ModelRunner().start();
    updater.start();
  }

  private void updateView() {
    terrainView.draw();
    generation.setText(String.format(generationFormat, arena.getGeneration()));
  }

  private void updateControls() {
    if (running) {
      reset.setDisable(true);
      toggleRun.setText(resources.getString(STOP_TEXT_KEY));
    } else {
      reset.setDisable(false);
      toggleRun.setText(resources.getString(START_TEXT_KEY));
      if (toggleRun.isSelected()) {
        toggleRun.setSelected(false);
      }
      updateView();
    }
  }

  private class ModelRunner extends Thread {

    @Override
    public void run() {
      while (running) {
        for (int i = 0; i < ITERATIONS_PER_SLEEP; i++) {
          arena.advance();
        }
        running &= !arena.isAbsorbed();
        try {
          Thread.sleep(SLEEP_INTERVAL);
        } catch (InterruptedException expected) {
          // Ignore the innocuous exception.
        }
      }
      Platform.runLater(RpsController.this::updateControls);
    }

  }

  private class ViewUpdater extends AnimationTimer {

    @Override
    public void handle(long now) {
      updateView();
    }

  }

}
