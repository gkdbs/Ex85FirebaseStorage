package com.mrhi2020.ex85firebasestorage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv= findViewById(R.id.iv);
    }

    public void clickLoad(View view) {
        //Firebase Storage에 저장되어 있는 이미지 파일 읽어오기

        //Firebase Storage 관리객체 얻어오기
        FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();

        //root(최상위) 참조객체 얻어오기
        StorageReference rootRef= firebaseStorage.getReference();

        //읽어오길 원하는 파일의 참조객체 얻어오기
        //StorageReference imgRef= rootRef.child("moana02.jpg");
        StorageReference imgRef= rootRef.child("photos/moana05.jpg");//폴더지정 가능

        if(imgRef != null){
            //참조객체로 부터 다운로드 URL을 얻어오는 작업을 수행하고 성공되었다는 리스너추가
            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //토큰값도 함께 있는 다운로드 URL( uri )를 이미지뷰에 설정
                    Glide.with(MainActivity.this).load(uri).into(iv);
                }
            });
        }
    }

    //멤버변수
    Uri imgUri=null; //선택된 이미지의 경로를 가진 Uri객체

    public void clickSelect(View view) {
        Intent intent= new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==10 && resultCode==RESULT_OK){
            imgUri= data.getData();
            Glide.with(this).load(imgUri).into(iv);
        }

    }

    public void clickSave(View view) {
        //Firebase Storage는 파일의 절대주소[실제경로]가 없이 Uri(콘텐츠 주소)로 업로드 가능함.

        //Firebase Storage 관리객체 소환
        FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();

        //storage 업로드할 파일명이 중복되면 덮어쓰기가 되므로..
        //날짜를 이용하여 파일명 만들어내기
        SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddhhmmss");
        String fileName= sdf.format(new Date()) + ".png"; //원본파일명을 알려면 절대주소까지 구해야 해서 시간상 그냥 무조건 png로[jpg를 png로 저장해도 문제없음]

        //저장할 파일위치에 대한 참조객체 얻어오기
        StorageReference imgRef= firebaseStorage.getReference("uploads/"+fileName); //"uploads"라는 폴더가 없으면 만들어 줌

        //선택한 이미지파일 업로드..하고 그 결과를 리턴받는 작업을 해주는 작업자를 리턴
        UploadTask uploadTask= imgRef.putFile(imgUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(MainActivity.this, "success upload", Toast.LENGTH_SHORT).show();
            }
        });

        //업로드한 파일의 다운로드 경로를 실시간 DB에 저장하면
        //게시판처럼 데이터와 이미지가 함께 저장되어 보여지는 앱도 구현가능함.

        //내일 DB, Storage를 이용해서 채팅만들기
    }
}