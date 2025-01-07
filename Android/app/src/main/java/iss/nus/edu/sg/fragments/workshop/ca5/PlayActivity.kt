package iss.nus.edu.sg.fragments.workshop.ca5

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import iss.nus.edu.sg.fragments.workshop.ca5.databinding.ActivityPlayBinding

class PlayActivity : AppCompatActivity() {
    //Code by Chen Sirui
    private lateinit var binding:ActivityPlayBinding
    private lateinit var imageAdapter: PlayImageAdapter
    private var flippedPositions = mutableListOf<Int>()
    private var matchedCount = 0
    private val totalMatches = 6

    private lateinit var duplicatedImages:MutableList<String>
    private var isTiming = false
    private var elapsedTime = 0L
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable{
        override fun run(){
            //Code by Chen Sirui
            if(isTiming){
                elapsedTime += 1000
                updateTimerText()
                handler.postDelayed(this,1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //Code by Chen Sirui
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedImages = intent.getStringArrayListExtra("selectedImages")?: ArrayList()
        duplicatedImages = duplicate(selectedImages)

        imageAdapter = PlayImageAdapter(duplicatedImages){position -> onImageClicked(position)}
        binding.apply {
            imageGrid.adapter = imageAdapter
            matchingCountText.text = "Matched: $matchedCount / $totalMatches"
        }
    }
    private fun onImageClicked(position:Int){
        //Code by Chen Sirui
        if(flippedPositions.size<2 && !imageAdapter.isFlipped(position)){
            imageAdapter.flipImage(position)
            flippedPositions.add(position)
            if (flippedPositions.size == 1){
                startTimer()
            }
            if (flippedPositions.size == 2){
                checkForMatch()
            }
        }
    }

    private fun checkForMatch(){
        //Code by Chen Sirui
        if(flippedPositions.size != 2){return}
        val firstImage = flippedPositions[0]
        val secondImage = flippedPositions[1]
        if (duplicatedImages[firstImage]==duplicatedImages[secondImage]){
            matchedCount++
            binding.matchingCountText.text = "Matched: $matchedCount / $totalMatches"
            flippedPositions.clear()

            if (matchedCount == totalMatches){
                endTimer()
                showGameEndDialog()
            }
        }else{
            Handler(Looper.getMainLooper()).postDelayed({
                imageAdapter.hideImage(firstImage)
                imageAdapter.hideImage(secondImage)
                flippedPositions.clear()
            },2000)
        }

    }
    private fun startTimer(){
        //Code by Chen Sirui
        isTiming = true
        handler.post(runnable)
    }
    private fun endTimer(){
        //Code by Chen Sirui
        isTiming = false
        handler.removeCallbacks(runnable)
    }
    private fun updateTimerText(){
        //Code by Chen Sirui
        val time = (elapsedTime/1000).toInt()
        val seconds = time%60
        val minutes = time/60
        binding.timerText.text = String.format("Timer: %02d:%02d",minutes,seconds)
    }
    private fun showGameEndDialog(){
        //Code by Chen Sirui
        val time = (elapsedTime/1000).toInt()
        val minutes = time/60
        val seconds = time%60

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Congratulations!")
        dialogBuilder.setMessage("Your time is $minutes minutes $seconds seconds!")
        dialogBuilder.setPositiveButton("Roger that!"){dialog,_ ->dialog.dismiss()}

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }
    private fun duplicate(images: List<String>):MutableList<String>{
        //Code by Chen Sirui
        val duplicatedImages = mutableListOf<String>()
        for(image in images){
            duplicatedImages.add(image)
            duplicatedImages.add(image)
        }
        duplicatedImages.shuffle()
        return duplicatedImages
    }
}