
#include "Oscillator.h"
#include "utils.h"

void Oscillator::setSampleRate(float r) {
    sr = r;
    updateInc();
}

void Oscillator::setFrequency(float f) {
    freq = f;
    updateInc();
}

void Oscillator::setAmplitude(float a) {
    amp = a;
}

void Oscillator::setSource(std::shared_ptr<MorphUnit> m) {
    source = m;
}

float Oscillator::render() {
    float v = source ? source->render(phase) : 0.0f;
    float out = amp * v;
    phase += inc;
    if (phase >= kTwoPi) phase -= kTwoPi;
    return out;
}

void Oscillator::updateInc() {
    inc = kTwoPi * freq / sr;
}
