package com.vocamoney.feature.history

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vocamoney.R
import com.vocamoney.core.designsystem.SectionScreen

/** S08. The entry list arrives in slice L1, filters in L4. */
@Composable
fun HistoryScreen() {
    SectionScreen(title = stringResource(R.string.history_title)) {
        Text(stringResource(R.string.history_empty), style = MaterialTheme.typography.bodyLarge)
        Text(stringResource(R.string.coming_in_slice), style = MaterialTheme.typography.bodyMedium)
    }
}
