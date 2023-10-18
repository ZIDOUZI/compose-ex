package zdz.libs.compose.ex

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ShapeDefaults
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

private val default = SquircleShape(15)

val ShapeDefaults.squircleShape: SquircleShape
    get() = default

class SquircleShape(
    topStart: CornerSize,
    topEnd: CornerSize,
    bottomEnd: CornerSize,
    bottomStart: CornerSize,
) : CornerBasedShape(topStart, topEnd, bottomEnd, bottomStart) {
    
    constructor(size: CornerSize) : this(size, size, size, size)
    
    constructor(size: Dp) : this(CornerSize(size))
    constructor(percentage: Int) : this(CornerSize(percentage))
    
    override fun copy(
        topStart: CornerSize,
        topEnd: CornerSize,
        bottomEnd: CornerSize,
        bottomStart: CornerSize,
    ): SquircleShape = SquircleShape(topStart, topEnd, bottomEnd, bottomStart)
    
    override fun createOutline(
        size: Size,
        topStart: Float,
        topEnd: Float,
        bottomEnd: Float,
        bottomStart: Float,
        layoutDirection: LayoutDirection,
    ): Outline = if (topStart + topEnd + bottomEnd + bottomStart == 0f) {
        Outline.Rectangle(size.toRect())
    } else {
        Outline.Generic(path = Path().apply {
            val (width, height) = size
            moveTo(width / 2, 0f)
            cubicTo(
                x1 = width - topStart,
                y1 = 0f,
                x2 = width,
                y2 = topStart,
                x3 = width,
                y3 = height / 2
            )
            cubicTo(
                x1 = width,
                y1 = height - bottomEnd,
                x2 = width - bottomEnd,
                y2 = height,
                x3 = width / 2,
                y3 = height
            )
            cubicTo(
                x1 = bottomStart,
                y1 = height,
                x2 = 0f,
                y2 = height - bottomStart,
                x3 = 0f,
                y3 = height / 2
            )
            cubicTo(
                x1 = 0f,
                y1 = topStart,
                x2 = topStart,
                y2 = 0f,
                x3 = width / 2,
                y3 = 0f
            )
            close()
        })
    }
}