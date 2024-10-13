package com.iolab.channel.util;

public final class SleepUtil {
  private SleepUtil() {
  }

  public static  void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException ie) {
      assert false; // shouldn't happen
    }
  }
}
