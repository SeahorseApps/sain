package com.sain.azmoon;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.sain.azmoon.adapters.EndpointListAdapter;
import com.sain.azmoon.helpers.EndPointUriProcessor;
import com.sain.azmoon.helpers.Utils;

import java.util.Map;

public class EndpointSettingsActivity extends AppCompatActivity
{
    private ListView endpointsListView;
    private Button saveEndpointsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endpoint_settings);

        saveEndpointsButton = findViewById(R.id.saveEndpointsButton);
        saveEndpointsButton.setOnClickListener(this::saveEndpointsButtonClicked);

        endpointsListView = findViewById(R.id.endpointsList);
        EndpointListAdapter adapter = new EndpointListAdapter(this, R.layout.endpoint_view_item, EndPointUriProcessor.getListViewItems(this));
        endpointsListView.setAdapter(adapter);
    }

    private void saveEndpointsButtonClicked(View v)
    {
        Utils.showMessageBoxYesNo(this, "ذخیره", "مقادیر ذخیره شوند؟",
                (dlg, w) ->
                {
                    Map<String, String> contents = ((EndpointListAdapter) endpointsListView.getAdapter()).getContents();

                    for (Map.Entry<String, String> item : contents.entrySet())
                        EndPointUriProcessor.setSavedEndPoint(this, item.getKey(), item.getValue());

                    dlg.dismiss();
                },
                (dlg, w) ->
                {
                    dlg.dismiss();
                });
    }
}