package xyz.dnglabs.inventorysql;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import xyz.dnglabs.inventorysql.data.ProductContract.ProductEntry;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int PRODUCT_LOADER = 0;
    private Uri mCurrentProductUri;

    ImageView mProductImage;
    TextView mProductName;
    TextView mProductPrice;
    TextView mProductQuantity;
    ImageButton mPlusButton;
    ImageButton mMinusButton;
    ImageButton mOrderButton;
    ImageButton mDeleteProduct;
    EditText setQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mProductName = (TextView) findViewById(R.id.name_details);
        mProductPrice = (TextView) findViewById(R.id.price_details);
        mProductQuantity = (TextView) findViewById(R.id.quantity_details);
        mPlusButton = (ImageButton) findViewById(R.id.details_plus);
        mMinusButton = (ImageButton) findViewById(R.id.details_minus);
        mOrderButton = (ImageButton) findViewById(R.id.details_cart);
        mDeleteProduct = (ImageButton) findViewById(R.id.details_delete);
        mProductImage = (ImageView) findViewById(R.id.image_details);
        setQuantity = (EditText) findViewById(R.id.input_quantity_details);


        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        if(mCurrentProductUri != null) {
            getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                mCurrentProductUri,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
        if(cursor.moveToFirst()) {
            int getName = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int getPrice = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            final int getQuantity = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int getImage = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);

            final String name = cursor.getString(getName);
            final double price = cursor.getDouble(getPrice);
            int quantity = cursor.getInt(getQuantity);
            final int quantity2 = cursor.getInt(getQuantity);
            byte[] imageByteArray = cursor.getBlob(getImage);

            mProductName.setText(name);
            mProductPrice.setText(Double.toString(price));
            mProductQuantity.setText(Integer.toString(quantity));

            Bitmap productImage = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
            mProductImage.setImageBitmap(productImage);

            mPlusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int flagQuantity;
                    String checkValue = setQuantity.getText().toString();
                    if (checkValue.equals("")){
                        flagQuantity = 1;
                    } else {
                        flagQuantity = Integer.parseInt(checkValue);
                    }
                    int sendQuantity = quantity2 + flagQuantity;
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, sendQuantity);
                    getContentResolver().update(ContentUris.withAppendedId(ProductEntry.CONTENT_URI, (cursor.getInt(cursor.getColumnIndex(ProductEntry._ID)))), values, null, null);
                }
            });

            mMinusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int flagQuantity = 0;
                    String checkValue = setQuantity.getText().toString();
                    if (checkValue.equals("")){
                        flagQuantity = 1;
                    } else {
                        flagQuantity = Integer.parseInt(checkValue);
                    }
                        int sendQuantity = 0;
                    if ((quantity2 - flagQuantity) <= 0){
                        sendQuantity = 0;
                    } else {
                        sendQuantity = quantity2 - flagQuantity;
                    }
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, sendQuantity);
                    getContentResolver().update(ContentUris.withAppendedId(ProductEntry.CONTENT_URI, (cursor.getInt(cursor.getColumnIndex(ProductEntry._ID)))), values, null, null);
                }
            });

            mDeleteProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this, R.style.MyDialogTheme);
                    builder.setMessage(R.string.delete_message);
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getContentResolver().delete(ContentUris.withAppendedId(ProductEntry.CONTENT_URI, (cursor.getInt(cursor.getColumnIndex(ProductEntry._ID)))), null, null);
                            finish();
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });

            mOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, "supplier@email.com");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Order " + name);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
