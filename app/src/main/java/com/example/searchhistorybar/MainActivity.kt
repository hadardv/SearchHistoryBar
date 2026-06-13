package com.example.searchhistorybar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.searchhistorybar.ui.theme.SearchHistoryBarTheme
import com.example.smartsearchbar.SearchDatabase
import com.example.smartsearchbar.SearchQuery
import com.example.smartsearchbar.SmartSearchHistoryBar
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SearchHistoryBarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DemoScreen()
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun DemoScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val db = remember {
        Room.databaseBuilder(
            context.applicationContext,
            SearchDatabase::class.java,
            "search_db"
        ).build()
    }
    val dao = db.searchQueryDao()
    val recentQueries by dao.getAllQueries().collectAsState(initial = emptyList())

    var currentQuery by remember { mutableStateOf("") }
    var lastSubmittedSearch by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "SmartSearchHistoryBar Demo",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "This app shows how to use the library search bar with Room storage.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Try this:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("1. Type a word and tap Search on the keyboard.")
                Text("2. Tap the search field again to see saved history.")
                Text("3. Tap a history row to search it again.")
                Text("4. Tap X on a row to delete it from the database.")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Library component",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "The search bar below comes from the smartsearchbar module.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        SmartSearchHistoryBar(
            query = currentQuery,
            onQueryChange = { currentQuery = it },
            recentQueries = recentQueries,
            onSearchSubmit = { submittedText ->
                if (submittedText.isNotBlank()) {
                    lastSubmittedSearch = submittedText
                    currentQuery = submittedText
                    scope.launch {
                        dao.insertQuery(SearchQuery(queryText = submittedText))
                    }
                }
            },
            onDeleteClick = { id ->
                scope.launch {
                    dao.deleteQuery(id)
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "What you just searched",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (lastSubmittedSearch == null) {
                    Text(
                        text = "Nothing searched yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Submit a search or pick an item from history.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = lastSubmittedSearch!!,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "This text updates when you press Search or tap a history item.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Saved in Room database (${recentQueries.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "These items persist after you close the app.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (recentQueries.isEmpty()) {
                    Text(
                        text = "No saved searches yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    recentQueries.forEachIndexed { index, item ->
                        Text(
                            text = "${index + 1}. ${item.queryText}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
