
#ifndef PHONESYNTH_LFO_H
#define PHONESYNTH_LFO_H

#include "utils.h"
#include "Wavetable.h"

class LFO {
public:
    void setup(float rateHz, float sampleRate);
    float next();
    float getRate() const;
    void setWaveTable(std::shared_ptr<WaveTable> table);
private:
    float sr=48000.0f, inc=0.0f, phase=0.0f, rate=1.0f;
    std::shared_ptr<WaveTable> mWaveTable;
};

#endif //PHONESYNTH_LFO_H
