package xyz.dnglabs.inventorysql;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import xyz.dnglabs.inventorysql.data.ProductContract.ProductEntry;

public class EditorActivity extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private ImageButton addImage;
    private ImageButton addProduct;
    Uri uriImage = null;
    ImageView mImageView;
    int flagImage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mNameEditText = (EditText) findViewById(R.id.input_name);
        mPriceEditText = (EditText) findViewById(R.id.input_price);
        mQuantityEditText = (EditText) findViewById(R.id.input_quantity);
        addImage = (ImageButton) findViewById(R.id.add_image_button);
        addProduct = (ImageButton) findViewById(R.id.add_save_button);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getImage;
                getImage = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                getImage.addCategory(Intent.CATEGORY_OPENABLE);
                getImage.setType("image/*");
                startActivityForResult(getImage, 42);
            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameString = mNameEditText.getText().toString();
                String priceString = mPriceEditText.getText().toString();
                String quantityString = mQuantityEditText.getText().toString();

                if (nameString.length() > 0 && priceString.length() > 0 && quantityString.length() > 0 && flagImage == 1) {
                    double price = Double.valueOf(priceString);
                    int quantity = Integer.valueOf(quantityString);
                    Bitmap bitmapProduct = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream imageArray = new ByteArrayOutputStream();
                    bitmapProduct.compress(Bitmap.CompressFormat.PNG, 100, imageArray);
                    byte[] imageData = imageArray.toByteArray();

                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
                    values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
                    values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, imageData);
                    getContentResolver().insert(ProductEntry.CONTENT_URI, values);
                    finish();
                } else {
                    Toast.makeText(EditorActivity.this, getString(R.string.error_field), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == 42 && resultCode == EditorActivity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, resultData);
            if (resultData != null) {
                try {
                    uriImage = resultData.getData();
                    Bitmap imageProduct = MediaStore.Images.Media.getBitmap(getContentResolver(), uriImage);
                    mImageView = (ImageView) findViewById(R.id.image_product);
                    mImageView.setImageBitmap(imageProduct);
                    flagImage = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

