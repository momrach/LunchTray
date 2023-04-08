/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lunchtray

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lunchtray.ui.*
import com.example.lunchtray.ui.OrderUseCase.OrderViewModel
import com.example.lunchtray.ui.OrderUseCase.Shared.OrderAppBar

// TODO: Screen enum
enum class OrderUCScreens() {
    Start,
    Entree,
    SideDish,
    Accompaniment,
    Checkout,
}


@Composable
fun OrderUseCaseScreen(modifier: Modifier = Modifier){
    // TODO: Create Controller and initialization
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route ?: OrderUCScreens.Start.name

    // Create ViewModel
    val orderViewModel: OrderViewModel = viewModel()

    Scaffold(
        topBar = {
            // TODO: OrderAppBar
            OrderAppBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                currentDestination = currentScreen
            )
        }
    ) { innerPadding -> //Es un parametro que recibimos del content lambda del Scaffold
        //Creamos el uiState que se utiliza en la pantalla CheckOut
        //Cada vez que se recompone se obtiene el ultimo estado desde el orderViewModel
        val uiState by orderViewModel.uiState.collectAsState()

        // Obtenemos el contexto para usarlo en el toast de la CheckOut
        val context : Context = LocalContext.current

        // TODO: Navigation host
        NavHost(
            navController = navController,
            startDestination = OrderUCScreens.Start.name,
            modifier = modifier.padding(innerPadding),
        ){
            composable(route = OrderUCScreens.Start.name){
                StartOrderScreen(
                    onStartOrderButtonClicked = { navController.navigate(OrderUCScreens.Entree.name) }
                )
            }

            composable(route = OrderUCScreens.Entree.name){
                EntreeMenuScreen(
                    options = com.example.lunchtray.datasource.DataSource.entreeMenuItems,
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(orderViewModel,navController) },
                    onNextButtonClicked = { navController.navigate(OrderUCScreens.SideDish.name) },
                    onSelectionChanged = {
                        orderViewModel.updateEntree(it)
                    }
                )
            }

            composable(route = OrderUCScreens.SideDish.name){
                SideDishMenuScreen(
                    options = com.example.lunchtray.datasource.DataSource.sideDishMenuItems,
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(orderViewModel,navController) },
                    onNextButtonClicked = { navController.navigate(OrderUCScreens.Accompaniment.name) },
                    onSelectionChanged = {
                        orderViewModel.updateSideDish(it)
                    }
                )
            }

            composable(route = OrderUCScreens.Accompaniment.name){
                AccompanimentMenuScreen(
                    options = com.example.lunchtray.datasource.DataSource.accompanimentMenuItems,
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(orderViewModel,navController) },
                    onNextButtonClicked = { navController.navigate(OrderUCScreens.Checkout.name) },
                    onSelectionChanged = {
                        orderViewModel.updateAccompaniment(it)
                    }
                )
            }

            composable(route = OrderUCScreens.Checkout.name){
                CheckoutScreen(
                    orderUiState = uiState,
                    onNextButtonClicked = { showToastAndNavigateToStart(context,orderViewModel,navController) },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(orderViewModel,navController) })
            }

        }
    }
}

private fun cancelOrderAndNavigateToStart(
    orderViewModel: OrderViewModel,
    navController: NavHostController
) {
    orderViewModel.resetOrder()
    navController.popBackStack(OrderUCScreens.Start.name, inclusive = false)
}

private fun showToastAndNavigateToStart(
    context: Context,
    orderViewModel: OrderViewModel,
    navController: NavHostController
){
    Toast.makeText(context,"Your order has been sent. Thank You !",Toast.LENGTH_LONG).show()
    cancelOrderAndNavigateToStart(orderViewModel,navController)
}
