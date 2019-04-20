package com.example.taskq;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        int a = intent.getIntExtra("currentStreak", 0);
        ArrayList<Integer> b = intent.getIntegerArrayListExtra("maxStreak");
        String title = intent.getStringExtra("title");

        AnyChartView anyChartView = findViewById(R.id.graph);
        anyChartView.setProgressBar(findViewById(R.id.top_progress_bar));

        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();

        for (int i = 0; i < b.size() - 1; i++) {
            data.add(new ValueDataEntry((i + 1) + "Max", b.get(i)));
        }
        data.add(new ValueDataEntry("Last Max Streak", b.get(b.size() - 1)));
        data.add(new ValueDataEntry("Current Streak", a));

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }");
        cartesian.animation(true);
        cartesian.title("Your Performance Streak for " + title);
        cartesian.yScale().minimum(0d).ticks().allowFractional(false);
        cartesian.xAxis(0).labels().format("{%Value}{groupsSeparator:}");
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);
        cartesian.xAxis(0).title("Streaks");
        cartesian.yAxis(0).title("Days");
        anyChartView.setChart(cartesian);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
