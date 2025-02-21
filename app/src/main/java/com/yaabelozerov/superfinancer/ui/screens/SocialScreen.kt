package com.yaabelozerov.superfinancer.ui.screens

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.MainScreenDestination
import com.ramcosta.composedestinations.generated.destinations.OpenArticleScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SocialScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.yaabelozerov.superfinancer.feed.ui.FeedScreen
import com.yaabelozerov.superfinancer.ui.navigation.bottomNavigate

@Destination<RootGraph>
@Composable
fun SocialScreen(addToPostArticleUrl: String?, navigator: DestinationsNavigator) = FeedScreen(
    addToPostArticleUrl,
    onAdd = { navigator.navigate(SocialScreenDestination(null)) { restoreState = false } },
    onClickArticle = { navigator.navigate(OpenArticleScreenDestination(it.link)) })
