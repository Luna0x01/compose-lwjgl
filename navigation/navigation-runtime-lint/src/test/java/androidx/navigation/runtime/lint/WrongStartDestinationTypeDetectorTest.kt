/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.navigation.runtime.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestMode
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import org.junit.Test

class WrongStartDestinationTypeDetectorTest : LintDetectorTest() {

    @Test
    fun testEmptyConstructorNoError() {
        lint()
            .files(
                kotlin(
                        """
                package com.example

                import androidx.navigation.*

                fun createGraph() {
                    val navController = NavController()
                    navController.createGraph(startDestination = TestClass())

                    val navHost = TestNavHost()
                    navHost.createGraph(startDestination = TestClass())
                }
                """
                    )
                    .indented(),
                byteCode,
            )
            .skipTestModes(TestMode.FULLY_QUALIFIED)
            .run()
            .expectClean()
    }

    @Test
    fun testNavController_classNoErrors() {
        lint()
            .files(
                kotlin(
                        """
                package com.example

                import androidx.navigation.*

                fun createGraph() {
                    val navController = NavController()
                    navController.createGraph(startDestination = TestClassWithArg(15)) {}
                    navController.createGraph(startDestination = classInstanceRef) {}
                    navController.createGraph(startDestination = classInstanceWithArgRef) {}
                    navController.createGraph(startDestination = TestClass::class) {}
                    navController.createGraph(startDestination = Outer.InnerClass::class) {}
                    navController.createGraph(startDestination = Outer.InnerClass(15)) {}
                    navController.createGraph(startDestination = InterfaceChildClass(true)) {}
                    navController.createGraph(startDestination = AbstractChildClass(true)) {}
                }
                """
                    )
                    .indented(),
                byteCode,
            )
            .run()
            .expectClean()
    }

    @Test
    fun testNavController_objectNoError() {
        lint()
            .files(
                kotlin(
                        """
                package com.example

                import androidx.navigation.*

                fun createGraph() {
                    val navController = NavController()
                    navController.createGraph(startDestination = Outer) {}
                    navController.createGraph(startDestination = Outer.InnerObject) {}
                    navController.createGraph(startDestination = TestObject) {}
                    navController.createGraph(startDestination = TestObject::class) {}
                    navController.createGraph(startDestination = Outer.InnerObject::class) {}
                    navController.createGraph(startDestination = InterfaceChildObject) {}
                    navController.createGraph(startDestination = AbstractChildObject) {}
                }
                """
                    )
                    .indented(),
                byteCode,
            )
            .run()
            .expectClean()
    }

    @Test
    fun testNavController_classHasError() {
        lint()
            .files(
                kotlin(
                        """
                package com.example

                import androidx.navigation.*

                fun createGraph() {
                    val navController = NavController()
                    navController.createGraph(startDestination = TestClass) {}
                    navController.createGraph(startDestination = TestClassWithArg) {}
                    navController.createGraph(startDestination = Outer.InnerClass) {}
                    navController.createGraph(startDestination = InterfaceChildClass) {}
                    navController.createGraph(startDestination = AbstractChildClass) {}
                }
                """
                    )
                    .indented(),
                byteCode,
            )
            .run()
            .expect(
                """
src/com/example/test.kt:7: Error: StartDestination should not be a simple class name reference.
Did you mean to call its constructor TestClass(...)?
If the class TestClass does not contain arguments,
you can also pass in its KClass reference TestClass::class [WrongStartDestinationType]
    navController.createGraph(startDestination = TestClass) {}
                                                 ~~~~~~~~~
src/com/example/test.kt:8: Error: StartDestination should not be a simple class name reference.
Did you mean to call its constructor TestClassWithArg(...)?
If the class TestClassWithArg does not contain arguments,
you can also pass in its KClass reference TestClassWithArg::class [WrongStartDestinationType]
    navController.createGraph(startDestination = TestClassWithArg) {}
                                                 ~~~~~~~~~~~~~~~~
src/com/example/test.kt:9: Error: StartDestination should not be a simple class name reference.
Did you mean to call its constructor InnerClass(...)?
If the class InnerClass does not contain arguments,
you can also pass in its KClass reference InnerClass::class [WrongStartDestinationType]
    navController.createGraph(startDestination = Outer.InnerClass) {}
                                                 ~~~~~~~~~~~~~~~~
src/com/example/test.kt:10: Error: StartDestination should not be a simple class name reference.
Did you mean to call its constructor InterfaceChildClass(...)?
If the class InterfaceChildClass does not contain arguments,
you can also pass in its KClass reference InterfaceChildClass::class [WrongStartDestinationType]
    navController.createGraph(startDestination = InterfaceChildClass) {}
                                                 ~~~~~~~~~~~~~~~~~~~
src/com/example/test.kt:11: Error: StartDestination should not be a simple class name reference.
Did you mean to call its constructor AbstractChildClass(...)?
If the class AbstractChildClass does not contain arguments,
you can also pass in its KClass reference AbstractChildClass::class [WrongStartDestinationType]
    navController.createGraph(startDestination = AbstractChildClass) {}
                                                 ~~~~~~~~~~~~~~~~~~
5 errors, 0 warnings
            """
            )
    }

    @Test
    fun testHost_classNoErrors() {
        lint()
            .files(
                kotlin(
                        """
                package com.example

                import androidx.navigation.*

                fun createGraph() {
                    val navHost = TestNavHost()
                    navHost.createGraph(startDestination = TestClassWithArg(15)) {}
                    navHost.createGraph(startDestination = classInstanceRef) {}
                    navHost.createGraph(startDestination = classInstanceWithArgRef) {}
                    navHost.createGraph(startDestination = TestClass::class) {}
                    navHost.createGraph(startDestination = Outer.InnerClass::class) {}
                    navHost.createGraph(startDestination = Outer.InnerClass(15)) {}
                    navHost.createGraph(startDestination = InterfaceChildClass(true)) {}
                    navHost.createGraph(startDestination = AbstractChildClass(true)) {}
                }
                """
                    )
                    .indented(),
                byteCode,
            )
            .run()
            .expectClean()
    }

    @Test
    fun testNavHost_objectNoError() {
        lint()
            .files(
                kotlin(
                        """
                package com.example

                import androidx.navigation.*

                fun createGraph() {
                    val navHost = TestNavHost()
                    navHost.createGraph(startDestination = Outer) {}
                    navHost.createGraph(startDestination = Outer.InnerObject) {}
                    navHost.createGraph(startDestination = TestObject) {}
                    navHost.createGraph(startDestination = TestObject::class) {}
                    navHost.createGraph(startDestination = Outer.InnerObject::class) {}
                    navHost.createGraph(startDestination = InterfaceChildObject) {}
                    navHost.createGraph(startDestination = AbstractChildObject) {}
                }
                """
                    )
                    .indented(),
                byteCode,
            )
            .run()
            .expectClean()
    }

    @Test
    fun testNavHost_classHasError() {
        lint()
            .files(
                kotlin(
                        """
                package com.example

                import androidx.navigation.*

                fun createGraph() {
                    val navHost = TestNavHost()
                    navHost.createGraph(startDestination = TestClass) {}
                    navHost.createGraph(startDestination = TestClassWithArg) {}
                    navHost.createGraph(startDestination = Outer.InnerClass) {}
                    navHost.createGraph(startDestination = InterfaceChildClass) {}
                    navHost.createGraph(startDestination = AbstractChildClass) {}
                }
                """
                    )
                    .indented(),
                byteCode,
            )
            .run()
            .expect(
                """
src/com/example/test.kt:7: Error: StartDestination should not be a simple class name reference.
Did you mean to call its constructor TestClass(...)?
If the class TestClass does not contain arguments,
you can also pass in its KClass reference TestClass::class [WrongStartDestinationType]
    navHost.createGraph(startDestination = TestClass) {}
                                           ~~~~~~~~~
src/com/example/test.kt:8: Error: StartDestination should not be a simple class name reference.
Did you mean to call its constructor TestClassWithArg(...)?
If the class TestClassWithArg does not contain arguments,
you can also pass in its KClass reference TestClassWithArg::class [WrongStartDestinationType]
    navHost.createGraph(startDestination = TestClassWithArg) {}
                                           ~~~~~~~~~~~~~~~~
src/com/example/test.kt:9: Error: StartDestination should not be a simple class name reference.
Did you mean to call its constructor InnerClass(...)?
If the class InnerClass does not contain arguments,
you can also pass in its KClass reference InnerClass::class [WrongStartDestinationType]
    navHost.createGraph(startDestination = Outer.InnerClass) {}
                                           ~~~~~~~~~~~~~~~~
src/com/example/test.kt:10: Error: StartDestination should not be a simple class name reference.
Did you mean to call its constructor InterfaceChildClass(...)?
If the class InterfaceChildClass does not contain arguments,
you can also pass in its KClass reference InterfaceChildClass::class [WrongStartDestinationType]
    navHost.createGraph(startDestination = InterfaceChildClass) {}
                                           ~~~~~~~~~~~~~~~~~~~
src/com/example/test.kt:11: Error: StartDestination should not be a simple class name reference.
Did you mean to call its constructor AbstractChildClass(...)?
If the class AbstractChildClass does not contain arguments,
you can also pass in its KClass reference AbstractChildClass::class [WrongStartDestinationType]
    navHost.createGraph(startDestination = AbstractChildClass) {}
                                           ~~~~~~~~~~~~~~~~~~
5 errors, 0 warnings
            """
            )
    }

    private val sourceCode =
        """
package androidx.navigation

import kotlin.reflect.KClass
import kotlin.reflect.KType

public open class NavDestination

// NavGraph
public open class NavGraph: NavDestination() {
    public fun <T : Any> setStartDestination(startDestRoute: T) {}
}

// NavController
public open class NavController

public inline fun NavController.createGraph(
    startDestination: Any,
    route: KClass<*>? = null,
): NavGraph { return NavGraph() }

// NavHost
public interface NavHost
public class TestNavHost: NavHost

public inline fun NavHost.createGraph(
    startDestination: Any,
    route: KClass<*>? = null,
): NavGraph { return NavGraph() }

val classInstanceRef = TestClass()

val classInstanceWithArgRef = TestClassWithArg(15)

val innerClassInstanceRef = Outer.InnerClass(15)

object TestGraph

object TestObject

class TestClass

class TestClassWithArg(val arg: Int)

object Outer {
    data object InnerObject

    data class InnerClass (
        val innerArg: Int,
    )
}

interface TestInterface
class InterfaceChildClass(val arg: Boolean): TestInterface
object InterfaceChildObject: TestInterface

abstract class TestAbstract
class AbstractChildClass(val arg: Boolean): TestAbstract()
object AbstractChildObject: TestAbstract()

"""

    // Stub
    private val byteCode =
        compiled(
            "libs/StartDestinationLint.jar",
            kotlin(sourceCode).indented(),
            0x88777768,
            """
                META-INF/main.kotlin_module:
                H4sIAAAAAAAA/2NgYGBmYGBgBGJOBijgUucSTsxLKcrPTKnQy0ssy0xPLMnM
                zxMS8Essc0ktLsnMA/O9S7hEubiT83P1UisScwtyUoXYQoCy3iVKDFoMAGXO
                +shYAAAA
                """,
            """
                androidx/navigation/AbstractChildClass.class:
                H4sIAAAAAAAA/41QTW8SURQ9780wwBRkwC9K/ajVNpSF0MaN0TRSjAkJdtE2
                LGD1YCb0hWEmmfcgXfJbXLsx0Zi4MMSlP8p431CNCxYu3rn33Hdy7sfPX9++
                A3iBfYYDEflJLP3rZiQWciK0jKNme6R0Isa6cyVDvxMKpbJgDLubtJeB0n/0
                WVgMzmsZSX3CYNcHh30Gq37YLyCDrAsbOeIimTCwQQEutvLgKJBUX0nFUO/9
                3zSvqMsk0G1jRPYDhnJvGutQRs33gRa+0IIkfLawaE1mIG8A1HZK9WtpWIsy
                /4jhdLWsuLzK07dautzbcnnOqq6Wx7zFTosVx+M13rJ+fHC4Z5+X/7IcqWt2
                LuM5xumYYW/j+P8eiKaiIcpnYvGWqjJKFc+nmi7Qif2AodSTUXA2n42C5FKM
                QqpUevFYhH2RSMNviu5FPE/GwTtpyPb5PNJyFvSlkvTbjqJYp8YKR3ReO128
                Yq5NGac8A4dwl9gJcU7RbXxFvrHzBcVPqeYJodEAL7FHeG+twi2UzB0pM260
                CTx6a6+mOS/FTOMzih832hTWghsbjqcpPsYzim/SITO4PYTVxZ0u7nap7X1K
                Ue1iG7UhmMIOHgyRVSgpPFRwFR4pOAqeQvk3MHA2w9YCAAA=
                """,
            """
                androidx/navigation/AbstractChildObject.class:
                H4sIAAAAAAAA/41Sy2oUQRQ9VfPq6Yzm4SMzxkdMhBgXdhJcaRDGUaGhbcEM
                A5JV9YOkMj3V0F0zZDkrP8Q/CC4CCjLozo8Sb5XjA8zCbvreuueeOtX3UN++
                f/wM4BHuMWwJlRS5TE49JSbySGiZK68blboQse4dyyx5HZ2ksW6AMaxfRO6n
                pf61oYEKQ31fKqmfMlTubw9aqKHuoooGQ1Ufy5JhO/jPM58wOPtxZtVccCPh
                +OFBvxv2XrRwCW6TwMsMm0FeHHknqY4KIVXpCaVybVVLL8x1OM4ykloOhrkm
                Me9VqkUitCCMjyYVcoKZ0DQBDGxI+Kk01Q6tkl06YDZ1Xd7m9ptNna/veHs2
                3eM77FnD4V/e1/kSN9Q9ho0Lh/vbI/MroZg8J0gq23441Axrb8ZKy1Hqq4ks
                ZZSl3T9TkHW9PEkZFgOp0nA8itKiL4jDsBLkscgGopCmnoPuQT4u4vSlNEVn
                Ljz4Rxa75F/VDt0xdlK+TVWd8hJlTm/NVneo8ow1lGsPzuGc2fb6nAw8xl2K
                rZ8ENEkKcLDwe/Mqsc2z8An87TlaH7B4ZgGODRtvYdPeSPKGBFYOUfFxxcdV
                H9dwnZZY9dFG5xCsxA2sUb+EW+JmifoP1gZuws4CAAA=
                """,
            """
                androidx/navigation/InterfaceChildClass.class:
                H4sIAAAAAAAA/41Qz28SQRT+ZhZY2FJZqFZK/VWrtnBwaaMnTWNbY0KCbVIb
                DnAaYKVTltlkZyA98rd49mKiMfFgiEf/KOMbSpqYcPAw773vvW++9+P3nx8/
                AbzADsOOUP0klv2rQImJHAgjYxU0lAmTj6IXHl/IqH8cCa1dMAb/UkxEEAk1
                CE67l2HPuHAYtpZJnIfa3Mi4SDNkXkslzQFDarddbTE4u9VWHi5yHlLwCItk
                wMDaeeSxmgPHLaKaC6kZqs3/nPIVtRmE5tAqkX6bodgcxiaSKngfGtEXRhCF
                jyYO7c+syVkD6juk/JW0qE5Rf4/haDYtebzM52829bi/4vGsU55N93mdHa2W
                Mj6v8Lrz61OG+6mz4g3KEruSyqb9jFXaZ9heOv8/J6KxaIriiZi8pbRUc8rz
                oaEbHMf9kKHQlCo8GY+6YXIuuhFlSs24J6KWSKTFi6T3IR4nvfCdtGDjbKyM
                HIUtqSVVD5WKzVxYY48OnKKOGXole3FanFPsIkv2MaEDwpy8V/uOldrmNxS+
                zDnbZO0v4CWekF2/ZsFH0Z6SIqtGu5Du2kIrsBcmn659ReHzUpn8NWEhw/F0
                brfwjPwbqt2m2p0OnAbWG7jbQBkbFKLSwCbudcA07uNBB65GUeOhRl7jkUZW
                o6Sx9hcxw6Dy8gIAAA==
                """,
            """
                androidx/navigation/InterfaceChildObject.class:
                H4sIAAAAAAAA/41SS2/TQBD+dpMmjmtoWl4J5VXaotIDbivEhQqpBJAsBSPR
                KBLqaRMv6SbOWrI3Vo858UP4BxWHSiChCG78KMSsCeUAEtjaeXwz83lm1t++
                f/wM4CHuMWwJHaWJik58LXI1EEYl2g+0kelb0ZetYxVHr3pD2TdVMIb6UOTC
                j4Ue+L/QEsPa3zg6MjPnPFUsMFT2lVbmCUNp637XQxWOizJqDGVzrDKG7fb/
                9vKYwdnvxwWdC245nCA87ByEreceluDVCKwzrLeTdOAPpemlQunMF1onpqDN
                /DAx4SSOiWq5PUoMkfkvpRGRMIIwPs5LtCJmRc0KMLAR4SfKejtkRbv0gdnU
                dXmDF2c2db6+443ZdI/vsKdVh395X+F1blP3bC//3JLtJRT5M8KULuIPRoZh
                9fVEGzWWgc5VpnqxPPg9Bi2vlUSSYamttAwn455MO4JyGFbaSV/EXZEq689B
                9zCZpH35QlmnOSfu/kGLXVpgmWau0GnajZK+Q4Nbf4U0p5cukLw18ny7HdIL
                22dwT4vw3Xky8AjrJL2fCVgkC1R44bz4GmXbZ/ET+JszXPyA5dMC4Ngo5G1s
                Fn8rwyUiuHyEUoArAa4GVNogE80A17F6BJbhBm5SPIOX4VYG5wfApo4N6gIA
                AA==
                """,
            """
                androidx/navigation/NavController.class:
                H4sIAAAAAAAA/4VRu0oDQRQ9d2I2ukZNfMYXGGzUwlWxUwSNCIGooJLGapId
                4pjNDOxOgmW+xT+wEiwkWPpR4t01vc3hPGbuHO58/3x8AjjGJqEqTRhbHb4E
                Rg50RzptTXAjBzVrXGyjSMUFEKH0LAcyiKTpBLetZ9V2BeQI3qk22p0Rcju7
                zSLy8HxMoECYcE86IWw3/p1+Qig3utZF2gTXyslQOsme6A1yXJFSmEoBBOqy
                /6JTdcAsPCRsjYa+LyrCFyVmo+HkcmU0PBIHdJH/evVESaTnjii9XeZnL1Xi
                tMla7Hcd16zZUBHmGtqom36vpeIH2YrYmW/YtoyaMtapHpv+ve3HbXWlU7F6
                1zdO91RTJ5rTc2OsywYnqELwFsad06UwVlgFmQbye++YfGMisMroZeYs1hiL
                fwcwBT/L1zNcwUb2XYRpzoqPyNUxU8dsHXMoMUW5jnksPIISLGKJ8wR+guUE
                3i+86bUs6wEAAA==
                """,
            """
                androidx/navigation/NavDestination.class:
                H4sIAAAAAAAA/4VRO08CQRD+ZoEDTpSHiuAjUWOhFh4SO42Jj5iQICZqaKwW
                7oLLYy/hFkLJb/EfWJlYGGLpjzLOnTRWNl++x+zMZPbr+/0DwAm2CLtSu0Nf
                uRNHy7HqSKN87TTk+NoLjNKRTIIIua4cS6cvdce5a3W9tkkiRrDOlFbmnBDb
                P2hmkIBlI44kIW6eVUDYq//f/pSQr/d801faufWMdKWR7InBOMZLUgjpEECg
                HvsTFaoKM/eYsD2b2rYoCVvkmM2mqWJpNq2KCl0mPl8skRNhXZXC1/m/c496
                hve88l2PkK0r7TVGg5Y3fJStPjuFut+W/aYcqlDPTfvBHw3b3o0KRfl+pI0a
                eE0VKE4vtPZN1DjADgSfYb5zeBXGEisn0kDi8A2pVyYCZUYrMi2sM2Z+C5CG
                HeUbEa5hM/owwgJnmSfEalisYamGLHJMka+hgOUnUIAVrHIewA5QDGD9AKYj
                0APtAQAA
                """,
            """
                androidx/navigation/NavDestinationKt.class:
                H4sIAAAAAAAA/61WbVPbRhB+zsbYCGOECW8GDAGHGGgQEJI0hZJSaILKS1Kg
                pJS26WGEEQgpo5OZdDrT5lP/Q7/2FyTlQzplppPJt/ZHdbqnCgw2BibDB5/2
                9naffXZv787//PvnXwDGsMGQ4fam65ibLzSb75t57pmOrS3y/RlDeKbtT+e8
                KBiDusP3uWZxO6893tgxcqQNM9TmXIN7xiOXP99msLLzFfCmHdtzHcsy3PH5
                UqDx+V3Hs0xbc40ti+ba3LTFhRjvrwTmRxtncK4y3MTA5MURe+cdN6/tGN6G
                y01baNy2Hc+3Etqi4y0WLIusMudZkQnfsAwyq57wtk0xGYPCkA447ezvaabt
                Ga7NLU2nJMjfzIko4gxNuW0jtxuEecJdvmeQIcPN7Bk5FjXLEiQ/3r8aRwL1
                Cuqg0m4Kj7veiV2OIcnQcV76UVyTnE3b9CYZwlkJ2IwWBU1oJcCMmdnKnOoG
                pjM0ZGSOp/W9l9g1hmR5UgwR1yl4BkNLhZZhaDwRKrNpbPGC5TH8fKWNqZdb
                Xtg5HWWFeFYYHTsmuFWR4KwjvCs8M+bVBLrUaek8NxTDD1eU9PvsR2Pe8Hxv
                3aajYOeMJWOL4Xr2bMcVOihHHabmyty6LnCKYwCDNQjhA4ZUaeCnprc95eZ9
                oOxF8QNjotGSqwTSdzmIOIYxIkmNMrQSKd22Dbe8JJUoPaaD6GaKTkSpyTwb
                4mxCZQBx3MU9SehDhthEzgpums5zs4liXMGEvIHOfMpKs46CEKuy+v+X1ycK
                HmCqgmspvyimFcxI84ajPlwwPL7JPU65h/b2w/SoMjnUyAF0Ae5KIUSLL0wp
                DZO0OcLw99uXo8rbl0qoNaSEYuGzv/STJioNR6atodQNNZ4KDdcNhIYTo9Vq
                PcnqaCIWUhtSsSS5trLh5Oy7X2In7BrPs+tpIHxaZO9+q45RnFQV2YRJW0XK
                SFFZrUZJGSNlTVGpqLUyIWqf9EWnjYrRVrHGUawwKMVCU4FP/wMZ2qUbsn2p
                YHvmnqHb+6Yw6QWdKr6qtKXTzia9C/Xzpm0sFvY2DHdFvrLyGXFy3Frlrinn
                gbJm2cwTdMElOVOKe/yyngpQt+zx3O4Cfx5AxIt8DVpWlp2CmzMemnKtLYBc
                LSOKEWrvKmqFMFLyTqC6fEWzavrG6JuSR7JMR6eiRBdDGyI0q8Iazb4LMJsH
                krV/oGEw2UhjePIQTWtv0PZKtiC+DrwTaMQ6yQPkkSCcFNohW7MZHeiUXUtS
                EmmylFIXusn3Gx8heorBt/RrDAeTo7EGUGtwHT0kS2I/kluEvunOyE+/IsIW
                KhIMUxJyZDGfadLPR6VoSYJWiUmRdfMJ1mn0BqzTx6zTAWtZocx7VaijYoX6
                zq3QjctX6ObVVKiNorX73dB8gnVphfoqViiLfvpKIh3+ClD1O2698qke1UPa
                n8yrDUPQyr1ul3p1l3iN4U651/1Sr56SPv8ILUGppv18gN5DTFBRPj7ArUM8
                WFPr3+DTA9w+xIwvf3aA+6+PQRNBERSi00zgYTyjuUKrM/gSq0Tre3/rnoLT
                N0/6h7Qfj9YR1jGrQ9fxOeZ0zGNBxyIer4MJPMEX67gmMCAwKJAV6BcYppMt
                MCSgCdwVuCcwJnBHICKwJNApkBRYFugS6Bbo+w//HMZ8gQ0AAA==
                """,
            """
                androidx/navigation/NavGraph.class:
                H4sIAAAAAAAA/31SXU8TQRQ9s6XbbUFaQBAq+AEoBZStxCdLSPwIWlOr0qYv
                PE3bSZl+zJqdacNjf4v/wCeND6bx0R9lvLOtCkHcZO6de+65d87OnR8/v34D
                8Bh7DKtcNcNANs98xQeyxY0MlF/mg5ch/3CaAGNYv4LxQmgjVRQmEGNwD6SS
                5pAhltuuzSAON4UpJBimzKnUDLdK/zuqwLCghakYHppznRkWc6U2H3C/y1XL
                f1tvi4YpbNdI+EH1yeXMYa5ajdIbpSBs+W1h6iGXSvtcqcBELbVfDky53+3S
                kbP693nHQd8ID2nS2QlMVyq/Pej5UhkRKt71i8qE1EY2dAJzJKpxKhqdSZ93
                POQ9QUSGrX+IPYdUbJNWwV7PAq6nMI9FhvnLJQxzpYmKN8LwJjecMKc3iNHY
                mDVJa8DAOoSfSRvladd8xPB+NMymnGVnvDxaGSc1GpKzxnO8peXRcN/Js2fx
                7x9dSr5ey8SyTn5q3fNGw0x8x8m7+24mkXVejQmebbzPsHnVAM/Ni2RaVVX6
                g4uJvY6hl/A8aAqGdEkqUe736iKs8npX2DsIGrxb46G08QRMVmSLivsh7TeP
                +8rIniiqgdSS0n8u/enfwTKkKkE/bIgjaetXJjW1ccU5Iu7CobdpP4fk0lMl
                u0WRb8WTj+98hvcpSufIuhGYxDbZmTGBohT5OUwTEouKC8R2yCd25zNfsHSx
                3CW6LV8aUybldpfGDcrvROxr2LWYFTEbAQ8iex8PyR8Rukwnr5wgVkS2iJtF
                rGKNtrhVxG3cOQGzv7Z+gqRGSmNDw9WY1tjUuBfZtMbML5qRw8f+AwAA
                """,
            """
                androidx/navigation/NavHost.class:
                H4sIAAAAAAAA/31OTUvDQBB9s9F+xK9ELVTEv2Da4s2TUMRAVVDwktO2Wcs2
                6S50t6HH/i4P0nN/lDiJd2fgzZt5w5vZ/3x9A7hDj3AtTb6yOt8kRlZ6Lr22
                JnmR1ZN1vg0iRAtZyaSUZp68ThdqxtOAEE8K60ttkmflZS69vCeIZRWwLdXQ
                rQEEKni+0XU3YJYPCb3dthOKvghFxOyzv9uOxIBqcUS4mfzzD99gy5i7sXJe
                m0a8LTwhfLfr1Uw96lIRrt7Wxuul+tBOT0v1YIz1zapr8RUc4C8ELho8xyXX
                ITsfcrYyBCnaKTopugiZ4ijFMU4ykMMpzjIIh8gh/gUZbPE0RgEAAA==
                """,
            """
                androidx/navigation/Outer＄InnerClass.class:
                H4sIAAAAAAAA/41U30/bVhT+rp0fjgngAG35kbXdyFgS2jqwdusK7QZ0DLMQ
                OpjQOvZySbxgCDazHdS9TDz1T6i0vUyapj3x0EobTKtUsfZtf9M07VzbTbrQ
                IST7nnOPz/nOd88513/988czANexypDjds11rNoD3eZ7Vp37lmPry03fdHOG
                bZvuXIN7XhKMQdvie1xvcLuuL29smVU/CZkhMW3Zln+HIZY3CmsMcr6wlkYc
                SRUxKAyKJVBm3DoDM9JQ0ZWChDT5+5uWxzBWPguBKYauuukbLSxKYzCoVWdn
                17FN258gwKqz+y1DgXicFXO07Lh1fcv0N1xu2Z7ObdvxA29Przh+pdloTInD
                JFTifJ4hLVLkaubXvNnwGTbyZ0tkGOXO2k2dkWMa/RgQ2YeplL6z6ruWTccf
                yBdegQytdJ4LnbbZptWomW4SF1VcEu0YaGPnX3bmtoI3qZF8d9e0awxX8yeh
                T2aLkIngKHIC/G2GrCj9aY7vCMe8cJw73bEoHMfTyOINoV2lw29yb3POqZkM
                mXakYftmXZyvFA4gTZiOSRUTeJdOZH7T5A2asXP519T/S5r909pPvecbDZOq
                Gnf8TdNl6DuJQmTK247fsGx9yfR5jfucbNLOnkz3i4klJRbQ8G+T/YEldsRV
                qtHA/ny8f1GVBiVV0o73VXokTVElJUGyi6RMskd5/lAZPN6flEpstrsvoUnD
                Ukl+/lNC0mKLKS0pdgsvHsqL/ZpCOjkqihQ6kZmROUW6OqloXcOxQVZiCy8e
                yRSYDj0eMdK7Se8R+kqmBa8QneGYEtcSguskEycY+t+BTWKe7mJ7sqgqFb53
                1/R8yw7crm3TbYmF3estW7ZZae5smO7nosCirk6VN9a4a4l9ZBxZadq+tWMa
                9p7lWWSaaTeHoXvV59XtJb4beec6ve9xl++YxO0/Yek2R5O26qrTdKvmvCUg
                hiKItRPpaJok+pmJGvSJHxhpCun0W6B1kXbz9F0iqRaPkCqO/IbuJ7ST8Cmt
                PRAt1ym+hBTJMu3Oh970rVcMB2kClaoGjd4QUxczQzJe/BXdBy24RGAsBTDp
                0CGCyRC5l8GjncHstQH0ayFYETBBLAWn1FNI90eOcOFxKygkm2qRTUVklyI2
                5wAthUEMRbnHomJlsrHvvociGEwXRw4xEkJWaJXBBAJd7ij9LZKCWvYpLt0/
                wuW+tw4xJiIPUdAKh7hyiGuPO46RjRi9woNWvVWDsagGAYPfcb2zDEoUz3AD
                70U8viIp2pUrjv+CeOxg/E9IPyAuH4wfQ1oSQFfo/VFYYmFPKkH75KTyNzJJ
                2rcrlmtVLIeb+IDyLJOeFKTeD2pwLwil+4VPsEDl+ywANLBC8guy36JOTa1D
                NjBt4LaBO/iQVHxkYAaz62Ae5nB3Hb2eeD72oAZrwoPmIeOhz0O/hxuB8aYH
                3UOW9H8BSGQIivsHAAA=
                """,
            """
                androidx/navigation/Outer＄InnerObject.class:
                H4sIAAAAAAAA/41US08TURT+7p0+plMe5SFPxQdFXsoUxBXEBFHjkFKMJRhl
                dWlHGGhndOa2YcnKnVsXLl24YiFxQaKJqRI3/ijiudNREKIxac/5zrnnNd+5
                Mz+OP30BMIvbDCPCLfueU941XVF3NoV0PNdcqUnbz1qua/srG9t2SSbBGDLb
                oi7MinA3zV9ejSEx77iOvMOgjY2vtSCOhIEYkgwxueUEDKP5/+owx6BLryh9
                x91k6B4bz590a3opYjjv+Zvmti03fOG4gSlc15NhwcAseLJQq1QoKn2qrI42
                Krwlgq1Fr2yHQ1ra99nj1zS4/bImKjThhbH82SebG3/GkP1XN2olNio2tYt7
                csv2GTrPV6HW86VKyI8BrkjRrUJxdaGweL8FAzBS5Bxk6MjveJLCzGVbirKQ
                ghJ5ta7RjpgSKSXAwHbIv+soK0eoPM3wvLE3ZPA+bvBMY8/gugLpSOuGcmXa
                9KNXRl9jb4bn2N2kzr+9S/AMX+rKaAM8F5vRM/GBWB/LsYdHb7SlVCZB3iRh
                RlgnnFJYdZthaob+v24ziXF6lIKo37MD6bjhydSOZBh8XHOlU7Utt+4EDpG2
                cEIkXZPmYtrzjmsXatUN219VxCo+vZKorAnfUXbkbC1KUdpZFi8iO3u29iPh
                i6pN4/zRpCW8EosVEQQ2mUbRq/kl+4GjSvRHJdbODYdp2k8spL5frYv0DbIS
                pFtJx+k0Hlo3yTLVgpR34hD6AQGOqSgYFGCSbGkGIEWlVNE0eXiYfDVK1jrb
                P4RHJ+FaFH66M72L6Ij6nqR27v8llaEL3VEnizQn3Tsx+R7x2P7kV/C3iGv7
                kw3wJ7H9cPAcyRh4Ug+L9TQTomIK9dCfETtQV5peIAI6+n5T0RsmAOnP4E8P
                0f8RFw9Ch4YZkopHjgm0Eau3wn6T9C1SozFcInqG1qFZuGzhikVPd40ghi1k
                MbIOFuA6RtdhBOo3FiARoCsEPQEyIUiT/AmvDPuL4QQAAA==
                """,
            """
                androidx/navigation/Outer.class:
                H4sIAAAAAAAA/4VRW2sTQRT+ZjaXzSbaNF6aWFsvTbWp4rbFp1qEGhUW0hRs
                CUieJskQJ9nMwu4k9DFP/hD/QfGhoCBB3/xR4tltNA9S3GHPN+f2nTnn/Pz1
                5RuA53jCUBG6Fwaqd+ZqMVF9YVSg3eOxkWEWjKE4EBPh+kL33ePOQHZNFhZD
                5kBpZV4yWFu1VgFpZBykkGVImQ8qYlhtXMn6gsE+6PpJvgMeJ9le8+T0sFl/
                U8A1ODkyXmfYaARh3x1I0wmF0pErtA5MwhO5zcA0x75PVMuNYWCIzD2SRvSE
                EWTjo4lF3bFY5GIBBjYk+5mKtR269XYZarNpweFl7vDibOpw27J/fOTl2XSP
                77B9bqVeZW3+/VOGF3mcsMdiGsfTWoZ1X0TUZD5RLqfCUL2y4+oiKYt7DJv/
                ifwz5wfUXlNMXsvIKJ1EPRtSodV3Y23USHp6oiLV8eXhYjK0gHrQkwxLDaVl
                czzqyPBUUAxDqRF0hd8SoYr1ubGweJqkZOckGIdd+VbFvsq8TuufKtilFaWS
                uVbijRFWScsQFgk5nXSibZLmxtMnTG9fwD5P3I/mwcBTPCZZuAxAjqgAG/m/
                ySsUHX/5r+DvL1D4jKXzxGBhi2SJ3PfpX6N3PCRcJ6wlJTawTbhPNMtEXGrD
                8nDDw00Pt3CbrljxUEalDRbhDlbbSEdwItyNkImwFmH9NzJGDiwjAwAA
                """,
            """
                androidx/navigation/TestAbstract.class:
                H4sIAAAAAAAA/4VRy0oDMRQ9SduxHau29dX6AHUh6sLR4kJQhKoIA7WCSjeu
                0s6gsdMMTNList/iH7gSXEhx6UeJN2P3bg7nkdwcbr5/Pj4BHGGdYUOoIIll
                8OIpMZSPwshYefehNo2ONonomikwhtKzGAovEurRu+k8h9bNMDinUklzxpDZ
                2W0XkYPjIosphqx5kpphq/nf8BOGcrMXm0gq7zo0IhBGkMf7wwwVZBYKFsDA
                euS/SKsOiAWH1H08cl1e5S4vERuP8tvV8ajOD9h57uvV4SVuz9WZvV1uieEl
                PSxVWmK/Z6jlRRyEDHNNqcLWoN8Jk3vRicipNOOuiNoikVZPTPcuHiTd8Epa
                UbsdKCP7YVtqSWlDqdikg3V2E5yWMOlsd0JYJeWlGsjtvSP/RoSjRuik5jFW
                CIt/B1CAm+arKS5jLf0shmnKig/I+JjxMetjDiWiKPuoYP4BTGMBi5RruBpL
                Gs4vELgJXekBAAA=
                """,
            """
                androidx/navigation/TestClass.class:
                H4sIAAAAAAAA/31Ru04CQRQ9d4BFVlTAF/hs1cJFYqcxUYwJCWKihsZqYDc4
                sMwmzEAs+Rb/wMrEwhBLP8p4d6W2OTmPO/femfn++fgEcIpdwq7U/ihS/oun
                5UT1pFWR9h4DY+uhNCYLIhT6ciK9UOqed9fpB12bRYrgnCut7AUhdXDYziMD
                x0UaWULaPitD2G/+2/mMUGwOIhsq7d0GVvrSSvbEcJLi1SiGXAwg0ID9FxWr
                KjP/hLA3m7quKAtXFJjNpuXZtCaqdJX5enVEQcRVNYrPFltycs0zlU7mHw8s
                L1iP/ICw0lQ6aI2HnWD0KDshO6Vm1JVhW45UrOem+xCNR93gRsWicj/WVg2D
                tjKK00utI5s0NjiB4PvPN46fg7HMyks0kDl6x8IbE4EKo5OYB9hizP8VIAc3
                ybcT3MRO8kmERc7yT0g1sNTAcgMrKDBFsYESVp9ABmtY59zANdgwcH4BpKME
                iuEBAAA=
                """,
            """
                androidx/navigation/TestClassWithArg.class:
                H4sIAAAAAAAA/41QTWsTURQ9781kkoyJmcSvNFWrtpQ2Cyct7pRijAgDsUIt
                cZHVSzKkr5m8gXkvocv8FtduBEVwIcGlP0q8b1JcuRBmzr3nvsO5H79+f/8B
                4Bn2GPaEmmSpnFyFSizlVBiZqvA81qaXCK0/SHPRzaZFMIbgUixFmAg1Dd+N
                LuOxKcJh8F5IJc0Jg3sQHQ4YnIPDQQUFFH24KBEX2ZSBRRX4uFEGR4Wk5kJq
                hv3+//R+Tj2mselaGzKPGOr9WWoSqcK3sRETYQRJ+Hzp0ErMQtkCqOmM6lfS
                sg5lkyOG3nrV8HmT+zxYr3z6eFDyeclprlfHvMNeVRtewFu84/z86PHAPav/
                ZSVSt9xSIfCs1TGzDeqnYvmaxpUqH/3pzNBuvXQSM9T6UsWni/kozs7FKKFK
                o5+ORTIQmbT8uui/TxfZOH4jLdk6Wygj5/FAakmvXaVSkxtrHNHh3Hyphr0j
                ZZzyAjzCHWInxDlFv/0N5fb2V1Q/55pHhFYDtPGY8O5GhZuo2RtRZt1oEwT0
                b7xCezqKhfYXVD/906ayEVzbcDzJ8SF2Kb7Mhyzg1hBOhNsR7kTU9h6laEbY
                QmsIprGN+0MUNWoaDzT8HD2NQKP+B9uGEJmeAgAA
                """,
            """
                androidx/navigation/TestGraph.class:
                H4sIAAAAAAAA/31SXWsTQRQ9M0k2m220af1oYq1Vmwf1wW2Lbxah1g8W1hVs
                CJQ+TbJDOslmVnYnSx/z5A/xHxQfCgoS9M0fJd5Zgz4IzsC995w5c5h7mR8/
                P38F8ARdhi2h4yxV8bmvRaFGwqhU+z2Zm9eZeH9WB2NojUUh/ETokf92MJZD
                U0eFwTlQWplnDJUHD/tN1OB4qKLOUDVnKmfYDv/r/JTBPRgmpYcHbi+6QXTc
                O4yOXjZxBV6DyKsMO2GajfyxNINMKJ37QuvUlF65H6UmmiUJWa2Fk9SQmf9G
                GhELI4jj06JCXTIbGjaAgU2IP1cW7VIV7zF0F3PP423u8RZVi7n7/QNvL+b7
                fJc9r7v820eHt7jV7jPrsBaJ4gU1oXT5iMcTw7D5bqaNmspAFypXg0Qe/n0k
                zeMojSXDaqi0jGbTgcx6gjQM62E6FElfZMriJekdp7NsKF8pCzpL4/4/ttij
                8VTLnjp2WpTvEHIotyhz2rUSbRPybeeUa48u4V6Ux3eXYqCLexSbvwVokBXg
                YuXP5Q1S27XyBfzkEs1PWL0oCY77ZdzCTvmZaDZksH6KSoBrAa4HuIGbVGIj
                QBudU7Act7BJ5zm8HLdzOL8ATyx+j4kCAAA=
                """,
            """
                androidx/navigation/TestInterface.class:
                H4sIAAAAAAAA/4VOTUvDQBB9s9GmjV+JWqhH8W7a0psnQYRAVVDxktM22ZZt
                0g10t6HH/i4P0rM/SpzEH+AMvHkz83gz3z+fXwAm6BOupcnXlc63sZG1Xkin
                KxO/K+sS49R6LjPlgwjhUtYyLqVZxC+zpcqcD48QTYvKldrET8rJXDp5RxCr
                2mNzaqDXAAhU8Hyrm27ILB8R+vtdNxADEYiQ2Xyw343FkJrlmHAz/fcrvsTG
                0bOsH3isTSu5LRwheKs260w96lIRrl43xumV+tBWz0p1b0zlWqnt8C0c4C8E
                Llo8xyXXETsfcnZSeAn8BN0EPQRMcZTgGCcpyOIUZymERWgR/QKEKxfgUgEA
                AA==
                """,
            """
                androidx/navigation/TestNavHost.class:
                H4sIAAAAAAAA/4VRu04CQRQ9d3koKyrgA/BF7NTCBWKnMfERIwlqooaGamA3
                OLLMJsxAKPkW/8DKxMIQSz/KeHcxxsLEYk7O487MnTsfn69vAA5QIpSEcvuB
                dEeOEkPZEUYGyrn3tLkWw8tAmxkQIfMohsLxheo4N61Hr81ujLD+19afbQlC
                8kgqaY4JsZ3dRhozmLURR4oQNw9SE7br/1x+SMjWu4HxpXKuPCNcYQR7Vm8Y
                4/4phFQIIFCX/ZEMVZmZWyFsTsa2bRWsaE3Ghcm4apXpNPH+lLQyVlhU5aI/
                e/h1P9Nz7kiqKNnvGm7/LHA9wmJdKu960Gt5/XvR8tnJ1YO28BuiL0P9bdp3
                waDf9i5kKIq3A2Vkz2tILTk9USow0cEaFVg8HZ7a9D3huBjXWDmRBhJ7L7Cf
                mVhYZ0xGZh4bjOlpAeaYhflmhEVsRf9MmOdsoYlYDYs1ZGrIIscUSzUsY6UJ
                0lhFnnONtEZBY/YLEThWOyQCAAA=
                """,
            """
                androidx/navigation/TestObject.class:
                H4sIAAAAAAAA/31S0WoTQRQ9M0k2m220aW1tYrVWW0R96LbFN4tQq8LCuoIN
                AenTJDvESTazsDtZ+pgnP8Q/KH0oKEjQNz9KvLNGfRCcgXvvOXPmMPcy3398
                +gLgCXYZtoSOs1TF574WhRoKo1Ltd2Vu3vRHcmDqYAytkSiEnwg99H+zFQbn
                SGllnjFUHj7qNVGD46GKOkPVvFc5w3b4f+unDO7RIClNPHB70w2i0+5xdPKy
                iWvwGkReZ9gJ02zoj6TpZ0Lp3Bdap6Y0y/0oNdE0SchqJRynhsz819KIWBhB
                HJ8UFeqT2dCwAQxsTPy5smifqviAYXc+8zze5h5vUTWfud8+8PZ8dsj32fO6
                y79+dHiLW+0hsw4rkSheUBdKl4/YGxuGzbdTbdREBrpQueon8vjvI2kgJ2ks
                GZZDpWU0nfRl1hWkYVgN04FIeiJTFi9I7zSdZgP5SlnQWRj3/rHFAY2nWvbU
                sdOivEXIodyizGnXSnSXkG87p1x7fAX3ojzeXoiBB7hHsflLgAZZAS6W/lze
                ILVdS5/B312heYnli5LguF/GO9gpvxPNhgxWz1AJcCPAWoB13KQSGwHa6JyB
                5biFTTrP4eW4ncP5CW9bciiLAgAA
                """
        )

    override fun getDetector(): Detector = WrongStartDestinationTypeDetector()

    override fun getIssues(): MutableList<Issue> =
        mutableListOf(WrongStartDestinationTypeDetector.WrongStartDestinationType)
}
