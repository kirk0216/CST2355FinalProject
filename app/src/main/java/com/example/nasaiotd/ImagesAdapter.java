package com.example.nasaiotd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ImagesAdapter extends BaseAdapter {

    private final LayoutInflater layoutInflater;
    private final List<ImageData> images = new ArrayList<ImageData>();

    public ImagesAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    /**
     * Adds a new ImageData object.
     * @param image The ImageData object to add.
     */
    public void add(ImageData image) {
        images.add(image);
    }

    public void insertAt(int position, ImageData image) {
        images.add(position, image);
    }

    /**
     * Removes the ImageData at index position.
     * @param position The index in the array of the item.
     */
    public void removeAt(int position) {
        images.remove(position);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long)position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageData imageData = images.get(position);

        View view = layoutInflater.inflate(R.layout.imagelist_item, parent, false);

        ImageView imageImage = view.findViewById(R.id.ImageListItemImage);
        imageImage.setImageBitmap(imageData.getImage());

        TextView imageDate = view.findViewById(R.id.ImageDate);
        imageDate.setText(imageData.getDate());

        TextView imageTitle = view.findViewById(R.id.ImageTitle);
        imageTitle.setText(imageData.getTitle());

        return view;
    }
}
