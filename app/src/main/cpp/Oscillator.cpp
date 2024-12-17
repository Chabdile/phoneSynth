#include <oboe/Oboe.h>

//    std::string waveform[4] =  {
//        "Sine",
//        "Triangle",
//        "Sawtooth",
//        "Square"
//    };

class Oscillator {
public:
//    void setFrequency(float frequency);
//    void setAmplitude(float amplitude);
//    void setWaveform(int num);
//    void setSampleRate(float sampleRate);
//    void setPulseWidth(float pulseWidth);
//    float render();

    void setFrequency(float frequency) {
        mFrequency = frequency;
        mPhaseIncrement = (float)(kTwoPi * mFrequency / mSampleRate);
    }

    void setAmplitude(float amplitude) {
        mAmplitude = amplitude;
    }

    void setWaveform(int num) {
        mWaveform = num;
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
            case 0:
                //Sine
                sample = (float)(mAmplitude * std::sin(mPhase));
                break;
            case 1:
                //Triangle
                sample = (float)(mAmplitude * (2.0f * std::fabs(2.0f * (mPhase / kTwoPi) - 1.0f) - 1.0f));
                break;
            case 2:
                //Sawtooth
                sample = (float)(mAmplitude * (2.0f * (mPhase / kTwoPi) - 1.0f));
                break;
            case 3:
                //Square
                sample = (float)(mAmplitude * (mPhase < (mPulseWidth * M_PI) ? 1.0f : -1.0f));
                break;
        }

        mPhase += mPhaseIncrement;
        if (mPhase >= kTwoPi) mPhase -= (float)kTwoPi;
        return sample;
    }

private:
    //default values
    float mSampleRate = 48000.0;
    float mFrequency = 440.0;
    float mPhase = 0.0;
    float mPhaseIncrement = 0.0;
    float mAmplitude = 0.5f;
    float mPulseWidth = 0.5f; // パルス波のデフォルト幅
    int mWaveform = 1;  //Tri
//    std::string currentWaveform = waveform[mWaveform];
    const double kTwoPi = 2.0 * M_PI;
};