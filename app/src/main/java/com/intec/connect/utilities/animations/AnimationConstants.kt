package com.intec.connect.utilities.animations

/**
 * This is a helper class for defining animation constants.
 */
class AnimationConstants {
    /**
     * Defines views properties.
     */
    object ViewProperties {
        const val TRANSLATION_X: String = "translationX"
        const val TRANSLATION_Y: String = "translationY"
        const val ALPHA: String = "alpha"
        const val ROTATION: String = "rotation"
        const val ROTATION_Y: String = "rotationY"
        const val PROGRESS: String = "progress"
    }

    /**
     * Defines properties for Lottie Animations.
     */
    object LottieAnimationProperties {
        const val FORWARD_ANIMATION_SPEED: Float = 1.5f
        const val BACKWARDS_ANIMATION_SPEED: Float = -2.0f
        const val MAX_FORWARD_ANIMATION_SPEED: Float = 100.0f
        const val NONE_ANIMATION_SPEED: Float = 0.0f
    }
}