package nl.vaneijndhoven.dukes.roscoe;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import nl.vaneijndhoven.daisy.Daisy;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Roscoe extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(Roscoe.class);

    @Override
    public void start() throws Exception {
        LOG.info("Starting Roscoe (lane detection debug image web server");
        vertx.createHttpServer().requestHandler(this::sendImage).listen(8081);
    }

    private void sendImage(HttpServerRequest request) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", Daisy.MAT, matOfByte);
        byte[] bytes = matOfByte.toArray();

        request.response().putHeader("content-type", "image/png");
        request.response().putHeader("content-length", ""+bytes.length);
        request.response().write(Buffer.buffer().appendBytes(bytes));
    }


}