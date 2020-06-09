package com.india.mkit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;


import java.util.List;

public class MainActivity extends AppCompatActivity {
private ImageView img;
private TextView dttext;
private Button capture,detect;
    Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img=findViewById(R.id.image);
        dttext=findViewById(R.id.dttext);
        capture=findViewById(R.id.capture);
        detect=findViewById(R.id.dt);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
                dttext.setText("");
            }
        });
        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectTextfromImage();
            }
        });
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
             imageBitmap = (Bitmap) extras.get("data");
            img.setImageBitmap(imageBitmap);
        }
    }

    private void detectTextfromImage() {

        FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
        detector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displatTextfromImage(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displatTextfromImage(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.Block> blockList=firebaseVisionText.getBlocks();
        if (blockList.size()<0){
            Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show();
        }else {
            for (FirebaseVisionText.Block block:firebaseVisionText.getBlocks()){
                String txt=block.getText();
                dttext.setText(txt);
            }
        }
    }

}
