package com.intec.connect.utilities.animations;

/**
 * This is a helper class for defining animation constants.
 */
public class AnimationConstants {
    /**
     * Defines views properties.
     */
    public static class ViewProperties {
        public static final String TRANSLATION_X = "translationX";
        static final String TRANSLATION_Y = "translationY";
        public static final String ALPHA = "alpha";
        static final String ROTATION = "rotation";
        public static final String ROTATION_Y = "rotationY";
        static final String PROGRESS = "progress";
    }

    /**
     * Defines properties for Lottie Animations.
     */
    public static class LottieAnimationProperties {
        public static final float FORWARD_ANIMATION_SPEED = 1.5f;
        public static final float BACKWARDS_ANIMATION_SPEED = -2.0f;
        public static final float MAX_FORWARD_ANIMATION_SPEED = 100.0f;
        public static final float NONE_ANIMATION_SPEED = 0.0f;
    }
}