
#include "Wavetable.h"
#include "utils.h"
#include <cmath>

WaveTable::WaveTable(size_t tableSize) {
    samples.resize(tableSize, 0.0f);
}

size_t WaveTable::size() const {
    return samples.size();
}

float WaveTable::readPhase(float phase) const {
    float pos = phase * (float)samples.size() / kTwoPi; // 0..N
    int i0 = (int)std::floor(pos);
    float t = pos - (float)i0;
    int i1 = (i0 + 1) % (int)samples.size();
    i0 = i0 % (int)samples.size();
    if (i0 < 0) i0 += (int)samples.size();
    return samples[i0] + (samples[i1] - samples[i0]) * t;
}
