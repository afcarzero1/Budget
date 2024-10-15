package com.example.budgetahead.ui.onboarding.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.budgetahead.R
import com.example.budgetahead.ui.onboarding.Page
import com.example.budgetahead.ui.theme.BudgetApplicationTheme

@Composable
fun OnBoardingPage(
    modifier: Modifier = Modifier,
    page: Page,
) {
    Column(modifier = modifier) {
        Image(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.60f),
            painter = painterResource(id = page.image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.medium)))
        Text(
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.medium)),
            text = page.title,
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.medium)),
            text = page.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
        )
    }
}

@Preview(showBackground = true)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun OnBoardingPagePreview() {
    BudgetApplicationTheme {
        OnBoardingPage(
            page =
                Page(
                    title = "Lorem Ipsum is simply dummy",
                    description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
                    image = R.drawable.budget,
                ),
        )
    }
}
