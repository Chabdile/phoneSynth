if(NOT TARGET oboe::oboe)
add_library(oboe::oboe SHARED IMPORTED)
set_target_properties(oboe::oboe PROPERTIES
    IMPORTED_LOCATION "/Users/x21008xx/.gradle/caches/transforms-4/e4d1471708cdca4847f085a9ea256c91/transformed/oboe-1.9.0/prefab/modules/oboe/libs/android.x86/liboboe.so"
    INTERFACE_INCLUDE_DIRECTORIES "/Users/x21008xx/.gradle/caches/transforms-4/e4d1471708cdca4847f085a9ea256c91/transformed/oboe-1.9.0/prefab/modules/oboe/include"
    INTERFACE_LINK_LIBRARIES ""
)
endif()

