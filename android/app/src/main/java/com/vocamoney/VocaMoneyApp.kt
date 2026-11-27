package com.vocamoney

import android.app.Application
import com.vocamoney.data.local.VocaMoneyDatabase

/** Manual dependency container. A DI framework can replace it once there are enough bindings. */
class VocaMoneyApp : Application() {
    val database: VocaMoneyDatabase by lazy { VocaMoneyDatabase.build(this) }
}
