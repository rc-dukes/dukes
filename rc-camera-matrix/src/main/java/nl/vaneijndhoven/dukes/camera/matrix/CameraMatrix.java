package nl.vaneijndhoven.dukes.camera.matrix;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.undistort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * camera matrix operations
 *
 */
public class CameraMatrix implements UnaryOperator<Mat> {

    public static final CameraMatrix DEFAULT = new CameraMatrix(0, 0);
    Mat cameraMatrix = null;
    Mat distCoeffs = null;

    private List<Mat> rvecs = null;
    private List<Mat> tvecs = null;

    List<Mat> imagePoints = new ArrayList<>();
    List<Mat> objectPoints = new ArrayList<>();

    private int columns;
    private int rows;

    public List<Mat> getRvecs() {
      return rvecs;
    }

    public List<Mat> getTvecs() {
      return tvecs;
    }

    /**
     * create me with the given number of columns and rows
     * @param columns
     * @param rows
     */
    public CameraMatrix(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
    }

    /**
     * calibrate me with the given images
     * @param images
     */
    public void calibrate(Mat... images) {
        for (Mat image : images) {
            addCalibrationImage(image);
        }
    }

    /**
     * calculate me with the given size
     * @param size
     */
    private void calculate(Size size) {
        if (imagePoints.isEmpty()) {
            System.err.println("Could not find chessboard corners");
            return;
        }

        Mat cameraMatrix = new Mat();
        Mat distCoeffs = new Mat();
        List<Mat> rvecs = new ArrayList<>();
        List<Mat> tvecs= new ArrayList<>();

        Calib3d.calibrateCamera(objectPoints, imagePoints, size, cameraMatrix, distCoeffs, rvecs, tvecs);

        this.cameraMatrix = cameraMatrix;
        this.distCoeffs = distCoeffs;
        this.rvecs = rvecs;
        this.tvecs = tvecs;
    }

    /**
     * add the given calibration image
     * @param image
     */
    private void addCalibrationImage(Mat image) {
        Mat grey = new Mat();
        Imgproc.cvtColor(image, grey, COLOR_BGR2GRAY);

        columns = 8;
        rows = 6;
        MatOfPoint2f corners = new MatOfPoint2f();
        boolean patternWasFound = findCorners(grey, columns, rows, corners);

        Calib3d.drawChessboardCorners(image, new Size(columns, rows), corners, patternWasFound);

        if (!patternWasFound) {
            return;
        }

        imagePoints.add(corners);

        MatOfPoint3f obj = new MatOfPoint3f();
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                obj.push_back(new MatOfPoint3f(new Point3(y, x, 0.0)));
            }
        }

        objectPoints.add(obj);

        calculate(image.size());
    }


    /**
     * find the corners
     * @param image - the image
     * @param columns - the columns
     * @param rows - rows
     * @param corners - corners
     * @return - true if found
     */
    private boolean findCorners(Mat image, int columns, int rows, MatOfPoint2f corners) {
        return Calib3d.findChessboardCorners(image, new Size(columns, rows), corners);
    }


    @Override
    public Mat apply(Mat image) {
//        Objects.requireNonNull(cameraMatrix, "CameraMatrix must first be calibrated by calling calibrate");

        if (cameraMatrix == null) {
            return image;
        }

        Mat newImage = new Mat();
        undistort(image, newImage, cameraMatrix, distCoeffs);
        return newImage;
    }

    public String serialize() {
        JsonObject matrix = serializeMat(cameraMatrix);
        JsonObject dists = serializeMat(distCoeffs);

        return new JsonObject()
                .put("cameraMatrix", matrix)
                .put("distCoeffs", dists)
                .toString();
    }

    private JsonObject serializeMat(Mat mat) {
        JsonObject matrix = new JsonObject();
        matrix.put("size", new JsonObject()
                .put("cols", mat.size().width)
                .put("rows", mat.size().height));

        matrix.put("type", mat.type());

        JsonArray entries = new JsonArray();

        for (int x = 0; x < mat.size().width; x++) {
            for (int y = 0; y < mat.size().height; y++) {
                double[] vals = mat.get(y,x);
                JsonArray jsonVals = new JsonArray();

                Arrays.stream(vals).forEach(jsonVals::add);

                entries.add(new JsonObject()
                        .put("x", x)
                        .put("y", y)
                        .put("val", jsonVals));
            }
        }

        matrix = matrix.put("entries", entries);
        return matrix;
    }

    public static CameraMatrix deserizalize(String serialized) {
        JsonObject object = new JsonObject(serialized);

        JsonObject cameraMatrixJson = object.getJsonObject("cameraMatrix");
        JsonObject distCoeffsJson = object.getJsonObject("distCoeffs");

        CameraMatrix cameraMatrix = new CameraMatrix(0, 0);

        cameraMatrix.cameraMatrix = deserializeMat(cameraMatrixJson);
        cameraMatrix.distCoeffs = deserializeMat(distCoeffsJson);

        return cameraMatrix;
    }

    private static Mat deserializeMat(JsonObject matJson) {
        Mat mat = new Mat(
                matJson.getJsonObject("size").getInteger("rows"),
                matJson.getJsonObject("size").getInteger("cols"),
                matJson.getInteger("type"));

        matJson.getJsonArray("entries").forEach(obj -> {
            JsonObject entry = (JsonObject)obj;

            double[] values = entry.getJsonArray("val").stream()
                    .mapToDouble(x -> (Double)x)
                    .toArray();

            mat.put(entry.getInteger("y"), entry.getInteger("x"), values);
        });

        return mat;
    }
}
