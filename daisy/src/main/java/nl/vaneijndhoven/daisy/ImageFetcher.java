package nl.vaneijndhoven.daisy;

import nl.vaneijndhoven.opencv.tools.MemoryManagement;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

public class ImageFetcher {

    private VideoCapture capture = new VideoCapture();
    private String source;

    public ImageFetcher(String source) {
        this.source = source;
    }

    public Mat fetch() {
        if (!this.capture.isOpened()) {
            this.capture.open(source);
            throw new IllegalStateException("Trying to fetch image from unopened VideoCapture");
        }

        final Mat frame = new Mat();

        this.capture.read(frame);

        return !frame.empty() ? frame : null;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public Observable<Mat> toObservable() {
        // Resource creation.
        Func0<VideoCapture> resourceFactory = () -> {
            VideoCapture capture = new VideoCapture();
            capture.open(source);
            return capture;
        };

        // Convert to observable.
        Func1<VideoCapture, Observable<Mat>> observableFactory =
                capture -> Observable.<Mat>create(subscription -> {
                    boolean hasNext = true;
                    while (hasNext) {
                        final Mat frame = new Mat();

                        hasNext = capture.read(frame);

                        subscription.onNext(frame);
                    }

                    subscription.onCompleted();
                }).switchMap(MemoryManagement::disposable);

        // Disposal function.
        Action1<VideoCapture> dispose = VideoCapture::release;

        return Observable.using(resourceFactory, observableFactory, dispose);
    }
}
