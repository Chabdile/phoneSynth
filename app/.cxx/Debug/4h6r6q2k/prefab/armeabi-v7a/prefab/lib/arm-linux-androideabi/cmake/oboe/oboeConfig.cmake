if(NOT TARGET oboe::oboe)
add_library(oboe::oboe SHARED IMPORTED)
set_target_properties(oboe::oboe PROPERTIES
    IMPORTED_LOCATION "/Users/x21008xx/.gradle/caches/transforms-4/39ef7f8bcb60bb24f35fce69de249825/transformed/oboe-1.9.3/prefab/modules/oboe/libs/android.armeabi-v7a/liboboe.so"
    INTERFACE_INCLUDE_DIRECTORIES "/Users/x21008xx/.gradle/caches/transforms-4/39ef7f8bcb60bb24f35fce69de249825/transformed/oboe-1.9.3/prefab/modules/oboe/include"
    INTERFACE_LINK_LIBRARIES ""
)
endif()

