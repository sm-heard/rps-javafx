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
package edu.cnm.deepdive.rps;

import edu.cnm.deepdive.rps.controller.RpsController;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Entry point for RPS Ecosystem JavaFX application.
 *
 * @author Nicholas Bennett
 */
public class Main extends Application {

  private static final String LAYOUT_RESOURCE = "rps.fxml";
  private static final String RESOURCE_BUNDLE = "strings";
  private static final String WINDOW_TITLE_KEY = "window_title";
  private static final String ICON_RESOURCE = "app-icon-72.png";

  private RpsController controller;

  /**
   * Invokes {@link #launch(String...)} to initialize the JavaFX framework.
   *
   * @param args command line arguments (currently ignored).
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Loads FXML layout, resource bundle, and icon used by application.
   *
   * @param stage primary application window.
   * @throws Exception if a required resource cannot be loaded.
   */
  @Override
  public void start(Stage stage) throws Exception {
    ClassLoader classLoader = getClass().getClassLoader();
    ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE);
    FXMLLoader fxmlLoader = new FXMLLoader(classLoader.getResource(LAYOUT_RESOURCE), bundle);
    Parent root = fxmlLoader.load();
    controller = fxmlLoader.getController();
    Scene scene = new Scene(root);
    stage.setTitle(bundle.getString(WINDOW_TITLE_KEY));
    stage.getIcons().add(new Image(classLoader.getResourceAsStream(ICON_RESOURCE)));
    stage.setResizable(false);
    stage.setResizable(true);
    stage.setScene(scene);
    stage.sizeToScene();
    stage.show();
    setStageSize(stage, root);
  }

  /**
   * Terminates RPS ecosystem simulation thread managed by {@link RpsController}, then invokes
   * standard JavaFX shutdown processing.
   *
   * @throws Exception
   */
  @Override
  public void stop() throws Exception {
    controller.stop();
    super.stop();
  }

  private void setStageSize(Stage stage, Parent root) {
    Bounds bounds = root.getLayoutBounds();
    stage.setMaxWidth(stage.getWidth());
    stage.setMaxHeight(stage.getHeight());
    stage.setMinWidth(root.minWidth(-1) + stage.getWidth() - bounds.getWidth());
    stage.setMinHeight(root.minHeight(-1) + stage.getHeight() - bounds.getHeight());
  }

}
