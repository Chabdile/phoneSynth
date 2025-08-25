
#ifndef PHONESYNTH_REVERB_H
#define PHONESYNTH_REVERB_H

#include "EffectUnit.h"
#include <vector>

// Simple delay line class
class DelayLine {
public:
    DelayLine() = default;
    void setSize(int size);
    void write(float value);
    float read(int delay) const;
private:
    std::vector<float> mBuffer;
    int mWritePos = 0;
};

// Simple Schroeder Reverb
class Reverb : public EffectUnit {
public:
    Reverb();
    void setRoomSize(float size); // 0.0 to 1.0
    void setDamping(float damping); // 0.0 to 1.0
    void setMix(float mix); // 0.0 to 1.0

    float processSample(float input) override;

private:
    void updateParameters();

    float mRoomSize = 0.5f;
    float mDamping = 0.5f;
    float mMix = 0.5f;

    // 4 parallel comb filters
    std::vector<DelayLine> mCombs;
    std::vector<float> mCombFeedbacks;
    std::vector<float> mCombDamping;

    // 2 series all-pass filters
    std::vector<DelayLine> mAllPasses;
    const float mAllPassFeedback = 0.5f;
    std::vector<float> mLastCombOut;
};

#endif //PHONESYNTH_REVERB_H
