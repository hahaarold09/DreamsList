package harold.delacerna.com.dreamlist;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {


    EditText mEtName, mEtPrice, mEtDesc;
    Button mBtnPick, mBtnAdd, mBtnList;
    ImageView mImgView;

    public static SQLiteHandler sqLiteHandler;
    final int REQUEST_CODE = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        sqLiteHandler = new SQLiteHandler(this, "WishDB.sqlite", null, 1);
        sqLiteHandler.queryData("DROP TABLE IF EXISTS WISH");
        sqLiteHandler.queryData("CREATE TABLE IF NOT EXISTS WISH (Id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, price VARCHAR, description VARCHAR, image BLOG)");


        mBtnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CODE);
            
            }
        });

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sqLiteHandler.insertData(
                            mEtName.getText().toString().trim(),
                            mEtPrice.getText().toString().trim(),
                            mEtDesc.getText().toString().trim(),
                            imageViewToByte(mImgView)
                    );
                    Toast.makeText(getApplicationContext(), "SUCCESSFUL", Toast.LENGTH_SHORT).show();
                    mEtName.setText("");
                    mEtPrice.setText("");
                    mEtDesc.setText("");
                    mImgView.setImageResource(R.mipmap.ic_launcher);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "FAILED", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBtnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DreamList.class);
                startActivity(intent);
            }
        });

    }



    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
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

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                mImgView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void findViews() {
        mEtName = (EditText) findViewById(R.id.etName);
        mEtPrice = (EditText) findViewById(R.id.etPrice);
        mBtnPick = (Button) findViewById(R.id.btnPick);
        mBtnAdd = (Button) findViewById(R.id.btnAdd);
        mBtnList = (Button) findViewById(R.id.btnList);
        mImgView = (ImageView) findViewById(R.id.imageView);
        mEtDesc = (EditText) findViewById(R.id.etDesc);
    }


}
