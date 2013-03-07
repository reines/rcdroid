package com.jamierf.rcdroid.input.sensor;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import com.google.common.base.Optional;
import com.jamierf.rcdroid.input.api.BatteryStatus;
import org.jboss.netty.util.internal.ExecutorUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BatterySensor extends AbstractSensor<BatteryStatus> implements Runnable {

    private static final long POLLING_FREQUENCY = 60; // seconds

    private final Context context;
    private final ScheduledExecutorService executor;

    private BatteryStatus battery;

    public BatterySensor(Context context) {
        this.context = context;

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(this, 0, POLLING_FREQUENCY, TimeUnit.SECONDS);
    }

    @Override
    public Optional<BatteryStatus> getValue() {
        return Optional.fromNullable(battery);
    }

    @Override
    public void forceUpdate() {
        executor.execute(this);
    }

    @Override
    public void run() {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        final Intent battery = context.registerReceiver(null, filter);

        final int status = battery.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        final boolean charging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

        final int voltage = battery.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        final int temperature = battery.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);

        final float level = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        final float scale = battery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        this.battery = new BatteryStatus(charging, voltage / 1000F, temperature / 10F, level / scale);
        this.setValueChanged();
    }

    @Override
    public void close() {
        ExecutorUtil.terminate(executor);
    }
}
