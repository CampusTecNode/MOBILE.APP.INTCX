package com.intec.connect.interfaces

import com.intec.connect.data.model.Product

interface LikeClickListener {
    fun onLike(product: Product, position: Int)
    fun onUnlike(product: Product, position: Int)
}