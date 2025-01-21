package io.ktor.di

class KoinResolver(
    override val reflection: DependencyReflection,

) : DependencyResolver {
    override fun <T : Any> get(key: DependencyKey): T {

    }
}