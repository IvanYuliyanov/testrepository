package com.example.bahk0.firstapp;

import android.app.Activity;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends Activity {
    GestureOverlayView gestureView;
    String path;
    File file;
    Bitmap bitmap;
    public boolean gestureTouch=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        createSignature();
        //test
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createSignature() {
        Button donebutton = (Button) findViewById(R.id.DoneButton);
        donebutton.setText("Done");
        Button clearButton = (Button) findViewById(R.id.ClearButton);
        clearButton.setText("Clear");

        path = Environment.getExternalStorageDirectory() + "/signature.png";
        file = new File(path);
        file.delete();
        gestureView = (GestureOverlayView) findViewById(R.id.signaturePad);
        gestureView.setDrawingCacheEnabled(true);

        gestureView.setAlwaysDrawnWithCacheEnabled(true);
        gestureView.setHapticFeedbackEnabled(false);
        gestureView.cancelLongPress();
        gestureView.cancelClearAnimation();
        gestureView.addOnGestureListener(new GestureOverlayView.OnGestureListener() {

            @Override
            public void onGesture(GestureOverlayView arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onGestureCancelled(GestureOverlayView arg0,
                                           MotionEvent arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onGestureEnded(GestureOverlayView arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onGestureStarted(GestureOverlayView arg0,
                                         MotionEvent arg1) {
                // TODO Auto-generated method stub
                if (arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    gestureTouch = false;
                } else {
                    gestureTouch = true;
                }
            }
        });

        donebutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap = Bitmap.createBitmap(gestureView.getDrawingCache());
                    Bitmap b = Bitmap.createScaledBitmap(bitmap,150,150,false);
                    /*file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    fos = new FileOutputStream(file);
                    // compress to specified format (PNG), quality - which is
                    // ignored for PNG, and out stream*/
                    b.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image myImg = Image.getInstance(stream.toByteArray());
                    createPDF(myImg);
                    //fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (gestureTouch == false) {
                    setResult(0);
                    finish();
                } else {
                    setResult(1);
                    finish();
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                gestureView.invalidate();
                gestureView.clear(true);
                gestureView.clearAnimation();
                gestureView.cancelClearAnimation();
            }
        });
    }

    public void createPDF(Image signature) {
        Document doc = new Document();


        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TESTANDROID";

            File dir = new File(path);
            if (!dir.exists()) {
                System.out.println("directory not exists");
                dir.mkdirs();
            } else {
                System.out.println("directory exirsts");
            }
            Log.d("path=", path);

            Log.d("PDFCreator", "PDF Path: " + path);


            File file = new File(dir, "sample.pdf");
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();


            Paragraph p1 = new Paragraph("Hi! I am generating my first PDF using DroidText");
            p1.setAlignment(Paragraph.ALIGN_CENTER);

            //add paragraph to document
            doc.add(p1);

            Paragraph p2 = new Paragraph("This is an example of a simple paragraph");
            p2.setAlignment(Paragraph.ALIGN_CENTER);

            doc.add(p2);

            /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = Bitmap.createBitmap(signature);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , stream);
            Image myImg = Image.getInstance(stream.toByteArray());*/
            //myImg.setAlignment(Image.MIDDLE);
            //add image to document
            signature.setAlignment(Element.ALIGN_CENTER);
            doc.add(signature);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            Toast.makeText(this,"Success",Toast.LENGTH_LONG).show();

        } catch (DocumentException de) {
            Log.e("PDFCreator EXCEPTION", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator EXCEPTION", "ioException:" + e);
        } finally {
            doc.close();
        }
    }
}
