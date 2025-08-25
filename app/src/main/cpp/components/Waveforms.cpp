
#include "Waveforms.h"
#include "utils.h"
#include <cmath>

namespace Waveforms {

    std::shared_ptr<WaveTable> createSineTable(int size) {
        auto table = std::make_shared<WaveTable>(size);
        for (int i = 0; i < size; ++i) {
            table->samples[i] = std::sin(kTwoPi * (float)i / (float)size);
        }
        return table;
    }

    std::shared_ptr<WaveTable> createTriangleTable(int size) {
        auto table = std::make_shared<WaveTable>(size);
        for (int i = 0; i < size; ++i) {
            float t = (float)i / (float)size;
            table->samples[i] = 2.0f * std::abs(2.0f * t - 1.0f) - 1.0f;
        }
        return table;
    }

    std::shared_ptr<WaveTable> createSawtoothTable(int size) {
        auto table = std::make_shared<WaveTable>(size);
        for (int i = 0; i < size; ++i) {
            float t = (float)i / (float)size;
            table->samples[i] = 2.0f * t - 1.0f;
        }
        return table;
    }

    std::shared_ptr<WaveTable> createSquareTable(int size) {
        auto table = std::make_shared<WaveTable>(size);
        for (int i = 0; i < size; ++i) {
            float t = (float)i / (float)size;
            table->samples[i] = (t < 0.5f) ? 1.0f : -1.0f;
        }
        return table;
    }

}
