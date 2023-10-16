import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


@Composable
fun ProgressionIndicator(isPathEnabled: MutableState<Boolean>, animationProgress: Float) {

    AnimatedContent(
        isPathEnabled.value,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(500)
            ) togetherWith fadeOut(animationSpec = tween(300))
        },
        modifier = Modifier
    ){
        targetState ->
        when(targetState) {
            true -> {
                // Галочка
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Check mark",
                    tint = Color.Green
                )
            }
            false ->{
                // Индикатор прогресса
                CircularProgressIndicator(
                    modifier = Modifier,
                    color = Color.LightGray,
                    progress = animationProgress
                )

            }
        }

    }
}