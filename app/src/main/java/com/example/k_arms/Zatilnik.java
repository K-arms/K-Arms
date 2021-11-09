package com.example.k_arms;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.k_arms.Data.Movie;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class Zatilnik extends AppCompatActivity {
    private static String DB_URL = "https://k-arms.firebaseio.com/";
    EditText etA, etB, etC, etD, etE, etF, etName, etNasechka, etContactZat, etComment, etWidthZat;
    static String tA, tB, tC, tD, tE, tModZat, email;

    String[] ModZat = {"TSK", "Универсальный"};
    private int on = 1;
    private int OFF = 0;

    private Button saveButton;
    private Button photoButton;

    //таблицы
    private TableRow tableD;
    private TableRow tableE;
    private TableRow tableF;
    TableRow.LayoutParams invisibleTable = new TableRow.LayoutParams(0, 0);
    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250);
    TableRow.LayoutParams visibleTable = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);


    //изображение затыльника схематично
    ImageView ZatView;


    // Фото экрана, путь фото в Storage
    private ImageView photoZat;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 72;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    FirebaseUser user = mAuth.getInstance().getCurrentUser();
    FirebaseListAdapter mAdapter;
    FirebaseStorage storage;
    StorageReference storageReference;


    public Zatilnik() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zatilnik);

        // взять Емайл
        Bundle arguments = getIntent().getExtras();
        email = arguments.get("email").toString();

        //initialize
        initializeFirebase();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        etName = findViewById(R.id.etNameZat);
        etNasechka = findViewById(R.id.etNasechka);
        etContactZat = findViewById(R.id.etContactZat);
        etComment=findViewById(R.id.etComment);
        etWidthZat=findViewById(R.id.etWidthZat);

        etA = findViewById(R.id.etA);
        etB = findViewById(R.id.etB);
        etC = findViewById(R.id.etC);
        etD = findViewById(R.id.etD);
        etE = findViewById(R.id.etE);
        etF = findViewById(R.id.etF);
        saveButton = findViewById(R.id.SaveZat);
        photoButton = findViewById(R.id.btnCreatePhotoZat);

        photoZat = (ImageView) findViewById(R.id.imgViewPhotoZat);
        ZatView = (ImageView) findViewById(R.id.imageZat);

        tableD = findViewById(R.id.tableD);
        tableE = findViewById(R.id.tableE);
        tableF = findViewById(R.id.tableF);


        //Spinners
        Spinner sModZat = (Spinner) findViewById(R.id.sModZat);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ModZat);
        // Определяем разметку для использования при выборе элемента
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        sModZat.setAdapter(adapter1);


        //Spinner Listener
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), "Нажатие "+view.getId(), Toast.LENGTH_SHORT).show();

                switch (parent.getId()) {
                    case (R.id.sModZat):
                        tModZat = (String) parent.getItemAtPosition(position);
                        if (tModZat == "TSK") {

                            ZatView.setImageResource(R.drawable.tsk2);
                            ZatView.setLayoutParams(lp2);
                            photoButton.setVisibility(View.GONE);
                            tableD.setVisibility(View.GONE);
                            photoZat.setVisibility(View.GONE);
                            tableE.setVisibility(View.GONE);

                        } else {
                            ZatView.setImageResource(R.drawable.universal);
                            ZatView.setLayoutParams(lp1);
                            photoButton.setVisibility(View.VISIBLE);

                            tableD.setVisibility(View.VISIBLE);
                            tableE.setVisibility(View.VISIBLE);
                            tableF.setVisibility(View.VISIBLE);
                        }

                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        sModZat.setOnItemSelectedListener(itemSelectedListener);


        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Zatilnik.this, MyCameraZat.class);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });


        // Get Firebase instance
        final Firebase fire = new Firebase(DB_URL);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ("".equals(etName.getText().toString())){
                    Toast.makeText(Zatilnik.this, "Поле Фамилия должно быть заполнено", Toast.LENGTH_SHORT).show();
                    return;
                }
                Movie m = new Movie();
                m.setEmail("Email "+ email);
                m.settContactZat("Контакт: " + etContactZat.getText().toString());
                m.settModZat("Тип затыльника: " + tModZat);
                m.settA("A: " + etA.getText().toString());
                m.settB("B: " + etB.getText().toString());
                m.settC("C: " + etC.getText().toString());
                m.settD("D: " + etD.getText().toString());
                m.settE("E: " + etE.getText().toString());
                m.settF("F: " + etF.getText().toString());
                m.setWidthZat(" Толщина затыльника: "+ etWidthZat.getText().toString());
                m.settNadpis("Надпись: " + etNasechka.getText().toString());
                m.settComment("Комментарий: " + etComment.getText().toString());

                //Persist
                fire.child(etName.getText().toString()+" Затыльник").setValue(m);
                // отправка фото
                if (tModZat.equals("Универсальный")) {
                    uploadImage();
                }
                Toast.makeText(Zatilnik.this, "Данные сохранены", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initializeFirebase() {
        Firebase.setAndroidContext(this);

    }


    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child(etName.getText().toString() + "/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(Zatilnik.this, "Фото загружено", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Zatilnik.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("myLogs", "requestCode = " + requestCode + ", resultCode = " + resultCode);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            filePath = data.getData();
            Log.d("myLogs", "filePath = " + filePath.toString());
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                photoZat.setVisibility(View.VISIBLE);
                photoZat.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
