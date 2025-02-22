package com.yaabelozerov.superfinancer.ui.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.FavouritesDestination
import com.ramcosta.composedestinations.generated.destinations.OpenStoryDestination
import com.ramcosta.composedestinations.generated.destinations.SocialDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.yaabelozerov.superfinancer.feed.FavouriteScreen
import com.yaabelozerov.superfinancer.feed.FeedScreen
import com.yaabelozerov.superfinancer.finance.FinanceScreen
import com.yaabelozerov.superfinancer.stories.OpenArticleScreen
import com.yaabelozerov.superfinancer.tickers.TickerScreen
import com.yaabelozerov.superfinancer.ui.MainScreen

@Destination<RootGraph>(start = true)
@Composable
fun Main(navigator: DestinationsNavigator) = MainScreen(navigator)

@Destination<RootGraph>
@Composable
fun Finance() = FinanceScreen()

@Destination<RootGraph>(style = SlideInVertically::class)
@Composable
fun OpenStory(url: String, navigator: DestinationsNavigator) =
    OpenArticleScreen(url, onBack = { navigator.navigateUp() }, onAddToPost = {
        navigator.navigateUp()
        navigator.bottomNavigate(
            SocialDestination(
                addToPostArticleUrl = url
            ), restore = false
        )
    })

@Destination<RootGraph>
@Composable
fun Social(addToPostArticleUrl: String?, navigator: DestinationsNavigator) = FeedScreen(
    addToPostArticleUrl,
    onNavigateToFavs = { navigator.navigate(FavouritesDestination()) },
    onAdd = { navigator.navigate(SocialDestination(null)) { restoreState = false } },
    onClickArticle = { navigator.navigate(OpenStoryDestination(it)) })

@Destination<RootGraph>
@Composable
fun Favourites(navigator: DestinationsNavigator) = FavouriteScreen(onClickArticle = {
    navigator.navigate(OpenStoryDestination(it))
})

@Destination<RootGraph>
@Composable
fun TickerDetails(symbol: String) = TickerScreen(symbol)