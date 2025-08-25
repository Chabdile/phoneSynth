
#ifndef PHONESYNTH_COMPRESSOR_H
#define PHONESYNTH_COMPRESSOR_H

#include "EffectUnit.h"

class Compressor : public EffectUnit {
public:
    void setThreshold(float thresholdDb);
    void setRatio(float ratio);
    void setAttack(float attackSeconds);
    void setRelease(float releaseSeconds);
    void setMakeupGain(float gain);

    float processSample(float input) override;

private:
    float mThreshold = 0.0f;    // Threshold in linear gain
    float mRatio = 1.0f;
    float mAttack = 0.01f;   // Attack coefficient
    float mRelease = 0.1f;  // Release coefficient
    float mMakeupGain = 1.0f;

    float mEnvelope = 0.0f;
};

#endif //PHONESYNTH_COMPRESSOR_H
