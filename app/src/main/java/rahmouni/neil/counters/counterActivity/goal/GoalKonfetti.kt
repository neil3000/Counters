package rahmouni.neil.counters.counterActivity.goal

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun GoalKonfetti() {
    val party = Party(
        speed = 5f,
        maxSpeed = 30f,
        damping = 0.9f,
        spread = 400,
        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
        emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
        position = Position.Relative(0.5, 0.3)
    )
    KonfettiView(
        modifier = Modifier.fillMaxSize(),
        parties = listOf(party),
    )
}