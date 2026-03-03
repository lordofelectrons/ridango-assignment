package com.example.ridangoassignmentnewsapi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ridangoassignmentnewsapi.di.ServiceLocator
import com.example.ridangoassignmentnewsapi.ui.screens.articledetail.ArticleDetailScreen
import com.example.ridangoassignmentnewsapi.ui.screens.articledetail.ArticleDetailViewModel
import com.example.ridangoassignmentnewsapi.ui.screens.newslist.NewsListScreen
import com.example.ridangoassignmentnewsapi.ui.screens.newslist.NewsListViewModel

@Composable
fun NewsNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "news_list") {
        composable("news_list") {
            val viewModel: NewsListViewModel = viewModel(
                factory = NewsListViewModel.Factory(ServiceLocator.newsRepository)
            )
            NewsListScreen(
                viewModel = viewModel,
                onArticleClick = { article ->
                    ArticleCache.selectedArticle = article
                    navController.navigate("article_detail")
                }
            )
        }
        composable("article_detail") {
            val viewModel: ArticleDetailViewModel = viewModel()
            val article = ArticleCache.selectedArticle
            if (article != null) {
                viewModel.setArticle(article)
            }
            ArticleDetailScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
