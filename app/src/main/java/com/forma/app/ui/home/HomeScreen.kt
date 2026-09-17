package com.forma.app.ui.home

import androidx.compose.runtime.Composable

@Composable
fun HomeScreen(
    onOpenTab: (String) -> Unit,
    onOpenArticle: (String) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenNotifications: () -> Unit,
) = Unit

@Composable
fun NotificationsScreen(onBack: () -> Unit) = Unit
