package com.example.habitup;
import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;

import com.example.habitup.Controller.ElasticSearchController;
import com.example.habitup.Model.Habit;
import com.example.habitup.Model.UserAccount;
import com.example.habitup.View.LoginActivity;
import com.example.habitup.View.MainActivity;
import com.example.habitup.View.ViewHabitActivity;
import com.robotium.solo.Solo;

import java.util.ArrayList;
import java.util.Collections;


public class ViewHabitActivityTest extends ActivityInstrumentationTestCase2 {

    private Solo solo;

    public ViewHabitActivityTest() {
        super(ViewHabitActivity.class);
    }

    public void setUp() throws Exception {
        // get user info
        ElasticSearchController.GetUser getUser = new ElasticSearchController.GetUser();
        getUser.execute("tatata");

        UserAccount user = new UserAccount("tatata","tatata",null);
        try {
            user = getUser.get().get(0);
        }
        catch (Exception e) {
            //nothing here
        }

        // get user habits
        ElasticSearchController.GetHabitsTask getHabits = new ElasticSearchController.GetHabitsTask();
        getHabits.execute("tatata");
        ArrayList<Habit> habits = new ArrayList<Habit>();
        try {
            habits.addAll(getHabits.get());
            Collections.sort(habits);
        }
        catch (Exception e) {
            Log.d("Error", "Failed to retrieve habits from ES");
        }

        solo = new Solo(getInstrumentation(),getActivity());
    }

    public void testStart() throws Exception {
        Activity activity = getActivity();
    }

    public void testUser() {
        solo.assertCurrentActivity("Wrong activity", ViewHabitActivity.class);
        assertTrue(solo.waitForText("Habits"));
    }

}
