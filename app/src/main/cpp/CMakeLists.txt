cmake_minimum_required(VERSION 3.18.1)
project("phonesynth")
# Build our own native library
add_library (native-lib SHARED
		native-lib.cpp
		Oscillator.cpp
)

# Find the Oboe package
find_package (oboe REQUIRED CONFIG)

# Specify the libraries which our native library is dependent on, including Oboe
target_link_libraries(native-lib
        log
        oboe::oboe
)