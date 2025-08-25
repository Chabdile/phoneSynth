
#ifndef PHONESYNTH_CHORUS_H
#define PHONESYNTH_CHORUS_H

#include "EffectUnit.h"
#include "../components/LFO.h"
#include <vector>

class Chorus : public EffectUnit {
public:
    Chorus();
    void setRate(float rate);
    void setDepth(float depth); // 0.0 to 1.0
    void setMix(float mix);     // 0.0 to 1.0

    float processSample(float input) override;

private:
    LFO mLfo;
    std::vector<float> mDelayBuffer;
    int mWritePos = 0;
    float mDepth = 0.5f;
    float mMix = 0.5f;
    const int MAX_DELAY_SAMPLES = 2048; // Corresponds to ~42ms at 48kHz
};

#endif //PHONESYNTH_CHORUS_H
