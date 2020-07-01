package com.sain.azmoon.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.sain.azmoon.R;
import com.sain.azmoon.models.EndpointDataModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EndpointListAdapter extends ArrayAdapter<EndpointDataModel>
{
    private Context context;
    private int resource;
    private Map<String, String> contents;

    public EndpointListAdapter(@NonNull Context context, int resource, @NonNull EndpointDataModel[] objects)
    {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;

        contents = new HashMap<>();
        for (EndpointDataModel item : objects)
        {
            contents.put(item.Name, item.Value);
        }
    }

    public Map<String, String> getContents()
    {
        return contents;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        EndpointDataModel model=getItem(position);

        if(model!=null)
        {
            convertView.setTag(model.Name);

            ((TextView) convertView.findViewById(R.id.endpointNameText)).setText(model.Name);

            EditText et= convertView.findViewById(R.id.endpointValueInput);
            et.setText(getContent(model.Name));
            et.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {

                }

                @Override
                public void afterTextChanged(Editable s)
                {
                    contents.put(model.Name, s.toString());
                }
            });
        }

        return convertView;
    }

    private String getContent(String name)
    {
        if (contents.containsKey(name))
            return contents.get(name);
        else
            return null;
    }
}
