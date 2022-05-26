package com.example.testmed.doctor.profile.base

interface Abstract {
    abstract class Object<T, M>() {
        abstract fun map(mapper: M): T
    }

    interface Mapper {
        class Empty : Mapper
    }
}