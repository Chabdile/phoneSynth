#include <cmath>

class Compressor {
public:
    void setThreshold(float threshold) {
        mThreshold = threshold;
    }

    void setRatio(float ratio) {
        mRatio = ratio;
    }

    void setAttackTime(float attack) {
        mAttackTime = attack;
    }

    void setReleaseTime(float release) {
        mReleaseTime = release;
    }

    void setMakeupGain(float gain) {
        mMakeupGain = gain;
    }

    float processSample(float inputSample) {
        // 入力サンプルの絶対値を取得
        float inputLevel = std::fabs(inputSample);

        // スレッショルドをdBに変換
        float thresholdLinear = std::pow(10.0f, mThreshold / 20.0f);

        // エンベロープの更新
        if (inputLevel > mEnvelope) {
            mEnvelope += (inputLevel - mEnvelope) * mAttackTime;
        } else {
            mEnvelope += (inputLevel - mEnvelope) * mReleaseTime;
        }

        // 圧縮を適用
        if (mEnvelope > thresholdLinear) {
            float gainReduction = 1.0f + (std::log10(mEnvelope / thresholdLinear) * (1.0f - 1.0f / mRatio));
            inputSample /= gainReduction;
        }

        // メイクアップゲインを適用
        inputSample *= mMakeupGain;

        return inputSample;
    }

private:
    float mThreshold = -24.0f;  // デフォルト: -24dB
    float mRatio = 4.0f;        // デフォルト: 4:1
    float mAttackTime = 0.01f;  // デフォルト: 10ms
    float mReleaseTime = 0.1f;  // デフォルト: 100ms
    float mMakeupGain = 1.0f;   // デフォルト: 1.0 (0dB)

    float mEnvelope = 0.0f;     // 内部エンベロープ
};