package rahmouni.neil.counters.testViews

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import rahmouni.neil.counters.MainActivity

class HomeScreenNewEntryModalTestView(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>,
    private val counterName: String? = null
) {
    //----------- ACTIONS ----------//

    /*fun addEntry(): HomeScreenTestView {
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.action_addEntry))
            .performClick()

        return HomeScreenTestView(composeTestRule, counterName)
    }*/

    //---------- ASSERTS ----------//


}