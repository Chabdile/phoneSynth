
#ifndef PHONESYNTH_MORPH_H
#define PHONESYNTH_MORPH_H

#include <atomic>
#include <memory>
#include "Wavetable.h"
#include "utils.h"

class MorphUnit {
public:
    virtual ~MorphUnit() = default;
    virtual void setMorph(float m){ morph.store(clamp(m,0.0f,1.0f)); requestRefresh(); }
    virtual float getMorph() const { return morph.load(); }
    virtual void requestRefresh(){}
    virtual float render(float phase){ (void)phase; return 0.0f; }
protected:
    std::atomic<float> morph{0.0f};
};

class MorphMixer : public MorphUnit {
public:
    void setUnits(std::shared_ptr<MorphUnit> u1, std::shared_ptr<MorphUnit> u2);
    void setMix(float m);
    float getMix() const;

    std::shared_ptr<MorphUnit> getUnit1() const { return U1; }
    std::shared_ptr<MorphUnit> getUnit2() const { return U2; }

    float render(float phase) override;
private:
    std::shared_ptr<MorphUnit> U1, U2;
    std::atomic<float> mix{0.5f};
};

// A simple MorphUnit that reads from a single WaveTable
class SimpleWaveMorph : public MorphUnit {
public:
    explicit SimpleWaveMorph(std::shared_ptr<WaveTable> table);

    float render(float phase) override;

private:
    std::shared_ptr<WaveTable> waveTable;
};

#endif //PHONESYNTH_MORPH_H
