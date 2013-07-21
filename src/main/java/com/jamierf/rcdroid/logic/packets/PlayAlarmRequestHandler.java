package com.jamierf.rcdroid.logic.packets;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import com.jamierf.rcdroid.http.Packet;
import com.jamierf.rcdroid.http.listener.PacketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayAlarmRequestHandler implements PacketListener {

    private static final Logger LOG = LoggerFactory.getLogger(PlayAlarmRequestHandler.class);

    private final Ringtone ringtone;

    public PlayAlarmRequestHandler(Context context) {
        final Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
    }

    @Override
    public void onPacket(Packet packet) {
        if (ringtone.isPlaying())
            return;

        LOG.trace("Playing alarm: {}", ringtone);
        ringtone.play();
    }
}
