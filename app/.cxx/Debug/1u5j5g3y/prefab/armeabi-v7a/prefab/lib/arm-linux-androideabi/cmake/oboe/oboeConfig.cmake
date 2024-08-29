if(NOT TARGET oboe::oboe)
add_library(oboe::oboe SHARED IMPORTED)
set_target_properties(oboe::oboe PROPERTIES
    IMPORTED_LOCATION "/Users/x21008xx/.gradle/caches/transforms-4/0bab344ebe757f849118e3ead79c4486/transformed/oboe-1.9.0/prefab/modules/oboe/libs/android.armeabi-v7a/liboboe.so"
    INTERFACE_INCLUDE_DIRECTORIES "/Users/x21008xx/.gradle/caches/transforms-4/0bab344ebe757f849118e3ead79c4486/transformed/oboe-1.9.0/prefab/modules/oboe/include"
    INTERFACE_LINK_LIBRARIES ""
)
endif()

