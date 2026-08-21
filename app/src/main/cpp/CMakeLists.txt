cmake_minimum_required(VERSION 3.22.1)
project(lsfg-mali LANGUAGES C CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_library(lsfg-mali SHARED
    gpu_frame_blender.cpp
    main_jni.cpp
)

target_include_directories(lsfg-mali PRIVATE .)

find_library(log-lib log)
find_library(vulkan-lib vulkan)
find_library(android-lib android)

target_link_libraries(lsfg-mali
    PRIVATE
        ${log-lib}
        ${vulkan-lib}
        ${android-lib}
)
