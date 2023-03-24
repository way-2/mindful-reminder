package com.example.mindful_reminder.fragments;

import static com.example.mindful_reminder.config.Constants.ENABLE_GRATITUDE_TUTORIAL;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.example.mindful_reminder.R;
import com.example.mindful_reminder.databases.AppDatabase;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;
import com.kizitonwose.calendar.view.ViewContainer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class GratitudeJournalCalendar extends Fragment {

    private CalendarView calendarView;
    private TextView gratitudeJournalDayEntry;
    private List<DayOfWeek> daysOfWeek;
    private LocalDate selectedDate;
    private LocalDate oldDate;
    private List<LocalDate> databaseDates;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gratitude_journal_calendar, container, false);
        checkIfRunTutorial();
        getExistingDates();
        setupCalendarView(view);
        return view;
    }

    private void setupCalendarView(View view) {
        gratitudeJournalDayEntry = (TextView) view.findViewById(R.id.gratitude_journal_day_entry);
        calendarView = (CalendarView) view.findViewById(R.id.gratitude_journal_events_calendar);
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        if (sharedPreferences.getBoolean(ENABLE_GRATITUDE_TUTORIAL, true)) {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction().replace(R.id.fragment_frame, new GratitudeJournalStart()).addToBackStack(null).commit();
        }
    }

    private void getExistingDates() {
        AppDatabase database = null;
        try {
            if (databaseDates == null) {
                database = AppDatabase.getInstance(requireContext());
                databaseDates = database.gratitudeJournalDao().getEntryDates().get();
            }
        } catch (ExecutionException | InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            database.cleanUp();
            database = null;
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
        TextView monthHeader = (TextView) container.getView().findViewById(R.id.month_title);
        monthHeader.setText(calendarMonth.getYearMonth().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        if (null == container.titlesContainer.getTag()) {
            container.titlesContainer.setTag(calendarMonth.getYearMonth());
            for (int index = 0; index < (((ViewGroup) container.titlesContainer).getChildCount()); index++) {
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
            container.textView.setTextColor(getResources().getColor(R.color.md_theme_dark_outline, requireContext().getTheme()));
        } else {
            if (calendarDay.getDate() == selectedDate) {
                setSelectedDateBackground(container, calendarDay);
            } else {
                setNotSelectedDateBackground(container, calendarDay);
            }
            if ((calendarDay.getDate() != selectedDate) && (calendarDay.getDate() != oldDate)) {
                gratitudeJournalDayEntry.setVisibility(View.GONE);
            }
        }
    }

    private void setNotSelectedDateBackground(DayViewContainer container, CalendarDay calendarDay) {
        if (databaseDates.contains(calendarDay.getDate())) {
            container.getView().setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.calendar_day_entry_background, requireContext().getTheme()));
        } else {
            container.getView().setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.calendar_day_background, requireContext().getTheme()));
        }
    }

    private void setSelectedDateBackground(DayViewContainer container, CalendarDay calendarDay) {
        if (databaseDates.contains(calendarDay.getDate())) {
            container.getView().setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selected_calendar_day_entry_background, requireContext().getTheme()));
        } else {
            container.getView().setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selected_calendar_day_background, requireContext().getTheme()));
        }
    }

    static class MonthViewContainer extends ViewContainer {
        ViewGroup titlesContainer;
        public MonthViewContainer(@NonNull View view) {
            super(view);
            titlesContainer = (ViewGroup) view.findViewById(R.id.calendar_day_titles_container);
        }
    }

    class DayViewContainer extends ViewContainer {
        TextView textView;
        CalendarDay day;
        public DayViewContainer(@NonNull View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.calendarDayText);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView entryTextView = (TextView) view.getRootView().findViewById(R.id.gratitude_journal_day_entry);
                    if (day.getPosition() == DayPosition.MonthDate) {
                        setSelectedOldDate(day);
                        DateTimeFormatter HEADER_DATE_FORMAT = DateTimeFormatter.ofPattern("MMMM dd yyyy");
                        AppDatabase database = null;
                        try {
                            database = AppDatabase.getInstance(requireContext());
                            String entryString = database.gratitudeJournalDao().getEntryForDate(day.getDate()).get();
                            String dateString = day.getDate().format(HEADER_DATE_FORMAT);
                            String displayString = "On " + dateString + ", I was grateful for...\n" + entryString;
                            if (null != entryString) {
                                entryTextView.setText(displayString);
                                entryTextView.setVisibility(View.VISIBLE);
                            } else {
                                entryTextView.setVisibility(View.GONE);
                            }
                        } catch (ExecutionException | InterruptedException ex) {
                            ex.printStackTrace();
                        } finally {
                            database.cleanUp();
                            database = null;
                        }
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