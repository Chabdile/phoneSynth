
#ifndef PHONESYNTH_WAVEFORMS_H
#define PHONESYNTH_WAVEFORMS_H

#include <memory>
#include "Wavetable.h"

namespace Waveforms {
    std::shared_ptr<WaveTable> createSineTable(int size);
    std::shared_ptr<WaveTable> createTriangleTable(int size);
    std::shared_ptr<WaveTable> createSawtoothTable(int size);
    std::shared_ptr<WaveTable> createSquareTable(int size);
}

#endif //PHONESYNTH_WAVEFORMS_H
