package com.skh.jwplayerdemo;

import android.annotation.TargetApi;
import android.os.Parcel;
import android.text.TextUtils;

import com.google.android.exoplayer2.drm.ExoMediaDrm;
import com.jwplayer.pub.api.media.drm.MediaDrmCallback;

import java.io.IOException;
import java.util.UUID;

@TargetApi(18)
public class WidevineMediaDrmCallback implements MediaDrmCallback {

    private static final String WIDEVINE_GTS_DEFAULT_BASE_URI =
            "https://proxy.uat.widevine.com/proxy";

    private final String defaultUri;

    public WidevineMediaDrmCallback(String contentId, String provider) {
        String params = "?video_id=" + contentId + "&provider=" + provider;
        defaultUri = WIDEVINE_GTS_DEFAULT_BASE_URI + params;
    }

    public WidevineMediaDrmCallback(String auth) {
        defaultUri = auth;
    }

    protected WidevineMediaDrmCallback(Parcel in) {
        defaultUri = in.readString();
    }

    public static final Creator<WidevineMediaDrmCallback> CREATOR = new Creator<WidevineMediaDrmCallback>() {
        @Override
        public WidevineMediaDrmCallback createFromParcel(Parcel in) {
            return new WidevineMediaDrmCallback(in);
        }

        @Override
        public WidevineMediaDrmCallback[] newArray(int size) {
            return new WidevineMediaDrmCallback[size];
        }
    };

    @Override
    public byte[] executeProvisionRequest(UUID uuid, ExoMediaDrm.ProvisionRequest request) throws IOException {
        String url = request.getDefaultUrl() + "&signedRequest=" + new String(request.getData());
        return Util.executePost(url, null, null);
    }

    @Override
    public byte[] executeKeyRequest(UUID uuid, ExoMediaDrm.KeyRequest request) throws IOException {
        String url = request.getLicenseServerUrl();
        if (TextUtils.isEmpty(url)) {
            url = defaultUri;
        }
        return Util.executePost(url, request.getData(), null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(defaultUri);
    }
}