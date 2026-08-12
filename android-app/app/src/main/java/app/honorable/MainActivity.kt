package app.honorable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) = super.onCreate(savedInstanceState).also {
        setContent { HonorableApp() }
    }
}

@Composable fun HonorableApp() {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
        Surface(Modifier.fillMaxSize()) { HomeScreen() }
    }
}

@Composable private fun HomeScreen() {
    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Spacer(Modifier.height(18.dp)); Text("Honorable", style = MaterialTheme.typography.headlineLarge)
        Text("Private intelligence, on your device", color = MaterialTheme.colorScheme.onSurfaceVariant)
        FeatureCard("Memories AI", "Find a moment using ordinary words.", Icons.Rounded.PhotoLibrary)
        FeatureCard("Terms AI", "Understand the fine print before you agree.", Icons.Rounded.Policy)
        Spacer(Modifier.weight(1f))
        AssistChip(onClick = {}, label = { Text("Local processing • Nothing uploaded") })
    }
}

@Composable private fun FeatureCard(title: String, body: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    ElevatedCard(shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(22.dp), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            FilledIconButton(onClick = {}) { Icon(icon, null) }
            Column { Text(title, style = MaterialTheme.typography.titleLarge); Text(body) }
        }
    }
}
