/*
 * Copyright (C) 2024 Rahmouni Neïl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.rahmouni.neil.counters.core.designsystem.paddingValues

import androidx.compose.ui.unit.dp

object Rn3PaddingValuesDirection {
    val START = Rn3PaddingValues(start = 1.dp)
    val TOP = Rn3PaddingValues(top = 1.dp)
    val END = Rn3PaddingValues(end = 1.dp)
    val BOTTOM = Rn3PaddingValues(bottom = 1.dp)

    val HORIZONTAL = START + END
    val VERTICAL = TOP + BOTTOM
}
