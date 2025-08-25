#include "distortion.h"
#include <cmath>

void Distortion::setType(DistortionType type) {
    mType = type;
}

void Distortion::setDrive(float drive) {
    mDrive = drive > 0 ? drive : 1.0f;
}

float Distortion::processSample(float input) {
    if (!mEnabled || mType == DistortionType::None) {
        return input;
    }

    float x = input * mDrive;

    if (mType == DistortionType::Hard) {
        float threshold = 1.0f;
        if (x > threshold) return threshold;
        if (x < -threshold) return -threshold;
        return x;
    }
    if (mType == DistortionType::Soft) {
        return x / (1.0f + std::abs(x));
    }
    return input;
}