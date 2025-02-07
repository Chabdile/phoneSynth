//#include <stdio.h>
//#include <stdlib.h>
#include "../Oscillator.cpp"

class Distortion {
public:
    void setDistortion(const std::string &type, float drive) {
        mDistortionType = type;
        mDrive = drive;
    }

    float prosess() {
        float sample = prosessSample(mWaveform, mPhase, mAmplitude, mPulseWidth);
        sample = applyDistortion(sample, mDrive, mDistortionType); // ディストーションを適用
        mPhase += mPhaseIncrement;
        if (mPhase >= 2.0 * M_PI) mPhase -= 2.0 * M_PI;
        return sample;
    }


    static float applyDistortion(float sample, float drive, const std::string &type) {
        if (type == "hard") {
            // ハードクリッピング
            float threshold = 1.0f / drive;
            if (sample > threshold) return threshold;
            if (sample < -threshold) return -threshold;
            return sample;
        } else if (type == "soft") {
            // ソフトクリッピング
            float x = sample * drive;
            return (x / (1.0f + std::fabs(x)));
        }
        return sample; // デフォルトは何もせず返す
    }



private:
    std::string mDistortionType = "none"; // ディストーションの種類: "none", "hard", "soft"
    float mDrive = 1.0f;                  // ディストーションの強度
};
