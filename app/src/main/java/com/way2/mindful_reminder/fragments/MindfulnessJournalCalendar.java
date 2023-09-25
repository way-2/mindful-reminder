package com.way2.mindful_reminder.fragments;

import static com.way2.mindful_reminder.config.Constants.ENABLE_MINDFULNESS_TUTORIAL;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;
import com.kizitonwose.calendar.view.ViewContainer;
import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.databases.AppDatabase;
import com.way2.mindful_reminder.entities.JournalEntry;
import com.way2.mindful_reminder.util.MindfulReminder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MindfulnessJournalCalendar extends Fragment {

    private CalendarView calendarView;
    private LinearLayout journalReviewLayout;
    private TextView thisDaysText;
    private List<DayOfWeek> daysOfWeek;
    private LocalDate selectedDate;
    private LocalDate oldDate;
    private List<LocalDate> databaseDates;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mindfulness_journal_calendar, container, false);
        checkIfRunTutorial();
        getExistingDates();
        setupCalendarView(view);
        return view;
    }

    private void setupCalendarView(View view) {
        thisDaysText = view.findViewById(R.id.this_days_affirmation);
        journalReviewLayout = view.findViewById(R.id.journal_review_layout);
        calendarView = view.findViewById(R.id.mindfulness_journal_events_calendar);
        YearMonth current = YearMonth.now();
        YearMonth start = current.minusMonths(100);
        YearMonth end = current.plusMonths(100);
        daysOfWeek = com.kizitonwose.calendar.core.ExtensionsKt.daysOfWeek(DayOfWeek.SUNDAY);
        calendarView.setup(start, end, DayOfWeek.SUNDAY);
        calendarView.setDayBinder(getMonthDayBinder());
        calendarView.setMonthHeaderBinder(getMonthHeaderBinder());
        calendarView.scrollToMonth(current);
    }

    private void checkIfRunTutorial() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MindfulReminder.getContext());
        if (sharedPreferences.getBoolean(ENABLE_MINDFULNESS_TUTORIAL, true)) {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction().replace(R.id.fragment_frame, new MindfulnessJournalStart()).addToBackStack(null).commit();
        }
    }

    private void getExistingDates() {
        AppDatabase database = null;
        try {
            if (databaseDates == null) {
                database = AppDatabase.getInstance();
                databaseDates = database.gratitudeJournalDao().getEntryDates().get();
            }
        } catch (ExecutionException | InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            database.cleanUp();
        }
    }

    private MonthHeaderFooterBinder<?> getMonthHeaderBinder() {
        return new MonthHeaderFooterBinder<MonthViewContainer>() {
            @NonNull
            @Override
            public MonthViewContainer create(@NonNull View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(@NonNull MonthViewContainer container, CalendarMonth calendarMonth) {
                monthHeaderFooterBind(container, calendarMonth);
            }
        };
    }

    private void monthHeaderFooterBind(@NonNull MonthViewContainer container, CalendarMonth calendarMonth) {
        TextView monthHeader = container.getView().findViewById(R.id.month_title);
        monthHeader.setText(calendarMonth.getYearMonth().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        if (null == container.titlesContainer.getTag()) {
            container.titlesContainer.setTag(calendarMonth.getYearMonth());
            for (int index = 0; index < (container.titlesContainer.getChildCount()); index++) {
                TextView textView = (TextView) container.titlesContainer.getChildAt(index);
                DayOfWeek dayOfWeek = daysOfWeek.get(index);
                String title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault());
                textView.setText(title);
            }
        }
    }

    private MonthDayBinder<?> getMonthDayBinder() {
        return new MonthDayBinder<DayViewContainer>() {

            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(@NonNull DayViewContainer container, CalendarDay calendarDay) {
                monthDayBinderBind(container, calendarDay);
            }
        };
    }

    private void monthDayBinderBind(DayViewContainer container, CalendarDay calendarDay) {
        container.textView.setText(String.valueOf(calendarDay.getDate().getDayOfMonth()));
        container.day = calendarDay;
        if (calendarDay.getPosition() != DayPosition.MonthDate) {
            container.textView.setTextColor(com.google.android.material.R.attr.colorOutlineVariant);
        } else {
            if (calendarDay.getDate() == selectedDate) {
                setSelectedDateBackground(container, calendarDay);
            } else {
                setNotSelectedDateBackground(container, calendarDay);
            }
            if ((calendarDay.getDate() != selectedDate) && (calendarDay.getDate() != oldDate)) {
                thisDaysText.setVisibility(View.GONE);
                journalReviewLayout.setVisibility(View.GONE);
            }
        }
    }

    private void setNotSelectedDateBackground(DayViewContainer container, CalendarDay calendarDay) {
        if (databaseDates.contains(calendarDay.getDate())) {
            container.getView().setBackgroundResource(R.drawable.calendar_day_entry_background);
        } else {
            container.getView().setBackgroundResource(R.drawable.calendar_day_background);
        }
    }

    private void setSelectedDateBackground(DayViewContainer container, CalendarDay calendarDay) {
        if (databaseDates.contains(calendarDay.getDate())) {
            container.getView().setBackgroundResource(R.drawable.selected_calendar_day_entry_background);
        } else {
            container.getView().setBackgroundResource(R.drawable.selected_calendar_day_background);
        }
    }

    static class MonthViewContainer extends ViewContainer {
        final ViewGroup titlesContainer;
        public MonthViewContainer(@NonNull View view) {
            super(view);
            titlesContainer = view.findViewById(R.id.calendar_day_titles_container);
        }
    }

    class DayViewContainer extends ViewContainer {
        final TextView textView;
        CalendarDay day;
        public DayViewContainer(@NonNull View view) {
            super(view);
            textView = view.findViewById(R.id.calendarDayText);
            view.setOnClickListener(v -> {
                TextView entryTextView = (TextView) view.getRootView().findViewById(R.id.mindfulness_journal_day_entry);
                TextView entryHeaderText = (TextView) view.getRootView().findViewById(R.id.entry_header_text);
                TextView howWasITodayJournalDayEntry = (TextView) view.getRootView().findViewById(R.id.how_was_i_today_entry);
                TextView thisDaysText = (TextView) view.getRootView().findViewById(R.id.this_days_affirmation);
                LinearLayout journalReviewLayout = (LinearLayout) view.getRootView().findViewById(R.id.journal_review_layout);
                if (day.getPosition() == DayPosition.MonthDate) {
                    setSelectedOldDate(day);
                    DateTimeFormatter HEADER_DATE_FORMAT = DateTimeFormatter.ofPattern("MMMM dd yyyy");
                    AppDatabase database = null;
                    try {
                        database = AppDatabase.getInstance();
                        JournalEntry journalEntry = database.gratitudeJournalDao().getEntryForDate(day.getDate()).get();
                        String dateString = day.getDate().format(HEADER_DATE_FORMAT);
                        String headerString = "On " + dateString + "...";
                        if (journalEntry != null) {
                            entryHeaderText.setText(headerString);
                            if (null != journalEntry.getGratitudeEntry()) {
                                String displayString = "I was grateful for...\n" + journalEntry.getGratitudeEntry();
                                entryTextView.setText(displayString);
                            }
                            if (null != journalEntry.getFeelingEntry()) {
                                howWasITodayJournalDayEntry.setText(journalEntry.getFeelingEntry());
                            }
                            if (null != journalEntry.getDailyAffirmation()) {
                                String affirmationDisplayString = "My affirmation was...\n" + journalEntry.getDailyAffirmation();
                                thisDaysText.setText(affirmationDisplayString);
                            }
                            thisDaysText.setVisibility(View.VISIBLE);
                            journalReviewLayout.setVisibility(View.VISIBLE);
                        } else {
                            journalReviewLayout.setVisibility(View.GONE);
                            thisDaysText.setVisibility(View.GONE);
                        }
                    } catch (ExecutionException | InterruptedException ex) {
                        ex.printStackTrace();
                    } finally {
                        database.cleanUp();
                        database = null;
                    }
                }
            });
        }

        private void setSelectedOldDate(CalendarDay day) {
            if (selectedDate == day.getDate()) {
                selectedDate = null;
                calendarView.notifyDayChanged(day);
            } else {
                oldDate = selectedDate;
                selectedDate = day.getDate();
                calendarView.notifyDateChanged(day.getDate());
                if (oldDate != null) {
                    calendarView.notifyDateChanged(oldDate);
                }
            }
        }
    }

}