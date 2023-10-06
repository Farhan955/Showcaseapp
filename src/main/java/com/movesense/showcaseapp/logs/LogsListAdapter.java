package com.movesense.showcaseapp.logs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.movesense.showcaseapp.BuildConfig;
import com.movesense.showcaseapp.google_drive.SendLogsToGoogleDriveActivity;

import java.io.File;
import java.util.List;

/**
 * Logs Adapter
 */

public class LogsListAdapter extends BaseAdapter {

    private final List<File> fileList;

    public LogsListAdapter(List<File> fileList) {
        this.fileList = fileList;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = ViewHolder.get(convertView, android.R.id.text1);

        File fileItem = (File) getItem(position);
        textView.setText(fileItem.getName());


        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                new AlertDialog.Builder(parent.getContext())
                        .setTitle("Alert!")
                        .setMessage("Do you want to delete the file?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                fileList.remove(position);
                                fileItem.delete();
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();
                            }
                        })
                        .show();

                return false;
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context context = parent.getContext();
                Toast.makeText(context, "Sending 1 " , Toast.LENGTH_SHORT).show();

                try {
                    final File clickedFile = fileList.get(position);
                    Uri uri = FileProvider.getUriForFile(context,
                            BuildConfig.APPLICATION_ID, clickedFile);

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        shareIntent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                    shareIntent.setType(getMimeType(clickedFile.getName()));
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "App");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    context.startActivity(Intent.createChooser(shareIntent, "Share"));

                } catch (Exception e) {

                    Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }

    private String getMimeType(String url) {
        String parts[] = url.split("\\.");
        String extension = parts[parts.length - 1];
        String type = null;
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private static class ViewHolder {

        @SuppressWarnings("unchecked")
        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<>();
                view.setTag(viewHolder);
            }

            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }
}
