package com.edurda77.impuls.ui.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edurda77.impuls.R
import com.edurda77.impuls.domain.model.Song
import com.edurda77.impuls.ui.theme.MyApplicationTheme
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DialogSongs(
    modifier: Modifier = Modifier,
    songs: List<Song>
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Заголовок секции
        SectionHeader(
            title = stringResource(R.string.last_songs),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Список песен
        if (songs.isEmpty()) {
            EmptyStateView()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = songs,
                    key = { song -> song.id }
                ) { song ->
                    ItemSong(
                        song = song,
                    )
                }
            }
        }
    }
}


@Composable
private fun SectionHeader(
    title: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun EmptyStateView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.songs),
                contentDescription = "No songs",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Text(
                text = "Нет недавних композиций",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Прослушайте несколько треков, чтобы они появились здесь",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun Preview() {
    val demoLastPlayedSongs = listOf(
        Song(
            artist = "The Beatles",
            duration = "3:45",
            id = 1,
            lastPlayed = LocalDateTime.now().minusMinutes(5),
            radioId = 101,
            title = "Yesterday"
        ),
        Song(
            artist = "Queen",
            duration = "4:15",
            id = 2,
            lastPlayed = LocalDateTime.now().minusHours(1),
            radioId = 102,
            title = "Bohemian Rhapsody"
        ),
        Song(
            artist = "Nirvana",
            duration = "5:01",
            id = 3,
            lastPlayed = LocalDateTime.now().minusDays(1),
            radioId = 103,
            title = "Smells Like Teen Spirit"
        ),
        Song(
            artist = "Michael Jackson",
            duration = "4:57",
            id = 4,
            lastPlayed = LocalDateTime.now().minusDays(2),
            radioId = 104,
            title = "Billie Jean"
        ),
        Song(
            artist = "Pink Floyd",
            duration = "6:23",
            id = 5,
            lastPlayed = LocalDateTime.now().minusDays(3),
            radioId = 105,
            title = "Another Brick in the Wall"
        )
    )
    MyApplicationTheme {
        DialogSongs(
            songs = demoLastPlayedSongs
        )
    }
}