package com.example.jurnalku.ui.components.icon

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.jurnalku.R

sealed class AppIconClass {

//    Default Icons
    data class Vector(val icon: ImageVector) : AppIconClass()

//    XML Icons
    data class Drawable(@DrawableRes val resId: Int) : AppIconClass()

    companion object {
//        Define default icons
        val Add = Vector(Icons.Default.AddCircle)
        val Profile = Vector(Icons.Default.AccountCircle)
        val Check = Vector(Icons.Default.CheckCircle)

//       Define XML
        val Journal = Drawable(R.drawable.journal)
        val Calendar = Drawable(R.drawable.calendar)
        val MoodVerySad = Drawable(R.drawable.cry)
        val MoodSad = Drawable(R.drawable.tear)
        val MoodHappy = Drawable(R.drawable.smile)
        val MoodVeryHappy = Drawable(R.drawable.smilestar)
        val PenTool = Drawable(R.drawable.pen)
        val Image = Drawable(R.drawable.image)
        val Undo = Drawable(R.drawable.undo)
        val Redo = Drawable(R.drawable.redo)
    }
}