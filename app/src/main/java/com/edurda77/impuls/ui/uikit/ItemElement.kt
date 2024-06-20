package com.edurda77.impuls.ui.uikit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edurda77.impuls.ui.theme.blue53
import com.edurda77.impuls.ui.theme.white

@Composable
fun ItemElement(
    modifier: Modifier = Modifier,
    name:String,
    isCenter: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(shape = RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .background(color = blue53)
            .clickable(onClick = onClick)
            .padding(10.dp),
        horizontalAlignment = if (isCenter) Alignment.CenterHorizontally else Alignment.Start
    ) {
        Text(
            modifier = modifier.fillMaxWidth(),
            text = name,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight(600),
                textAlign = if (isCenter) TextAlign.Center else TextAlign.Start,
                color = white
            )
        )
    }
}