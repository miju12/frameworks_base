/*
 * Copyright 2023 The Android Open Source Project
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
#include <jni.h>

#define LOG_TAG "ChoreographerTestsJniOnLoad"

extern int register_android_android_view_tests_ChoreographerNativeTest(JNIEnv* env);

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void*) {
    JNIEnv* env = NULL;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    if (register_android_android_view_tests_ChoreographerNativeTest(env)) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_6;
}