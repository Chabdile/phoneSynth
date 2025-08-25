#include "LFO.h"
#include "utils.h"
#include "Wavetable.h" // Include Wavetable.h
#include <cmath>

void LFO::setup(float rateHz, float sampleRate) {
    sr = sampleRate;
    rate = rateHz;
    inc = kTwoPi * rate / sr;
}

float LFO::next() {
    float v = 0.0f;
    if (mWaveTable) {
        // Read from the wavetable and scale from -1..1 to 0..1
        v = 0.5f + 0.5f * mWaveTable->readPhase(phase);
    } else {
        // Fallback to std::sin if no wavetable is set
        v = 0.5f + 0.5f * std::sin(phase);
    }

    phase += inc;
    if (phase >= kTwoPi) phase -= kTwoPi;
    return v;
}

float LFO::getRate() const {
    return rate;
}

void LFO::setWaveTable(std::shared_ptr<WaveTable> table) {
    mWaveTable = table;
}