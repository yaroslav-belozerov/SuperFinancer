package com.yaabelozerov.superfinancer.ui.screens

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.yaabelozerov.superfinancer.feed.ui.FeedScreen

@Destination<RootGraph>
@Composable
fun SocialScreen() = FeedScreen()
