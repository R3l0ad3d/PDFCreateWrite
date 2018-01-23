package com.example.user.pdfcreate;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    AppCompatEditText etName;
    AppCompatEditText etLocation;
    PDFView pdfView;

    String LOG_TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etLocation = findViewById(R.id.etLocation);

        pdfView = findViewById(R.id.pdfView);

        checkPermission();

    }


    public void createPDF(View view)  {
        try {
            if(canWriteOnExternalStorage()){
                createPdf();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("FIle Not Found : ",e.getMessage());
            Toast.makeText(this, "FIle Not Found : "+e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (DocumentException e) {
            e.printStackTrace();
            Log.d("Document Exception : ",e.getMessage());
            Toast.makeText(this, "Document error : "+e.getMessage(), Toast.LENGTH_LONG).show();
        }

        //CreateDirectory();
    }

    private void createPdf() throws FileNotFoundException, DocumentException {

        File pdfFolder = new File(Environment.getExternalStorageDirectory().toString(), "pdfdemo");


        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
            Toast.makeText(this, "Create Directory", Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "Pdf Directory created");
        }else {
            Toast.makeText(this, "Not Create Directory", Toast.LENGTH_LONG).show();
        }
        //Create time stamp
        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

        File myFile = new File(pdfFolder.toString()+File.separator + timeStamp + ".pdf");

        try {
            myFile.createNewFile();
            Toast.makeText(this,"File Create Success ......"+myFile.toString(),Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"File Create Failed Error : "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
        //OutputStream output = null;
        OutputStream output = new FileOutputStream(myFile.toString());
        Log.d("Path : ",myFile.toString());
        //Step 1
        Document document = new Document();

        //Step 2
        PdfWriter.getInstance(document, output);

        //Step 3
        document.open();

        //Step 4 Add content
        document.add(new Paragraph(etName.getText().toString()));
        document.add(new Paragraph(etLocation.getText().toString()));

        //Step 5: Close the document
        document.close();
        pdfView.fromFile(myFile).load();
    }



    public static boolean canWriteOnExternalStorage() {
        // get the state of your external storage
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // if storage is mounted return true
            return true;
        }
        return false;
    }

    void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
        }else {
            Toast.makeText(this,"permission ok",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 111) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //start audio recording or whatever you planned to do
            }else if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show an explanation to the user *asynchronously*
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This permission is important to record audio.")
                            .setTitle("Important permission required");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
                        }
                    });
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
                }else{
                    //Never ask again and handle your app without permission.
                }
            }
        }
    }
}
