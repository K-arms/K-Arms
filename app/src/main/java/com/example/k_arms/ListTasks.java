package com.example.k_arms;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.k_arms.Data.Movie;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class ListTasks extends AppCompatActivity {
    private static String DB_URL = "https://k-arms.firebaseio.com/";
    EditText etName, etOtvod1, etOtvod2, etLenArm, etNizLad, etFinAngle, etComment, etPalmAngle, etNeckLong, etNeckCross;
    static String tModWeap, tLogeType, tLogeMod, tTilType, email;
    String[] ModWeap = {"Beretta 682", "Beretta DT 10/11", "Perazzi MX-8 (2000)"};
    String[] LogeType = {"Цельный", "Сборный"};
    String[] LogeMod = {"Классический", "Спорт", "Ортопедический"};
    String[] TilType = {"TSK", "Nill-Griffe", "Kozmi"};


    private Button saveButton;
    private Button photoButton;

    //таблицы
    private TableRow tableTypeBack;
    private TableRow tableLenArm;
    private TableRow tableOtvodNosok;


    // Фото экрана, путь фото в Storage
    private ImageView imageView;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    FirebaseUser user = mAuth.getInstance().getCurrentUser();
    FirebaseListAdapter mAdapter;
    FirebaseStorage storage;
    StorageReference storageReference;


    public ListTasks() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tasks);

        // взять Емайл
        Bundle arguments = getIntent().getExtras();
        email = arguments.get("email").toString();

        //initialize
        initializeFirebase();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        etName = findViewById(R.id.etName);

        etOtvod1 = findViewById(R.id.etOtvod1);
        etOtvod2 = findViewById(R.id.etOtvod2);
        etLenArm = findViewById(R.id.etLenArm);
        etNizLad = findViewById(R.id.etNizLad);
        etFinAngle = findViewById(R.id.etFinAngle);
        etComment = findViewById(R.id.etCommentListTasks);
        etPalmAngle = findViewById(R.id.etPalmAngle);
        etNeckCross = findViewById(R.id.etNeckCross);
        etNeckLong = findViewById(R.id.etNeckLong);

        saveButton = findViewById(R.id.Save);
        photoButton = findViewById(R.id.btnCreatePhoto);

        imageView = (ImageView) findViewById(R.id.imgView);

        tableTypeBack = findViewById(R.id.tableTypeBack);
        tableLenArm = findViewById(R.id.tableLenArm);
        tableOtvodNosok = findViewById(R.id.tableOtvodNosok);

        //Spinners
        Spinner sModWeap = (Spinner) findViewById(R.id.sModWeap);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ModWeap);
        // Определяем разметку для использования при выборе элемента
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        sModWeap.setAdapter(adapter1);

        Spinner sLogeType = (Spinner) findViewById(R.id.sLogeType);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, LogeType);
        // Определяем разметку для использования при выборе элемента
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        sLogeType.setAdapter(adapter2);

        Spinner sLogeMod = (Spinner) findViewById(R.id.sLogeMod);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, LogeMod);
        // Определяем разметку для использования при выборе элемента
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        sLogeMod.setAdapter(adapter3);

        Spinner sTilType = (Spinner) findViewById(R.id.sTilType);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, TilType);
        // Определяем разметку для использования при выборе элемента
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        sTilType.setAdapter(adapter4);

        //Spinner Listener
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), "Нажатие "+view.getId(), Toast.LENGTH_SHORT).show();

                switch (parent.getId()) {
                    case (R.id.sModWeap):
                        tModWeap = (String) parent.getItemAtPosition(position);
                        //Toast.makeText(getApplicationContext(), tModWeap, Toast.LENGTH_SHORT).show();
                        break;
                    case (R.id.sLogeType):
                        tLogeType = (String) parent.getItemAtPosition(position);

                        if (tLogeType == "Цельный") {

                            tableTypeBack.setVisibility(View.GONE);
                            tableLenArm.setVisibility(View.VISIBLE);
                            tableOtvodNosok.setVisibility(View.VISIBLE);
                        } else {
                            tableTypeBack.setVisibility(View.VISIBLE);
                            tableLenArm.setVisibility(View.GONE);
                            tableOtvodNosok.setVisibility(View.GONE);
                        }


                        break;
                    case (R.id.sLogeMod):
                        tLogeMod = (String) parent.getItemAtPosition(position);
                        break;
                    case (R.id.sTilType):
                        tTilType = (String) parent.getItemAtPosition(position);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        sModWeap.setOnItemSelectedListener(itemSelectedListener);
        sLogeType.setOnItemSelectedListener(itemSelectedListener);
        sLogeMod.setOnItemSelectedListener(itemSelectedListener);
        sTilType.setOnItemSelectedListener(itemSelectedListener);

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ListTasks.this, MyCamera.class);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });


        // Get Firebase instance
        final Firebase fire = new Firebase(DB_URL);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(etName.getText().toString())) {
                    Toast.makeText(ListTasks.this, "Поле Фамилия должно быть заполнено", Toast.LENGTH_SHORT).show();
                    return;
                }
                Movie m = new Movie();
                m.setEmail("Email " + email);
                m.settModWeap("Модель оружия: " + tModWeap);
                m.settLogeType("Тип приклада: " + tLogeType);
                m.settLogeMod("Модель приклада: " + tLogeMod);
                m.settTilType("Тип тыла ручки: " + tTilType);
                m.settFinAngle("Угол наклона линии пальцев: " + etFinAngle.getText().toString());
                m.settPalmAngle("Угол положения ладони: " + etPalmAngle.getText().toString());
                m.settLenArm("Длинна руки: " + etLenArm.getText().toString());
                m.settOtvod1("Отвод Верх: " + etOtvod1.getText().toString());
                m.settOtvod2("Отвод низ: " + etOtvod2.getText().toString());
                m.settNizLad("Низ ладони: " + etNizLad.getText().toString());
                m.settNeckLong("Обхват шейки продольный: " + etNeckLong.getText().toString());
                m.settNeckLong("Обхват шейки поперечный: " + etNeckCross.getText().toString());
                m.settComment("Комментарий: " + etComment.getText().toString());

                //Persist
                fire.child(etName.getText().toString() + " Ложе").setValue(m);
                // отправка фото
                uploadImage();
                Toast.makeText(ListTasks.this, "Данные сохранены", Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(ListTasks.this, "Фото загружено", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ListTasks.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
