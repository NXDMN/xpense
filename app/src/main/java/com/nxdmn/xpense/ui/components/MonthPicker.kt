package com.nxdmn.xpense.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTonalElevationEnabled
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nxdmn.xpense.helpers.toEpochMilli
import com.nxdmn.xpense.helpers.toLocalDate
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthPicker(state: DatePickerState) {
    val months =
        listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    val displayedMonth = state.displayedMonthMillis.toLocalDate().monthValue

    val colors = DatePickerDefaults.colors()

    val tonalElevationEnabled = LocalTonalElevationEnabled.current
    val containerColor =
        if (colors.containerColor == MaterialTheme.colorScheme.surface && tonalElevationEnabled) {
            MaterialTheme.colorScheme.surfaceColorAtElevation(LocalAbsoluteTonalElevation.current)
        } else {
            colors.containerColor
        }

    var yearPickerVisible by rememberSaveable { mutableStateOf(false) }

    Column {
        YearPickerMenuButton(
            onClick = { yearPickerVisible = !yearPickerVisible },
            expanded = yearPickerVisible,
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp),
        ) {
            Text(
                text = state.displayedMonthMillis.toLocalDate().year.toString(),
                fontSize = 16.sp
            )
        }
        HorizontalDivider(color = colors.dividerColor)
        Box {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .requiredHeight(
                        48.dp * 5 - DividerDefaults.Thickness
                    )
                    .padding(horizontal = 12.dp)
                    .background(color = containerColor),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(months) { index, month ->
                    val selectedMonth = index + 1
                    val selected = selectedMonth == displayedMonth
                    val currentMonth = selectedMonth == LocalDate.now().monthValue

                    val colorTarget = if (selected) {
                        colors.selectedYearContainerColor
                    } else {
                        Color.Transparent
                    }

                    val contentColorTarget = when {
                        selected -> colors.selectedYearContentColor
                        currentMonth -> colors.currentYearContentColor
                        else -> colors.yearContentColor
                    }

                    val border = remember(currentMonth, selected) {
                        if (currentMonth && !selected) {
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
                        modifier = Modifier
                            .requiredSize(
                                width = 72.dp,
                                height = 48.dp,
                            ),
                        onClick = {
                            state.selectedDateMillis =
                                state.selectedDateMillis?.toLocalDate()?.withMonth(selectedMonth)
                                    ?.toEpochMilli()
                            state.displayedMonthMillis =
                                state.displayedMonthMillis.toLocalDate().withMonth(selectedMonth)
                                    .toEpochMilli()
                        },
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
                        border = border
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = month)
                        }
                    }
                }
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = yearPickerVisible,
                modifier = Modifier.clipToBounds(),
                enter = expandVertically() + fadeIn(initialAlpha = 0.6f),
                exit = shrinkVertically() + fadeOut()
            ) {
                YearPickerContent(state = state, colors = colors)
            }
        }
        HorizontalDivider(color = colors.dividerColor)
    }
}

@Composable
private fun YearPickerMenuButton(
    onClick: () -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        colors = ButtonDefaults.textButtonColors(contentColor = LocalContentColor.current),
        elevation = null,
        border = null,
    ) {
        content()
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Icon(
            Icons.Filled.ArrowDropDown,
            contentDescription = "YearPickerMenuButtonDropDown",
            Modifier.rotate(if (expanded) 180f else 0f)
        )
    }
}