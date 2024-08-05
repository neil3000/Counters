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

package dev.rahmouni.neil.counters.core.model.data

data class PhoneInfo(
    val number: String?,
    val code: Country?,
) {
    fun isValid(): Boolean {
        if (number == null || code == null) {
            return false
        }

        return number.matches(Regex(code.regex))
    }

    fun getFormatedNumber(): String? {
        return if (isValid()) {
            val formattedNumber = number?.removePrefix("0")
            "+${code?.phoneCode}$formattedNumber"
        } else {
            null
        }
    }
}
