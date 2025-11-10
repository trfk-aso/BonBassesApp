package com.bonbasses.platform

import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

actual object HapticFeedback {
    private val generator = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)
    
    init {
        generator.prepare()
    }
    
    actual fun trigger() {
        generator.impactOccurred()

        generator.prepare()
    }
}
