package nl.vaneijndhoven.opencv.tools;

import org.opencv.core.Mat;
import rx.Observable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MemoryManagement {

    public static <S extends Mat> ClosableMat<S> closable(S resource) {
        return new ClosableMat<>(resource);
    }

    public static <T, S extends Mat> T scoped(Supplier<S> resource, Function<S, T> execution) {
        try(ClosableMat<S> res = new ClosableMat<>(resource.get())) {
            return execution.apply(res.get());
        }
    }

    public static <T, S extends Mat> T scoped(String title, Supplier<S> resource, Function<S, T> execution) {
        return scoped(resource, execution);
    }

    public static <S extends Mat> void scoped(Supplier<S> resource, Consumer<S> execution) {
        try(ClosableMat<S> res = new ClosableMat<>(resource.get())) {
            execution.accept(res.get());
        }
    }

    public static <S extends Mat> void scoped(String title, Supplier<S> resource, Consumer<S> execution) {
        scoped(resource, execution);
    }

    public static class ClosableMat<S extends Mat> extends Mat implements AutoCloseable {

        private S mat;

        public ClosableMat(S mat) {
            this.mat = mat;
        }

        @Override
        public void close() {
            mat.release();
        }

        public S get() {
            return mat;
        }
    }

    public static Observable<Mat> disposable(Mat mat) {
        return Observable.using(() -> mat, Observable::just, Mat::release);
    }
}