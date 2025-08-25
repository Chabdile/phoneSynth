#include <jni.h>
#include <memory>
#include <cmath>
#include <atomic>
#include <vector>
#include "oboe/Oboe.h"
#include "components/Oscillator.h"
#include "components/Wavetable.h"
#include "components/Morph.h"
#include "components/LFO.h"
#include "components/utils.h"
#include "components/Waveforms.h"
#include "effects/distortion.h"
#include "effects/compressor.h"
#include "effects/StateVariableFilter.h"
#include "effects/Chorus.h"
#include "effects/Reverb.h"
#include "effects/EffectUnit.h"

static Oscillator osc;
static LFO lfo;
static std::shared_ptr<MorphMixer> morpher;
static std::atomic<bool> lfoEnabled{false};

static Distortion distortion;
static StateVariableFilter filter;
static Chorus chorus;
static Reverb reverb;
static Compressor compressor;

// Dynamic effect chain
static std::vector<EffectUnit*> activeEffects;

static std::shared_ptr<WaveTable> sineTable;
static std::shared_ptr<WaveTable> triangleTable;
static std::shared_ptr<WaveTable> sawtoothTable;
static std::shared_ptr<WaveTable> squareTable;

// Helper to get a table by index
std::shared_ptr<WaveTable> getTableByIndex(int index) {
    switch (index) {
        case 0: return sineTable;
        case 1: return triangleTable;
        case 2: return sawtoothTable;
        case 3: return squareTable;
        default: return sineTable;
    }
}

// Function to rebuild the active effects chain
void rebuildActiveEffectsChain() {
    activeEffects.clear();
    // Add effects in desired order
    if (distortion.isEnabled()) activeEffects.push_back(&distortion);
    if (filter.isEnabled()) activeEffects.push_back(&filter);
    if (chorus.isEnabled()) activeEffects.push_back(&chorus);
    if (reverb.isEnabled()) activeEffects.push_back(&reverb);
    if (compressor.isEnabled()) activeEffects.push_back(&compressor);
}

void initialize_components(float sampleRate) {
    sineTable = Waveforms::createSineTable(kDefaultTableSize);
    triangleTable = Waveforms::createTriangleTable(kDefaultTableSize);
    sawtoothTable = Waveforms::createSawtoothTable(kDefaultTableSize);
    squareTable = Waveforms::createSquareTable(kDefaultTableSize);

    auto waveA = std::make_shared<SimpleWaveMorph>(triangleTable);
    auto waveB = std::make_shared<SimpleWaveMorph>(sawtoothTable);

    morpher = std::make_shared<MorphMixer>();
    morpher->setUnits(waveA, waveB);

    lfo.setup(1.0f, sampleRate);
//    lfo.setWaveTable(sineTable);

    osc.setSource(morpher);
    osc.setSampleRate(sampleRate);

    // Initial build of the effect chain (all disabled by default)
    rebuildActiveEffectsChain();
}

class AudioCallback : public oboe::AudioStreamCallback {
public:
    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *stream, void *audioData, int32_t numFrames) override {
        auto *floatData = static_cast<float *>(audioData);
        for (int i = 0; i < numFrames; ++i) {
            if (lfoEnabled.load()) {
                float lfoValue = lfo.next();
                morpher->setMix(lfoValue);
            }
            float signal = osc.render();

            // Process through active effects
            for (EffectUnit* effect : activeEffects) {
                signal = effect->processSample(signal);
            }
            floatData[i] = signal;
        }
        return oboe::DataCallbackResult::Continue;
    }
};

oboe::AudioStream *stream;
AudioCallback audioCallback;

//---------------------------------------------------------------------

extern "C" {
    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_startStream(JNIEnv *env, jobject obj) {
        initialize_components(kDefaultSampleRate);

        oboe::AudioStreamBuilder builder;
        builder.setCallback(&audioCallback)
        ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
        ->setSharingMode(oboe::SharingMode::Exclusive)
        ->setFormat(oboe::AudioFormat::Float)
        ->setChannelCount(oboe::ChannelCount::Mono)
        ->setSampleRate(kDefaultSampleRate);

        oboe::Result result = builder.openStream(&stream);
        if (result == oboe::Result::OK) {
            stream->start();
        }
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_stopStream(JNIEnv *env, jobject obj) {
        if (stream != nullptr) {
            stream->stop();
            stream->close();
            stream = nullptr;
        }
    }

    // Oscillator controls
    /**
     * @brief Sets the frequency of the main oscillator.
     * @param env JNI environment pointer.
     * @param obj JNI object (this).
     * @param frequency The desired frequency in Hz.
     */
    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setFrequency(JNIEnv *env, jobject obj, jfloat frequency) {
        osc.setFrequency(frequency);
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setAmplitude(JNIEnv *env, jobject obj, jfloat amplitude) {
        osc.setAmplitude(amplitude);
    }

    // LFO and Morph controls
    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setLfoRate(JNIEnv *env, jobject obj, jfloat rate) {
        lfo.setup(rate, stream->getSampleRate());
        lfoEnabled.store(false);
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setMorphMix(JNIEnv *env, jobject obj, jfloat mix) {
        lfoEnabled.store(false);
        morpher->setMix(mix);
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setWaveform(JNIEnv *env, jobject obj, jint type) {
        auto newWaveA = std::make_shared<SimpleWaveMorph>(getTableByIndex(type));
        morpher->setUnits(newWaveA, newWaveA);
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setWaveformA(JNIEnv *env, jobject obj, jint type) {
        auto newWaveA = std::make_shared<SimpleWaveMorph>(getTableByIndex(type));
        morpher->setUnits(newWaveA, morpher->getUnit2());
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setWaveformB(JNIEnv *env, jobject obj, jint type) {
        auto newWaveB = std::make_shared<SimpleWaveMorph>(getTableByIndex(type));
        morpher->setUnits(morpher->getUnit1(), newWaveB);
    }

    // --- Effects Controls ---

    // Distortion
    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_enableDistortion(JNIEnv *env, jobject obj, jboolean enable) {
        distortion.setEnabled(enable);
        rebuildActiveEffectsChain();
    }
    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setDistortionType(JNIEnv *env, jobject obj, jint type) {
        distortion.setEnabled(true);
        distortion.setType(static_cast<DistortionType>(type));
        rebuildActiveEffectsChain();
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setDistortionDrive(JNIEnv *env, jobject obj, jfloat drive) {
        distortion.setEnabled(true);
        distortion.setDrive(drive);
        rebuildActiveEffectsChain();
    }

    // Filter
    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_enableFilter(JNIEnv *env, jobject obj, jboolean enable) {
        filter.setEnabled(enable);
        rebuildActiveEffectsChain();
    }
    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setFilterType(JNIEnv *env, jobject obj, jint type) {
        filter.setEnabled(true);
        filter.setType(static_cast<FilterType>(type));
        rebuildActiveEffectsChain();
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setFilterCutoff(JNIEnv *env, jobject obj, jfloat cutoff) {
        filter.setEnabled(true);
        filter.setCutoff(cutoff);
        rebuildActiveEffectsChain();
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setFilterResonance(JNIEnv *env, jobject obj, jfloat resonance) {
        filter.setEnabled(true);
        filter.setResonance(resonance);
        rebuildActiveEffectsChain();
    }

    // Chorus
    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_enableChorus(JNIEnv *env, jobject obj, jboolean enable) {
        chorus.setEnabled(enable);
        rebuildActiveEffectsChain();
    }
    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setChorusRate(JNIEnv *env, jobject obj, jfloat rate) {
        chorus.setEnabled(true);
        chorus.setRate(rate);
        rebuildActiveEffectsChain();
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setChorusDepth(JNIEnv *env, jobject obj, jfloat depth) {
        chorus.setEnabled(true);
        chorus.setDepth(depth);
        rebuildActiveEffectsChain();
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setChorusMix(JNIEnv *env, jobject obj, jfloat mix) {
        chorus.setEnabled(true);
        chorus.setMix(mix);
        rebuildActiveEffectsChain();
    }

    // Reverb
    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_enableReverb(JNIEnv *env, jobject obj, jboolean enable) {
        reverb.setEnabled(enable);
        rebuildActiveEffectsChain();
    }
    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setReverbRoomSize(JNIEnv *env, jobject obj, jfloat roomSize) {
        reverb.setEnabled(true);
        reverb.setRoomSize(roomSize);
        rebuildActiveEffectsChain();
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setReverbDamping(JNIEnv *env, jobject obj, jfloat damping) {
        reverb.setEnabled(true);
        reverb.setDamping(damping);
        rebuildActiveEffectsChain();
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setReverbMix(JNIEnv *env, jobject obj, jfloat mix) {
        reverb.setEnabled(true);
        reverb.setMix(mix);
        rebuildActiveEffectsChain();
    }

    // Compressor
    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_enableCompressor(JNIEnv *env, jobject obj, jboolean enable) {
        compressor.setEnabled(enable);
        rebuildActiveEffectsChain();
    }
    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setCompressorThreshold(JNIEnv *env, jobject obj, jfloat threshold) {
        compressor.setEnabled(true);
        compressor.setThreshold(threshold);
        rebuildActiveEffectsChain();
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setCompressorRatio(JNIEnv *env, jobject obj, jfloat ratio) {
        compressor.setEnabled(true);
        compressor.setRatio(ratio);
        rebuildActiveEffectsChain();
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setCompressorAttack(JNIEnv *env, jobject obj, jfloat attack) {
        compressor.setEnabled(true);
        compressor.setAttack(attack);
        rebuildActiveEffectsChain();
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setCompressorRelease(JNIEnv *env, jobject obj, jfloat release) {
        compressor.setEnabled(true);
        compressor.setRelease(release);
        rebuildActiveEffectsChain();
    }

    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setCompressorMakeupGain(JNIEnv *env, jobject obj, jfloat gain) {
        compressor.setEnabled(true);
        compressor.setMakeupGain(gain);
        rebuildActiveEffectsChain();
    }


    JNIEXPORT void JNICALL
    Java_com_example_phonesynth_component_Oboe_setSampleRate(JNIEnv *env, jobject obj, jfloat sampleRate) {
        osc.setSampleRate(sampleRate);
        lfo.setup(lfo.getRate(), sampleRate);
    }

}