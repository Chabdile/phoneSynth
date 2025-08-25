
#include "compressor.h"
#include <cmath>

void Compressor::setThreshold(float thresholdDb) {
    mThreshold = std::pow(10.0f, thresholdDb / 20.0f);
}

void Compressor::setRatio(float ratio) {
    mRatio = ratio > 1.0f ? ratio : 1.0f;
}

void Compressor::setAttack(float attackSeconds) {
    // A simple formula to convert time to a coefficient
    // This is not perfect, but good enough for now.
    mAttack = 1.0f - std::exp(-2.2f / (48000 * attackSeconds));
}

void Compressor::setRelease(float releaseSeconds) {
    mRelease = 1.0f - std::exp(-2.2f / (48000 * releaseSeconds));
}

void Compressor::setMakeupGain(float gain) {
    mMakeupGain = gain;
}

float Compressor::processSample(float input) {
    if (!mEnabled) {
        return input;
    }
    float inputLevel = std::abs(input);

    if (inputLevel > mEnvelope) {
        mEnvelope += (inputLevel - mEnvelope) * mAttack;
    } else {
        mEnvelope += (inputLevel - mEnvelope) * mRelease;
    }

    float gainReduction = 1.0f;
    if (mEnvelope > mThreshold) {
        gainReduction = 1.0f / (1.0f + (mEnvelope - mThreshold) * (mRatio - 1.0f));
    }

    float output = input * gainReduction * mMakeupGain;
    return output;
}
