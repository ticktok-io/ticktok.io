package e2e.test.io.ticktok.server

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class E2ETest {

    @Test
    fun tt() {
        println("test0")
    }

    @Nested
    inner class E2E1Tests {

        @Test
        fun test1() {
            println("test1")
        }

        @ParameterizedTest
        @ValueSource(strings = ["kuku2", "kuku1"])
        fun test2(name: String) {
            println("test2: $name")
        }
    }

    @Nested
    inner class E2ETest_2 {
        @Test
        fun test22() {
            println("test22")
        }

        @Test
        fun test3() {
            println("test3")
        }
    }
}