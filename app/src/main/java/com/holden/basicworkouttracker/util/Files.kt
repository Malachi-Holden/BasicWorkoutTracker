package com.holden.basicworkouttracker.util

import java.io.File

operator fun File.div(fileName: String): File = File(absolutePath + File.separator + fileName)