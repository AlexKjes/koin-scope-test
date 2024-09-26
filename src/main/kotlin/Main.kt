package org.example

import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

typealias Connection = String

class DataSource() {
    fun transaction(block: (Connection) -> Unit) {
        block("Yolo")
    }
}

class TransactionScope

class SomeRepository(private val conection: Connection) {
    fun persist() = println("${conection} is persisted")
}

class SomeService(val someRepository: SomeRepository) {
    fun doStuff() {
        // does stuff
        //writes resulting stuff to DB
        someRepository.persist()
    }
}

fun scopedModle(dataSource: DataSource) = module {

    scope<TransactionScope> {
        dataSource.transaction { connection ->
            scoped { SomeRepository(connection) }
            scoped { SomeService(get()) }
        }
    }
}

class Motor() {
    fun kjørIgang() {
        val scope = KoinPlatform.getKoin().createScope<TransactionScope>()
        scope.get<SomeService>().doStuff()
        scope.close()
    }
}

fun main() {
    val dataSource = DataSource()
    startKoin {
        modules(scopedModle(dataSource))
    }

    Motor().kjørIgang()
}