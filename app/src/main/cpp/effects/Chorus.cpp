
#include "Chorus.h"
#include "../components/utils.h"

Chorus::Chorus() {
    mDelayBuffer.resize(MAX_DELAY_SAMPLES, 0.0f);
    mLfo.setup(1.0f, 48000); // Default rate
}

void Chorus::setRate(float rate) {
    mLfo.setup(rate, 48000); // Assuming fixed sample rate for now
}

void Chorus::setDepth(float depth) {
    mDepth = clamp(depth, 0.0f, 1.0f);
}

void Chorus::setMix(float mix) {
    mMix = clamp(mix, 0.0f, 1.0f);
}

float Chorus::processSample(float input) {
    if (!mEnabled) {
        return input;
    }
    // LFO generates modulation from 0 to 1
    float lfoValue = mLfo.next();

    // Calculate delay in samples, modulated by LFO
    // The delay will sweep between a min and max value
    float minDelay = 5.0f * 48.0f / 1000.0f; // 5ms
    float maxDelay = 35.0f * 48.0f / 1000.0f; // 35ms
    float sweepWidth = (maxDelay - minDelay) * mDepth;
    float delayInSamples = minDelay + sweepWidth * lfoValue;

    // Calculate read position with fractional part
    float readPos = mWritePos - delayInSamples;
    while (readPos < 0) {
        readPos += MAX_DELAY_SAMPLES;
    }

    // Linear interpolation for fractional delay
    int i0 = static_cast<int>(readPos);
    float frac = readPos - i0;
    int i1 = (i0 + 1) % MAX_DELAY_SAMPLES;

    float delayedSample = mDelayBuffer[i0] * (1.0f - frac) + mDelayBuffer[i1] * frac;

    // Store current sample in the buffer
    mDelayBuffer[mWritePos] = input;
    mWritePos = (mWritePos + 1) % MAX_DELAY_SAMPLES;

    // Mix original and delayed samples
    return input * (1.0f - mMix) + delayedSample * mMix;
}
