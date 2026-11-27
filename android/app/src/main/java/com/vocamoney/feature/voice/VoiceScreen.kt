package com.vocamoney.feature.voice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vocamoney.R
import com.vocamoney.core.designsystem.Spacing

/**
 * S04/S05 entry point. The mic button is disabled until slice L1 wires the
 * fake recognizer and parser; it never records in L0.
 */
@Composable
fun VoiceScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.l),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.l, Alignment.CenterVertically),
    ) {
        Text(
            text = stringResource(R.string.voice_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.voice_tagline),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        FilledIconButton(
            onClick = {},
            enabled = false,
            modifier = Modifier.size(96.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Mic,
                contentDescription = stringResource(R.string.voice_mic_description),
                modifier = Modifier.size(48.dp),
            )
        }
        Text(
            text = stringResource(R.string.voice_not_ready),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}
