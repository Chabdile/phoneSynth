#ifndef PHONESYNTH_EFFECTUNIT_H
#define PHONESYNTH_EFFECTUNIT_H

class EffectUnit {
public:
    EffectUnit() : mEnabled(false) {}
    virtual ~EffectUnit() = default;

    virtual float processSample(float input) = 0;

    void setEnabled(bool enabled) { mEnabled = enabled; }
    bool isEnabled() const { return mEnabled; }

protected:
    bool mEnabled;
};

#endif //PHONESYNTH_EFFECTUNIT_H