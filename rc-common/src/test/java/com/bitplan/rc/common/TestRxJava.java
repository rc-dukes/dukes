package com.bitplan.rc.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import rx.Observable;
import rx.Subscription;

public class TestRxJava {
  @Test
  public void testSubscribe() {
    List<Integer> lens=new ArrayList<Integer>();
    Observable<String> o = Observable.just("one", "ten", "hundred", "thousand");
    Observable<Integer> m = o.map(String::length);
    Observable<Integer> f = m.filter(it -> it > 6);
    Subscription s = f.subscribe(l -> lens.add(l));
    assertNotNull(s);
    assertTrue(s.isUnsubscribed());
    assertEquals(2,lens.size());
    assertEquals(Integer.valueOf(7),lens.get(0));
    assertEquals(Integer.valueOf(8),lens.get(1));
  }
  @Test
  public void testSubscribeOneLiner() {
    List<Integer> lens=new ArrayList<Integer>();
    Observable.just("one", "ten", "hundred", "thousand")
    .map(String::length)
    .filter(it -> it > 6)
    .subscribe(l -> lens.add(l));
    assertEquals(2,lens.size());
    assertEquals(Integer.valueOf(7),lens.get(0));
    assertEquals(Integer.valueOf(8),lens.get(1));
  }
}
