package xyz.dnglabs.inventorysql;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import xyz.dnglabs.inventorysql.data.ProductContract.ProductEntry;

public class ProductsCursorAdapter extends CursorAdapter {

    public ProductsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView productName = (TextView) view.findViewById(R.id.product_name);
        TextView productPrice = (TextView) view.findViewById(R.id.product_price);
        TextView productQuantity = (TextView) view.findViewById(R.id.product_quantity);

        final int productId = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));
        String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        Double price = cursor.getDouble(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
        final int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));

        productName.setText(name);
        productPrice.setText(String.valueOf(price));
        productQuantity.setText(String.valueOf(quantity));

        Button saleButton = (Button) view.findViewById(R.id.sale_button);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0){
                    int avaliable = quantity - 1;
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, avaliable );
                    context.getContentResolver().update(ContentUris.withAppendedId(ProductEntry.CONTENT_URI, productId), values, null, null);
                }
            }
        });
    }


}

