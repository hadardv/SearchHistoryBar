# SearchHistoryBar

A simple Android project with a reusable Jetpack Compose search bar library and a demo app.

## Modules

- **smartsearchbar** — library module (`com.example.smartsearchbar`)
  - Room database for storing search history
  - `SmartSearchHistoryBar` Compose component
- **app** — demo app (`com.example.searchhistorybar`) showing how to integrate the library

## Features

- Search bar with keyboard submit action
- Recent searches shown in a dropdown when the field is focused
- Delete individual history items
- History persisted with Room

## Requirements

- Android Studio
- JDK 11+
- minSdk 24

## Run the demo

1. Open the project in Android Studio.
2. Sync Gradle.
3. Run the **app** module.

In the demo:
- Type a query and press Search on the keyboard to save it
- Tap the search field to see saved history
- Tap a history row to search it again
- Tap X to delete an item
- Tap outside the search field to hide the dropdown

## Library usage

Add the module dependency:

```kotlin
implementation(project(":smartsearchbar"))
```

Create the database and collect history:

```kotlin
val db = Room.databaseBuilder(context, SearchDatabase::class.java, "search_db").build()
val dao = db.searchQueryDao()
val recentQueries by dao.getAllQueries().collectAsState(initial = emptyList())
```

Use the component:

```kotlin
SmartSearchHistoryBar(
    query = currentQuery,
    onQueryChange = { currentQuery = it },
    recentQueries = recentQueries,
    onSearchSubmit = { text ->
        scope.launch { dao.insertQuery(SearchQuery(queryText = text)) }
    },
    onDeleteClick = { id ->
        scope.launch { dao.deleteQuery(id) }
    }
)
```

## Tech stack

- Kotlin
- Jetpack Compose
- Material 3
- Room
