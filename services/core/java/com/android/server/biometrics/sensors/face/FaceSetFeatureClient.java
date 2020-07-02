/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.biometrics.sensors.face;

import android.annotation.NonNull;
import android.content.Context;
import android.hardware.biometrics.BiometricsProtoEnums;
import android.hardware.biometrics.face.V1_0.IBiometricsFace;
import android.hardware.biometrics.face.V1_0.Status;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;

import com.android.server.biometrics.sensors.ClientMonitor;
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;

import java.util.ArrayList;

/**
 * Face-specific setFeature client supporting the {@link android.hardware.biometrics.face.V1_0}
 * and {@link android.hardware.biometrics.face.V1_1} HIDL interfaces.
 */
public class FaceSetFeatureClient extends ClientMonitor {

    private static final String TAG = "FaceSetFeatureClient";

    private final IBiometricsFace mDaemon;
    private final int mFeature;
    private final boolean mEnabled;
    private final ArrayList<Byte> mHardwareAuthToken;
    private final int mFaceId;

    FaceSetFeatureClient(@NonNull FinishCallback finishCallback, @NonNull Context context,
            @NonNull IBiometricsFace daemon, @NonNull IBinder token,
            @NonNull ClientMonitorCallbackConverter listener, int userId,
            @NonNull String owner, int sensorId, int feature, boolean enabled,
            byte[] hardwareAuthToken, int faceId) {
        super(finishCallback, context, token, listener, userId, false /* restricted */,
                owner, 0 /* cookie */, sensorId, BiometricsProtoEnums.MODALITY_UNKNOWN,
                BiometricsProtoEnums.ACTION_UNKNOWN, BiometricsProtoEnums.CLIENT_UNKNOWN);
        mDaemon = daemon;
        mFeature = feature;
        mEnabled = enabled;
        mFaceId = faceId;

        mHardwareAuthToken = new ArrayList<>();
        for (byte b : hardwareAuthToken) {
            mHardwareAuthToken.add(b);
        }
    }

    @Override
    public void start() {
        startHalOperation();
        mFinishCallback.onClientFinished(this);
    }

    @Override
    protected void startHalOperation() {
        try {
            final int result = mDaemon.setFeature(mFeature, mEnabled, mHardwareAuthToken, mFaceId);
            getListener().onFeatureSet(result == Status.OK, mFeature);
        } catch (RemoteException e) {
            Slog.e(TAG, "Unable to set feature: " + mFeature + " to enabled: " + mEnabled, e);
        }
    }

    @Override
    protected void stopHalOperation() {
        // Not supported for SetFeature
    }
}
