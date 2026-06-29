package com.nxdmn.xpense.domain

import com.nxdmn.xpense.data.models.ExpenseModel
import com.nxdmn.xpense.data.repositories.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

data class GroupedExpensesResult(
    val groupedExpenses: List<ExpenseGroup>,
    val totalAmount: Double,
    val filteredExpenses: List<ExpenseModel>
)

data class ExpenseGroup(
    val groupName: String,
    val amount: Double,
    val expenses: List<ExpenseModel>
)

enum class ViewMode(val title: String) {
    DAY("Day"),
    MONTH("Month"),
    YEAR("Year")
}

class GetGroupedExpensesUseCase(
    private val repository: ExpenseRepository
) {
    operator fun invoke(
        viewMode: ViewMode,
        selectedDate: LocalDate,
        isGroupByCategory: Boolean
    ): Flow<GroupedExpensesResult> {
        return repository.expenseListFlow.map { expenses ->
            val filteredExpenses = when (viewMode) {
                ViewMode.DAY -> expenses.filter { e -> e.date == selectedDate }
                ViewMode.MONTH -> expenses.filter { e -> e.date.year == selectedDate.year && e.date.month == selectedDate.month }
                ViewMode.YEAR -> expenses.filter { e -> e.date.year == selectedDate.year }
            }

            val groupedExpenses = if (isGroupByCategory || viewMode == ViewMode.DAY) {
                filteredExpenses.groupBy { it.category }
                    .map { (category, expenses) ->
                        ExpenseGroup(
                            category.name,
                            expenses.sumOf { it.amount },
                            expenses
                        )
                    }
            } else {
                when (viewMode) {
                    ViewMode.MONTH -> filteredExpenses.groupBy { it.date }
                        .toSortedMap(compareByDescending { it })
                        .map { (key, expenses) ->
                            ExpenseGroup(
                                key.toString(),
                                expenses.sumOf { it.amount },
                                expenses
                            )
                        }

                    ViewMode.YEAR -> filteredExpenses.groupBy { it.date.month }
                        .map { (key, expenses) ->
                            ExpenseGroup(
                                key.name,
                                expenses.sumOf { it.amount },
                                expenses
                            )
                        }
                }
            }

            GroupedExpensesResult(
                groupedExpenses = groupedExpenses,
                totalAmount = filteredExpenses.sumOf { it.amount },
                filteredExpenses = filteredExpenses
            )
        }
    }
}
