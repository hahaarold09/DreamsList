package harold.delacerna.com.dreamlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by gwapogerald on 10/11/2017.
 */

public class DreamListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Dream> dreamsList;

    public DreamListAdapter(Context context, int layout, ArrayList<Dream> dreamsList) {
        this.context = context;
        this.layout = layout;
        this.dreamsList = dreamsList;
    }

    @Override
    public int getCount() {
        return dreamsList.size();
    }

    @Override
    public Object getItem(int position) {
        return dreamsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView txtName, txtPrice;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtName = (TextView) row.findViewById(R.id.etWishName);
            holder.txtPrice = (TextView) row.findViewById(R.id.etWishPrice);
            holder.imageView = (ImageView) row.findViewById(R.id.imgWish);
            row.setTag(holder);
        } else{
            holder = (ViewHolder) row.getTag();
        }

        Dream dream = dreamsList.get(position);

        holder.txtName.setText(dream.getName());
        holder.txtPrice.setText(dream.getPrice());

        byte[] dreamImage = dream.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(dreamImage, 0, dreamImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }
}
