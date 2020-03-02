package org.rcdukes.video;

import io.reactivex.Observable;

/**
 * allow access to images via Observable
 */
public interface ImageSource {
  Observable<Image> getImageObservable();
}
