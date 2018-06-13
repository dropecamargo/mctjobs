package com.koiti.mctjobs;

import android.content.Intent;

import com.glidebitmappool.GlideBitmapPool;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.koiti.mctjobs.services.DocumentService;
import com.koiti.mctjobs.services.MaintenanceService;
import com.koiti.mctjobs.services.NotificationService;
import com.koiti.mctjobs.services.TrackerGpsService;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class Application extends android.app.Application {

    private Tracker mTracker;

    public Application() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Universal image loader
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

        // Glide bitmap pool image
        GlideBitmapPool.initialize(10 * 1024 * 1024);

        // Service notification
        startService(new Intent(this, NotificationService.class));
        startService(new Intent(this, DocumentService.class));

        // Service tracker gps
        startService(new Intent(this, TrackerGpsService.class));

        // Service maintenance
        startService(new Intent(this, MaintenanceService.class));
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}