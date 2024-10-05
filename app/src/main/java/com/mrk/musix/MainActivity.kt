package com.mrk.musix

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.mrk.musix.adapter.CategoryAdapter
import com.mrk.musix.adapter.SectionSongListAdapter
import com.mrk.musix.databinding.ActivityMainBinding
import com.mrk.musix.models.CategoryModel
import com.mrk.musix.models.SongModel

@Suppress("UNUSED_EXPRESSION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var categoryAdapter: CategoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        getCategories()
        setUpSection(
            "section_1",
            binding.section1MainLayout,
            binding.section1Title,
            binding.section1RecyclerView
        )
        setUpSection(
            "section_2",
            binding.section2MainLayout,
            binding.section2Title,
            binding.section2RecyclerView
        )
        setUpSection(
            "section_3",
            binding.section3MainLayout,
            binding.section3Title,
            binding.section3RecyclerView
        )

        setUpMostlyPlayed(
            "mostly_played",
            binding.mostlyPlayedMainLayout,
            binding.mostlyPlayedTitle,
            binding.mostlyPlayedRecyclerView
        )

        binding.optionBtn.setOnClickListener {
            showPopUpMenu()
        }
    }

    private fun showPopUpMenu() {
        val popupMenu = PopupMenu(this, binding.optionBtn)
        val inflator = popupMenu.menuInflater
        inflator.inflate(R.menu.option_menu, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    logout()
                    true
                }
            }
            false
        }
    }

    private fun logout() {
        MyExoplayer.getInstance()?.release()
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onResume() {
        super.onResume()
        showPlayerView()
    }

    private fun showPlayerView() {
        binding.playerView.setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
        }
        MyExoplayer.getCurrentSong()?.let {
            binding.playerView.visibility = View.VISIBLE
            binding.songTitleTextView.text = "Playing: ${it.title}"
            Glide.with(binding.songCoverImageView).load(it.coverUrl)
                .apply(
                    RequestOptions().transform(RoundedCorners(32))
                )
                .into(binding.songCoverImageView)
        } ?: run {
            binding.playerView.visibility = View.GONE
        }
    }

    // Categories or Genres
    private fun getCategories() {
        FirebaseFirestore.getInstance().collection("category").get().addOnSuccessListener {
            val categoryList = it.toObjects(CategoryModel::class.java)
            setUpCategoryRecyclerView(categoryList)
        }
    }

    private fun setUpCategoryRecyclerView(categoryList: List<CategoryModel>) {
        categoryAdapter = CategoryAdapter(categoryList)
        binding.categoriesRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.categoriesRecyclerView.adapter = categoryAdapter
    }

    // Sections
    private fun setUpSection(
        id: String,
        mainLayout: RelativeLayout,
        titleView: TextView,
        recyclerView: RecyclerView,
    ) {
        FirebaseFirestore.getInstance().collection("sections")
            .document(id)
            .get().addOnSuccessListener {
                val section = it.toObject(CategoryModel::class.java)
                section?.apply {
                    mainLayout.visibility = View.VISIBLE
                    titleView.text = name
                    recyclerView.layoutManager =
                        LinearLayoutManager(
                            this@MainActivity,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                    recyclerView.adapter = SectionSongListAdapter(songs)

                    mainLayout.setOnClickListener {
                        SongsListActivity.category = section
                        startActivity(Intent(this@MainActivity, SongsListActivity::class.java))
                    }
                }
            }
    }


    private fun setUpMostlyPlayed(
        id: String,
        mainLayout: RelativeLayout,
        titleView: TextView,
        recyclerView: RecyclerView,
    ) {
        FirebaseFirestore.getInstance().collection("sections")
            .document(id)
            .get().addOnSuccessListener {
                // get most played song
                FirebaseFirestore.getInstance().collection("songs")
                    .orderBy("count", Query.Direction.DESCENDING)
                    .limit(5)
                    .get().addOnSuccessListener {songListSnapshot->
                        val songModelList = songListSnapshot.toObjects<SongModel>()
                        val songsIdList = songModelList.map {
                            it.id
                        }.toList()

                        val section = it.toObject(CategoryModel::class.java)
                        section?.apply {
                            section.songs = songsIdList
                            mainLayout.visibility = View.VISIBLE
                            titleView.text = name
                            recyclerView.layoutManager =
                                LinearLayoutManager(
                                    this@MainActivity,
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                                )
                            recyclerView.adapter = SectionSongListAdapter(songs)

                            mainLayout.setOnClickListener {
                                SongsListActivity.category = section
                                startActivity(Intent(this@MainActivity, SongsListActivity::class.java))
                            }
                        }
                    }


            }
    }
}