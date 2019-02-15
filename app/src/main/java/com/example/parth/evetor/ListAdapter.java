package com.example.parth.evetor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import java.util.ArrayList;

import javax.security.auth.Subject;


public class ListAdapter extends ArrayAdapter<Event> {

    public ArrayList<Event> MainList;

    public ArrayList<Event> EventListTemp;

    public ListAdapter.EventDataFilter eventDataFilter ;

    public ListAdapter(Context context, int id, ArrayList<Event> eventArrayList) {

        super(context, id, eventArrayList);

        this.EventListTemp = new ArrayList<Event>();

        this.EventListTemp.addAll(eventArrayList);

        this.MainList = new ArrayList<Event>();

        this.MainList.addAll(eventArrayList);
    }

    @Override
    public Filter getFilter() {

        if (eventDataFilter == null){

            eventDataFilter  = new ListAdapter.EventDataFilter();
        }
        return eventDataFilter;
    }


    public class ViewHolder {

        TextView EventName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ListAdapter.ViewHolder holder = null;

        if (convertView == null) {

            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.custom_layout, null);

            holder = new ListAdapter.ViewHolder();

            holder.EventName = (TextView) convertView.findViewById(R.id.textviewName);


            convertView.setTag(holder);

        } else {
            holder = (ListAdapter.ViewHolder) convertView.getTag();
        }

        Event event = EventListTemp.get(position);

        holder.EventName.setText(event.getEveName());


        return convertView;

    }

    private class EventDataFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            charSequence = charSequence.toString().toLowerCase();

            FilterResults filterResults = new FilterResults();

            if(charSequence != null && charSequence.toString().length() > 0)
            {
                ArrayList<Event> arrayList1 = new ArrayList<Event>();

                for(int i = 0, l = MainList.size(); i < l; i++)
                {
                    Event event = MainList.get(i);

                    if(event.toString().toLowerCase().contains(charSequence))

                        arrayList1.add(event);
                }
                filterResults.count = arrayList1.size();

                filterResults.values = arrayList1;
            }
            else
            {
                synchronized(this)
                {
                    filterResults.values = MainList;

                    filterResults.count = MainList.size();
                }
            }
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            EventListTemp = (ArrayList<Event>)filterResults.values;

            notifyDataSetChanged();

            clear();

            for(int i = 0, l = EventListTemp.size(); i < l; i++)
                add(EventListTemp.get(i));

            notifyDataSetInvalidated();
        }
    }


}
