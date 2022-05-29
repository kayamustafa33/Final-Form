package com.mustafakaya.fiform.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mustafakaya.fiform.R;
import com.mustafakaya.fiform.databinding.ActivityShareNoteBinding;
import com.mustafakaya.fiform.ui.faculties.ArchitectureActivity;
import com.mustafakaya.fiform.ui.faculties.ArtsActivity;
import com.mustafakaya.fiform.ui.faculties.DentistryActivity;
import com.mustafakaya.fiform.ui.faculties.EconomicsActivity;
import com.mustafakaya.fiform.ui.faculties.EducationActivity;
import com.mustafakaya.fiform.ui.faculties.EngineerActivity;
import com.mustafakaya.fiform.ui.faculties.HealthActivity;
import com.mustafakaya.fiform.ui.faculties.LawActivity;
import com.mustafakaya.fiform.ui.faculties.PharmacyActivity;

import java.util.HashMap;

public class ShareNoteActivity extends AppCompatActivity {

    String[] items = {"Faculty of Architecture and Fine Arts","Faculty of Arts and Sciences","Faculty of Dentistry",
            "Faculty of Economics and Administrative Sciences", "Faculty of Education","Faculty of Engineering",
            "Faculty of Health Sciences", "Faculty of Law","Faculty of Pharmacy"};
    AutoCompleteTextView autoCompleteTxt;
    ArrayAdapter<String> adapterItems;
    ImageView pdf_image;
    String item;
    Button share_pdf;
    EditText pdf_name;
    TextView upload_pdf_text;
    private ActivityShareNoteBinding binding;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    double progress;
    String userPdfName,username;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShareNoteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setTitle("Share Notes");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("uploadPDF");

        pdf_image = findViewById(R.id.pdf_image);
        pdf_name = findViewById(R.id.pdf_name);
        share_pdf = findViewById(R.id.share_pdf);
        share_pdf.setVisibility(View.INVISIBLE);
        upload_pdf_text = findViewById(R.id.upload_pdf_txt);

        pdf_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectPDF(view);
            }
        });


        autoCompleteTxt = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item,items);
        autoCompleteTxt.setAdapter(adapterItems);

        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                item = adapterView.getItemAtPosition(i).toString();

                Toast.makeText(ShareNoteActivity.this,item, Toast.LENGTH_SHORT).show();
                if(!(item.equals(""))){
                    binding.selectFaculty.setVisibility(View.INVISIBLE);
                    share_pdf.setVisibility(View.VISIBLE);
                }else{
                    share_pdf.setVisibility(View.INVISIBLE);
                    binding.selectFaculty.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void selectPDF(View view) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed!",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(ShareNoteActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},12);
                    }
                }).show();
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},12);
            }
        }else{
            Intent intent = new Intent();
            intent.setType("application/pdf");
            intent.setAction(intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"PDF FILE SELECT"),12);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 12 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                pdf_image.setImageResource(R.drawable.newpdfimage);
                upload_pdf_text.setText("Enter PDF name");
                //pdf_name.setText("Enter New PDF Name");
                share_pdf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(pdf_name.getText().toString().equals("")){
                            Toast.makeText(ShareNoteActivity.this, "Enter PDF name!", Toast.LENGTH_SHORT).show();
                        }else{
                            uploadPDFFileFirebase(data.getData());
                        }
                    }
                });
            } else {
                Toast.makeText(this, "File didn't upload!", Toast.LENGTH_SHORT).show();
            }

    }

    private void uploadPDFFileFirebase(Uri data) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("File is loading...");
        progressDialog.setCancelable(true);
        progressDialog.show();

        userPdfName = binding.pdfName.getText().toString();
        String pdf_name = "uploadPDF/" + userPdfName + ".pdf";

        storageReference = storageReference.child(pdf_name);
        storageReference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageReference newReference = firebaseStorage.getReference(pdf_name);
                newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();
                        FirebaseUser user = auth.getCurrentUser();
                        String email = user.getEmail();

                        get_user_data();

                        HashMap<String,Object> postData = new HashMap<>();
                        postData.put("email",email);
                        postData.put("pdfname",userPdfName);
                        postData.put("downloadurl",downloadUrl);
                        postData.put("date",FieldValue.serverTimestamp());
                        postData.put("faculty",item);
                        postData.put("user_name",username);

                        firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                if(progress == 100){
                                    progressDialog.dismiss();
                                    Intent intent;
                                    if(item.equals("Faculty of Architecture and Fine Arts")){
                                        intent = new Intent(ShareNoteActivity.this,ArchitectureActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        binding.pdfName.setText("");
                                        pdf_image.setImageResource(R.drawable.pdfdownloadimage);
                                        upload_pdf_text.setText("Upload PDF file");
                                    }else if(item.equals("Faculty of Arts and Sciences")){
                                        intent = new Intent(ShareNoteActivity.this,ArtsActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        binding.pdfName.setText("");
                                        pdf_image.setImageResource(R.drawable.pdfdownloadimage);
                                        upload_pdf_text.setText("Upload PDF file");
                                    }else if(item.equals("Faculty of Dentistry")){
                                        intent = new Intent(ShareNoteActivity.this,DentistryActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        binding.pdfName.setText("");
                                        pdf_image.setImageResource(R.drawable.pdfdownloadimage);
                                        upload_pdf_text.setText("Upload PDF file");
                                    }else if(item.equals("Faculty of Economics and Administrative Sciences")){
                                        intent = new Intent(ShareNoteActivity.this,EconomicsActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        binding.pdfName.setText("");
                                        pdf_image.setImageResource(R.drawable.pdfdownloadimage);
                                        upload_pdf_text.setText("Upload PDF file");
                                    }else if(item.equals("Faculty of Education")){
                                        intent = new Intent(ShareNoteActivity.this,EducationActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        binding.pdfName.setText("");
                                        pdf_image.setImageResource(R.drawable.pdfdownloadimage);
                                        upload_pdf_text.setText("Upload PDF file");
                                    }else if(item.equals("Faculty of Engineering")){
                                        intent = new Intent(ShareNoteActivity.this,EngineerActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        binding.pdfName.setText("");
                                        pdf_image.setImageResource(R.drawable.pdfdownloadimage);
                                        upload_pdf_text.setText("Upload PDF file");
                                    }else if(item.equals("Faculty of Health Sciences")){
                                        intent = new Intent(ShareNoteActivity.this,HealthActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        binding.pdfName.setText("");
                                        pdf_image.setImageResource(R.drawable.pdfdownloadimage);
                                        upload_pdf_text.setText("Upload PDF file");
                                    }else if(item.equals("Faculty of Law")){
                                        intent = new Intent(ShareNoteActivity.this,LawActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        binding.pdfName.setText("");
                                        pdf_image.setImageResource(R.drawable.pdfdownloadimage);
                                        upload_pdf_text.setText("Upload PDF file");
                                    }else if(item.equals("Faculty of Pharmacy")){
                                        intent = new Intent(ShareNoteActivity.this,PharmacyActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        binding.pdfName.setText("");
                                        pdf_image.setImageResource(R.drawable.pdfdownloadimage);
                                        upload_pdf_text.setText("Upload PDF file");
                                    }else{
                                        Toast.makeText(ShareNoteActivity.this, "Home Page", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ShareNoteActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progress = (100.0 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                progressDialog.setMessage("File Uploaded... "+(int) progress+"%");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShareNoteActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void get_user_data(){
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.child("email").getValue().equals(firebaseUser.getEmail())){
                        username = ds.child("name").getValue(String.class);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}