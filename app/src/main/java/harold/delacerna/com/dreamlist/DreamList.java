package harold.delacerna.com.dreamlist;



import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by gwapogerald on 10/11/2017.
 */

class DreamList extends AppCompatActivity {
    private static final int REQUEST_CODE = 888 ;
    GridView gridView;
    ArrayList<Dream> list;
    DreamListAdapter adapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dream_list_activity);

        gridView = (GridView) findViewById(R.id.gridView);
        list = new ArrayList<>();
        adapter = new DreamListAdapter(this, R.layout.dream_items, list);
        gridView.setAdapter(adapter);

        // get all data from sqlite
        Cursor cursor = MainActivity.sqLiteHandler.getData("SELECT * FROM WISH");
        list.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String price = cursor.getString(2);
            String desc = cursor.getString(3);
            byte[] image = cursor.getBlob(4);

            list.add(new Dream(id, name, price, desc, image));
        }
        adapter.notifyDataSetChanged();

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                CharSequence [] items = {"Update", "Delete"};
                final AlertDialog.Builder dialog = new AlertDialog.Builder(DreamList.this);

                dialog.setTitle("Choose an action");
                AlertDialog.Builder builder = dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        if (item == 0) {
                            // update
                            Cursor c = MainActivity.sqLiteHandler.getData("SELECT id FROM WISH");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()) {
                                arrID.add(c.getInt(0));
                            }
                            // show dialog update at here
                            showDialogUpdate(DreamList.this, arrID.get(position));

                        } else {
                            // delete
                            Cursor c = MainActivity.sqLiteHandler.getData("SELECT id FROM WISH");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()) {
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));
                        }

                    }
                });
                dialog.show();
                return true;
            }
        });
    }



    ImageView imageViewDream;
    private void showDialogUpdate(DreamList activity, final int position){
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_dream_activity);
        dialog.setTitle("Update");

        imageViewDream = (ImageView) dialog.findViewById(R.id.imgWishUpdate);
        final EditText etName = (EditText) dialog.findViewById(R.id.etNameUpdate);
        final EditText etPrice = (EditText) dialog.findViewById(R.id.etPriceUpdate);
        final EditText etDesc = (EditText) dialog.findViewById(R.id.etDescUpdate);
        Button btnUpdate = (Button) dialog.findViewById(R.id.btnUpdate);

        // set width for dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        // set height for dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.95);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        imageViewDream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // request photo library
                ActivityCompat.requestPermissions(
                        DreamList.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE
                );
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MainActivity.sqLiteHandler.updateData(
                            etName.getText().toString().trim(),
                            etPrice.getText().toString().trim(),
                            etDesc.getText().toString().trim(),
                            MainActivity.imageViewToByte(imageViewDream),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "UPDATE SUCCESSFUL", Toast.LENGTH_SHORT).show();

                } catch (Exception e){
                    Log.e("Update error: ", e.getMessage());
                }
                updateDreamList();
            }
        });
    }

    private void showDialogDelete(final int idDream){
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(DreamList.this);

        dialogDelete.setTitle("WARNING");
        dialogDelete.setMessage("Are you sure you want to delete this?");
        dialogDelete.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                try {
                    MainActivity.sqLiteHandler.deleteData(idDream);
                    Toast.makeText(getApplicationContext(), "DELETE SUCCESSFUL", Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    Log.e("Error: ", e.getMessage());
                }
                updateDreamList();
            }
        });

        dialogDelete.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    private void updateDreamList(){
        Cursor cursor = MainActivity.sqLiteHandler.getData("SELECT * FROM WISH");
        list.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String price = cursor.getString(2);
            String desc = cursor.getString(3);
            byte[] image = cursor.getBlob(4);

            list.add(new Dream(id, name, price, desc, image));
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageViewDream.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
