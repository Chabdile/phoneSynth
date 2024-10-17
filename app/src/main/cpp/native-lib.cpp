#include <jni.h>
#include "Oscillator.cpp"

Oscillator osc;

class AudioCallback : public oboe::AudioStreamCallback {
public:
    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *stream, void *audioData, int32_t numFrames) override {
        float *floatData = static_cast<float *>(audioData);
        for (int i = 0; i < numFrames; ++i) {
            floatData[i] = osc.render();
        }
        return oboe::DataCallbackResult::Continue;
    }
};

oboe::AudioStream *stream;
AudioCallback audioCallback;

//---------------------------------------------------------------------

extern "C" {
    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_MainActivity_startStream(JNIEnv *env, jobject obj) {
        oboe::AudioStreamBuilder builder;
        builder.setCallback(&audioCallback)
        ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
        ->setSharingMode(oboe::SharingMode::Exclusive)
        ->setFormat(oboe::AudioFormat::Float)
        ->setChannelCount(oboe::ChannelCount::Mono)
        ->setSampleRate(48000);

        oboe::Result result = builder.openStream(&stream);
        if (result == oboe::Result::OK) {
            stream->start();
        }
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_MainActivity_stopStream(JNIEnv *env, jobject obj) {
        if (stream != nullptr) {
            stream->stop();
            stream->close();
            stream = nullptr;
        }
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_MainActivity_setFrequency(JNIEnv *env, jobject obj, jfloat frequency) {
        osc.setFrequency(frequency);
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_MainActivity_setAmplitude(JNIEnv *env, jobject obj, jfloat amplitude) {
        osc.setAmplitude(amplitude);
    }

    JNIIMPORT void JNICALL
    Java_com_example_phonesynth_MainActivity_setWaveform(JNIEnv *env, jobject obj, jint type) {
        // Oscillatorに対応するchar_16tに
        osc.setWaveform(type);
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_MainActivity_setSampleRate(JNIEnv *env, jobject obj, jfloat sampleRate) {
        osc.setSampleRate(sampleRate);
    }

    JNIIMPORT void JNICALL
    Java_com_example_phonesynth_MainActivity_setPulseWidth(JNIEnv *env, jobject obj, jfloat pulseWidth) {
        osc.setPulseWidth(pulseWidth);
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_MainActivity_oscDestroy(JNIEnv *env, jobject obj, jlong instance) {
        delete reinterpret_cast<Oscillator*>(instance);
    }

}
