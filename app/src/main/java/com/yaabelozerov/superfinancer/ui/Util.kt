package com.yaabelozerov.superfinancer.ui

fun Double.toString(precision: Int) = "%.${precision}f".format(this)