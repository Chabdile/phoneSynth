if(NOT TARGET oboe::oboe)
add_library(oboe::oboe SHARED IMPORTED)
set_target_properties(oboe::oboe PROPERTIES
    IMPORTED_LOCATION "/Users/x21008xx/.gradle/caches/transforms-4/175556e54fd77ebeeef845220b07f679/transformed/oboe-1.9.0/prefab/modules/oboe/libs/android.arm64-v8a/liboboe.so"
    INTERFACE_INCLUDE_DIRECTORIES "/Users/x21008xx/.gradle/caches/transforms-4/175556e54fd77ebeeef845220b07f679/transformed/oboe-1.9.0/prefab/modules/oboe/include"
    INTERFACE_LINK_LIBRARIES ""
)
endif()

