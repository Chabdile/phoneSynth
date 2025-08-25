
#ifndef PHONESYNTH_OSCILLATOR_H
#define PHONESYNTH_OSCILLATOR_H

#include <memory>
#include "Morph.h"

class Oscillator {
public:
    void setSampleRate(float r);
    void setFrequency(float f);
    void setAmplitude(float a);
    void setSource(std::shared_ptr<MorphUnit> m);

    float render();
private:
    void updateInc();
    float sr=48000.0f, freq=220.0f, amp=0.2f;
    float phase=0.0f, inc=0.0f;
    std::shared_ptr<MorphUnit> source;
};

#endif //PHONESYNTH_OSCILLATOR_H
