
#ifndef PHONESYNTH_STATEVARIABLEFILTER_H
#define PHONESYNTH_STATEVARIABLEFILTER_H

#include "EffectUnit.h"

enum class FilterType { LowPass, HighPass, BandPass };

class StateVariableFilter : public EffectUnit {
public:
    void setType(FilterType type);
    void setCutoff(float cutoff); // 0.0 to 1.0 (normalized)
    void setResonance(float resonance); // 0.0 to 1.0

    float processSample(float input) override;

private:
    void calculateCoefficients();

    FilterType mType = FilterType::LowPass;
    float mCutoff = 0.5f;
    float mResonance = 0.0f;

    // Coefficients
    float g; // gain
    float R; // resonance
    float h; // high-pass output
    float b; // band-pass output
    float l; // low-pass output
};

#endif //PHONESYNTH_STATEVARIABLEFILTER_H
