package com.vocamoney.feature.dashboard

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vocamoney.R
import com.vocamoney.core.designsystem.SectionScreen

/** S11. Totals of confirmed entries arrive in slice L5. */
@Composable
fun DashboardScreen() {
    SectionScreen(title = stringResource(R.string.dashboard_title)) {
        Text(stringResource(R.string.dashboard_empty), style = MaterialTheme.typography.bodyLarge)
        Text(stringResource(R.string.coming_in_slice), style = MaterialTheme.typography.bodyMedium)
    }
}
