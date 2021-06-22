package com.ui.widget

abstract class ProgressBarListener {
    fun onProgressStart(){}
    abstract fun onProgressComplete()
    fun onProgressCancel(){}
}