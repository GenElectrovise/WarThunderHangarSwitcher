package com.genelectrovise.warthunderhangarswitcher;

public class WTHSExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        WarThunderHangarSwitcher.err(" ! ERROR ! :: " + e.getMessage());
        WarThunderHangarSwitcher.err(" ! ");
        WarThunderHangarSwitcher.err(" ! Stacktrace: ");
        e.printStackTrace();
        WarThunderHangarSwitcher.err(" ! ");
        System.exit(-1);
    }
}
