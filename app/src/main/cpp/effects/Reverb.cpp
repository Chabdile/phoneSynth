
#include "Reverb.h"
#include "../components/utils.h"

// --- DelayLine --- //
void DelayLine::setSize(int size) {
    mBuffer.assign(size, 0.0f);
}

void DelayLine::write(float value) {
    mBuffer[mWritePos] = value;
    mWritePos = (mWritePos + 1) % mBuffer.size();
}

float DelayLine::read(int delay) const {
    int readPos = mWritePos - delay;
    while (readPos < 0) {
        readPos += mBuffer.size();
    }
    return mBuffer[readPos % mBuffer.size()];
}

// --- Reverb --- //
// Prime numbers for delay lengths
const int comb_delays[] = {1687, 1601, 2053, 2251};
const int allpass_delays[] = {556, 441};

Reverb::Reverb() {
    mCombs.resize(4);
    mCombFeedbacks.resize(4);
    mCombDamping.resize(4);
    mAllPasses.resize(2);
    mLastCombOut.resize(4, 0.0f);

    for(int i=0; i<4; ++i) mCombs[i].setSize(comb_delays[i]);
    for(int i=0; i<2; ++i) mAllPasses[i].setSize(allpass_delays[i]);

    updateParameters();
}

void Reverb::setRoomSize(float size) {
    mRoomSize = clamp(size, 0.0f, 1.0f);
    updateParameters();
}

void Reverb::setDamping(float damping) {
    mDamping = clamp(damping, 0.0f, 1.0f);
    updateParameters();
}

void Reverb::setMix(float mix) {
    mMix = clamp(mix, 0.0f, 1.0f);
}

void Reverb::updateParameters() {
    for(int i=0; i<4; ++i) {
        mCombFeedbacks[i] = 0.8f + mRoomSize * 0.18f; // feedback based on room size
        mCombDamping[i] = mDamping;
    }
}

float Reverb::processSample(float input) {
    if (!mEnabled) {
        return input;
    }
    float output = 0.0f;

    // Parallel comb filters
    for (int i = 0; i < 4; ++i) {
        float comb_out = mCombs[i].read(comb_delays[i] - 1);
        output += comb_out;
        float damped_out = comb_out * (1.0f - mCombDamping[i]) + mLastCombOut[i] * mCombDamping[i];
        mLastCombOut[i] = damped_out;
        mCombs[i].write(input + damped_out * mCombFeedbacks[i]);
    }

    // Series all-pass filters
    for (int i = 0; i < 2; ++i) {
        float allpass_out = mAllPasses[i].read(allpass_delays[i] - 1);
        float allpass_in = output;
        output = allpass_out - allpass_in;
        mAllPasses[i].write(allpass_in + output * mAllPassFeedback);
    }

    return input * (1.0f - mMix) + output * mMix;
}

// Need to add mLastCombOut member to the header
