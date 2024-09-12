#include <oboe/Oboe.h>
#include <cmath>

constexpr double kPi = M_PI;
constexpr double kTwoPi = kPi * 2;

class Oscillator {
public:
    enum Waveform {
        Sine,
        Triangle,
        Sawtooth,
        Square
    };

    void setFrequency(float frequency) {
        mFrequency = frequency;
        mPhaseIncrement = (float)(kTwoPi * mFrequency / mSampleRate);
    }

    void setAmplitude(float amplitude) {
        mAmplitude = amplitude;
    }

    void setWaveform(Waveform waveform) {
        mWaveform = waveform;
    }

    void setSampleRate(float sampleRate) {
        mSampleRate = sampleRate;
        setFrequency(mFrequency);
    }

    void setPulseWidth(float pulseWidth) {
        mPulseWidth = pulseWidth;
    }

    float render() {
        float sample = 0.0f;
        switch (mWaveform) {
            case Sine:
                sample = (float)(mAmplitude * std::sin(mPhase));
                break;
            case Triangle:
                sample = (float)(mAmplitude * (2.0f * std::fabs(2.0f * (mPhase / kTwoPi) - 1.0f) - 1.0f));
                break;
            case Sawtooth:
                sample = (float)(mAmplitude * (2.0f * (mPhase / kTwoPi) - 1.0f));
                break;
            case Square:
                sample = (float)(mAmplitude * (mPhase < (mPulseWidth * kTwoPi) ? 1.0f : -1.0f));
                break;
        }

        mPhase += mPhaseIncrement;
        if (mPhase >= kTwoPi) mPhase -= (float)kTwoPi;
        return sample;
    }

private:
    float mSampleRate = 48000.0;
    float mFrequency = 440.0;
    float mPhase = 0.0;
    float mPhaseIncrement = 0.0;
    float mAmplitude = 0.5f;
    float mPulseWidth = 0.5; // パルス波のデフォルト幅
    Waveform mWaveform = Triangle;
    const double kTwoPi = 2.0 * M_PI;
};