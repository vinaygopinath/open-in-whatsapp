package org.vinaygopinath.launchchat.utils

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class DispatcherUtil @Inject constructor() {

    fun getIoDispatcher() = Dispatchers.IO
}