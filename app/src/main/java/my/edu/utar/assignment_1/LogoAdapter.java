package my.edu.utar.assignment_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class LogoAdapter extends ArrayAdapter<LogoItem> {
    private List<LogoItem> fullList;
    private List<LogoItem> filteredList;

    public LogoAdapter(@NonNull Context context, @NonNull List<LogoItem> objects) {
        super(context, 0, objects);
        this.fullList = new ArrayList<>(objects);
        this.filteredList = new ArrayList<>(objects);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<LogoItem> suggestions = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    suggestions.addAll(fullList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (LogoItem item : fullList) {
                        if (item.getName().toLowerCase().contains(filterPattern)) {
                            suggestions.add(item);
                        }
                    }
                }

                results.values = suggestions;
                results.count = suggestions.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                addAll((List) results.values);
                notifyDataSetChanged();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((LogoItem) resultValue).getName();
            }
        };
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_logo_dropdown, parent, false);
        }

        LogoItem item = getItem(position);
        if (item != null) {
            ImageView icon = convertView.findViewById(R.id.ivLogoIcon);
            TextView name = convertView.findViewById(R.id.tvLogoName);

            icon.setImageResource(item.getIconRes());
            name.setText(item.getName());
        }

        return convertView;
    }
}
