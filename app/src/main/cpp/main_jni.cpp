#include <android/log.h>
#include <jni.h>

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "lsfg-mali", __VA_ARGS__)

extern "C" {
    JNIEXPORT jstring JNICALL
    Java_com_lsfg_mali_MainActivity_getVersionString(JNIEnv* env, jobject obj) {
        LOGD("getVersionString called");
        return env->NewStringUTF("1.0 Mali G57 GPU Blender");
    }
}
