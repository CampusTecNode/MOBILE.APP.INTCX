package com.intec.connect.utilities.animations

import android.animation.Animator

/**
 * This class contains helper methods for animations.
 */
object AnimatorUtil {
    /**
     * Merges Animator arrays into one array containing them all.
     *
     * @param animators Animator arrays to merge.
     * @return Array of Animator objects.
     */
    fun concatAnimators(vararg animators: Array<Animator?>): Array<Animator> {
        val allAnimators = mutableListOf<Animator>() // Use a mutable list for flexibility

        for (animatorArray in animators) {
            for (animator in animatorArray) {
                animator?.let { allAnimators.add(it) } // Add only non-null animators
            }
        }

        return allAnimators.toTypedArray() // Convert the list to a non-nullable array
    }

    /**
     * Determines the length of an Animator array.
     *
     * @param animators Animator arrays to get length.
     * @return The length of all Animator arrays as a single value.
     */
    private fun getArrayLength(vararg animators: Array<Animator>): Int {
        var length = 0

        // Add each animator array length.
        for (animator in animators) {
            length += animator.size
        }

        // Return length of all animator arrays.
        return length
    }
}