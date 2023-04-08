package com.example.lunchtray.ui.OrderUseCase.Shared

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.lunchtray.R

// TODO: OrderAppBar
@Composable
fun OrderAppBar(
    canNavigateBack: Boolean,
    navigateUp: ()->Unit,
    modifier: Modifier = Modifier,
    currentDestination: String = "",
){
    TopAppBar(
        title = {
            if (canNavigateBack) {
                Text(stringResource(id = R.string.app_name) + " $currentDestination")
            }
            else{
                Text(stringResource(id = R.string.app_name))
            }
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }        }
    )
}