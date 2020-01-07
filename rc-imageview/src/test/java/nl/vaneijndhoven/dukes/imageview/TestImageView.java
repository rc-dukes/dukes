package nl.vaneijndhoven.dukes.imageview;

import org.junit.Test;

import nl.vaneijndhoven.dukes.common.ClusterStarter;
import nl.vaneijndhoven.dukes.common.Environment;

/**
 * test the imageView verticle
 * @author wf
 *
 */
public class TestImageView {
  @Test
  public void testImageViewStart() throws Exception {
    Environment.mock();
    ClusterStarter clusterStarter=new ClusterStarter();
    ImageView imageView=new ImageView();
    clusterStarter.deployVerticles(imageView);
    imageView.waitStarted(20000,10);
  }
}
