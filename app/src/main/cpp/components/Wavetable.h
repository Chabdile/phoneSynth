
#ifndef PHONESYNTH_WAVETABLE_H
#define PHONESYNTH_WAVETABLE_H

#include <vector>

struct WaveTable {
    std::vector<float> samples;

    explicit WaveTable(size_t tableSize = 1024);

    size_t size() const;

    float readPhase(float phase) const;
};

#endif //PHONESYNTH_WAVETABLE_H
