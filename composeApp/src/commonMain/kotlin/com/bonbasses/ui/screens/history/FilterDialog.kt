package com.bonbasses.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.bonbasses.platform.utils.TimeUtils
import com.bonbasses.ui.theme.LocalThemeColors
import com.bonbasses.ui.theme.RobotoSlabFontFamily

enum class DatePickerMode {
    FROM, TO
}

data class FilterState(
    val dateFrom: String = "",
    val dateTo: String = "",
    val selectedLength: String? = null,
    val selectedCategories: Set<String> = emptySet(),
    val selectedScores: Set<Int> = emptySet()
)

@Composable
fun FilterDialog(
    filterState: FilterState,
    onDismiss: () -> Unit,
    onApply: (FilterState) -> Unit,
    onReset: () -> Unit,
    showDateFields: Boolean = true
) {
    var currentFilterState by remember { mutableStateOf(filterState) }
    var showDatePicker by remember { mutableStateOf(false) }
    var datePickerMode by remember { mutableStateOf<DatePickerMode>(DatePickerMode.FROM) }
    val themeColors = LocalThemeColors.current
    
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .background(
                    color = themeColors.dialogBackground,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showDateFields) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Period",
                            color = themeColors.dialogText,
                            fontSize = 16.sp,
                            fontFamily = RobotoSlabFontFamily(),
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            FilterDateField(
                                label = "From",
                                date = currentFilterState.dateFrom,
                                onDateChange = { currentFilterState = currentFilterState.copy(dateFrom = it) },
                                onClick = {
                                    datePickerMode = DatePickerMode.FROM
                                    showDatePicker = true
                                },
                                modifier = Modifier.weight(1f)
                            )
                            
                            FilterDateField(
                                label = "To",
                                date = currentFilterState.dateTo,
                                onDateChange = { currentFilterState = currentFilterState.copy(dateTo = it) },
                                onClick = {
                                    datePickerMode = DatePickerMode.TO
                                    showDatePicker = true
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Length",
                            color = themeColors.dialogText,
                            fontSize = 12.sp,
                            fontFamily = RobotoSlabFontFamily(),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        FilterChip(
                            text = "Short",
                            isSelected = currentFilterState.selectedLength == "Short",
                            onClick = { 
                                currentFilterState = currentFilterState.copy(
                                    selectedLength = if (currentFilterState.selectedLength == "Short") null else "Short"
                                )
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        FilterChip(
                            text = "Medium",
                            isSelected = currentFilterState.selectedLength == "Medium",
                            onClick = { 
                                currentFilterState = currentFilterState.copy(
                                    selectedLength = if (currentFilterState.selectedLength == "Medium") null else "Medium"
                                )
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        FilterChip(
                            text = "Long",
                            isSelected = currentFilterState.selectedLength == "Long",
                            onClick = { 
                                currentFilterState = currentFilterState.copy(
                                    selectedLength = if (currentFilterState.selectedLength == "Long") null else "Long"
                                )
                            }
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(280.dp)
                            .background(themeColors.dialogStroke)
                    )
                    
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Category",
                            color = themeColors.dialogText,
                            fontSize = 12.sp,
                            fontFamily = RobotoSlabFontFamily(),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        listOf("Adventure", "Slice of Life", "Romance", "Sci-Fi", "Mystery", "Fantasy").forEach { category ->
                            FilterChip(
                                text = category,
                                isSelected = category in currentFilterState.selectedCategories,
                                onClick = {
                                    currentFilterState = currentFilterState.copy(
                                        selectedCategories = if (category in currentFilterState.selectedCategories) {
                                            currentFilterState.selectedCategories - category
                                        } else {
                                            currentFilterState.selectedCategories + category
                                        }
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(280.dp)
                            .background(themeColors.dialogStroke)
                    )
                    
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Score",
                            color = themeColors.dialogText,
                            fontSize = 12.sp,
                            fontFamily = RobotoSlabFontFamily(),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        listOf(3, 4, 5).forEach { score ->
                            FilterChip(
                                text = score.toString(),
                                isSelected = score in currentFilterState.selectedScores,
                                onClick = {
                                    currentFilterState = currentFilterState.copy(
                                        selectedScores = if (score in currentFilterState.selectedScores) {
                                            currentFilterState.selectedScores - score
                                        } else {
                                            currentFilterState.selectedScores + score
                                        }
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .border(
                                width = 1.5.dp,
                                color = Color(0xFFAD8E7D),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(
                                color = Color(0xFF312117),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                currentFilterState = FilterState()
                                onReset()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Reset",
                            color = Color(0xFFAD8E7D),
                            fontSize = 16.sp,
                            fontFamily = RobotoSlabFontFamily(),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .border(
                                width = 1.5.dp,
                                color = Color(0xFFAD8E7D),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(
                                color = Color(0xFF613923),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                onApply(currentFilterState)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Apply",
                            color = Color(0xFFAD8E7D),
                            fontSize = 16.sp,
                            fontFamily = RobotoSlabFontFamily(),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
    
    if (showDatePicker) {
        SimpleDatePickerDialog(
            currentDate = if (datePickerMode == DatePickerMode.FROM) currentFilterState.dateFrom else currentFilterState.dateTo,
            onDateSelected = { selectedDate ->
                if (datePickerMode == DatePickerMode.FROM) {
                    currentFilterState = currentFilterState.copy(dateFrom = selectedDate)
                } else {
                    currentFilterState = currentFilterState.copy(dateTo = selectedDate)
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun FilterDateField(
    label: String,
    date: String,
    onDateChange: (String) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalThemeColors.current
    val displayDate = remember(date) {
        if (date.isEmpty()) {
            TimeUtils.formatCurrentDateMMDDYY()
        } else {
            date
        }
    }
    
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    width = 1.5.dp,
                    color = themeColors.dialogStroke,
                    shape = RoundedCornerShape(12.dp)
                )
                .background(
                    color = themeColors.dialogBackground,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = label,
                    color = themeColors.dialogStroke,
                    fontSize = 11.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = displayDate,
                    color = themeColors.dialogText,
                    fontSize = 11.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val themeColors = LocalThemeColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .border(
                width = 1.5.dp,
                color = themeColors.dialogStroke,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = if (isSelected) themeColors.dialogChipSelectedBackground else themeColors.dialogBackground,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) themeColors.dialogChipSelectedText else themeColors.dialogText,
            fontSize = 10.sp,
            fontFamily = RobotoSlabFontFamily(),
            fontWeight = FontWeight.Normal,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SimpleDatePickerDialog(
    currentDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val themeColors = LocalThemeColors.current
    

    val initialYear = remember {
        if (currentDate.isNotEmpty()) {
            val parts = currentDate.split("/")
            if (parts.size == 3) (2000 + (parts[2].toIntOrNull() ?: 0)) else TimeUtils.getCurrentYear()
        } else TimeUtils.getCurrentYear()
    }
    
    val initialMonth = remember {
        if (currentDate.isNotEmpty()) {
            val parts = currentDate.split("/")
            if (parts.size == 3) ((parts[0].toIntOrNull() ?: 1) - 1) else TimeUtils.getCurrentMonth()
        } else TimeUtils.getCurrentMonth()
    }
    
    val initialDay = remember {
        if (currentDate.isNotEmpty()) {
            val parts = currentDate.split("/")
            if (parts.size == 3) (parts[1].toIntOrNull() ?: 1) else TimeUtils.getCurrentDay()
        } else TimeUtils.getCurrentDay()
    }
    
    var selectedYear by remember { mutableStateOf(initialYear) }
    var selectedMonth by remember { mutableStateOf(initialMonth) }
    var selectedDay by remember { mutableStateOf(initialDay) }
    
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .background(
                    color = themeColors.dialogBackground,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Date",
                    color = themeColors.dialogText,
                    fontSize = 16.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "${(selectedMonth + 1).toString().padStart(2, '0')}/${selectedDay.toString().padStart(2, '0')}/${(selectedYear % 100).toString().padStart(2, '0')}",
                    color = themeColors.dialogStroke,
                    fontSize = 20.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Month", color = themeColors.dialogStroke, fontSize = 11.sp, fontFamily = RobotoSlabFontFamily())
                        Spacer(modifier = Modifier.height(8.dp))
                        ScrollablePicker(
                            value = selectedMonth + 1,
                            range = 1..12,
                            onValueChange = { selectedMonth = it - 1 }
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(20.dp))
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Day", color = themeColors.dialogStroke, fontSize = 11.sp, fontFamily = RobotoSlabFontFamily())
                        Spacer(modifier = Modifier.height(8.dp))
                        ScrollablePicker(
                            value = selectedDay,
                            range = 1..31,
                            onValueChange = { selectedDay = it }
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(20.dp))
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Year", color = themeColors.dialogStroke, fontSize = 11.sp, fontFamily = RobotoSlabFontFamily())
                        Spacer(modifier = Modifier.height(8.dp))
                        ScrollablePicker(
                            value = selectedYear % 100,
                            range = 20..30,
                            onValueChange = { selectedYear = 2000 + it }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .border(1.5.dp, themeColors.dialogStroke, RoundedCornerShape(12.dp))
                            .background(themeColors.dialogBackground, RoundedCornerShape(12.dp))
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Cancel", color = themeColors.dialogText, fontSize = 14.sp, fontFamily = RobotoSlabFontFamily())
                    }
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .border(1.5.dp, themeColors.dialogStroke, RoundedCornerShape(12.dp))
                            .background(themeColors.dialogBackground, RoundedCornerShape(12.dp))
                            .clickable {
                                val dateString = "${(selectedMonth + 1).toString().padStart(2, '0')}/${selectedDay.toString().padStart(2, '0')}/${(selectedYear % 100).toString().padStart(2, '0')}"
                                onDateSelected(dateString)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("OK", color = themeColors.dialogText, fontSize = 14.sp, fontFamily = RobotoSlabFontFamily())
                    }
                }
            }
        }
    }
}

@Composable
fun ScrollablePicker(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    val themeColors = LocalThemeColors.current
    Column(
        modifier = Modifier
            .width(60.dp)
            .height(100.dp)
            .border(1.dp, themeColors.dialogStroke, RoundedCornerShape(8.dp))
            .background(themeColors.dialogBackground, RoundedCornerShape(8.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clickable {
                    if (value < range.last) onValueChange(value + 1)
                },
            contentAlignment = Alignment.Center
        ) {
            Text("▲", color = themeColors.dialogStroke, fontSize = 14.sp)
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(themeColors.dialogBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString().padStart(2, '0'),
                color = themeColors.dialogText,
                fontSize = 22.sp,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.Medium
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clickable {
                    if (value > range.first) onValueChange(value - 1)
                },
            contentAlignment = Alignment.Center
        ) {
            Text("▼", color = themeColors.dialogStroke, fontSize = 14.sp)
        }
    }
}
