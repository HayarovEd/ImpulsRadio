package com.edurda77.impuls.ui.uikit

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edurda77.impuls.R
import com.edurda77.impuls.ui.theme.blue34
import com.edurda77.impuls.ui.theme.blue53
import com.edurda77.impuls.ui.theme.white
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AccessScreen (
    modifier: Modifier = Modifier,
    cameraPermissionState: MultiplePermissionsState,
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = blue34)
            .padding(15.dp),
        verticalArrangement = Arrangement.Center
    ) {
        val textPerm = getTextToShowGivenPermissions(
            permissions = cameraPermissionState.revokedPermissions,
            shouldShowRationale = cameraPermissionState.shouldShowRationale,
            context = context
        )
        /*val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
            stringResource(R.string.important_access)
        } else {
            stringResource(R.string.access_record)
        }*/
        Text(
            modifier = modifier.fillMaxWidth(),
            text = textPerm,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight(600),
                textAlign = TextAlign.Center,
                color = white
            )
        )
        Spacer(modifier = modifier.height(10.dp))
        Button(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            contentPadding = PaddingValues(vertical = 10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = blue53
            ),
            onClick = { cameraPermissionState.launchMultiplePermissionRequest() }) {
            Text(
                text = stringResource(R.string.get_access),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight(400),
                    color = white
                )
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
private fun getTextToShowGivenPermissions(
    permissions: List<PermissionState>,
    shouldShowRationale: Boolean,
    context: Context
): String {
    val revokedPermissionsSize = permissions.size
    if (revokedPermissionsSize == 0) return ""

    val textToShow = StringBuilder().apply {
        append("The ")
    }

    for (i in permissions.indices) {
        textToShow.append(permissions[i].permission)
        when {
            revokedPermissionsSize > 1 && i == revokedPermissionsSize - 2 -> {
                textToShow.append(", and ")
            }
            i == revokedPermissionsSize - 1 -> {
                textToShow.append(" ")
            }
            else -> {
                textToShow.append(", ")
            }
        }
    }
    textToShow.append(if (revokedPermissionsSize == 1) "permission is" else "permissions are")
    textToShow.append(
        if (shouldShowRationale) {
            context.getString(R.string.important_access)
        } else {
            context.getString(R.string.access_record)
        }
    )
    return textToShow.toString()
}