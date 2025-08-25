
#include "Morph.h"
#include "utils.h"
#include <utility>

void MorphMixer::setUnits(std::shared_ptr<MorphUnit> u1, std::shared_ptr<MorphUnit> u2) {
    U1 = u1;
    U2 = u2;
}

void MorphMixer::setMix(float m) {
    mix.store(clamp(m, 0.0f, 1.0f));
}

float MorphMixer::getMix() const {
    return mix.load();
}

float MorphMixer::render(float phase) {
    float a = U1 ? U1->render(phase) : 0.0f;
    float b = U2 ? U2->render(phase) : 0.0f;
    float m = mix.load();
    return (1.0f - m) * a + m * b;
}

SimpleWaveMorph::SimpleWaveMorph(std::shared_ptr<WaveTable> table) : waveTable(std::move(table)) {}

float SimpleWaveMorph::render(float phase) {
    if (waveTable) {
        return waveTable->readPhase(phase);
    }
    return 0.0f;
}
