if(NOT TARGET oboe::oboe)
add_library(oboe::oboe SHARED IMPORTED)
set_target_properties(oboe::oboe PROPERTIES
    IMPORTED_LOCATION "/Users/x21008xx/.gradle/caches/transforms-4/12c3b8e7c9f87ffc828bc26b74dc5821/transformed/oboe-1.9.0/prefab/modules/oboe/libs/android.x86/liboboe.so"
    INTERFACE_INCLUDE_DIRECTORIES "/Users/x21008xx/.gradle/caches/transforms-4/12c3b8e7c9f87ffc828bc26b74dc5821/transformed/oboe-1.9.0/prefab/modules/oboe/include"
    INTERFACE_LINK_LIBRARIES ""
)
endif()

