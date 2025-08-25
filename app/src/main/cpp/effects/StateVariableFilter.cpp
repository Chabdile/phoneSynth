
#include "StateVariableFilter.h"
#include <cmath>
#include "../components/utils.h"

void StateVariableFilter::setType(FilterType type) {
    mType = type;
}

void StateVariableFilter::setCutoff(float cutoff) {
    mCutoff = clamp(cutoff, 0.0f, 1.0f);
    calculateCoefficients();
}

void StateVariableFilter::setResonance(float resonance) {
    mResonance = clamp(resonance, 0.0f, 1.0f);
    calculateCoefficients();
}

void StateVariableFilter::calculateCoefficients() {
    // A simple and effective mapping from normalized cutoff to filter coefficient 'g'
    g = std::tan(kPi * mCutoff * 0.5f);
    // Resonance to damping factor 'R'
    R = 1.0f / (2.0f * (1.0f + mResonance * 4.0f));
}

float StateVariableFilter::processSample(float input) {
    if (!mEnabled) {
        return input;
    }
    // SVF processing algorithm
    h = (input - (2.0f * R + g) * b - l) / (1.0f + 2.0f * R * g + g * g);
    b = g * h + b;
    l = g * b + l;

    switch (mType) {
        case FilterType::LowPass:
            return l;
        case FilterType::HighPass:
            return h;
        case FilterType::BandPass:
            return b;
    }
    return l; // Default to low-pass
}
