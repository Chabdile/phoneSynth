
#ifndef PHONESYNTH_DISTORTION_H
#define PHONESYNTH_DISTORTION_H

#include <string>
#include "EffectUnit.h"

enum class DistortionType { None, Hard, Soft };

class Distortion : public EffectUnit {
public:
    void setType(DistortionType type);
    void setDrive(float drive);
    float processSample(float input) override;

private:
    DistortionType mType = DistortionType::None;
    float mDrive = 1.0f;
};

#endif //PHONESYNTH_DISTORTION_H
