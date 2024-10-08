package com.intec.connect.interfaces

import android.view.View

interface ClickListener<T> {
    fun onClick(view: View, item: T, position: Int)
}