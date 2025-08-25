
#ifndef PHONESYNTH_UTILS_H
#define PHONESYNTH_UTILS_H

#include <cmath>
#include <algorithm>

static constexpr float kPi = M_PI;
static constexpr float kTwoPi = kPi * 2;

static constexpr int kDefaultTableSize = 1024;
static constexpr float kDefaultSampleRate = 48000.0f;

template<typename T>
static inline T clamp(T v, T lo, T hi){ return std::max(lo, std::min(hi, v)); }

static inline float fracf(float x){ return x - std::floor(x); }

#endif //PHONESYNTH_UTILS_H
