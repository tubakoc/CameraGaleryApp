package com.example.kameragaleri.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kameragaleri.R
import com.example.kameragaleri.adapter.ResimAdapter
import com.example.kameragaleri.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException




class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    lateinit var geciciResimUri : Uri
    var uriList = ArrayList<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        initializeEvents()
        setDefaults()
    }

    private fun initializeViews() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvList.layoutManager = GridLayoutManager(this, 2)
    }

    private fun initializeEvents() {
        binding.btnYeni.setOnClickListener {
            kameraIzinKontrol()
        }

        binding.btnResimKapat.setOnClickListener {
            binding.clResim.visibility = View.GONE
            binding.ivBuyukResim.setImageURI(null)
        }
    }

    private fun setDefaults() {
        binding.rvList.adapter = ResimAdapter(this, uriList, ::itemClick, ::itemLongClick)

    }

    fun itemClick(position : Int)
    {
        binding.ivBuyukResim.setImageURI(uriList.get(position))
        binding.clResim.visibility = View.VISIBLE
    }

    fun itemLongClick(position : Int) : Boolean
    {
        sil(position)

        return false
    }

    @SuppressLint("NewApi")
    fun kameraIzinKontrol()
    {
        val requestList = ArrayList<String>()

        var izinDurum = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

        if (!izinDurum)
        {
            requestList.add(Manifest.permission.CAMERA)
        }

        izinDurum = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        if (!izinDurum)
        {
            requestList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        izinDurum = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        if (!izinDurum)
        {
            requestList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (requestList.size == 0)
        {
            kaynakSec()
        }
        else
        {
            requestPermissions(requestList.toTypedArray(), 0)
        }
    }

    @Throws(IOException::class)
    fun resimDosyasiOlustur()
    {
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        File.createTempFile("resim", ".jpg", dir).apply {
            geciciResimUri = FileProvider.getUriForFile(this@MainActivity, packageName, this)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for (gr in grantResults)
        {
            if (gr != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "İzinlerin tümü verilmedi", Toast.LENGTH_LONG).show()

                return
            }
        }

        kaynakSec()
    }

    fun kaynakSec()
    {
        val adb = AlertDialog.Builder(this)
        adb.setTitle("Kaynak Seç")
            .setMessage("Fotoğrafı ekleyeceğiniz kaynağı seçiniz.")
            .setPositiveButton("Kamera") { dialog, which ->
                kameraAc()
            }
            .setNegativeButton("Galeri") { dialog, which ->
                galeriAc()
            }
            .show()
    }

    fun kameraAc()
    {
        resimDosyasiOlustur()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, geciciResimUri)
        cameraRl.launch(intent)
    }

    var cameraRl= registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
        if (it.resultCode == RESULT_OK)
        {
            uriList.add(geciciResimUri)
            listeGuncelle()
        }
    }

    fun galeriAc()
    {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galeriRl.launch(intent)

    }
    var galeriRl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            uriList.add(result.data!!.data as Uri)
            listeGuncelle()
        }
    }

    fun listeGuncelle()
    {
        binding.rvList.adapter!!.notifyDataSetChanged()
    }

    fun sil(position: Int)
    {
        val adb = AlertDialog.Builder(this)
        adb.setTitle("Sil")
            .setMessage("Fotoğraf silinecektir. Onaylıyor musunuz?")
            .setPositiveButton("Evet") { dialog, which ->
                uriList.removeAt(position)
                listeGuncelle()
            }
            .setNegativeButton("vazgeç", null)
            .show()
    }
}