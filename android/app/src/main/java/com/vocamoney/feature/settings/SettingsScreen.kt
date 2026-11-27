package com.vocamoney.feature.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vocamoney.R
import com.vocamoney.core.designsystem.SectionScreen

/** S10. Shows the seeded category count as proof the database is wired; full settings in L6. */
@Composable
fun SettingsScreen(categoryCount: Int) {
    SectionScreen(title = stringResource(R.string.settings_title)) {
        Text(stringResource(R.string.settings_categories, categoryCount), style = MaterialTheme.typography.bodyLarge)
        Text(stringResource(R.string.settings_privacy_note), style = MaterialTheme.typography.bodyMedium)
    }
}
