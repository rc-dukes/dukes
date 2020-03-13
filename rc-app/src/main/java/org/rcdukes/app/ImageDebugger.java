package org.rcdukes.app;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.rcdukes.action.Navigator;
import org.rcdukes.action.StraightLaneNavigator;
import org.rcdukes.app.GUIDisplayer.PictureStep;
import org.rcdukes.common.EventbusLogger;
import org.rcdukes.detect.ImageFetcher;
import org.rcdukes.video.Image;
import org.rcdukes.video.ImageCollector;
import org.rcdukes.video.ImageCollector.ImageType;
import org.rcdukes.video.VideoRecorders.VideoInfo;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * debugger for synchronized info between recorded video images
 * and navigation info from graph database
 * 
 * @author wf
 *
 */
public class ImageDebugger {
  Navigator nav;
  Map<ImageType, ImageFetcher> imageFetcherMap;
  boolean active = false;
  ImageCollector imageCollector;
  protected static final Logger LOG = LoggerFactory
      .getLogger(StraightLaneNavigator.class);

  /**
   * create an image Debugger
   * 
   * @param videoFile
   * @throws Exception
   */
  public ImageDebugger(File videoFile) throws Exception {
    nav = new StraightLaneNavigator();
    imageFetcherMap = new HashMap<ImageType, ImageFetcher>();
    File graphFile = VideoInfo.getNavigationFile(videoFile);
    if (graphFile.exists()) {
      nav.loadGraph(graphFile);
      active = true;
      this.imageCollector = new ImageCollector();
      for (ImageType imageType : ImageType.values()) {
        switch (imageType) {
        case simulator:
        case startlight:
        case mask:
        case morph:
          break;
        default:
          String videoFileName = videoFile.getPath().replaceAll(".*_mp4v_",
              "/" + imageType.name() + "_mp4v_");
          File file = new File(videoFile.getParent(), videoFileName);
          if (file.exists()) {
            ImageFetcher imageFetcher = new ImageFetcher(file.getPath());
            imageFetcherMap.put(imageType, imageFetcher);
          } else {
            throw new Exception("missing video file " + file.getPath());
          }
        }
      }
    }
  }

  public boolean isActive() {
    return active;
  }

  /**
   * step the given imageFetcher
   * 
   * @param imageFetcher
   * @param step
   * @return - the image for the ImageFetcher
   */
  public Image step(ImageFetcher imageFetcher, PictureStep step) {
    int index = imageFetcher.getFrameIndex();
    switch (step) {
    case FIRST:
      if (index >= 0)
        imageFetcher.close();
      imageFetcher.open();
      break;
    case PREV:
      if (index >= 0)
        imageFetcher.close();
      imageFetcher.open();
      for (int i = 1; i < index - 1; i++)
        imageFetcher.fetch();
      break;
    case NEXT:
      break;

    case FORWARD:
      for (int i = 1; i <= 9; i++)
        imageFetcher.fetch();
      break;
    }
    return imageFetcher.fetch();
  }

  public void step(PictureStep pStep) {
    for (ImageType imageType : ImageType.values()) {
      if (imageFetcherMap.containsKey(imageType)) {
        Image image = step(imageFetcherMap.get(imageType), pStep);
        this.imageCollector.addImage(image, imageType);
      }
    }
  }

  public long getFrameIndex() {
    Image cameraImage = this.imageCollector.getImage(ImageType.camera, false);
    return cameraImage.getFrameIndex();
  }
  
  /**
   * get the navigation nodes for the given frameIndex
   * @param frameIndex
   * @return - the navigation nodes
   */
  public List<Vertex> getNavNodes(long frameIndex) {
    List<Vertex> navNodes = nav.g().V()
        .has("frameIndex", frameIndex).toList();
    LOG.info(String.format("found %d nav nodes for frameIndex %d", navNodes.size(),frameIndex));
    return navNodes;
  }

  /**
   * show the debug Info via the eventbus logger
   * @param eventbusLogger
   */
  public void showDebugInfo(EventbusLogger eventbusLogger) {
    //Vertex infoVertex = nav.g().V().hasLabel("VideoInfo").next();
    //VideoInfo info = nav.fromVertex(infoVertex, VideoInfo.class);
    List<Vertex> navNodes = this.getNavNodes(this.getFrameIndex());
    if (navNodes.size() > 0) {
      for (Vertex navNode : navNodes) {
        for (String key : navNode.keys()) {
          eventbusLogger.log(
              String.format("%s=%s\n", key, navNode.property(key).value()));
        }
      }
    }
  }

}
