package com.oaksoft.commons.io.input;

import java.io.File;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

public class NameableTailer extends Tailer implements Runnable
{
    private static final int DEFAULT_BUFSIZE = 4096;
  
    public NameableTailer(File file, TailerListener listener, long delayMillis, boolean end, boolean reOpen, int bufSize)
    {
    	super(file, listener, delayMillis, end, reOpen, bufSize);

        listener.init(this);
    }
    
    public static Tailer create(File file, TailerListener listener, long delayMillis, boolean end, String threadName)
    {
        Tailer tailer = new Tailer(file, listener, delayMillis, end, DEFAULT_BUFSIZE);
        Thread thread = new Thread(tailer, threadName);
        thread.setDaemon(true);
        thread.start();
        return tailer;
    }
}
