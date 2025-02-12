package com.darach.westend.presentation.settings


import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.darach.westend.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val uiState = null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                PreferenceSection(title = "Appearance") {
                    var showThemeDialog by remember { mutableStateOf(false) }
                    PreferenceItem(
                        title = "Theme",
                        description = "Match system",
                        painterResource = painterResource(R.drawable.nightmode),
                        onClick = { showThemeDialog = true }
                    )

                    if (showThemeDialog) {
                        AlertDialog(
                            onDismissRequest = { showThemeDialog = false },
                            title = { Text("Choose theme") },
                            text = {
                                Column(Modifier.selectableGroup()) {
                                    ThemeMode.entries.forEach { themeMode ->
                                        RadioButtonPreference(
                                            text = themeMode.displayName,
                                            selected = ThemeMode.SYSTEM == themeMode,
                                            onClick = {}
                                        )
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = { showThemeDialog = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        SwitchPreference(
                            title = "Use dynamic colors",
                            description = "Apply colors based on your wallpaper",
                            painterResource = painterResource(R.drawable.theme),
                            checked = false,
                            onCheckedChange = { }
                        )
                    }
                }

                PreferenceSection(title = "Notifications") {
                    SwitchPreference(
                        title = "Show deals & offers",
                        description = "Get notified about special theatre deals",
                        icon = Icons.Default.Notifications,
                        checked = false,
                        onCheckedChange = { }
                    )

                    SwitchPreference(
                        title = "Price alerts",
                        description = "Notify when saved show prices change",
                        painterResource = painterResource(R.drawable.pricedown),
                        checked = false,
                        onCheckedChange = { }
                    )
                }

                PreferenceSection(title = "Account") {
                    PreferenceItem(
                        title = "Email preferences",
                        description = "Manage your email settings",
                        icon = Icons.Default.Email,
                        onClick = { }
                    )

                    PreferenceItem(
                        title = "Payment methods",
                        description = "Add or remove payment options",
                        painterResource = painterResource(R.drawable.paymentmethods),
                        onClick = { }
                    )
                }

                PreferenceSection(title = "Privacy & Security") {
                    PreferenceItem(
                        title = "Privacy settings",
                        description = "Manage your data and privacy options",
                        painterResource = painterResource(R.drawable.privacy),
                        onClick = { }
                    )

                    SwitchPreference(
                        title = "Send usage statistics",
                        description = "Help improve the app by sharing usage data",
                        painterResource = painterResource(R.drawable.data_usage),
                        checked = false,
                        onCheckedChange = { }
                    )
                }

                PreferenceSection(title = "About") {
                    PreferenceItem(
                        title = "App version",
                        description = "1.0.0 (2025.02.12)",
                        icon = Icons.Default.Info,
                        onClick = { }
                    )

                    PreferenceItem(
                        title = "Terms of Service",
                        description = "Read our terms and conditions",
                        painterResource = painterResource(R.drawable.terms),
                        onClick = { }
                    )

                    PreferenceItem(
                        title = "Open source licenses",
                        description = "View third-party licenses",
                        painterResource = painterResource(R.drawable.opensource),
                        onClick = { }
                    )
                }
            }
        }
    }
}

@Composable
private fun PreferenceSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
private fun PreferenceItem(
    title: String,
    description: String,
    painterResource: Painter? = null,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            when {
                painterResource != null -> Icon(
                    painter = painterResource,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterVertically)
                )

                icon != null -> Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterVertically)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SwitchPreference(
    title: String,
    description: String,
    painterResource: Painter? = null,
    icon: ImageVector? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = { onCheckedChange(!checked) },
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            when {
                painterResource != null -> Icon(
                    painter = painterResource,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterVertically)
                )

                icon != null -> Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterVertically)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun RadioButtonPreference(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(56.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

enum class ThemeMode(val displayName: String) {
    SYSTEM("Match system"),
    LIGHT("Light"),
    DARK("Dark")
}