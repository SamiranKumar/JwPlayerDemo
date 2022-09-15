package com.skh.jwplayerdemo;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jwplayer.pub.api.JWPlayer;
import com.jwplayer.pub.api.UiGroup;
import com.jwplayer.pub.api.configuration.PlayerConfig;
import com.jwplayer.pub.api.configuration.UiConfig;
import com.jwplayer.pub.api.license.LicenseUtil;
import com.jwplayer.pub.api.media.playlists.PlaylistItem;
import com.jwplayer.pub.view.JWPlayerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private CheckBox cbEnableDrm;
    private EditText streamURL;
    private EditText authURL;

    private JWPlayer player;
    private JWPlayerView playerView;

    public static String fileUrl = "https://drmstreamcdn01.tvhankook.com/media2/202207/6702424553843742/064001/hls/master.m3u8";
    public static String drmAuth = "https://tokyo.pallycon.com/ri/licenseManager.do?pallycon-customdata-v2=eyJkcm1fdHlwZSI6ICJXaWRldmluZSIsInNpdGVfaWQiOiAiQ0hQUCIsImRhdGEiOiAiL3JnV1hRTVlqVnJ1eXRXa25LUXJHN2pGOTltQUV1NUI4azNRd2RaTUNERC9DK24wU1BQNkZYWlF4eFRtWEEySDMvT1pSTVJSZHdXM1czVWpjbXpUL1E9PSJ9";
    public static String intro = "https://kfoodup.com/intro/intro.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerView = findViewById(R.id.jwplayerview);

        cbEnableDrm = findViewById(R.id.cbEnableDrm);
        streamURL = findViewById(R.id.stream_url);
        authURL = findViewById(R.id.auth_url);

        //----------------------------------------------------
        setUiData();
        //----------------------------------------------------

        cbEnableDrm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setUiData();
        });

        findViewById(R.id.setup).setOnClickListener(v -> {
            playVideo();
        });

        //----------------------------------------------------
        LicenseUtil.setLicenseKey(this, "CcK2r5kJRLylcc9T7uwO+4oRMRYEGAAp+qkzQPOUjeHDYUiZ");
        playerView.getPlayerAsync(this, this, jwPlayer -> player = jwPlayer);
        //----------------------------------------------------

    }

    private void setUiData() {
        if (!cbEnableDrm.isChecked()) {
            streamURL.setText(intro);
            authURL.setText("");

            authURL.setVisibility(View.GONE);
        } else {
            streamURL.setText(fileUrl);
            authURL.setText(drmAuth);

            authURL.setVisibility(View.VISIBLE);
        }
    }

    private void playVideo() {

        String file = streamURL.getText().toString();
        String auth = authURL.getText().toString();

        if (file.isEmpty() || !Util.isValidURL(file)) {
            showToast("Stream URL must be a valid URL");
            return;
        }

        if (cbEnableDrm.isChecked()) {
            if (auth.isEmpty() || !Util.isValidURL(auth)) {
                showToast("Authentication URL must be a valid URL");
                return;
            }
        }


        //-----------------------------------------------------------------

        List<PlaylistItem> playlist = new ArrayList<>();

        if (cbEnableDrm.isChecked()) {
            playlist.add(new PlaylistItem.Builder()
                    .file(file)
                    .mediaDrmCallback(new WidevineMediaDrmCallback(auth))
                    .build());
        } else {
            playlist.add(new PlaylistItem.Builder()
                    .file(file)
                    .build());
        }


        UiConfig uiConfig = new UiConfig.Builder()
                .displayAllControls()
                .hide(UiGroup.SETTINGS_MENU)
                .hide(UiGroup.OVERLAY)
                .hide(UiGroup.NEXT_UP)
                .build();

        player.setup(new PlayerConfig.Builder()
                .playlist(playlist)
                .uiConfig(uiConfig)
                .build());

        player.play();

        playerView.setVisibility(View.VISIBLE);
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    //================================================
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (player == null) {
            return;
        }

        if (!player.isInPictureInPictureMode()) {
            final boolean isFullscreen = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
            player.setFullscreen(isFullscreen, true);
        }
    }

}