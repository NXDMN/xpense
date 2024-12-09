package com.nxdmn.xpense.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.LocalTonalElevationEnabled
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nxdmn.xpense.helpers.toEpochMilli
import com.nxdmn.xpense.helpers.toLocalDate
import java.time.LocalDate
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearPicker(state: DatePickerState) {
    val colors = DatePickerDefaults.colors()
    val displayedYear = state.displayedMonthMillis.toLocalDate().year

    Column {
        val lazyGridState =
            rememberLazyGridState(
                // Set the initial index to a few years before the current year to allow quicker
                // selection of previous years.
                initialFirstVisibleItemIndex = max(
                    0,
                    displayedYear - DatePickerDefaults.YearRange.first - 3
                )
            )

        val tonalElevationEnabled = LocalTonalElevationEnabled.current
        val containerColor =
            if (colors.containerColor == MaterialTheme.colorScheme.surface && tonalElevationEnabled) {
                MaterialTheme.colorScheme.surfaceColorAtElevation(LocalAbsoluteTonalElevation.current)
            } else {
                colors.containerColor
            }

        Text(
            text = "Year",
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 16.dp),
            fontSize = 16.sp
        )
        HorizontalDivider(color = colors.dividerColor)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .requiredHeight(
                    48.dp * (6 + 1) - DividerDefaults.Thickness
                )
                .padding(horizontal = 12.dp)
                .background(color = containerColor),
            state = lazyGridState,
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(DatePickerDefaults.YearRange.count()) {
                val selectedYear = it + DatePickerDefaults.YearRange.first
                val selected = selectedYear == displayedYear
                val currentYear = selectedYear == LocalDate.now().year


                val colorTarget = if (selected) {
                    colors.selectedYearContainerColor
                } else {
                    Color.Transparent
                }

                val contentColorTarget = when {
                    selected -> colors.selectedYearContentColor
                    currentYear -> colors.currentYearContentColor
                    else -> colors.yearContentColor
                }

                val border = remember(currentYear, selected) {
                    if (currentYear && !selected) {
                        // Use the day's spec to draw a border around the current year.
                        BorderStroke(
                            1.0.dp,
                            colors.todayDateBorderColor
                        )
                    } else {
                        null
                    }
                }

                Surface(
                    selected = selected,
                    onClick = {
                        state.selectedDateMillis =
                            state.selectedDateMillis?.toLocalDate()?.withYear(selectedYear)
                                ?.toEpochMilli()
                        state.displayedMonthMillis =
                            state.displayedMonthMillis.toLocalDate().withYear(selectedYear)
                                .toEpochMilli()
                    },
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .requiredSize(
                            width = 72.dp,
                            height = 36.dp,
                        ),
                    color = animateColorAsState(
                        colorTarget,
                        tween(durationMillis = 100),
                        label = "YearPickerColor",
                    ).value,
                    contentColor = animateColorAsState(
                        contentColorTarget,
                        tween(durationMillis = 100),
                        label = "YearPickerContentColor",
                    ).value,
                    shape = RoundedCornerShape(6.dp),
                    border = border,
                ) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(text = selectedYear.toString())
                    }
                }
            }
        }
        HorizontalDivider(color = colors.dividerColor)
    }
}