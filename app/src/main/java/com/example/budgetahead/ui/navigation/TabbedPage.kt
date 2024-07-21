package com.example.budgetahead.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

data class TabItem(
    val title: String,
    val icon: @Composable () -> Unit,
    val screen: @Composable () -> Unit,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabbedPage(
    tabs: List<TabItem>,
    modifier: Modifier = Modifier,
    onTabChanged: (Int) -> Unit = {},
) {
    val pagerState =
        rememberPagerState(
            initialPage = 0,
            initialPageOffsetFraction = 0f,
            pageCount = { tabs.size },
        )
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
        ) {
            tabs.forEachIndexed { index, item ->
                Tab(
                    selected = index == pagerState.currentPage,
                    text = { Text(text = item.title) },
                    icon = item.icon,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                index,
                            )
                        }
                        onTabChanged(index)
                    },
                )
            }
        }
        HorizontalPager(
            modifier = Modifier,
            state = pagerState,
            pageSpacing = 0.dp,
            pageContent = {
                tabs[it].screen()
            },
            pageSize = PageSize.Fill,
        )
    }
}
